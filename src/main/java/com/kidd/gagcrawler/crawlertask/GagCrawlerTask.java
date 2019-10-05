package com.kidd.gagcrawler.crawlertask;

import com.alibaba.fastjson.JSON;
import com.kidd.gagcrawler.model.Gag;
import com.kidd.gagcrawler.model.GagPo;
import com.kidd.gagcrawler.model.HotGagVo;
import com.kidd.gagcrawler.service.GagService;
import com.kidd.gagcrawler.utils.HttpsUtils;
import com.kidd.gagcrawler.utils.MailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class GagCrawlerTask {

    @Value("${app.gag.hoturl}")
    private String gagHotUrl;

    @Value("${app.gag.crwalerpagecount}")
    private Integer crawlerPageCount;

    @Autowired
    private GagService gagService;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.queue-count}")
    private Integer queueCount;

    @Scheduled(fixedRate = 14400000)
    private void configureTasks() throws IOException, InterruptedException {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());

        // 1、爬取热门gag信息，存入数据库,发生异常之后记录并且发送邮件
        crawl9gagInfo(gagHotUrl);

        // 2、构造html邮件
        String htmlContent = null;
        try {
            htmlContent = constructGagHtmlContent();
        } catch (Exception e) {
            System.out.printf("构造html邮件失败：" + e.getMessage());
        }

        // 3、发送图片邮件
        try {
            //mailService.sendHtmlMail("1205889529@qq.com", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM月dd日HH时"))+"的9Gag热门数据", htmlContent);
            mailService.sendHtmlMail("13683177737@163.com", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM月dd日HH时")) + "的9Gag热门数据", htmlContent);
        } catch (MessagingException e) {
            System.out.printf("发送图片邮件失败：" + e.getMessage());
        }

        // 4、将MP4转成gif发邮件
        try {
            sendGagGifHtmlContent();
        } catch (Exception e) {
            System.out.printf("发送gif邮件失败：" + e.getMessage());
        }

    }

    private void sendGagGifHtmlContent() {
        System.out.println("开始gif任务");
        String pathFormat = "/tmp/9gaggif/%s.gif";
        List<String> cids = new ArrayList<String>();
        LocalDateTime dateTime = LocalDateTime.now().minusHours(2);
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<GagPo> gagVideoPos = gagService.selectVideoGagsGtDateTime(dateTime);
        gagVideoPos = gagVideoPos.stream().sorted(Comparator.comparing(GagPo::getUpvotecount).reversed()).collect(Collectors.toList());
        int count = 0;
        int loocount = 1;
        for (GagPo gagPo : gagVideoPos) {
            count++;
            String gifOutFilePath = String.format("/tmp/9gaggif/%s.gif", gagPo.getId());
            String mp4SourceUrl = String.format("https://img-9gag-fun.9cache.com/photo/%s_460sv.mp4", gagPo.getId());
            String command = String.format("ffmpeg -ss 0 -t 40 -i %s -s 213x320 -r 10 %s -y", mp4SourceUrl, gifOutFilePath);
            //操作ffmpeg生成gif图片
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(command);
                InputStream stderr = proc.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                int exitVal = proc.waitFor();
                System.out.println("gif图片处理成功: " + gagPo.getId());
            } catch (Exception e) {
                System.out.println("生成gif图片异常：" + e.getMessage());
            }
            //html邮件中加入文件
            cids.add(gagPo.getId());

            if (count >= queueCount || gagPo.equals(gagVideoPos.get(gagVideoPos.size()-1))) {
                //发送一次
                //过滤，只获取下载成功的gif图片
                List<GagPo> gagVideoPosTemp = new ArrayList<>();
                for (GagPo u : gagVideoPos) {
                    if (cids.contains(u.getId())) {
                        gagVideoPosTemp.add(u);
                    }
                }
                System.out.println("开始构造gif图片邮件");
                Context context = new Context();
                context.setVariable("gagVideoPos", gagVideoPosTemp);
                String emailContent = templateEngine.process("gagTemplate3.html", context);
                System.out.println("开始发送gif图片邮件 loopcount:"+loocount);
                try {
                    mailService.sendInlineResourceMailBatch("13683177737@163.com", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM月dd日HH时"))
                                    + "的9Gag热门gif数据" + "LoopCount" + loocount
                            , emailContent, pathFormat, cids);
                }
                catch (Exception e){
                    System.out.println("发送单个gif图片邮件失败："+e.getMessage());
                }
                System.out.println("发送gif图片邮件成功");
                //重置
                cids = new ArrayList<>();
                count = 0;
                loocount++;
            }
        }
        System.out.println("结束gif任务");
    }

    private void crawl9gagInfo(String gagUrl) throws IOException, InterruptedException {

        // 第一次尝试爬取热门gag（第一页）
        String hotGags = "";
        try {
            hotGags = HttpsUtils.doGetStr(gagUrl);

        } catch (Exception e) {
            System.out.println("The First Crawler Try Of GagUrl Fail, Reason:" + e.getMessage());
            return;
        }
        HotGagVo hotGagVoFirstTry = JSON.parseObject(hotGags, HotGagVo.class);
        List<Gag> gagListFirst = Arrays.asList(hotGagVoFirstTry.getData().getPosts());
        for (Gag gag : gagListFirst) {
            GagPo gagPo = gag2GagPo(gag);
            gagService.insertIfIdNE(gagPo);
        }

        // 循环抓取一定页数
        crawlerPageCount = (crawlerPageCount == null) ? 10 : crawlerPageCount;
        Integer currentPage = 2;
        String nextCursor = hotGagVoFirstTry.getData().getNextCursor();
        System.out.println("first page nextcursor:" + nextCursor);
        while (currentPage <= 40) {
            System.out.println(String.format("开始抓取第%d页", currentPage));
            Thread.sleep(5000);
            String currentHotUrl = URLDecoder.decode(String.format("%s?%s", gagUrl, nextCursor), "GBK");
            System.out.println("currentHotUrl：" + currentHotUrl);
            String hotGagVoStr = HttpsUtils.doGetStr(currentHotUrl);
            System.out.println("hotGagVoStr:" + hotGagVoStr);

            // mock
            // String hotGagVoStr = Application.getResource();
            HotGagVo hotGagVo = JSON.parseObject(hotGagVoStr, HotGagVo.class);
            if (hotGagVo == null) return;
            List<Gag> gagList = Arrays.asList(hotGagVo.getData().getPosts());
            if (gagList.size() <= 0) return;
            for (Gag gag : gagList) {
                GagPo gagPo = gag2GagPo(gag);
                try {
                    gagService.insertIfIdNE(gagPo);
                } catch (Exception e) {
                    System.out.println("抓取插入异常：" + e.getMessage() + " gagPo:" + JSON.toJSONString(gag));
                }
            }
            nextCursor = hotGagVo.getData().getNextCursor();
            currentPage++;
        }
        System.out.println(String.format("crawler gag end！"));
    }

    // 构造今天的图片html邮件
    public String constructGagHtmlContent() {
        LocalDateTime dateTime = LocalDateTime.now().minusHours(2);
        List<GagPo> gagPhotoPos = gagService.selectPhotoGagsGtDateTime(dateTime);
        gagPhotoPos = gagPhotoPos.stream().sorted(Comparator.comparing(GagPo::getUpvotecount).reversed()).collect(Collectors.toList());
        List<GagPo> gagVideoPos = gagService.selectVideoGagsGtDateTime(dateTime);
        gagVideoPos = gagVideoPos.stream().sorted(Comparator.comparing(GagPo::getUpvotecount).reversed()).collect(Collectors.toList());
        if (gagPhotoPos == null) {
            return "";
        }
        Context context = new Context();
        context.setVariable("gagPhotoPos", gagPhotoPos);
        context.setVariable("gagVideoPos", gagVideoPos);
        String emailContent = templateEngine.process("gagTemplate2.html", context);
        return emailContent;
    }


    private GagPo gag2GagPo(Gag gag) {
        if (gag == null) return null;
        GagPo gagPo = new GagPo() {{
            setUrl(gag.getUrl());
            setUpvotecount(gag.getUpVoteCount());
            setType(gag.getType());
            setTitle(gag.getTitle());
            setSourceurl(gag.getSourceUrl());
            setPromoted(gag.getPromoted());
            setSourcedomain(gag.getSourceDomain());
            setNsfw(gag.getNsfw());
            setIsvotemasked(gag.getIsVoteMasked());
            setImages(JSON.toJSONString(gag.getImages()));
            setId(gag.getId());
            setHaslongpostcover(gag.getHasLongPostCover());
            setDownvotecount(gag.getDownVoteCount());
            setDescriptionhtml(gag.getDescriptionHtml());
            setCreationts(gag.getCreationTs());
            setCommentscount(gag.getCommentsCount());
            setCrawlerdate(LocalDateTime.now());
        }};
        return gagPo;
    }

    private void testSendGifMail() {
        //String pathFormat = "/tmp/9gaggif/%s.gif";
        String pathFormat = "D:/tmp/9gaggif/%s.gif";
        List<String> cids = new ArrayList<String>() {{
            add("test1");
            add("test2");
        }};
        Context context = new Context();
        String emailContent = templateEngine.process("gagTemplate3.html", context);
        mailService.sendInlineResourceMailBatch("13683177737@163.com", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM月dd日HH时")) + "的9Gag热门gif数据"
                , emailContent, pathFormat, cids);
    }

}

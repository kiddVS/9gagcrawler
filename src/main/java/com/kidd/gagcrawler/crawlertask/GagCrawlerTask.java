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
import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    //@Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒 (单位：毫秒)
    @Scheduled(fixedRate=500000)
    private void configureTasks() throws IOException, InterruptedException {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());

        // 1、爬取热门gag信息，存入数据库,发生异常之后记录并且发送邮件
        //crawl9gagInfo(gagHotUrl);

        // 2、构造html邮件
        String htmlContent = constructGagHtmlContent();

        // 3、发送邮件
        try {
            mailService.sendHtmlMail("13683177737@163.com", LocalDateTime.now()+"的9Gag热门数据", htmlContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void crawl9gagInfo(String gagUrl) throws IOException, InterruptedException {

        // 第一次尝试爬取热门gag
        String hotGags = "";
        try {
            hotGags = HttpsUtils.doGetStr(gagUrl);

            // mock
            // hotGags = Application.getResource();

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
        System.out.println("first page nextcursor:"+nextCursor);
        while (currentPage <= 10) {
            System.out.println(String.format("开始抓取第%d页",currentPage));
            Thread.sleep(5000);
            String currentHotUrl = URLDecoder.decode( String.format("%s?%s", gagUrl, nextCursor), "GBK");
            System.out.println("currentHotUrl："+currentHotUrl);
            String hotGagVoStr = HttpsUtils.doGetStr(currentHotUrl);
            System.out.println("hotGagVoStr:"+hotGagVoStr);

            // mock
            //String hotGagVoStr = Application.getResource();

            HotGagVo hotGagVo = JSON.parseObject(hotGagVoStr, HotGagVo.class);
            if (hotGagVo == null) return;
            List<Gag> gagList = Arrays.asList(hotGagVo.getData().getPosts());
            if (gagList.size() <= 0) return;
            for (Gag gag : gagList) {
                GagPo gagPo = gag2GagPo(gag);
                gagService.insertIfIdNE(gagPo);
            }
            nextCursor = hotGagVo.getData().getNextCursor();
            currentPage++;
        }
        System.out.println(String.format("crawler gag end！"));
    }

    // 构造今天的图片html邮件
    public  String constructGagHtmlContent(){
        LocalDateTime dateTime = LocalDateTime.now().minusHours(10);
        List<GagPo> gagPos = gagService.selectPhotoGagsGtDateTime(dateTime);
        if(gagPos == null){
            return "";
        }
        Context context = new Context();
        context.setVariable("gagPos", gagPos);
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
}

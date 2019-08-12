package com.kidd.gagcrawler;

import com.kidd.gagcrawler.crawlertask.GagCrawlerTask;
import com.kidd.gagcrawler.httpsconf.HttpsClientRequestFactory;
import com.kidd.gagcrawler.mapper.GagMapper;
import com.kidd.gagcrawler.mapper.GagPoMapper;
import com.kidd.gagcrawler.model.GagPo;
import com.kidd.gagcrawler.model.HotGagVo;
import com.kidd.gagcrawler.utils.MailServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootApplication
@MapperScan("com.kidd.gagcrawler.mapper")
public class Application implements CommandLineRunner {

    @Autowired
    private GagMapper gagMapper;

    @Autowired
    private GagPoMapper gagPoMapper;

    @Autowired
    private MailServiceImpl mailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GagCrawlerTask gagCrawlerTask;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        String url = "https://m.9gag.com/v1/group-posts/group/default/type/hot?after=az1gPXK%2CabrXAv8%2CaN0rv86&c=20";
//        String url1 = "https://m.9gag.com/v1/group-posts/group/default/type/hot?after=az1gPXK,abrXAv8,aN0rv86&c=20";
//        String url3 = "https://ihome.jd.com/vender/getStrategyList?page=1&type=1&venderid=794218&rand=1565334380572";
//        RestTemplate restTemplateHttps = new RestTemplate(new HttpsClientRequestFactory());
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
//        messageConverters.add(stringHttpMessageConverter);
//        restTemplateHttps.setMessageConverters(messageConverters);
//        ResponseEntity<String> responseEntity2 = restTemplateHttps.getForEntity(url, String.class);
//        System.out.println(responseEntity2.getBody());
//         String hotGag = getResource();
//         HotGagVo hotGagVo = JSON.parseObject(hotGag, HotGagVo.class);
//         System.out.printf(gagMapper.testCount().toString());
//         GagPo gagPo = new GagPo(){{
//             setCommentscount(1);
//             setCrawlerdate(LocalDateTime.now());
//             setCreationts("646646");
//             setDescriptionhtml("");
//             setDownvotecount(1);
//             setHaslongpostcover(0);
//             setId("ijoi");
//             setImages("");
//             setIsvotemasked(1);
//             setNsfw(11);
//             setPromoted(1);
//             setSourcedomain("");
//             setSourceurl("");
//             setTitle("test");
//             setType("Photo");
//             setUpvotecount(1);
//             setUrl("www.test.com");
//         }};
//         gagPoMapper.insert(gagPo);
//         System.out.println("----------start-----------");
//         System.out.println(hotGagVo);
        //mailService.sendSimpleMail("13683177737@163.com","测试测试标题","测试测试内容");
//        String content = "<html><body><h3><font color=\"red\">" + "大家好，这是springboot发送的HTML邮件" + "</font></h3></body></html>";
//        mailService.sendHtmlMail("13683177737@163.com", "发送邮件测试", content);
//        System.out.println("OK");
//        Context context = new Context();
//        context.setVariable("testTxt", "测试填充内容");
//        String emailContent = templateEngine.process("gagTemplate.html", context);
//        mailService.sendHtmlMail("13683177737@163.com", "这是一个模板文件", emailContent);

//        String content = gagCrawlerTask.constructGagHtmlContent();
    }

    public static String getResource() throws IOException {

        Resource resource = new ClassPathResource("mock/hotgag_mock.json");
        File sourceFile = resource.getFile();
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(sourceFile),"GBK");

        // 建立一个对象，它把文件内容转成计算机能读懂的语言
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        StringBuilder sb = new StringBuilder();
        line = br.readLine();
        while (line != null) {
            sb.append(line);
            // 一次读入一行数据
            line = br.readLine();
        }
        return sb.toString();
    }
}

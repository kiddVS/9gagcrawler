package com.kidd.gagcrawler;

import com.kidd.gagcrawler.httpsconf.HttpsClientRequestFactory;
import com.kidd.gagcrawler.model.HotGagVo;
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
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;

@SpringBootApplication
public class Application implements CommandLineRunner {

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
         String hotGag = getResource();
         HotGagVo hotGagVo = JSON.parseObject(hotGag, HotGagVo.class);
         System.out.println("----------start-----------");
         System.out.println(hotGagVo);
    }

    private String getResource() throws IOException {

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

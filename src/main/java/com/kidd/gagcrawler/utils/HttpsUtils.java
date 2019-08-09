package com.kidd.gagcrawler.utils;

import com.kidd.gagcrawler.httpsconf.HttpsClientRequestFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HttpsUtils {
    public String doGetStr(String url){
        RestTemplate restTemplateHttps = new RestTemplate(new HttpsClientRequestFactory());
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        messageConverters.add(stringHttpMessageConverter);
        restTemplateHttps.setMessageConverters(messageConverters);
        ResponseEntity<String> responseEntity2 = restTemplateHttps.getForEntity(url, String.class);
       return responseEntity2.getBody();
    }
}

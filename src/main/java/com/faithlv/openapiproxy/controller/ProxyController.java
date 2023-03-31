package com.faithlv.openapiproxy.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 代理控制器
 *
 * @author zhengjh
 * @date 2023/03/31
 */
@Validated
@RestController
@RequestMapping
@Slf4j
public class ProxyController {

    @Value("${targetUrl}")
    String targetUrl = "https://api.openai.com";

    /**
     * 代理开放api
     *
     * @param httpServletRequest http servlet请求
     * @return {@link String}
     */
    @RequestMapping("/**")
    public Object proxyToOpenApi(HttpServletRequest httpServletRequest) throws IOException, URISyntaxException {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        //获取body
        String s = getBody(httpServletRequest) == "" ? null : getBody(httpServletRequest);
        Gson gson = new Gson();
        Object body = gson.fromJson(s, Object.class);
        log.info("body:{}",body);
        //获取url
        URI url = getUrl(httpServletRequest);
        log.info("url:{},",url.toString());
        //获取请求方法
        HttpMethod httpMethod = HttpMethod.valueOf(httpServletRequest.getMethod());
        log.info("httpMethod:{}",httpMethod);
        //获取请求头
        MultiValueMap<String, String> headers = getHeader(httpServletRequest);
        RequestEntity<Object> requestEntity = new RequestEntity<>(body, headers, httpMethod, url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            return response.getBody();
        }catch (HttpClientErrorException e){
            e.printStackTrace();
            return e.getResponseBodyAsString();
        }
    }

    String getBody(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while((str = br.readLine()) != null){
            wholeStr += str;
        }
        return wholeStr;
    }

    URI getUrl(HttpServletRequest httpServletRequest) throws URISyntaxException {
        //todo "#"情况
        String requestURI = (httpServletRequest.getRequestURI() == null) ? "" : httpServletRequest.getRequestURI();
        String queryString = (httpServletRequest.getQueryString() == null) ? "" : ("?" + httpServletRequest.getQueryString());
        URI url = new URI(targetUrl + requestURI + queryString);
        return url;
    }

    MultiValueMap<String,String> getHeader(HttpServletRequest httpServletRequest){
         //获取请求头
         Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
         MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
         while (headerNames.hasMoreElements()) {
             String name = headerNames.nextElement();
             String value = httpServletRequest.getHeader(name);
             map.add(name, value);
         }
         //
         map.remove("host");
         return map;
    }

}

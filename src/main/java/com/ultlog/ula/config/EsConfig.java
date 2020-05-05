package com.ultlog.ula.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.http.HttpHost.DEFAULT_SCHEME_NAME;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-02
 **/
@Configuration
public class EsConfig {


    @Value("${ultlog.es.address.host}")
    private String host;

    @Value("${ultlog.es.address.port}")
    private int port;

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, DEFAULT_SCHEME_NAME)));
    }
}

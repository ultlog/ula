package com.ultlog.ula.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultlog.ula.model.Log;
import com.ultlog.ula.service.EsService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @program: ula
 * @link: github.com/ultlog/collector
 * @author: will
 * @create: 2020-05-02
 **/
@Service
public class EsServiceImpl implements EsService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void insertLog(Log log) {

        ObjectMapper objectMapper = new ObjectMapper();


        final String s;
        try {
            s = objectMapper.writeValueAsString(log);
            IndexRequest request = new IndexRequest(log.getProject());
            request.id(String.valueOf(log.hashCode()));
            request.source(s, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // todo
            e.printStackTrace();
        }
    }

    @Override
    public void insertSystem() {

    }
}

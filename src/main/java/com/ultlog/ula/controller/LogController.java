package com.ultlog.ula.controller;

import com.ultlog.ula.model.Log;
import com.ultlog.ula.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @program: ula
 * @link: github.com/ultlog/collector
 * @author: will
 * @create: 2020-05-02
 **/
@RestController
public class LogController {

    @Autowired
    private EsService esService;

    @PostMapping(value = "/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void collectLog(@RequestBody Log log) {
        log.setAcceptTime(LocalDateTime.now());
        esService.insertLog(log);
    }
}

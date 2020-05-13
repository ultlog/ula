package com.ultlog.ula.controller;

import com.ultlog.common.model.Log;
import com.ultlog.common.model.Page;
import com.ultlog.common.model.Query;
import com.ultlog.common.model.Result;
import com.ultlog.ula.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-02
 **/
@RestController()
@RequestMapping("/api/v1")
public class LogController {

    @Autowired
    private EsService esService;

    @PostMapping(value = "/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void collectLog(@RequestBody Log log) {
        log.setAcceptTime(System.currentTimeMillis());
        esService.insertLog(log);
    }


    @GetMapping(value = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Page<Log>> getLog(Query query) {
        // check param all legal
        if (Objects.isNull(query.getSize())) {
            query.setSize(20);
        }
        if (Objects.isNull(query.getOffset())) {
            query.setOffset(0);
        }
        return new Result<>(HttpStatus.OK.value(),null,esService.getLog(query));

    }
}

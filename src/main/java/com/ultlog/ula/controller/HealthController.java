package com.ultlog.ula.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-20
 **/
@RestController()
@RequestMapping("/api/v1")
public class HealthController {

    @PostMapping(value = "/health", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void collectLog(HttpServletRequest httpServletRequest) {
        // todo add health message
    }

}

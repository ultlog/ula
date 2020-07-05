package com.ultlog.ula.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-06-28
 **/
@RequestMapping("/api/v1/project/")
@RestController
public class ProjectController {


    @RequestMapping("/")
    public List<String> getProjectNameList(String project){

        return null;
    }

    @RequestMapping("/module")
    public List<String> getModuleNameList(String project,String module){

        return null;
    }

    @RequestMapping("/uuid")
    public List<String> getUuidNameList(String project,String module,String uuid){

        return null;
    }

}

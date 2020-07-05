package com.ultlog.ula.controller;

import com.ultlog.ula.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-06-28
 **/
@RequestMapping("/api/v1/project")
@RestController
public class ProjectController {

    @Autowired
    private EsService esService;


    @RequestMapping("")
    public List<String> getProjectNameList(String project){

        return esService.getProjectNameList(project);
    }

    @RequestMapping("/module")
    public List<String> getModuleNameList(String project,String module){

        return esService.getModuleNameList(project,module);
    }

    @RequestMapping("/uuid")
    public List<String> getUuidNameList(String project,String module,String uuid){

        return esService.getUuidNameList(project,module,uuid);
    }

}

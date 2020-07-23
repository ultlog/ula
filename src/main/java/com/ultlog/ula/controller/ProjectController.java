package com.ultlog.ula.controller;

import com.ultlog.common.model.Result;
import com.ultlog.ula.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-06-28
 **/
@RequestMapping("/api/v1/")
@RestController
public class ProjectController {

    @Autowired
    private EsService esService;


    @RequestMapping("/project")
    public Result<List<String>> getProjectNameList(@RequestParam(value = "project",required = false,defaultValue = "") String project){

        return new Result<>(HttpStatus.OK.value(),null,esService.getProjectNameList(project));
    }

    @GetMapping("/module")
    public Result<List<String>> getModuleNameList(@RequestParam(value = "project") String project,
                                          @RequestParam(value = "module",required = false,defaultValue = "")String module){

        return new Result<>(HttpStatus.OK.value(),null,esService.getModuleNameList(project, module));
    }

    @RequestMapping("/uuid")
    public Result<List<String>> getUuidNameList(@RequestParam(value = "project") String project,
                                        @RequestParam(value = "module") String module,
                                        @RequestParam(value = "uuid",required = false,defaultValue = "") String uuid){

        return new Result<>(HttpStatus.OK.value(),null,esService.getUuidNameList(project,module,uuid));
    }

}

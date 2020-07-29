package com.ultlog.ula.service;

import com.ultlog.common.model.Log;
import com.ultlog.common.model.Page;
import com.ultlog.common.model.Query;

import java.util.List;

/**
 * @program: ula
 * @link: github.com/ultlog/collector
 * @author: will
 * @create: 2020-05-02
 **/
public interface EsService {

    void insertLog(Log log);

    void insertSystem();

    Page<Log> getLog(Query query);

    void insertProject(String project);

    void insertModule(String project,String module);

    void insertUuid(String project,String module,String uuid);

    List<String> getProjectNameList(String project);

    List<String> getModuleNameList(String project,String module);

    List<String> getUuidNameList(String project,String module,String uuid);
}

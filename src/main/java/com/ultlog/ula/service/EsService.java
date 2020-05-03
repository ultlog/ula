package com.ultlog.ula.service;

import com.ultlog.ula.model.Log;
import com.ultlog.ula.model.Page;
import com.ultlog.ula.model.Query;

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
}

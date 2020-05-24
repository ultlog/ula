package com.ultlog.ula.cache;

import com.ultlog.common.model.HealthInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-24
 **/
public interface HealthCache {

    ConcurrentHashMap<HealthInfo,Long> HEALTH_INFO_LONG_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();


}

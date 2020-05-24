package com.ultlog.ula.controller;

import com.ultlog.common.model.HealthInfo;
import com.ultlog.common.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.ultlog.ula.cache.HealthCache.HEALTH_INFO_LONG_CONCURRENT_HASH_MAP;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-20
 **/
@RestController()
@RequestMapping("/api/v1/health")
public class HealthController {


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void collectLog(@RequestBody HealthInfo healthInformation) {

        HEALTH_INFO_LONG_CONCURRENT_HASH_MAP.put(healthInformation, System.currentTimeMillis());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<HealthInfo>> getHealthMessage() {
        final List<HealthInfo> collect = HEALTH_INFO_LONG_CONCURRENT_HASH_MAP.entrySet().stream().map(healthInfoLongEntry -> {
            final HealthInfo key = healthInfoLongEntry.getKey();
            key.setRefreshTime(healthInfoLongEntry.getValue());
            return key;
        }).sorted((o1, o2) -> (int) (o1.getRefreshTime() - o2.getRefreshTime())).collect(Collectors.toList());
        return new Result<>(HttpStatus.OK.value(), null, collect);
    }
}

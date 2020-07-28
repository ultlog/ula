package com.ultlog.ula.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultlog.common.model.Log;
import com.ultlog.common.model.Page;
import com.ultlog.common.model.Query;
import com.ultlog.ula.service.EsService;
import com.ultlog.ula.util.ObjectUtil;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.ultlog.common.constant.ESConstant.*;

/**
 * @program: ula
 * @link: github.com/ultlog/collector
 * @author: will
 * @create: 2020-05-02
 **/
@Service
public class EsServiceImpl implements EsService {

    @Autowired
    private RestHighLevelClient client;

    ObjectMapper objectMapper = new ObjectMapper();

    public static final Logger LOGGER = LoggerFactory.getLogger(EsServiceImpl.class);

    @Override
    public void insertLog(Log log) {

        ObjectMapper objectMapper = new ObjectMapper();


        final String s;
        try {
            s = objectMapper.writeValueAsString(log);
            IndexRequest request = new IndexRequest(LOG_INDEX_NAME);
            request.source(s, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void insertSystem() {

    }

    @Override
    public Page<Log> getLog(Query query) {

        final String project = query.getProject();
        final String module = query.getModule();
        final String message = query.getMessage();
        final String uuid = query.getUuid();
        final String level = query.getLevel();
        final String stack = query.getStack();
        final Long gt = query.getGt();
        final Long lt = query.getLt();
        final int offset = query.getOffset();
        final int size = query.getSize();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        if (ObjectUtil.allObjectNullOrEmpty(project, uuid, level, module, message, stack) && Objects.nonNull(gt) && Objects.nonNull(lt)) {
            final RangeQueryBuilder createTime = new RangeQueryBuilder(FIELD_CREATE_TIME);
            createTime.lt(lt);
            createTime.gt(gt);
            sourceBuilder.query(createTime);
        } else if(ObjectUtil.anyObjectNullOrEmpty(project, uuid, level, module, message, stack)) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            if (!StringUtils.isEmpty(project)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_PROJECT, project).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }
            if (!StringUtils.isEmpty(module)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_MODULE, module).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }

            if (!StringUtils.isEmpty(message)) {
                final MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(FIELD_MESSAGE, message).operator(Operator.AND);
                boolQueryBuilder.must(matchQueryBuilder);
            }

            if (!StringUtils.isEmpty(uuid)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_UUID, uuid).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }

            if (!StringUtils.isEmpty(level)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_LEVEL, level).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }
            if (!StringUtils.isEmpty(stack)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_STACK, stack).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }
            if (Objects.nonNull(gt) && Objects.nonNull(lt)) {
                final RangeQueryBuilder createTime = new RangeQueryBuilder(FIELD_CREATE_TIME);
                createTime.lt(lt);
                createTime.gt(gt);
                boolQueryBuilder.must(createTime);
            }
            sourceBuilder.query(boolQueryBuilder);
        }


        // count request not allow sort
        int count;
        CountRequest countRequest = new CountRequest();
        countRequest.source(sourceBuilder);
        try {
            final CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            count = Long.valueOf(countResponse.getCount()).intValue();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

        sourceBuilder.from(offset);
        sourceBuilder.size(size);

        // sort
        sourceBuilder.sort(new FieldSortBuilder(FIELD_CREATE_TIME).order(SortOrder.DESC));
        SearchRequest searchRequest = new SearchRequest(LOG_INDEX_NAME);
        searchRequest.source(sourceBuilder);
        final SearchResponse search;
        List<Log> logs = new LinkedList<>();
        try {
            search = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits hits = search.getHits();
            for (SearchHit hit : hits.getHits()) {

                final String sourceAsString = hit.getSourceAsString();
                final Log log = objectMapper.readValue(sourceAsString, Log.class);
                logs.add(log);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

        return new Page<>(count, size, offset, logs);
    }

    @Override
    public void insertProject(String project) {
        IndexRequest request = new IndexRequest(PROJECT_INDEX_NAME);
        Map<String, String> projectMap = new HashMap<>();
        projectMap.put(FIELD_PROJECT, project);
        request.id(DigestUtils.md5DigestAsHex(project.getBytes()));
        request.source(projectMap);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void insertModule(String project, String module) {
        IndexRequest request = new IndexRequest(MODULE_INDEX_NAME);
        Map<String, String> projectMap = new HashMap<>();
        projectMap.put(FIELD_PROJECT, project);
        projectMap.put(FIELD_MODULE, module);
        request.id(DigestUtils.md5DigestAsHex((project + module).getBytes()));
        request.source(projectMap);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void insertUuid(String project, String module, String uuid) {

        insertModule(project, module);
        insertProject(project);

        IndexRequest request = new IndexRequest(UUID_INDEX_NAME);
        Map<String, String> projectMap = new HashMap<>();
        projectMap.put(FIELD_PROJECT, project);
        projectMap.put(FIELD_MODULE, module);
        projectMap.put(FIELD_UUID, uuid);
        request.id(DigestUtils.md5DigestAsHex((project + module + uuid).getBytes()));
        request.source(projectMap);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public List<String> getProjectNameList(String project) {
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(FIELD_PROJECT, "*" + project + "*");
        SearchRequest searchRequest = new SearchRequest(PROJECT_INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(wildcardQueryBuilder);
        searchRequest.source(sourceBuilder);
        final SearchResponse search;
        List<String> projectList = new LinkedList<>();

        try {
            search = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits hits = search.getHits();
            for (SearchHit hit : hits.getHits()) {

                final String sourceAsString = hit.getSourceAsString();
                final Map<String, String> map = objectMapper.readValue(sourceAsString, Map.class);
                projectList.add(map.get(FIELD_PROJECT));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return projectList;
    }

    @Override
    public List<String> getModuleNameList(String project, String module) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(FIELD_MODULE, "*" + module + "*");
        SearchRequest searchRequest = new SearchRequest(MODULE_INDEX_NAME);

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_PROJECT, project).operator(Operator.AND);
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(wildcardQueryBuilder);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        final SearchResponse search;
        List<String> moduleList = new LinkedList<>();

        try {
            search = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits hits = search.getHits();
            for (SearchHit hit : hits.getHits()) {

                final String sourceAsString = hit.getSourceAsString();
                final Map<String, String> map = objectMapper.readValue(sourceAsString, Map.class);
                moduleList.add(map.get(FIELD_MODULE));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return moduleList;
    }

    @Override
    public List<String> getUuidNameList(String project, String module, String uuid) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(FIELD_UUID, "*" + uuid + "*");
        SearchRequest searchRequest = new SearchRequest(UUID_INDEX_NAME);

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_PROJECT, project).operator(Operator.AND);
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);

        MatchQueryBuilder matchQueryBuilder2 = new MatchQueryBuilder(FIELD_MODULE, module).operator(Operator.AND);
        matchQueryBuilder2.fuzziness(Fuzziness.AUTO);

        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(wildcardQueryBuilder);
        boolQueryBuilder.must(matchQueryBuilder2);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        final SearchResponse search;
        List<String> projectList = new LinkedList<>();

        try {
            search = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHits hits = search.getHits();
            for (SearchHit hit : hits.getHits()) {

                final String sourceAsString = hit.getSourceAsString();
                final Map<String, String> map = objectMapper.readValue(sourceAsString, Map.class);
                projectList.add(map.get(FIELD_UUID));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return projectList;
    }
}

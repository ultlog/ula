package com.ultlog.ula.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultlog.common.constant.ESConstant;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
            IndexRequest request = new IndexRequest(ESConstant.INDEX_NAME);
            request.source(s, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
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

        // default select 0 ~ now
        if (ObjectUtil.AllObjectNull(project, module, uuid, level, message, stack, gt, lt)) {

            final RangeQueryBuilder createTime = new RangeQueryBuilder(FIELD_CREATE_TIME);
            createTime.gt(0);
            createTime.lt(System.currentTimeMillis());
            sourceBuilder.query(createTime);

        } else if (ObjectUtil.AllObjectNull(project, uuid, level, module, message, stack) && Objects.nonNull(gt) && Objects.nonNull(lt)) {
            final RangeQueryBuilder createTime = new RangeQueryBuilder(FIELD_CREATE_TIME);
            createTime.lt(lt);
            createTime.gt(gt);
            sourceBuilder.query(createTime);
        } else {
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
            if (lt != null && gt != null) {
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
            LOGGER.error(e.getMessage(),e);
            return null;
        }

        sourceBuilder.from(offset);
        sourceBuilder.size(size);

        // sort
        sourceBuilder.sort(new FieldSortBuilder(FIELD_CREATE_TIME).order(SortOrder.DESC));
        SearchRequest searchRequest = new SearchRequest();
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
            LOGGER.error(e.getMessage(),e);
            return null;
        }

        return new Page<>(count, size, offset, logs);
    }
}

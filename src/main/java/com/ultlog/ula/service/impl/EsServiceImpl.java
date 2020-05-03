package com.ultlog.ula.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultlog.ula.model.Log;
import com.ultlog.ula.model.Page;
import com.ultlog.ula.model.Query;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    private final String indexName = "ult_index";
    final String createTimeFieldName = "createTime";


    @Override
    public void insertLog(Log log) {

        ObjectMapper objectMapper = new ObjectMapper();


        final String s;
        try {
            s = objectMapper.writeValueAsString(log);
            IndexRequest request = new IndexRequest(indexName);
            //request.id(String.valueOf(log.hashCode()));
            request.source(s, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // todo
            e.printStackTrace();
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
        final Long gt = query.getGt();
        final Long lt = query.getLt();
        final int offset = query.getOffset();
        final int size = query.getSize();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // default select 0 ~ now
        if (ObjectUtil.AllObjectNull(project, module, message, gt, lt)) {

            final RangeQueryBuilder createTime = new RangeQueryBuilder(createTimeFieldName);
            createTime.gt(0);
            createTime.lt(System.currentTimeMillis());
            sourceBuilder.query(createTime);

        } else if (ObjectUtil.AllObjectNull(project, module, message) && Objects.nonNull(gt) && Objects.nonNull(lt)) {
            final RangeQueryBuilder createTime = new RangeQueryBuilder(createTimeFieldName);
            createTime.lt(lt);
            createTime.gt(gt);
            sourceBuilder.query(createTime);
        } else {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            if (!StringUtils.isEmpty(project)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("project", project).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }
            if (!StringUtils.isEmpty(module)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("module", module).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
            }

            if (!StringUtils.isEmpty(message)) {
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("message", message).operator(Operator.AND);
                matchQueryBuilder.fuzziness(Fuzziness.AUTO);
                boolQueryBuilder.must(matchQueryBuilder);
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
            // todo handle exception
            e.printStackTrace();
            return null;
        }

        sourceBuilder.from(offset);
        sourceBuilder.size(size);

        // sort
        sourceBuilder.sort(new FieldSortBuilder("createTime").order(SortOrder.DESC));
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
            // todo handle exception
            e.printStackTrace();
            return null;
        }

        return new Page<>(count, size, offset, logs);
    }
}

package com.ultlog.ula.model;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-03
 **/
public class Query {

    private String project;

    private String module;

    private String message;

    private Long gt;

    private Long lt;

    private Integer size;

    private Integer offset;

    public String getProject() {
        return project;
    }

    public Query setProject(String project) {
        this.project = project;
        return this;
    }

    public String getModule() {
        return module;
    }

    public Query setModule(String module) {
        this.module = module;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Query setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getGt() {
        return gt;
    }

    public Query setGt(Long gt) {
        this.gt = gt;
        return this;
    }

    public Long getLt() {
        return lt;
    }

    public Query setLt(Long lt) {
        this.lt = lt;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public Query setSize(Integer size) {
        this.size = size;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public Query setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }
}
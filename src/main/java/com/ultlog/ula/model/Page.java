package com.ultlog.ula.model;

import java.util.List;

/**
 * @program: ula
 * @link: github.com/ultlog/ula
 * @author: will
 * @create: 2020-05-03
 **/
public class Page<T> {

    private int count;

    private int size;

    private int offset;

    private List<T> data;

    public Page(int count, int size, int offset, List<T> data) {
        this.count = count;
        this.size = size;
        this.offset = offset;
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public Page<T> setCount(int count) {
        this.count = count;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Page<T> setSize(int size) {
        this.size = size;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public Page<T> setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public Page<T> setData(List<T> data) {
        this.data = data;
        return this;
    }
}

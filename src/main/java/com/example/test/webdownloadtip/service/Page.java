package com.example.test.webdownloadtip.service;

import lombok.Data;

import java.util.List;

// 分页对象
@Data
public class Page<T> {
    /** 分页参数，第几页 */
    protected int pageNo;

    /** 分页参数，当前页数量 */
    protected int pageSize;

    /** 数据列表 */
    protected List<T> list;

    /** 数据总量 */
    protected long count;

    public Page(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}

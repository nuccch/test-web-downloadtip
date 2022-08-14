package com.example.test.webdownloadtip.service;

import java.util.List;

// 数据分页查询接口
public interface PageQuerier<T> {
    /**
     * 查询指定页数据，同时会查询总数
     *
     * @param page
     * @param entity
     * @return
     */
    Page<T> findPage(Page<T> page, DataEntity entity);
    /**
     * 查询指定页数据，只查询数据，不查询总数
     *
     * @param page
     * @param entity
     * @return
     */
    List<T> findList(Page<T> page, DataEntity entity);
}

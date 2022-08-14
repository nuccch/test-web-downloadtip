package com.example.test.webdownloadtip.service;

import com.example.test.webdownloadtip.model.SubjectExport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 分页数据查询服务
@Service
public class SubjectPageQuerier implements PageQuerier<SubjectExport> {
    // 数据总量
    private int count = 2050000;
    @Override
    public Page<SubjectExport> findPage(Page<SubjectExport> page, DataEntity entity) {
        Page<SubjectExport> p = new Page<>(page.getPageNo(), page.getPageSize());
        p.setList(getPageList(page));
        p.setCount(getCount());
        return p;
    }

    @Override
    public List<SubjectExport> findList(Page<SubjectExport> page, DataEntity entity) {
        return getPageList(page);
    }

    // 模拟查询分页数据
    private List<SubjectExport> getPageList(Page<SubjectExport> page) {
        int pageSize = page.getPageSize();
        if (page.getPageNo()*pageSize >= getCount()) {
            // 模拟无法查询到数据的情况
            return new ArrayList<>();
        }
        List<SubjectExport> pageList = new ArrayList<>(pageSize);
        for (int i = 0; i < pageSize; i++) {
            pageList.add(SubjectExport.builder()
                    .name("name" + i)
                    .age(new Random().nextInt(100))
                    .addr("addr" + i)
                    .number(i)
                    .build());
        }
        return pageList;
    }

    // 模拟查询数据总量
    private long getCount() {
        return this.count;
    }

}

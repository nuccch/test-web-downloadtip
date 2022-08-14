package com.example.test.webdownloadtip.model;
import com.alibaba.excel.annotation.ExcelProperty;
import com.example.test.webdownloadtip.service.DataEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectExport extends DataEntity {
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("年龄")
    private int age;
    @ExcelProperty("住址")
    private String addr;
    @ExcelProperty("身份ID")
    private int number;
}

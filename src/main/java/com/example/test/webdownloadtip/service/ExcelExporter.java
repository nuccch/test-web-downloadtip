package com.example.test.webdownloadtip.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

// 分页导出Excel
public class ExcelExporter<T> {
    /** 实现分页查询 */
    private PageQuerier<T> pageQuerier;

    /**
     * @param pageQuerier 分页查询服务
     */
    public ExcelExporter(PageQuerier<T> pageQuerier) {
        this.pageQuerier = pageQuerier;
    }
    /**
     * 分页查询数据并写入到下载输出流
     *
     * @param response
     * @param fileName 文件名，如：测试.xlsx
     * @param header  表头映射类
     * @param entity 数据实体对象
     * @throws IOException
     */
    public void write(HttpServletResponse response, String fileName, Class header, DataEntity entity) throws IOException {
        if (header == null) {
            header = entity.getClass();
        }
        // 内容样式策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 垂直居中,水平居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        HorizontalCellStyleStrategy contentStyleStrategy = new HorizontalCellStyleStrategy();
        contentStyleStrategy.setContentWriteCellStyleList(Arrays.asList(new WriteCellStyle[]{contentWriteCellStyle}));
        // 一定要对文件名做URL编码，否则会出现中文乱码
        String file = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + file);
        response.addHeader("filename", file);
        response.setContentType("application/octet-stream; charset=UTF-8");
        ExcelWriter writer = new ExcelWriterBuilder()
                .autoCloseStream(true)
                .automaticMergeHead(false)
                .excelType(ExcelTypeEnum.XLSX)
                .file(response.getOutputStream())
                .head(header)
                .registerWriteHandler(contentStyleStrategy)
                .build();
        // xlsx文件单个工作簿上限是1048575行,这里如果超过104W需要分Sheet
        long sheetMax = 1048575;
        // 每一个工作簿可写入的数据量，需要把表头所占的行算上
        long sheetTotal = 1;
        // 工作簿编号
        int sheetNum = 1;
        // 分页数
        int pageNo = 1;
        // 每页查询数量
        int pageSize = 50000;
        WriteSheet writeSheet = EasyExcel.writerSheet("sheet"+sheetNum).head(header).build();
        // 先查询第一页，得到总数
        Page<T> page = this.pageQuerier.findPage(new Page(pageNo, pageSize), entity);
        writer.write(page.getList(), writeSheet);
        if (page.getCount() <= 0 || page.getCount() <= pageSize) {
            // 没有数据或只有一页数据
            writer.finish();
            return;
        }
        sheetTotal += page.getList().size();
        long total = page.getCount();
        long fetch = page.getList().size();
        for (;;) {
            // 只查询分页数据，不查询分页总数
            pageNo++;
            List<T> list = this.pageQuerier.findList(new Page(pageNo, pageSize), entity);
            fetch += list.size();
            sheetTotal += list.size();
            if (sheetTotal >= sheetMax) {
                sheetTotal = 1 + list.size();
                sheetNum++;
                writeSheet = EasyExcel.writerSheet("sheet"+sheetNum).head(header).build();
            }
            writer.write(list, writeSheet);
            if (fetch >= total) {
                writer.finish();
                break;
            }
        }
        writer.finish();
    }
}

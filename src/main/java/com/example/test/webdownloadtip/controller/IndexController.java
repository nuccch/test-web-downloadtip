package com.example.test.webdownloadtip.controller;

import com.alibaba.excel.EasyExcel;
import com.example.test.webdownloadtip.model.SubjectExport;
import com.example.test.webdownloadtip.service.ExcelExporter;
import com.example.test.webdownloadtip.service.SubjectPageQuerier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class IndexController {
    @Autowired
    SubjectPageQuerier subjectPageQuerier;

    int total = 1000;
    List<SubjectExport> dataList = new ArrayList<>(total);
    {
        for (int i = 0; i < total; i++) {
            SubjectExport subject = SubjectExport.builder()
                    .name("subject"+i)
                    .age(new Random().nextInt(100))
                    .addr("addr"+i)
                    .number(i)
                    .build();
            dataList.add(subject);
        }
    }

    // 展示页面
    @RequestMapping("/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("page", pageData());
        return "index";
    }

    // 以流的方式下载文件
    @RequestMapping("/download")
    public String download(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("name") String name,
                           @RequestParam("age") int age) throws IOException {
        System.out.println(String.format("name:%s, age:%s", name, age));
        String fileName = URLEncoder.encode(String.format("%s_%s.xlsx", "导出数据", System.currentTimeMillis()), "UTF-8");
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("filename", fileName);
        response.setContentType("application/octet-stream; charset=UTF-8");
        try {
            // 模拟查询数据耗时
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EasyExcel.write(response.getOutputStream(), SubjectExport.class).sheet("sheet").doWrite(dataList);
        return null;
    }

    // 以分页查询数据库方式导出百万级数据
    @RequestMapping("/downloadPage")
    public String downloadByPage(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("name") String name,
                                 @RequestParam("age") int age) throws IOException {
        // 使用
        SubjectExport param = SubjectExport.builder().name(name).age(age).build();
        String fileName = String.format("%s%s.xlsx", "数据导出", System.currentTimeMillis());
        new ExcelExporter<SubjectExport>(this.subjectPageQuerier).write(response, fileName, SubjectExport.class, param);
        return null;
    }

    private List<SubjectExport> pageData() {
        return dataList.subList(0, 30);
    }
}

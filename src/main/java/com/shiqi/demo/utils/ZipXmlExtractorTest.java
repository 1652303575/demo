package com.shiqi.demo.utils;

import java.util.List;
import java.util.Map;

public class ZipXmlExtractorTest {
    public static void main(String[] args) {
        String zipPath = "src/main/resources/workspace/datalake/selfAnalysis/ziyan/DNN/PM_20250430110000-20250430110500_UPF_17.57.1.23.zip";
        String measInfoId = "NE"; // 可根据需要修改
        try {
            List<Map<String, String>> result = ZipXmlExtractor.extractMeasInfoById(zipPath, measInfoId);
            System.out.println("解析结果：");
            for (Map<String, String> map : result) {
                System.out.println(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
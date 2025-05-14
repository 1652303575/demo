package com.shiqi.demo.utils;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipXmlExtractor {
    /**
     * 解压zip文件，解析xml，提取measInfoId="NE"的数据，组装成List<Map<measType, measValue>>
     * @param zipFilePath zip文件路径
     * @return List<Map<measType, measValue>>
     * @throws Exception 解析或IO异常
     */
    public static List<Map<String, String>> extractMeasInfoNE(String zipFilePath) throws Exception {
        // 1. 解压zip，找到xml文件
        String xmlFileName = null;
        Path tempDir = Files.createTempDirectory("xml_unzip_");
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".xml")) {
                    xmlFileName = entry.getName();
                    File outFile = tempDir.resolve(xmlFileName).toFile();
                    outFile.getParentFile().mkdirs();
                    try (InputStream is = zipFile.getInputStream(entry);
                         OutputStream os = new FileOutputStream(outFile)) {
                        is.transferTo(os);
                    }
                    break;
                }
            }
        }
        if (xmlFileName == null) throw new FileNotFoundException("zip包中未找到xml文件");
        File xmlFile = tempDir.resolve(xmlFileName).toFile();

        // 2. 解析xml，提取measInfoId="NE"的数据
        List<Map<String, String>> result = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList measInfoList = doc.getElementsByTagName("measInfo");
        for (int i = 0; i < measInfoList.getLength(); i++) {
            Element measInfo = (Element) measInfoList.item(i);
            if ("NE".equals(measInfo.getAttribute("measInfoId"))) {
                // 获取measTypes
                NodeList measTypesList = measInfo.getElementsByTagName("measTypes");
                if (measTypesList.getLength() == 0) continue;
                String[] measTypes = measTypesList.item(0).getTextContent().trim().split("\\s+");
                // 获取measValue
                NodeList measValueList = measInfo.getElementsByTagName("measValue");
                for (int j = 0; j < measValueList.getLength(); j++) {
                    Element measValueElem = (Element) measValueList.item(j);
                    String[] values = measValueElem.getTextContent().trim().split("\\s+");
                    Map<String, String> map = new LinkedHashMap<>();
                    for (int k = 0; k < measTypes.length && k < values.length; k++) {
                        map.put(measTypes[k], values[k]);
                    }
                    result.add(map);
                }
            }
        }
        // 清理临时文件
        deleteDir(tempDir.toFile());
        return result;
    }

    /**
     * 解压zip文件，解析xml，提取指定measInfoId的数据，组装成List<Map<measTypeId, measValue>>
     * @param zipFilePath zip文件路径
     * @param measInfoId  需要提取的measInfoId
     * @return List<Map<measTypeId, measValue>>
     * @throws Exception 解析或IO异常
     */
    public static List<Map<String, String>> extractMeasInfoById(String zipFilePath, String measInfoId) throws Exception {
        // 1. 解压zip，找到xml文件
        String xmlFileName = null;
        Path tempDir = Files.createTempDirectory("xml_unzip_");
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".xml")) {
                    xmlFileName = entry.getName();
                    File outFile = tempDir.resolve(xmlFileName).toFile();
                    outFile.getParentFile().mkdirs();
                    try (InputStream is = zipFile.getInputStream(entry);
                         OutputStream os = new FileOutputStream(outFile)) {
                        is.transferTo(os);
                    }
                    break;
                }
            }
        }
        if (xmlFileName == null) throw new FileNotFoundException("zip包中未找到xml文件");
        File xmlFile = tempDir.resolve(xmlFileName).toFile();

        // 2. 解析xml，提取指定measInfoId的数据
        List<Map<String, String>> result = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList measInfoList = doc.getElementsByTagName("measInfo");
        for (int i = 0; i < measInfoList.getLength(); i++) {
            Element measInfo = (Element) measInfoList.item(i);
            if (measInfoId.equals(measInfo.getAttribute("measInfoId"))) {
                // 获取measTypesUnique（ID数组）
                NodeList measTypesUniqueList = measInfo.getElementsByTagName("measTypesUnique");
                if (measTypesUniqueList.getLength() == 0) continue;
                String[] ids = measTypesUniqueList.item(0).getTextContent().trim().split("\\s+");
                // 获取measValue
                NodeList measValueList = measInfo.getElementsByTagName("measValue");
                for (int j = 0; j < measValueList.getLength(); j++) {
                    Element measValueElem = (Element) measValueList.item(j);
                    NodeList measResultsList = measValueElem.getElementsByTagName("measResults");
                    if (measResultsList.getLength() == 0) continue;
                    String[] values = measResultsList.item(0).getTextContent().trim().split("\\s+");
                    Map<String, String> map = new LinkedHashMap<>();
                    for (int k = 0; k < ids.length && k < values.length; k++) {
                        map.put(ids[k], values[k]); // key为ID
                    }
                    result.add(map);
                }
            }
        }
        // 清理临时文件
        deleteDir(tempDir.toFile());
        return result;
    }

    // 递归删除临时目录
    private static void deleteDir(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteDir(child);
            }
        }
        file.delete();
    }
} 
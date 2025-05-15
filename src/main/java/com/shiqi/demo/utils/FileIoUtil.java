package com.shiqi.demo.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FileIoUtil {
    /**
     * 用于读取和写入 二进制文件（如图片、视频等）
     */
    public static void copyBinaryFile(String srcFilePath, String destFilePath) throws IOException {
        try (
            FileInputStream fis = new FileInputStream(srcFilePath);
            FileOutputStream fos = new FileOutputStream(destFilePath);
        ) {
            fis.transferTo(fos);
            // byte[] buffer = new byte[1024];
            // int len;
            // while ((len = fis.read(buffer)) != -1) {
            //     fos.write(buffer, 0, len);
            // }
            log.info("文件复制完成！");
        }catch (IOException e) {
            e.printStackTrace();
        }
    } 
    /**
     * 读取和写入 文本文件
     */
    public static void copyTextFile(String srcFilePath, String destFilePath) throws IOException {
        try (
            FileReader reader = new FileReader(srcFilePath);
            FileWriter writer = new FileWriter(destFilePath);
        ) {
            reader.transferTo(writer);
            reader.transferTo(writer);
            // char[] buffer = new char[1024];
            // int len;
            // while ((len = reader.read(buffer)) != -1) {
            //     writer.write(buffer, 0, len);
            // }
            log.info("文本复制完成！");
            System.out.println("文本复制完成！");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 更高效的字符流，适合逐行读取文本。
     */
    public static void copyTextFileByLine(String srcFilePath, String destFilePath) throws IOException {
        try (
            BufferedReader br = new BufferedReader(new FileReader(srcFilePath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(destFilePath));
        ) {
            br.transferTo(bw);
            // String line;
            // while ((line = br.readLine()) != null) {
            //     bw.write(line);
            //     bw.newLine(); // 写入换行符
            // }
            log.info("逐行写入完成！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

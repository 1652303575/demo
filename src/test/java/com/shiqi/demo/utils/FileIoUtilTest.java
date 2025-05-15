package com.shiqi.demo.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileIoUtilTest {

    @TempDir
    Path tempDir;
    
    private File sourceFile;
    private File destFile;
    private String testContent;

    @BeforeEach
    void setUp() throws IOException {
        // 创建测试文件
        sourceFile = tempDir.resolve("source.txt").toFile();
        destFile = tempDir.resolve("dest.txt").toFile();
        testContent = "测试内容\n第二行\n第三行";
        Files.writeString(sourceFile.toPath(), testContent);
    }

    @AfterEach
    void tearDown() {
        // 清理测试文件
        sourceFile.delete();
        destFile.delete();
    }

    @Test
    void testCopyBinaryFile() throws IOException {
        // 准备二进制测试文件
        byte[] binaryData = new byte[]{1, 2, 3, 4, 5};
        Files.write(sourceFile.toPath(), binaryData);

        // 执行复制
        FileIoUtil.copyBinaryFile(sourceFile.getPath(), destFile.getPath());

        // 验证
        assertTrue(destFile.exists());
        byte[] copiedData = Files.readAllBytes(destFile.toPath());
        assertArrayEquals(binaryData, copiedData);
    }

    @Test
    void testCopyTextFile() throws IOException {
        // 执行复制
        FileIoUtil.copyTextFile(sourceFile.getPath(), destFile.getPath());

        // 验证
        assertTrue(destFile.exists());
        String copiedContent = Files.readString(destFile.toPath());
        assertEquals(testContent, copiedContent);
    }

    @Test
    void testCopyTextFileByLine() throws IOException {
        // 执行复制
        FileIoUtil.copyTextFileByLine(sourceFile.getPath(), destFile.getPath());

        // 验证
        assertTrue(destFile.exists());
        String copiedContent = Files.readString(destFile.toPath());
        assertEquals(testContent, copiedContent);
    }

    @Test
    void testCopyNonExistentFile() {
        // 测试复制不存在的文件
        assertThrows(IOException.class, () -> 
            FileIoUtil.copyTextFile("non_existent.txt", destFile.getPath())
        );
    }

    @Test
    void testCopyToNonWritableDirectory() {
        // 测试复制到不可写目录
        File nonWritableDir = new File("/root/non_writable");
        if (!nonWritableDir.exists()) {
            assertThrows(IOException.class, () ->
                FileIoUtil.copyTextFile(sourceFile.getPath(), 
                    nonWritableDir.getPath() + "/test.txt")
            );
        }
    }

    @Test
    void testReadFileByLine() throws IOException {
        //输出当前工作目录
        System.out.println(System.getProperty("user.dir"));
        // 执行读取
        FileIoUtil.readFileByLine("workspace/txt/1.txt");
    }
} 
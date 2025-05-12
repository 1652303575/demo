package com.shiqi.demo.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.function.Predicate;
import java.time.ZoneId;

@Slf4j
public class SftpUtil {
    private String host;
    private int port;
    private String username;
    private String password;
    private ChannelSftp sftp;
    private Session session;

    public SftpUtil(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void connect() throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;
    }

    public void disconnect() {
        if (sftp != null && sftp.isConnected()) {
            sftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 下载指定远程目录下所有zip文件到本地目录
     */
    public void downloadAllZipFiles(String remoteDir, String localDir) throws Exception {
        downloadAllZipFiles(remoteDir, localDir, null);
    }

    /**
     * 下载指定远程目录下所有zip文件到本地目录，支持文件名过滤
     */
    public void downloadAllZipFiles(String remoteDir, String localDir, Predicate<String> fileFilter) throws Exception {
        Vector<ChannelSftp.LsEntry> files = sftp.ls(remoteDir);
        for (ChannelSftp.LsEntry entry : files) {
            String filename = entry.getFilename();
            if (filename.endsWith(".zip") && (fileFilter == null || fileFilter.test(filename))) {
                String remoteFile = remoteDir + "/" + filename;
                String localFile = localDir + "/" + filename;
                try (OutputStream os = new FileOutputStream(localFile)) {
                    sftp.get(remoteFile, os);
                    log.info("下载完成: {} -> {}", remoteFile, localFile);
                }
            }
        }
    }

    /**
     * 递归下载远程目录下所有zip文件到本地对应目录
     */
    public void downloadAllZipFilesRecursively(String remoteDir, String localDir) throws Exception {
        downloadAllZipFilesRecursively(remoteDir, localDir, null);
    }

    /**
     * 递归下载远程目录下所有zip文件到本地对应目录，支持文件名过滤
     */
    public void downloadAllZipFilesRecursively(String remoteDir, String localDir, Predicate<String> fileFilter) throws Exception {
        java.io.File localDirFile = new java.io.File(localDir);
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }
        Vector<ChannelSftp.LsEntry> files = sftp.ls(remoteDir);
        for (ChannelSftp.LsEntry entry : files) {
            String filename = entry.getFilename();
            if (".".equals(filename) || "..".equals(filename)) continue;
            String remotePath = remoteDir + "/" + filename;
            if (entry.getAttrs().isDir()) {
                // 递归子目录
                downloadAllZipFilesRecursively(remotePath, localDir + "/" + filename, fileFilter);
            } else if (filename.endsWith(".zip") && (fileFilter == null || fileFilter.test(filename))) {
                String localFile = localDir + "/" + filename;
                try (OutputStream os = new FileOutputStream(localFile)) {
                    sftp.get(remotePath, os);
                    log.info("下载完成: {} -> {}", remotePath, localFile);
                }
            }
        }
    }

    /**
     * 递归创建远程目录
     */
    public void sftpMkdirs(String remoteDir) throws Exception {
        String[] folders = remoteDir.split("/");
        String path = "";
        for (String folder : folders) {
            if (folder.isEmpty()) continue;
            path += "/" + folder;
            try {
                sftp.cd(path);
            } catch (Exception e) {
                sftp.mkdir(path);
            }
        }
    }

    /**
     * 上传本地文件到远程
     */
    public void uploadFile(String localFile, String remoteFile) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(localFile)) {
            sftp.put(fis, remoteFile);
            log.info("上传完成: {} -> {}", localFile, remoteFile);
        }
    }

    /**
     * 检查远程文件是否存在
     */
    public boolean exists(String remoteFile) {
        try {
            sftp.lstat(remoteFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 递归下载远程目录下所有zip文件到本地对应目录，按mtime时间窗口和文件名前缀过滤
     * @return 成功下载的文件数量
     */
    public int downloadAllZipFilesRecursivelyByMTime(String remoteDir, String localDir, long startEpoch, long endEpoch, java.util.function.Predicate<String> nameFilter) throws Exception {
        int count = 0;
        java.io.File localDirFile = new java.io.File(localDir);
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }
        Vector<ChannelSftp.LsEntry> files;
        try {
            files = sftp.ls(remoteDir);
        } catch (com.jcraft.jsch.SftpException e) {
            if (e.getMessage() != null && e.getMessage().contains("No such file")) {
                log.warn("远程目录不存在: {}，跳过下载", remoteDir);
                return 0;
            } else {
                throw e;
            }
        }
        for (ChannelSftp.LsEntry entry : files) {
            String filename = entry.getFilename();
            if (".".equals(filename) || "..".equals(filename)) continue;
            String remotePath = remoteDir + "/" + filename;
            if (entry.getAttrs().isDir()) {
                count += downloadAllZipFilesRecursivelyByMTime(remotePath, localDir + "/" + filename, startEpoch, endEpoch, nameFilter);
            } else if (filename.endsWith(".zip") && (nameFilter == null || nameFilter.test(filename))) {
                int mtime = entry.getAttrs().getMTime(); // 秒级时间戳
                long fileEpoch = mtime * 1000L;
                if (fileEpoch >= startEpoch && fileEpoch < endEpoch) {
                    String localFile = localDir + "/" + filename;
                    try (OutputStream os = new FileOutputStream(localFile)) {
                        sftp.get(remotePath, os);
                        log.info("下载完成: {} -> {}", remotePath, localFile);
                        count++;
                    }
                }
            }
        }
        return count;
    }
} 
/**
 * All rights Reserved, Designed By Administrator
 *
 * @Title: FtpSupport.java
 * @Package .common.ftp.support
 * @date: 2016年9月6日 下午3:12:09
 * @version V1.0
 */
package com.stars.ftp.support;

import com.wang.ftpsftp.ftp.FtpFileInfo;
import com.wang.ftpsftp.ftp.FtpService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: FtpSupport
 * @Description:FTP实现类
 * @author: Administrator
 * @date: 2016年9月6日 下午3:12:09
 */
public class FtpSupport implements FtpService {
    private static final Logger LOG = LoggerFactory.getLogger(FtpSupport.class);

    /**
     * 默认传输缓存大小
     */
    private static final int DEF_BUFFER_SIZE = 2 * 1024 * 1024;

    /**
     * 默认超时时间
     */
    private static final int DEF_DEFAULT_TIMEOUT = 30 * 1000;

    /**
     * 默认连接超时时间
     */
    private static final int DEF_CONNECT_TIMEOUT = 30 * 1000;

    /**
     * 默认传输超时时间
     */
    private static final int DEF_DATA_TIMEOUT = 30 * 1000;

    private FTPClient ftpClient;

    /**
     * 主机名或ip
     */
    private String hostname;

    /**
     * ftp端口
     */
    private int port;

    /**
     * ftp账号名
     */
    private String username;

    /**
     * ftp密码
     */
    private String password;

    /**
     * ftp用户家目录
     */
    private String homeDir;

    public FtpSupport(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(DEF_CONNECT_TIMEOUT);
        ftpClient.setDefaultTimeout(DEF_DEFAULT_TIMEOUT);
        ftpClient.setDataTimeout(DEF_DATA_TIMEOUT);
    }

    private FTPClient getClient() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDefaultPort(port);
        ftpClient.setControlEncoding("GBK");
        // ftpClient.configure(getClientConfig());
        return ftpClient;
    }

    private static FTPClientConfig getClientConfig() {
        String sysType;
        if (SystemUtils.IS_OS_LINUX) {
            sysType = FTPClientConfig.SYST_UNIX;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            sysType = FTPClientConfig.SYST_NT;
        } else {
            sysType = FTPClientConfig.SYST_UNIX;
        }
        FTPClientConfig config = new FTPClientConfig(sysType);
        config.setRecentDateFormatStr("yyyy-MM-dd HH:mm");
        return config;
    }

    /**
     * @Title: ftpLogin
     * @Description:
     * @return
     * @see .common.ftp.FtpService#ftpLogin()
     */
    @Override
    public boolean ftpLogin() {
        boolean isLogin = false;
        // this.ftpClient.setControlEncoding("GBK");
        try {
            if (this.port > 0) {
                this.ftpClient.connect(this.hostname, this.port);
            } else {
                this.ftpClient.connect(this.hostname);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                LOG.error("连接ftp[{}, {}]服务器失败，错误码：{}", hostname, port, reply);
                this.ftpClient.disconnect();
                return isLogin;
            }

            isLogin = this.ftpClient.login(this.username, this.password);
            if (!isLogin) {
                LOG.error("登录ftp[{}, {}]服务器失败！失败原因：{}", hostname, port, ftpClient.getReplyString());
                this.ftpClient.disconnect();
            } else {
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
                    this.ftpClient.setControlEncoding("UTF-8");
                }
                this.ftpClient.enterLocalPassiveMode();
                // 设置传输协议
                this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                this.ftpClient.setBufferSize(DEF_BUFFER_SIZE);
                this.homeDir = ftpClient.printWorkingDirectory();
                LOG.info("登录ftp[{}, {}]服务器成功！", hostname, port);
            }
        } catch (Exception e) {
            LOG.error(String.format("登录ftp[%s, %s]服务器异常：", hostname, port), e);
            isLogin = false;
        }
        return isLogin;
    }

    /**
     * 重新登录
     *
     * @return
     */
    public boolean reLogin() {
        this.ftpLogout();
        return this.ftpLogin();
    }

    /**
     * @Title: ftpLogout
     * @Description:
     * @return
     * @see .common.ftp.FtpService#ftpLogout()
     */
    @Override
    public void ftpLogout() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.logout();// 退出FTP服务器
            } catch (IOException e) {
                // ignore
            } finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public String getHomeDir() {
        return homeDir;
    }

    /**
     * @Title: uploadFile
     * @Description:上传文件
     * @param localPath 本地目录
     * @param remoteUpLoadPath 远程目录
     * @param redoCount
     * @return
     * @throws Exception
     */
    @Override
    public boolean uploadFile(String localPath, String remoteUpLoadPath, int redoCount) throws Exception {
        FileInputStream inStream = null;
        boolean success = false;
        File srcFile = new File(localPath);
        if (!srcFile.exists() || srcFile.isDirectory()) {
            LOG.error("文件[{}]不存在或是文件夹", srcFile.getPath());
            return success;
        }
        try {
            inStream = new FileInputStream(srcFile);
            success = this.ftpClient
                    .storeFile(new String(remoteUpLoadPath.getBytes(), FTPClient.DEFAULT_CONTROL_ENCODING), inStream);
        } catch (IOException ioe) {
            // 连接不稳定是抛出IOException，这时候重试一次，实在不稳定就抛出失败
            LOG.error("使用FTP上传失败:{}", ioe);
            if (redoCount > 0) {
                LOG.info("文件上传进入重试:{}", localPath);
                if (this.reLogin()) {
                    redoCount = redoCount - 1;
                    success = this.uploadFile(localPath, remoteUpLoadPath, redoCount);
                } else {
                    throw new Exception(String.format("文件[%s]上传异常后，登录失败", srcFile.getPath()), ioe);
                }
            }
        } catch (Exception e) {
            throw new Exception(String.format("文件[%s]上传异常", srcFile.getPath()), e);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
        if (success) {
            LOG.debug("文件[{}]上传成功", srcFile.getPath());
        } else {
            LOG.error("文件[{}]上传失败", srcFile.getPath());
        }
        return success;
    }

    /**
     *
     * @Title: cdRoot
     * @Description: 进入目录，如果失败就返回上一级创建目录
     * @param remoteUpLoadDir
     * @author ZJJ
     * @version 1.0
     * @throws IOException
     */
    private void cdRoot(String remoteUpLoadDir) throws IOException {
        try {
            boolean cdResult = this.cd(remoteUpLoadDir);
            // 进入路径失败则创建路径再进
            if (!cdResult) {
                ftpClient.mkd(new String(remoteUpLoadDir.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
            }
            this.cd(remoteUpLoadDir);
        } catch (IOException e) {
            throw e;
        }

    }

    /**
     * @Title: uploadDir
     * @Description:
     * @param localDir
     * @param remoteUpLoadDir
     * @return
     * @throws Exception
     * @see .common.ftp.FtpService#uploadDir(String, String)
     */
    @Override
    public boolean uploadDir(String localDir, String remoteUpLoadDir) throws Exception {
        boolean success = false;
        File dirFile = new File(localDir);
        if (!dirFile.exists()) {
            LOG.error("路径[" + localDir + "]不存在");
            return success;
        }
        if (!dirFile.isDirectory()) {
            LOG.error("路径[" + localDir + "]不是一个文件夹");
            return success;
        }
        File[] dirs = dirFile.listFiles();
        try {
            // 进入目录，如果不存在则遍历创建
            cdRoot(remoteUpLoadDir);
            success = true;
            boolean isRoot = false;
            // 判断是否是根路径
            if ("/".equals(remoteUpLoadDir)) {
                isRoot = true;
            }
            for (File file : dirs) {
                if (file.isDirectory()) {
                    String targetPath = "";
                    if (isRoot) {
                        targetPath = remoteUpLoadDir + file.getName();
                    } else {
                        targetPath = remoteUpLoadDir + "/" + file.getName();
                    }
                    ftpClient.mkd(new String(targetPath.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
                    uploadDir(file.getAbsolutePath(), targetPath);
                } else {
                    success = this.uploadFile(file.getAbsolutePath(), remoteUpLoadDir + "/" + file.getName(),
                            FtpService.REDO_COUNT_ONE);
                }
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        if (success) {
            LOG.debug("文件夹[" + localDir + "]上传成功");
        }
        return success;
    }

    @Override
    public boolean download(String remotePath, String localPath) throws Exception {
        if (checkFile(remotePath)) {
            return this.downloadDir(remotePath, localPath);
        } else {
            return this.downloadFile(remotePath, localPath, FtpService.REDO_COUNT_ONE);
        }
    }

    /**
     * @Title: downloadFile
     * @Description: 文件下载
     * @param remotePath
     *            远程文件路径
     * @param localPath
     *            本地文件路径
     * @param redoCount
     *            重试次数
     * @return
     * @throws Exception
     * @author liuchao
     * @version 1.0
     */
    @Override
    public boolean downloadFile(String remotePath, String localPath, int redoCount) throws Exception {
        boolean result = false;
        try {
            result = this.downloadFile(remotePath, localPath);
        } catch (FileNotFoundException e) {
            throw new Exception(String.format("文件[%s]下载异常", remotePath), e);
        } catch (IOException e) {
            LOG.error("文件{}下载异常", remotePath, e);
            if (redoCount > 0) {
                if (this.reLogin()) {
                    LOG.info("文件下载进入重试：{}", localPath);
                    redoCount = redoCount - 1;
                    result = this.downloadFile(remotePath, localPath, redoCount);
                } else {
                    throw new Exception(String.format("文件[%s]下载异常后，重新登录FTP服务失败", remotePath));
                }
            }
        }

        return result;
    }

    /**
     * @Title: downloadFile @Description: 文件下载 @param remotePath 远程文件路径 @param
     *         localPath 本地文件路径 @return @throws Exception @author
     *         liuchao @version 1.0 @throws IOException @throws
     */
    public boolean downloadFile(String remotePath, String localPath) throws FileNotFoundException, IOException {
        FileOutputStream fos = null;
        File localFile = null;
        boolean success = false;
        try {
            localFile = new File(localPath);
            if (!localFile.getParentFile().exists()) {
                LOG.debug("创建本地目录[{}]", localFile.getParentFile());
                localFile.getParentFile().mkdirs();
            }

            long beginTime = System.currentTimeMillis();
            fos = new FileOutputStream(localFile);
            // 需要把文件名转换成默认格式，避免中文问题
            success = ftpClient.retrieveFile(new String(remotePath.getBytes(), FTP.DEFAULT_CONTROL_ENCODING), fos);

            if (success) {
                LOG.debug("文件[{}]下载成功！耗时:{}ms", remotePath, System.currentTimeMillis() - beginTime);
            } else {
                LOG.error("文件[{}]下载失败！FTP应答码：[{}]，耗时:{}ms", remotePath, ftpClient.getReplyCode(),
                        System.currentTimeMillis() - beginTime);
            }
        } finally {
            IOUtils.closeQuietly(fos);
            // 下载失败删除本地文件
            if (!success && localFile != null) {
                localFile.delete();
            }
        }
        return success;
    }

    /**
     * @Title: downloadDir
     * @Description:
     * @param remoteDir
     * @param localDir
     * @return
     * @see .common.ftp.FtpService#downloadDir(String,
     *      String)
     */
    @Override
    public boolean downloadDir(String remoteDir, String localDir) throws Exception {
        boolean result = true;
        if (!checkFile(remoteDir)) {
            LOG.error("[{}]不是一个文件夹", remoteDir);
            return false;
        }
        FTPFile[] listFiles = null;
        try {
            listFiles = this.listFiles(remoteDir, true, 1);
        } catch (IOException e) {
            throw new Exception(String.format("获取目录[%s]下文件列表异常", remoteDir), e);
        }

        // 先创建临时文件夹
        File localDirFile = new File(localDir);
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }
        for (FTPFile file : listFiles) {
            String filename = file.getName();
            if (".".equals(filename) || "..".equals(filename)) {
                // 排除"."和".."
                continue;
            }
            File localFile = new File(localDir, filename);
            String path = "";
            if ("/".equals(remoteDir)) {
                path = String.format("%s%s", remoteDir, filename);
            } else {
                path = String.format("%s/%s", remoteDir, filename);
            }
            if (file.isDirectory()) {
                // localFile.mkdirs();
                result = downloadDir(path, localFile.getAbsolutePath());
            } else {
                result = this.downloadFile(path, localFile.getAbsolutePath(), FtpService.REDO_COUNT_ONE);
            }
        }
        return result;
    }

    private FTPFile[] listFiles(String remoteDir, boolean showHidden, int redoCount) throws IOException {
        try {
            // 显示隐藏文件
            ftpClient.setListHiddenFiles(showHidden);
            return ftpClient.listFiles(new String(remoteDir.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
        } catch (IOException e) {
            if (redoCount > 0 && this.reLogin()) {
                LOG.error("获取目录[{}]下文件列表异常，马上进行重试！", remoteDir);
                return this.listFiles(remoteDir, showHidden, 0);
            }
            throw e;
        }
    }

    // 检查目录是否文件夹
    private boolean checkFile(String fileName) throws Exception {
        try {
            return cd(fileName);
        } catch (IOException e) {
            throw new Exception(String.format("检查[%s]是否文件异常", fileName), e);
        }
    }

    /**
     * @Title: cd
     * @Description: 切换目录
     * @param remoteDir
     * @return
     * @throws IOException
     * @version 1.0
     */
    private boolean cd(String remoteDir) throws IOException {
        // 解决中文目录切换失败问题
        return ftpClient.changeWorkingDirectory(new String(remoteDir.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
    }

    @Override
    public List<String> getDirList(String remoteDir) throws Exception {
        try {
            List<String> dirList = new ArrayList<>();
            if (!cd(remoteDir)) {
                LOG.error("切换到[{}]目录失败!", remoteDir);
                return dirList;
            }
            // 显示隐藏文件
            ftpClient.setListHiddenFiles(true);
            FTPFile[] listDirectories = ftpClient.listDirectories();
            for (FTPFile file : listDirectories) {
                String filename = file.getName();
                if (".".equals(filename) || "..".equals(filename)) {
                    // 排除"."和".."
                    continue;
                }
                dirList.add(filename);
            }
            return dirList;
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    @Override
    public List<Map<String, String>> getFileAndDirList(String remoteDir) throws Exception {
        try {
            List<Map<String, String>> dirList = new ArrayList<>();
            List<Map<String, String>> fileList = new ArrayList<>();
            if (!cd(remoteDir)) {
                LOG.error("切换到[{}]目录失败!", remoteDir);
                return dirList;
            }
            // 显示隐藏文件
            ftpClient.setListHiddenFiles(true);
            FTPFile[] listFiles = ftpClient.listFiles();
            for (FTPFile file : listFiles) {
                String fileName = file.getName();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    // 排除"."和".."
                    continue;
                }
                if (!file.isFile() && !file.isDirectory()) {
                    LOG.info("[{}]不是一个文件或目录", fileName);
                    continue;
                }
                Map<String, String> fileMap = new HashMap<>();
                fileMap.put("fileName", fileName);
                fileMap.put("fileType", String.valueOf(file.getType()));
                if (file.getType() == FTPFile.FILE_TYPE) {
                    fileList.add(fileMap);
                } else {
                    dirList.add(fileMap);
                }
            }
            dirList.addAll(fileList);
            return dirList;
        } catch (IOException e) {
            throw new Exception("获取FTP文件列表异常", e);
        }
    }

    @Override
    public void scan(String remoteDir, List<FtpFileInfo> fileList) throws Exception {
        if (this.checkFile(remoteDir)) {
            // 扫描目录
            scanDir(remoteDir, fileList);
        } else {
            try {
                FTPFile[] listFiles = listFiles(remoteDir, true, 1);
                for (FTPFile file : listFiles) {
                    FtpFileInfo fileInfo = new FtpFileInfo();
                    String fileName = file.getName();
                    fileInfo.setFileName(fileName);
                    fileInfo.setPath(remoteDir);
                    fileInfo.setSize(file.getSize());
                    fileInfo.setLastModifiedTime(file.getTimestamp().getTimeInMillis());
                    fileInfo.setFtpFileType(FtpFileInfo.FtpFileType.File);
                    fileList.add(fileInfo);
                }
            } catch (IOException e) {
                throw new Exception(e);
            }
        }
    }

    private void scanDir(String remoteDir, List<FtpFileInfo> fileList) throws Exception {
        try {
            FTPFile[] listFiles = listFiles(remoteDir, true, 1);
            for (FTPFile file : listFiles) {
                String fileName = file.getName();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    // 排除"."和".."
                    continue;
                }
                if (!file.isDirectory() && !file.isFile()) {
                    LOG.warn("[{}/{}]不是一个文件或目录", remoteDir, fileName);
                    continue;
                }
                FtpFileInfo fileInfo = new FtpFileInfo();
                fileInfo.setFileName(fileName);
                fileInfo.setPath(String.format("%s/%s", remoteDir, fileName));
                fileInfo.setSize(file.getSize());
                fileInfo.setLastModifiedTime(file.getTimestamp().getTimeInMillis());
                if (file.isDirectory()) {
                    fileInfo.setFtpFileType(FtpFileInfo.FtpFileType.Directory);
                    fileList.add(fileInfo);
                    this.scanDir(fileInfo.getPath(), fileList);
                } else {
                    fileInfo.setFtpFileType(FtpFileInfo.FtpFileType.File);
                    fileList.add(fileInfo);
                }
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

}

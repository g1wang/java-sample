/**
 * All rights Reserved, Designed By Administrator
 * 
 * @Title: FtpService.java
 * @Package  .common.ftp
 * @author: caiwzh
 * @date: 2016年9月6日 下午2:52:55
 * @version V1.0
 */
package com.stars.ftp;

import java.util.List;
import java.util.Map;


/**
 */
public interface FtpService {
  /**
   * 重试次数一次
   */
  public static final int REDO_COUNT_ONE = 1;
  /**
   * 文件类型
   */
  public static final String FILE = "0";

  /**
   * 目录类型
   */
  public static final String DIR = "1";

  /**
   * 
   * @Title: ftpLogin
   * @Description: ftp获取连接
   * @return
   * @author Administrator
   * @version 1.0
   */
  public boolean ftpLogin() throws Exception;

  /**
   * 
   * @Title: ftpLogout
   * @Description: ftp关闭连接/退出
   * @return
   * @author Administrator
   * @version 1.0
   */
  public void ftpLogout();

  /**
   * @Title: getHomeDir
   * @Description: 获取用户的家目录
   * @return
   * @author  
   * @version 1.0
   */
  public String getHomeDir();

  /**
   * 
   * @Title: uploadFile
   * @Description: ftp文件上传
   * @param localPath 本地目录
   * @param remoteUpLoadPath 远程目录
   */

  public boolean uploadFile(String localPath, String remoteUpLoadPath, int redeCount) throws Exception;

  /**
   * 
   * @Title: uploadDir
   * @Description: ftp文件夹上传
   * @param localDir 本地文件夹路径
   * @param remoteUpLoadDir 远程路径
   * @return
   * @author Administrator
   * @version 1.0
   */
  public boolean uploadDir(String localDir, String remoteUpLoadDir) throws Exception;

  /**
   * @Title: download
   * @Description: 下载目录或文件
   * @param remotePath
   * @param localPath
   * @return
   * @throws Exception
   * @author  
   * @version 1.0
   */
  public boolean download(String remotePath, String localPath) throws Exception;

  /**
   * 
   * @Title: downloadFile
   * @Description: ftp文件下载
   * @param remoteUpLoadPath 远程文件目录
   * @param localPath 本地目录
   * @return
   * @author Administrator
   * @version 1.0
   */
  public boolean downloadFile(String remoteUpLoadPath, String localPath, int redoCount) throws Exception;

  /**
   * 
   * @Title: downloadDir
   * @Description: ftp文件夹下载
   * @param remoteUpLoadDir 远程文件夹路径
   * @param localDir 本地路径
   * @return
   * @author Administrator
   * @version 1.0
   */
  public boolean downloadDir(String remoteUpLoadDir, String localDir) throws Exception;

  /**
   * @Title: getDirList
   * @Description: 获取目录列表
   * @param remoteDir
   * @return
   * @throws Exception
   * @author  
   * @version 1.0
   */
  public List<String> getDirList(String remoteDir) throws Exception;

  /**
   * @Title: getFileAndDirList
   * @Description: 获取目录下的文件和目录列表
   * @param remoteDir
   * @return
   * @throws Exception
   * @author  
   * @version 1.0
   */
  public List<Map<String, String>> getFileAndDirList(String remoteDir) throws Exception;
  
  /**
   * @Title: scan
   * @Description: 扫描ftp目录
   * @param remoteDir 远程路径
   * @param fileList 存放文件集合
   * @throws Exception
   * @author  
   * @version 1.0
   */
  public void scan(String remoteDir, List<FtpFileInfo> fileList) throws Exception;

}

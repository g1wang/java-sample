/**

 */
package com.stars.ftp;

/**
 */
public class FtpFileInfo {
  // ftp文件类型
  public enum FtpFileType {
    // 文件夹
    Directory,
    // 文件
    File
  };
  
  /**
   * 文件名称
   */
  private String fileName;
  
  /**
   * 文件类型
   */
  private FtpFileType fileType;
  
  /**
   * 文件路径
   */
  private String path;

  /**
   * 文件大小
   */
  private long size;
  
  /**
   * 最后修改时间
   */
  private long lastModifiedTime;
  
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public FtpFileType getFtpFileType() {
    return fileType;
  }

  public void setFtpFileType(FtpFileType fileType) {
    this.fileType = fileType;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  public void setLastModifiedTime(long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }
  
  @Override
  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append("FtpFileInfo [fileName=").append(fileName);
    buff.append(", fileType=").append(fileType);
    buff.append(", path=").append(path);
    buff.append(", size=").append(size);
    buff.append(", lastModifiedTime=").append(lastModifiedTime).append(']');
    return buff.toString();
  }
}

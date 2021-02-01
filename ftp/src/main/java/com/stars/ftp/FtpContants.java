package com.stars.ftp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FtpContants {

	// ftp Host
	public static  String ftpHost;
	// ftp port
	public static  int ftpPort;
	// ftp user name
	public static String ftpUserName;
	// ftpPasswd
	public static String ftpPasswd;
	// ftpLocalPath 本地文件保存
	public static String ftpLocalPath;
    // ftp timeout
    public static int ftpTimeout;
    // ftpRemotePath ftp端文件保存
    public static String ftpRemotePath;

    @Value("${ftp.ftpHost}")
    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }
    @Value("${ftp.ftpPort}")
    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }
    @Value("${ftp.ftpUserName}")
    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }
    @Value("${ftp.ftpPasswd}")
    public void setFtpPasswd(String ftpPasswd) {
        this.ftpPasswd = ftpPasswd;
    }
    @Value("${ftp.ftpLocalPath}")
    public void setFtpLocalPath(String ftpLocalPath) {
        this.ftpLocalPath = ftpLocalPath;
    }
    @Value("${ftp.ftpTimeout}")
    public void setFtpTimeout(int ftpTimeout) {
        this.ftpTimeout = ftpTimeout;
    }
    @Value("${ftp.ftpRemotePath}")
    public void setFtpRemotePath(String ftpRemotePath) {
        this.ftpRemotePath = ftpRemotePath;
    }


	
	

}

/**
 * All rights Reserved, Designed By Administrator
 * 
 * @Title: FtpSupport.java
 * @Package  .common.ftp.support
 * @author: Administrator
 * @date: 2016年9月7日 下午12:24:56
 * @version V1.0
 */
package com.stars.ftp.support;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.wang.ftpsftp.ftp.FtpFileInfo;
import com.wang.ftpsftp.ftp.FtpService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @ClassName: FtpSupport
 */
public class SFtpSupport implements FtpService {

	private static final Logger LOG = LoggerFactory.getLogger(SFtpSupport.class);
	private ChannelSftp sftp;
	private Session session;
	private String hostname;
	private int port;
	private String username;
	private String password;
	private int timeout;

	/**
	 * ftp用户家目录
	 */
	private String homeDir;

	public SFtpSupport(String hostname, int port, String username, String password, int timeout) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.timeout = timeout;
	}

	/**
	 * @Title: ftpLogin
	 * @Description:
	 * @return
	 * @throws Exception
	 * @see .common.ftp.FtpService#ftpLogin()
	 */
	@Override
	public boolean ftpLogin() throws Exception {
		boolean isconn = false;
		JSch jsch = new JSch();
		try {
			// 按照用户名，主机ip，端口获取一个Session对象
			session = jsch.getSession(username, hostname, port);
			LOG.debug("创建SESSION成功");
			session.setPassword(password);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("UseDNS", "no");
			session.setConfig(config); // 为Session对象设置properties
			session.setTimeout(timeout); // 设置timeout时候
			session.connect(); // 经由过程Session建树链接
			LOG.debug("SESSION建立连接成功");
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect(); // 建树SFTP通道的连接
			homeDir = sftp.getHome();
			LOG.debug("Connected successfully to ftpHost = " + hostname + "，as ftpUserName = " + username
					+ "， returning: " + sftp);
			isconn = true;
		} catch (Exception e) {
			throw new Exception(String.format("登录sftp[%s, %s]服务器异常:", hostname, port), e);
		}
		return isconn;
	}

	/**
	 * @Title: ftpLogout
	 * @Description:
	 * @throws Exception
	 * @see .common.ftp.FtpService#ftpLogout()
	 */
	@Override
	public void ftpLogout() {
		if (null != sftp && sftp.isConnected()) {
			sftp.disconnect();
		}
		if (null != session && session.isConnected()) {
			session.disconnect();
		}
	}

	/**
	 * 重新登录
	 * 
	 * @return
	 */
	public boolean reLogin() {

		try {
			this.ftpLogout();
			return this.ftpLogin();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getHomeDir() {
		return homeDir;
	}

	/**
	 * @Title: uploadFile
	 * @Description:
	 * @param localPath 本地目录
	 * @param remoteUpLoadPath 远程目录
	 * @param redoCount
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean uploadFile(String localPath, String remoteUpLoadPath, int redoCount) throws Exception {
		boolean success = false;
		File srcFile = new File(localPath);
		if (!srcFile.exists() || srcFile.isDirectory()) {
			LOG.error("文件[{}]不存在或不是个文件", srcFile.getAbsolutePath());
			return success;
		}
		if (StringUtils.isEmpty(remoteUpLoadPath)) {
			LOG.error("目标文件路径不能为空");
			return success;
		}
		try {
			sftp.put(srcFile.getAbsolutePath(), remoteUpLoadPath, ChannelSftp.OVERWRITE);
			success = true;
		} catch (SftpException e) {
			// 连接不稳定是抛出SftpException，这时候重试一次，实在不稳定就抛出失败
			LOG.error("使用SFTP上传失败:{}", e);
			if (redoCount > 0) {
				LOG.info("上传文件进入重试{}", localPath);
				if (this.reLogin()) {
					redoCount = redoCount - 1;
					success = this.uploadFile(localPath, remoteUpLoadPath, redoCount);
				} else {
					throw new Exception(String.format("文件[%s]上传异常", srcFile.getPath()), e);
				}
			}

		}
		if (success) {
			LOG.debug("文件[{}]上传成功", srcFile.getAbsolutePath());
		}
		return success;
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
			try {
				sftp.cd(remoteUpLoadDir);
			} catch (SftpException sException) {
				if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
					// 不存在则在ftp上创建文件
					sftp.mkdir(remoteUpLoadDir);
					sftp.cd(remoteUpLoadDir);
				}
			}
			success = true;
			for (File file : dirs) {
				if (file.isDirectory()) {
					String targetPath = "";
					if ("/".equals(remoteUpLoadDir)) {
						targetPath = remoteUpLoadDir + file.getName();
					} else {
						targetPath = remoteUpLoadDir + "/" + file.getName();
					}
					uploadDir(file.getAbsolutePath(), targetPath);
				} else {
					success = this.uploadFile(file.getAbsolutePath(), remoteUpLoadDir, FtpService.REDO_COUNT_ONE);
				}
			}
		} catch (SftpException e) {
			throw new Exception(e);
		}
		if (success) {
			LOG.debug("文件夹[{}]上传失成功", localDir);
		}
		return success;
	}

	@Override
	public boolean download(String remotePath, String localPath) throws Exception {
		if (isDirectory(remotePath)) {
			return this.downloadDir(remotePath, localPath);
		} else {
			return this.downloadFile(remotePath, new File(localPath).getParent(), FtpService.REDO_COUNT_ONE);
		}
	}

	/**
	 * @Title: downloadFile
	 * @Description:
	 * @param remotePath
	 * @param localPath 本地目录
	 * @param redoCount
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean downloadFile(String remotePath, String localPath, int redoCount) throws Exception {
		boolean success = false;
		try {
			File localDir = new File(localPath);
			if (!localDir.exists()) {
				LOG.debug("创建本地目录[{}]", localPath);
				localDir.mkdirs();
			}
			sftp.get(remotePath, localPath);
			success = true;
		} catch (SftpException sftpE) {
			LOG.error("sftp下载{}失败，进入重试{}", remotePath, sftpE);
			// 连接不稳定是抛出SftpException，这时候重试一次，实在不稳定就抛出失败
			if (redoCount > 0) {
				if (this.reLogin()) {
					LOG.info("下载文件进入重试{}", remotePath);
					redoCount = redoCount - 1;
					success = this.downloadFile(remotePath, localPath, redoCount);
				} else {
					throw new Exception(String.format("文件[%s]下载异常", remotePath), sftpE);
				}
			}
		} catch (Exception e) {
			LOG.error(String.format("文件[%s]下载异常", remotePath), e);
		}
		if (success) {
			LOG.debug("文件[{}]下载成功", remotePath);
		}
		return success;
	}

	/**
	 * @Title: downloadDir
	 * @Description:
	 * @param remoteDir
	 * @param localDir
	 * @return
	 * @throws Exception
	 * @see .common.ftp.FtpService#downloadDir(String,
	 *      String)
	 */
	@Override
	public boolean downloadDir(String remoteDir, String localDir) throws Exception {
		boolean success = false;
		List<String> list = getFileList(remoteDir);
		File localDirFile = new File(localDir);
		if (!localDirFile.exists()) {
			localDirFile.mkdirs();
		}
		for (String fileName : list) {
			String targetName = fileName.replace(remoteDir + "/", "");
			File localFile = new File(localDir, targetName);
			if (isDirectory(fileName)) {
				localFile.mkdirs();
				downloadDir(fileName, localFile.getAbsolutePath());
			} else {
				success = this.downloadFile(remoteDir + "/" + targetName, localDir, FtpService.REDO_COUNT_ONE);
			}
		}

		return success;
	}

	// 获取SFTP文件列表
	public List<String> getFileList(String remotePath) throws Exception {
		try {
			@SuppressWarnings("unchecked")
			Vector<LsEntry> v = sftp.ls(remotePath);
			List<String> objList = new ArrayList<String>();
			for (LsEntry entry : v) {
				String name = entry.getFilename();
				if (!".".equals(name) && !"..".equals(name)) {
					objList.add(remotePath + "/" + name);
				}
			}
			return objList;
		} catch (Exception exp) {
			throw new Exception(exp);
		}
	}

	// 检查目录是否文件夹
	private boolean isDirectory(String remotePath) {
		try {
			sftp.cd(remotePath);
			return true;
		} catch (SftpException e) {
			return false;
		}
	}

	@Override
	public List<String> getDirList(String remoteDir) throws Exception {
		try {
			List<String> dirList = new ArrayList<>();
			if (!isDirectory(remoteDir)) {
				return dirList;
			}
			remoteDir = sftp.pwd();
			Vector<ChannelSftp.LsEntry> ls = sftp.ls(remoteDir);
			for (LsEntry entry : ls) {
				String filename = entry.getFilename();
				if (".".equals(filename) || "..".equals(filename)) {
					// 排除"."和".."
					continue;
				}

				if (isDirectory(String.format("%s/%s", remoteDir, filename))) {
					dirList.add(filename);
				}
			}

			return dirList;
		} catch (SftpException e) {
			throw new Exception("获取目录列表异常", e);
		}
	}

	@Override
	public List<Map<String, String>> getFileAndDirList(String remoteDir) throws Exception {
		try {
			List<Map<String, String>> dirList = new ArrayList<>();
			List<Map<String, String>> fileList = new ArrayList<>();
			if (!isDirectory(remoteDir)) {
				return dirList;
			}
			remoteDir = sftp.pwd();
			Vector<ChannelSftp.LsEntry> ls = sftp.ls(remoteDir);
			for (LsEntry entry : ls) {
				String filename = entry.getFilename();
				if (".".equals(filename) || "..".equals(filename)) {
					// 排除"."和".."
					continue;
				}
				Map<String, String> fileMap = new HashMap<>();
				fileMap.put("fileName", filename);
				if (isDirectory(String.format("%s/%s", remoteDir, filename))) {
					fileMap.put("fileType", DIR);
					dirList.add(fileMap);
				} else {
					fileMap.put("fileType", FILE);
					fileList.add(fileMap);
				}
			}
			dirList.addAll(fileList);
			return dirList;
		} catch (SftpException e) {
			throw new Exception("获取文件和目录列表异常", e);
		}
	}

	@Override
	public void scan(String remoteDir, List<FtpFileInfo> fileList) throws Exception {
		if (isDirectory(remoteDir)) {
			scanDir(remoteDir, fileList);
		} else {
			try {
				Vector<ChannelSftp.LsEntry> ls = sftp.ls(remoteDir);
				for (LsEntry entry : ls) {
					FtpFileInfo fileInfo = new FtpFileInfo();
					String fileName = entry.getFilename();
					fileInfo.setFileName(fileName);
					fileInfo.setPath(remoteDir);
					fileInfo.setSize(entry.getAttrs().getSize());
					fileInfo.setLastModifiedTime(entry.getAttrs().getMTime());
					fileInfo.setFtpFileType(FtpFileInfo.FtpFileType.File);
					fileList.add(fileInfo);
				}
			} catch (SftpException e) {
				throw new Exception("获取文件和目录列表异常", e);
			}
		}
	}

	private void scanDir(String remoteDir, List<FtpFileInfo> fileList) throws Exception {
		try {
			Vector<ChannelSftp.LsEntry> ls = sftp.ls(remoteDir);
			for (LsEntry entry : ls) {
				String fileName = entry.getFilename();
				if (".".equals(fileName) || "..".equals(fileName)) {
					// 排除"."和".."
					continue;
				}
				if (!entry.getAttrs().isDir() && !entry.getAttrs().isReg()) {
					LOG.warn("[{}/{}]不是一个文件或目录", remoteDir, fileName);
					continue;
				}
				FtpFileInfo fileInfo = new FtpFileInfo();
				fileInfo.setFileName(fileName);
				fileInfo.setPath(String.format("%s/%s", remoteDir, fileName));
				fileInfo.setSize(entry.getAttrs().getSize());
				fileInfo.setLastModifiedTime(entry.getAttrs().getMTime());
				if (entry.getAttrs().isDir()) { // 目录
					fileInfo.setFtpFileType(FtpFileInfo.FtpFileType.Directory);
					fileList.add(fileInfo);
					this.scan(fileInfo.getPath(), fileList);
				} else { // 文件
					fileInfo.setFtpFileType(FtpFileInfo.FtpFileType.File);
					fileList.add(fileInfo);
				}
			}
		} catch (SftpException e) {
			throw new Exception("获取文件和目录列表异常", e);
		}
	}

}

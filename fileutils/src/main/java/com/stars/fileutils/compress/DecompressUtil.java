package com.stars.fileutils.compress;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.NativeStorage;
import de.innosystec.unrar.rarfile.FileHeader;
import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * 压缩或解压zip： 由于直接使用java.util.zip工具包下的类，会出现中文乱码问题，所以使用ant.jar中的org.apache.tools.zip下的工具类
 *
 * @author Administrator
 */

public class DecompressUtil {

  private static byte[] _byte = new byte[1024];

  /**
   * 解压缩ZIP文件，将ZIP文件里的内容解压到targetDIR目录下
   *
   * @param zipPath 待解压缩的ZIP文件名
   * @param descDir 目标目录
   */
  public static List<File> unzip(String zipPath, String descDir) throws IOException {
    return unzip(new File(zipPath), descDir);
  }

  /**
   * 解压缩ZIP文件，将ZIP文件里的内容解压到targetDIR目录下
   *
   * @param zipPath 待解压缩的ZIP文件名
   */
  public static List<File> unzip(String zipPath) throws IOException {
    File zipFile = new File(zipPath);
    String descDir = zipFile.getAbsolutePath()
        .substring(0, zipFile.getAbsolutePath().lastIndexOf("."));
    return unzip(zipFile, descDir);
  }

  /**
   * 对.zip文件进行解压缩
   *
   * @param zipFile 解压缩文件
   * @param descDir 压缩的目标地址，如：D:\\测试 或 /mnt/d/测试
   */
  public static List<File> unzip(File zipFile, String descDir) throws IOException {
    List<File> _list = new ArrayList<File>();
    try {
      ZipFile _zipFile = new ZipFile(zipFile, "UTF-8");
      for (Enumeration entries = _zipFile.getEntries(); entries.hasMoreElements(); ) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        File _file = new File(descDir + File.separator + entry.getName());
        if (entry.isDirectory()) {
          _file.mkdirs();
        } else {
          File _parent = _file.getParentFile();
          if (!_parent.exists()) {
            _parent.mkdirs();
          }
          InputStream _in = _zipFile.getInputStream(entry);
          OutputStream _out = new FileOutputStream(_file);
          int len = 0;
          while ((len = _in.read(_byte)) > 0) {
            _out.write(_byte, 0, len);
          }
          IOUtils.closeQuietly(_in);
          _out.flush();
          IOUtils.closeQuietly(_out);
          _list.add(_file);
        }
      }
    } catch (IOException e) {
      throw e;
    }
    return _list;
  }

  /**
   * 对临时生成的文件夹和文件夹下的文件进行删除
   */
  public static void deletefile(String delpath) {
    try {
      File file = new File(delpath);
      if (!file.isDirectory()) {
        file.delete();
      } else if (file.isDirectory()) {
        String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
          File delfile = new File(delpath + File.separator + filelist[i]);
          if (!delfile.isDirectory()) {
            delfile.delete();
          } else if (delfile.isDirectory()) {
            deletefile(delpath + File.separator + filelist[i]);
          }
        }
        file.delete();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 解压rar格式压缩包
   */
  public static List<File> unrar(String sourceRar) throws Exception {
    File rarFile = new File(sourceRar);
    String descDir = rarFile.getAbsolutePath()
        .substring(0, rarFile.getAbsolutePath().lastIndexOf("."));
    return unrar(sourceRar, descDir);
  }

  /**
   * 解压rar格式压缩包。
   */
  public static List<File> unrar(String sourceRar, String destDir) throws Exception {
    List<File> _fileList = new ArrayList<File>();
    Archive a = null;
    FileOutputStream fos = null;
    try {
      a = new Archive(new NativeStorage(new File(sourceRar)));
      FileHeader fh = a.nextFileHeader();
      while (fh != null) {
        if (!fh.isDirectory()) {
          String compressFileName = "";
          String destFileName = "";
          String destDirName = "";
          //非windows系统
          if (fh.isUnicode()) {//解決中文乱码
            compressFileName = fh.getFileNameW().trim();
          } else {
            compressFileName = fh.getFileNameString().trim();
          }
          if (File.separator.equals("/")) {
            destFileName = destDir + File.separator + compressFileName.replaceAll("\\\\", "/");
            destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
            //windows系统
          } else {
            destFileName = destDir + File.separator + compressFileName.replaceAll("/", "\\\\");
            destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
          }
          //创建文件夹
          File dir = new File(destDirName);
          if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
          }
          //3解压缩文件
          File destFile = new File(destFileName);
          fos = new FileOutputStream(destFile);
          a.extractFile(fh, fos);
          IOUtils.closeQuietly(fos);
          fos = null;
          _fileList.add(destFile);
        }
        fh = a.nextFileHeader();
      }
      return _fileList;
    } catch (Exception e) {
      throw e;
    } finally {
      IOUtils.closeQuietly(fos);
      if (a != null) {
        try {
          a.close();
          a = null;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }


}

package com.stars.fileutils.compress;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.List;


/**
 * 压缩或解压zip： 由于直接使用java.util.zip工具包下的类，会出现中文乱码问题，所以使用ant.jar中的org.apache.tools.zip下的工具类
 *
 * @author Administrator
 */

public class CompressUtil {

  private static byte[] _byte = new byte[1024];

  /**
   * 压缩文件或路径
   *
   * @param zip 压缩的目的地址
   * @param srcFiles 压缩的源文件
   */
  public static void zipFile(String zip, List<File> srcFiles) {
    try {
      if (zip.endsWith(".zip") || zip.endsWith(".ZIP")) {
        ZipOutputStream _zipOut = new ZipOutputStream(new FileOutputStream(new File(zip)));
        _zipOut.setEncoding("UTF-8");
        for (File _f : srcFiles) {
          handlerFile(zip, _zipOut, _f, "");
        }
        _zipOut.close();
      } else {
        System.out.println("target fileopt[" + zip + "] is not .zip type fileopt");
      }
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }
  }

  /**
   * 单个目录压缩
   */
  public static void zipFile(String srcFilePath) throws IOException {
    try {
      File srcFile = new File(srcFilePath);
      String zipFilePath = srcFile.getAbsolutePath() + ".zip";
      ZipOutputStream _zipOut = new ZipOutputStream(new FileOutputStream(new File(zipFilePath)));
      _zipOut.setEncoding("UTF-8");
      handlerFile(zipFilePath, _zipOut, srcFile, "");
      _zipOut.close();
    } catch (IOException e) {
      throw e;
    }
  }

  /**
   * @param zip 压缩的目的地址
   * @param srcFile 被压缩的文件信息
   * @param path 在zip中的相对路径
   */
  private static void handlerFile(String zip, ZipOutputStream zipOut, File srcFile, String path)
      throws IOException {
    System.out.println(" begin to compression fileopt[" + srcFile.getName() + "]");
    if (!"".equals(path) && !path.endsWith(File.separator)) {
      path += File.separator;
    }
    if (!srcFile.getPath().equals(zip)) {
      if (srcFile.isDirectory()) {
        File[] _files = srcFile.listFiles();
        if (_files.length == 0) {
          zipOut.putNextEntry(new ZipEntry(path + srcFile.getName() + File.separator));
          zipOut.closeEntry();
        } else {
          for (File _f : _files) {
            handlerFile(zip, zipOut, _f, path + srcFile.getName());
          }
        }
      } else {
        InputStream _in = new FileInputStream(srcFile);
        zipOut.putNextEntry(new ZipEntry(path + srcFile.getName()));
        int len = 0;
        while ((len = _in.read(_byte)) > 0) {
          zipOut.write(_byte, 0, len);
        }
        _in.close();
        zipOut.closeEntry();
      }
    }
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

}

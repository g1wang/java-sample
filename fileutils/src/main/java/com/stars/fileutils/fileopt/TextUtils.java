package com.stars.fileutils.fileopt;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TextUtils {
    /**
     * 文件追加
     */
    public static void appendFileUTF(String filePath, String content) {

        File dir = new File(filePath).getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        RandomAccessFile resultTxt = null;
        try {
            resultTxt = new RandomAccessFile(filePath, "rw");
            // 文件长度，字节数
            long fileLength = resultTxt.length();
            // 将写文件指针移到文件尾。
            resultTxt.seek(fileLength);
            resultTxt.write(content.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(resultTxt);
        }
    }
}

package com.stars.fileutils.fileopt;

import java.io.File;
import java.util.List;

public class FileUtils {

    /**
     * 扫描目录
     *
     * @param dir
     * @param list
     */
    public static void scanDir(File dir, List<File> list) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.exists() && file.isDirectory()) {
                scanDir(file, list);
            }
            if (file.exists() && file.isFile()) {
                list.add(file);
            }
        }
    }
}

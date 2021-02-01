package com.stars.opencv;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHelper {
    private final static Map<String, String> PIC_TYPE_MAP = new HashMap<>();
    private final static List<String> PIC_SUPPORT_LIST = new ArrayList<>();

    static {
        // 图片 Discription:[getAllFileType,常见文件头信息]
        PIC_TYPE_MAP.put("424d", "bmp"); // (bmp,dib)
        PIC_TYPE_MAP.put("ffd8ffe0", "jpg"); // JPEG (jpg,jpe,jfif)
        PIC_TYPE_MAP.put("89504e47", "png"); // PNG (png)
        PIC_TYPE_MAP.put("49492a00", "tif"); // TIFF (tif,tiff)
        PIC_TYPE_MAP.put("41433130", "dwg"); // CAD (dwg)
        PIC_TYPE_MAP.put("38425053", "psd"); // Adobe Photoshop (psd)
        PIC_SUPPORT_LIST.add("424d");
        PIC_SUPPORT_LIST.add("ffd8ffe0");
        PIC_SUPPORT_LIST.add("89504e47");
    }

    /**
     * 得到上传文件的文件头
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src){
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取支持的图片后缀
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getSupportSuffix(File file) throws IOException {
        String suffix = null;
        if (file.exists()) {
            InputStream is = null;
            try {
                is = FileUtils.openInputStream(file);
                byte[] b = new byte[4];
                is.read(b, 0, b.length);
                String fileCode = bytesToHexString(b);
                for (String key : PIC_SUPPORT_LIST) {
                    if (key.toLowerCase().startsWith(fileCode.toLowerCase())
                            || fileCode.toLowerCase().startsWith(key.toLowerCase())) {
                        // 匹配到图片文件
                        suffix = PIC_TYPE_MAP.get(key);
                    }
                }
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return suffix;
    }

}
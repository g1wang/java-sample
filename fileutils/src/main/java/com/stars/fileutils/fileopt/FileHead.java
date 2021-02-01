package com.stars.fileutils.fileopt;

import java.util.HashMap;
import java.util.Map;

public class FileHead {

  public final static Map<String, String> PIC_TYPE_MAP = new HashMap<String, String>();
  public final static Map<String, String> AUDIO_TYPE_MAP = new HashMap<String, String>();
  public final static Map<String, String> VIDEO_TYPE_MAP = new HashMap<String, String>();
  public final static Map<String, String> COMMON_TYPE_MAP = new HashMap<String, String>();

  public enum FileHeadType {
    ZIP("504B0304"), RAR("52617221");
    String headCode;

    FileHeadType(String headCode) {
      this.headCode = headCode;
    }

    public String getHeadCode() {
      return headCode;
    }
  }

  /**
   * Discription:[getAllFileType,常见文件头信息]
   */
  static {
    //普通文件
    COMMON_TYPE_MAP.put("504B0304", FileHeadType.ZIP.name());
    COMMON_TYPE_MAP.put("52617221 ", FileHeadType.RAR.name());

    // 图片
    PIC_TYPE_MAP.put("424d", "bmp"); // (bmp,dib)
    PIC_TYPE_MAP.put("ffd8ffe0", "jpg"); // JPEG (jpg,jpe,jfif)
    PIC_TYPE_MAP.put("89504e47", "png"); // PNG (png)
    PIC_TYPE_MAP.put("49492a00", "tif"); // TIFF (tif,tiff)
    PIC_TYPE_MAP.put("41433130", "dwg"); // CAD (dwg)
    PIC_TYPE_MAP.put("38425053", "psd"); // Adobe Photoshop (psd)

    // 音频
    AUDIO_TYPE_MAP.put("49443303", "mp3");
    AUDIO_TYPE_MAP.put("664c6143", "flac");
    AUDIO_TYPE_MAP.put("00000020", "m4a"); // m4a
    AUDIO_TYPE_MAP.put("52494646", "wav"); // Wave (wav)
    AUDIO_TYPE_MAP.put("4d414320", "ape"); // ape
    AUDIO_TYPE_MAP.put("4f676753", "ogg"); // Ogg
    AUDIO_TYPE_MAP.put("3026b275", "wma"); // wma

    AUDIO_TYPE_MAP.put("fffd8004", "mp2"); // mp2
    AUDIO_TYPE_MAP.put("2e736e64", "au"); // au
    AUDIO_TYPE_MAP.put("464f524d", "aiff"); // aiff
    AUDIO_TYPE_MAP.put("f07e0001", "sds"); // sds
    AUDIO_TYPE_MAP.put("fff15080", "aac"); // aac
    AUDIO_TYPE_MAP.put("2E7261FD", "ram"); // ram
    AUDIO_TYPE_MAP.put("2E7261FD", "mid"); // MIDI

    // 视频
    VIDEO_TYPE_MAP.put("00000020", "mp4"); // mp4
    VIDEO_TYPE_MAP.put("3026b275", "wmv"); // wmv与asf相同
    VIDEO_TYPE_MAP.put("52494646", "avi");
    VIDEO_TYPE_MAP.put("00000014", "3gp");
    VIDEO_TYPE_MAP.put("464c5601", "flv"); // flv与f4v相同
    VIDEO_TYPE_MAP.put("00000014", "mov"); // Quicktime (mov)
    VIDEO_TYPE_MAP.put("2e424f56", "bov"); // bov
    VIDEO_TYPE_MAP.put("2e524d46", "rmvb"); // rmvb/rm相同
    VIDEO_TYPE_MAP.put("000001b3", "mpg"); // 没有音频数据
    VIDEO_TYPE_MAP.put("000001ba", "mpg"); // 包含音频数据和视频数据
    VIDEO_TYPE_MAP.put("47494638", "gif"); // GIF (gif)
  }
}

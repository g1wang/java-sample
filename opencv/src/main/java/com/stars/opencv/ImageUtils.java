package com.stars.opencv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

public class ImageUtils {

	/**
	 * 
	 * @param position
	 * @param oriWidth
	 * @param oriHeight
	 * @param resizeWidth
	 * @param resizeHeight
	 */
	public static void convertImagePosition(JSONArray position, double oriWidth, double oriHeight, double resizeWidth,
			double resizeHeight) {

		for (Object object : position) {
			JSONObject jsonObject = (JSONObject) object;
			double x = jsonObject.getDoubleValue("x");
			jsonObject.put("x", convertSize(x, oriWidth, resizeWidth));
			double w = jsonObject.getDoubleValue("w");
			jsonObject.put("w", convertSize(w, oriWidth, resizeWidth));
			double y = jsonObject.getDoubleValue("y");
			jsonObject.put("y", convertSize(y, oriHeight, resizeHeight));
			double h = jsonObject.getDoubleValue("h");
			jsonObject.put("h", convertSize(h, oriHeight, resizeHeight));
		}
	}

	/**
	 * 座標轉換
	 * 
	 * @param positionSize
	 * @param oriSize
	 * @return
	 */
	public static int convertSize(double positionSize, double oriSize, double caffeResize) {

		double oriPosition = oriSize / caffeResize * positionSize;
		BigDecimal b = new BigDecimal(oriPosition);
		int f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		return f1;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getImageSize(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		int w = image.getWidth();
		jsonObject.put("w", w);
		int h = image.getHeight();
		jsonObject.put("h", h);
		return jsonObject;
	}

	/**
	 * 截取图片部分
	 * 
	 * @param srcImg
	 * @param destPath
	 * @param rect
	 * @return
	 * @throws IOException 
	 */
	private static boolean cutImage(File srcImg, String destPath, Rectangle rect) throws IOException {
		boolean isCutSuccess = false;
		if (srcImg.exists()) {
			FileInputStream fis = null;
			ImageInputStream iis = null;
			FileOutputStream out = null;
			try {
				fis = new FileInputStream(srcImg);
				// ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG,
				// JPEG, WBMP, GIF, gif]
				String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
				String suffix = null;
				// 获取图片后缀
				suffix = FileHelper.getSupportSuffix(srcImg);
				if (suffix != null && types.toLowerCase().indexOf(suffix.toLowerCase() + ",") > 0) {
					// 将FileInputStream 转换为ImageInputStream
					iis = ImageIO.createImageInputStream(fis);
					// 根据图片类型获取该种类型的ImageReader
					ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
					reader.setInput(iis, true);
					ImageReadParam param = reader.getDefaultReadParam();
					param.setSourceRegion(rect);
					BufferedImage bi = reader.read(0, param);
					File file = new File(destPath);
					file.getParentFile().mkdirs();
					out = new FileOutputStream(destPath);
					ImageIO.write(bi, suffix, out);
					isCutSuccess = true;
				}
			}  finally {
				IOUtils.closeQuietly(fis);
				IOUtils.closeQuietly(iis);
				IOUtils.closeQuietly(out);
			}
		}
		return isCutSuccess;
	}

	/**
	 * 
	 * @param position
	 *            坐标组
	 * @param file
	 *            源文件
	 * @param destDirPath
	 *            保存文件目录
	 * @param frameNum
	 *            幀號
	 * @throws IOException 
	 */
	public static void cutImage(JSONArray position, File file, String destDirPath, String frameNum) throws IOException {
		int count = 0;
		for (Object object : position) {
			count++;
			JSONObject jsonObject = (JSONObject) object;
			int x = (int) jsonObject.getDoubleValue("x");
			int w = (int) jsonObject.getDoubleValue("w");
			int y = (int) jsonObject.getDoubleValue("y");
			int h = (int) jsonObject.getDoubleValue("h");
			String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
			String suffix = file.getName().substring(file.getName().lastIndexOf("."));
			String destPath = destDirPath + "/" + fileName + "_" + count + suffix;
			cutImage(file, destPath, new Rectangle(x, y, w, h));
		}
	}

}

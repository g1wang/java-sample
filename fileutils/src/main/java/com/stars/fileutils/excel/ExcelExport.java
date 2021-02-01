package com.stars.fileutils.excel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;

public class ExcelExport {

    public void export(File file) throws IOException, WriteException {
        WritableWorkbook data = null;
        data = Workbook.createWorkbook(file);
        WritableSheet sheet = data.createSheet("第一页", 0);
        String[] titles = new String[]{"GA", "GB", "GC", "GD", "GE"};
        String[] contents = new String[]{"A1", "B1", "C1", "D1", "E1"};
        for (int i = 1; i < titles.length + 1; i++) {
            String strr = titles[i - 1];
            Label exceltitle = new Label(i - 1, 0, strr);
            sheet.addCell(exceltitle);
        }
        for (int i = 1; i < contents.length + 1; i++) {
            String strr = contents[i - 1];
            Label exceltitle = new Label(i - 1, 1, strr);
            sheet.addCell(exceltitle);
        }
        data.write();
        try {
            if (data != null) {
                data.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

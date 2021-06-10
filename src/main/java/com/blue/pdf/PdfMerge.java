package com.blue.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import javax.swing.text.Document;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PdfMerge {

    public static File mulFile2One(List<File> files, String targetPath) throws IOException {
        // pdf合并工具类
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        for (File f : files) {
            if (f.exists() && f.isFile()) {
                // 循环添加要合并的pdf
                mergePdf.addSource(f);
            }
        }
        // 设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(targetPath);
        // 合并pdf
        mergePdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return new File(targetPath);
    }

    public static void merge(File folder)throws IOException{
        File[] files = folder.listFiles((dir, name) -> name.endsWith("pdf"));
        List<File> fileList = Arrays.asList(files);
        mulFile2One(fileList,"d:/tmp/fapiao/merged.pdf");
    }
    public static void main(String[] args) throws IOException {
        merge(new File("d:/tmp/fapiao"));

    }
}
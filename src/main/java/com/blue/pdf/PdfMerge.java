package com.blue.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PdfMerge {
    /**
     * @Auther rangerhou
     * @Date 2022/11/17
     * @desc
     */
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
    /**
     * @author rangerhou
     * @date 2022/11/22
     * @desc
     */
    public static void merge(File folder)throws IOException{
        File[] files = folder.listFiles((dir, name) -> name.endsWith("pdf") && !name.equals("merged.pdf"));
        List<File> fileList = Arrays.asList(files);
        mulFile2One(fileList,folder.getPath()+"/merged.pdf");
    }
    public static void main(String[] args) throws IOException {
        String pathname = "d:/tmp/fapiao/20230206";
        merge(new File(pathname));

    }
}
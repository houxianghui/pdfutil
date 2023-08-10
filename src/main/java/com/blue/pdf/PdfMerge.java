package com.blue.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PdfMerge {
    /**
     * @author rangerhou
     * @date 2022/11/17
     * @desc
     */
    public static void mulFile2One(List<File> files, String targetPath) throws IOException {
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
        new File(targetPath);
    }
    /**
     * @author rangerhou
     * @date 2022/11/22
     * @desc
     */
    public static void merge(File folder)throws IOException{
        File[] files = folder.listFiles((dir, name) -> name.endsWith("pdf") && !name.equals("merged.pdf"));
        assert files != null;
        List<File> fileList = Arrays.asList(files);
        mulFile2One(fileList,folder.getPath()+"/merged.pdf");
    }

    public static void main(String[] args) throws IOException {
        File f = latestFile("d:/tmp/fapiao");
        if (f != null) {
            System.out.println(f);
            merge(f);
        }

    }

    public static File latestFile(String path) {
        File directory = new File(path);
        File[] subDirectories = directory.listFiles(File::isDirectory);
        if (subDirectories != null) {
            Arrays.sort(subDirectories, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            return subDirectories[0];
        }
        return null;
    }
}
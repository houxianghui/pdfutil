package com.blue;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

public class ExcelToPlantUML {

    public static void main(String[] args) throws Exception {
        String filePath = "E:/download/RUNBOOK_投产指令.xlsx";
        try (InputStream inputStream = new FileInputStream(new File(filePath))) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(1);

            // 存储任务编号和其依赖的任务编号列表
            Map<String, List<String>> taskDependencies = new HashMap<>();
            // 存储任务编号和其对应的指令内容
            Map<String, String> taskInstructions = new HashMap<>();
            // 步骤1：读取Excel并筛选任务
            for (Row row : sheet) {
                Cell taskNumberCell = row.getCell(0);
                Cell instructionCell = row.getCell(4);
                Cell dependencyCell = row.getCell(7);

                if (taskNumberCell != null && instructionCell != null) {
                    String taskNumber = getCellValue(taskNumberCell);
                    String instructionContent = getCellValue(instructionCell);
                    String dependencyString = getCellValue(dependencyCell);
                    taskInstructions.put(taskNumber, instructionContent);
//                    if (instructionContent.contains("CPS") || instructionContent.contains("GLP")) {

                    taskDependencies.put(taskNumber, Arrays.asList(dependencyString.split("\\s*,\\s*")));
//                    }
                }
            }

            // 步骤2：构建依赖树
            Map<String, Set<String>> dependencyTree = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : taskDependencies.entrySet()) {
                String task = entry.getKey();
                String inst = taskInstructions.get(task);
                if (inst.contains("CPS") || inst.contains("GLP")) {
                    buildDependencyTree(task, taskDependencies, dependencyTree);
                }
            }

            // 步骤3：输出PlantUML格式的依赖树
            System.out.println("@startuml");
            for (Map.Entry<String, Set<String>> entry : dependencyTree.entrySet()) {
                for (String dependency : entry.getValue()) {
                    if (dependency == null || dependency.isEmpty()) {
                        System.out.println("[*]-->" + entry.getKey());
                    } else {
                        System.out.println(dependency + " --> " + entry.getKey());
                        System.out.println(dependency + ":" + taskInstructions.get(dependency));
                    }
                }
                System.out.println(entry.getKey() + ":" + taskInstructions.get(entry.getKey()));
            }
            System.out.println("@enduml");

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildDependencyTree(String task, Map<String, List<String>> taskDependencies, Map<String, Set<String>> dependencyTree) {
        if (!dependencyTree.containsKey(task)) {
            dependencyTree.put(task, new HashSet<>());
        }

        List<String> dependencies = taskDependencies.get(task);
        if (dependencies == null) {
            return;
        }
        for (String dependency : dependencies) {
            if (!task.equals(dependency)) { // 防止自引用
                dependencyTree.get(task).add(dependency);
                buildDependencyTree(dependency, taskDependencies, dependencyTree); // 递归构建依赖树
            }
        }
    }

    static DecimalFormat df = new DecimalFormat("###,###");

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return df.format((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
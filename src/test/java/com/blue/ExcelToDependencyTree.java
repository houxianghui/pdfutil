package com.blue;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;

public class ExcelToDependencyTree {
    private static final String[] KEYWORDS = {"CPS", "GLP"};

    public static void main(String[] args) {
        try (FileInputStream file = new FileInputStream(new File("E:/download/RUNBOOK_投产指令.xlsx"))) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(1); // 假设数据都在第一个工作表
            Map<String, Map<String, Object>> records = readSheet(sheet);

            Map<String, Map<String, Object>> filteredRecords = filterRecords(records);
            Map<String, DependencyNode> dependencyTree = buildDependencyTree(filteredRecords, records);
            printDependencyTree(dependencyTree);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Map<String, Object>> readSheet(Sheet sheet) {
        Map<String, Map<String, Object>> records = new HashMap<>();
        Iterator<Row> rowIterator = sheet.iterator();

        int columnCount = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (columnCount == 0) {
                columnCount = row.getLastCellNum();
            }
            Map<String, Object> record = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                Cell cell = row.getCell(i);
                switch (i) {
                    case 0:
                        record.put("taskID", getCellValue(cell));
                        break;
                    case 4:
                        record.put("instruction", getCellValue(cell));
                        break;
                    case 7:
                        record.put("dependencies", getCellValue(cell));
                        break;
                }
            }
            records.put((String) record.get("taskID"), record);
        }
        return records;
    }

    private static Map<String, Map<String, Object>> filterRecords(Map<String, Map<String, Object>> records) {
        Map<String, Map<String, Object>> filteredRecords = new LinkedHashMap<>();
        for (String key : records.keySet()) {
            Map<String, Object> record = records.get(key);
            String instruction = (String) record.get("instruction");
            boolean containsKeywords = Arrays.stream(KEYWORDS).anyMatch(instruction::contains);
            if (containsKeywords) {
                filteredRecords.put((String) record.get("taskID"), record);
            }
        }
        return filteredRecords;
    }

    private static Map<String, DependencyNode> buildDependencyTree(Map<String, Map<String, Object>> filteredRecords, Map<String, Map<String, Object>> allRecords) {
        Map<String, DependencyNode> dependencyTree = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : filteredRecords.entrySet()) {
            String taskID = entry.getKey();
            if (dependencyTree.containsKey(taskID)) {
                continue;
            }
            String dependenciesStr = (String) entry.getValue().get("dependencies");

            DependencyNode node = new DependencyNode(taskID, (String) entry.getValue().get("instruction"), dependenciesStr);
            dependencyTree.put(taskID, node);
            addAllParentNode(node, allRecords, dependencyTree);
//            if (dependenciesStr != null && !dependenciesStr.isEmpty()) {
//                Arrays.stream(dependenciesStr.split(","))
//                        .map(String::trim)
//                        .forEach(dependencyID -> {
//                            DependencyNode dependencyNode = dependencyTree.computeIfAbsent(dependencyID, id -> new DependencyNode(id, (String) allRecords.get(id).get("instruction")));
//                            node.addDependency(dependencyNode);
//                        });
//            }
        }
        return dependencyTree;
    }

    private static void addAllParentNode(DependencyNode node, Map<String, Map<String, Object>> allRecords, Map<String, DependencyNode> dependencyTree) {
        if (node.getDependenciesStr() == null || node.getDependenciesStr().isEmpty()) {
            return;
        }
        for (String s : node.getDependenciesStr().split(",")) {
            DependencyNode p = dependencyTree.get(s);
            if (p == null) {
                p = new DependencyNode(s, (String) allRecords.get(s).get("instruction"), (String) allRecords.get(s).get("dependencies"));
                node.addDependency(p);
                dependencyTree.put(s, p);
            }
            addAllParentNode(p, allRecords, dependencyTree);
        }
    }

    private static void printDependencyTree(Map<String, DependencyNode> dependencyTree) throws UnsupportedEncodingException {
        for (DependencyNode node : dependencyTree.values()) {
//            if (node.dependenciesStr == null || node.dependenciesStr.isEmpty()) {
            node.printTree();
//            }

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

    static class DependencyNode {
        private final String id;
        private final String instruction;
        private final String dependenciesStr;
        private final List<DependencyNode> dependencies;

        public DependencyNode(String id, String instruction, String dependenciesStr) {
            this.id = id;
            this.instruction = instruction;
            this.dependenciesStr = dependenciesStr;
            this.dependencies = new ArrayList<>();
        }

        public void addDependency(DependencyNode dependency) {
            if (!dependencies.contains(dependency)) {
                dependencies.add(dependency);
            }
        }

        public String getDependenciesStr() {
            return dependenciesStr;
        }

        public void printTree() throws UnsupportedEncodingException {
            for (DependencyNode dependency : dependencies) {
//                System.out.println(dependency.id + "([" + dependency.instruction + "])" + " --> " + id + "([" + instruction + "])");
                System.out.println(dependency.id + "-->" + id);
                System.out.println(dependency.id + ":" + dependency.instruction);
                dependency.printTree();
            }
            System.out.println(id + ":" + instruction);
        }
    }
}
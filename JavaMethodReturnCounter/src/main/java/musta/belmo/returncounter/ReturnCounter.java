package musta.belmo.returncounter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import musta.belmo.returncounter.gui.MethodDescriber;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ReturnCounter {
    /**
     * @param src
     * @param out
     * @throws IOException
     */
    public void countReturnStatements(File src, File out) throws IOException {
        Map<Object, Object> all = new HashMap<>();
        if (src.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(src, new String[]{"java"}, true);
            for (File file : files) {
                all.putAll(countReturnStmtByMethod(file));
            }
        } else {
            all.putAll(countReturnStmtByMethod(src));
        }
        writeMapToExcel(all, out);
    }


    public Map<Object, Object> countReturnStatements(File src) throws IOException {
        Map<Object, Object> all = new HashMap<>();
        if (src.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(src, new String[]{"java"}, true);
            for (File file : files) {
                all.putAll(countReturnStmtByMethod(file));
            }
        } else {
            all.putAll(countReturnStmtByMethod(src));
        }
        return all;
    }

    public Set<MethodDescriber> countReturnStatementsM(File src) throws IOException {
        Set<MethodDescriber> all = new HashSet<>();
        if (src.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(src, new String[]{"java"}, true);
            for (File file : files) {

                all.addAll(countReturnStmtByMethodM(file));
            }
        } else {
            all.addAll(countReturnStmtByMethodM(src));
        }
        return all;
    }

    /**
     * @param src
     * @param out
     * @throws IOException
     */
    public void countReturnStatements(String src, String out) throws IOException {
        countReturnStatements(new File(src), new File(out));
    }


    /**
     * counts  the number of the return statements by method
     *
     * @param src
     * @return
     * @throws IOException
     */
    private Map<String, Integer> countReturnStmtByMethod(File src) throws IOException {
        Map<String, Integer> counter = new HashMap<>();
        CompilationUnit compilationUnit = JavaParser.parse(src);
        compilationUnit.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
            Optional<BlockStmt> body = methodDeclaration.getBody();
            if (body.isPresent()) {
                String string = body.get().toString();
                String[] words = string.toLowerCase().split("\\s+");
                Map<String, Integer> wordCounts = new HashMap<>();
                for (String word : words) {
                    Integer count = wordCounts.get(word);
                    if (count == null) {
                        count = 0;
                    }
                    wordCounts.put(word, count + 1);
                }
                counter.put(src.getAbsolutePath() + " @@ " + methodDeclaration.getBegin().get().line + "@@" + getSignature(methodDeclaration), wordCounts.getOrDefault("return", 0));
            }
        });
        return counter;
    }

    private Set<MethodDescriber> countReturnStmtByMethodM(File src) throws IOException {
        Set<MethodDescriber> counter = new HashSet<>();
        CompilationUnit compilationUnit = JavaParser.parse(src);
        compilationUnit.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
            Optional<BlockStmt> body = methodDeclaration.getBody();
            if (body.isPresent()) {
                String string = body.get().toString();
                String[] words = string.toLowerCase().split("\\s+");
                Map<String, Integer> wordCounts = new HashMap<>();
                for (String word : words) {
                    Integer count = wordCounts.get(word);
                    if (count == null) {
                        count = 0;
                    }
                    wordCounts.put(word, count + 1);
                }
                MethodDescriber methodDescriber = new MethodDescriber();

                methodDescriber.setEmplacement(src.getAbsolutePath());
                methodDescriber.setLigne(methodDeclaration.getBegin().get().line);
                methodDescriber.setName(getSignature(methodDeclaration));
                methodDescriber.setNbReturns(wordCounts.getOrDefault("return", 0));

                counter.add(methodDescriber);

            }
        });
        return counter;
    }

    /**
     * TODO: provide a description for this method
     *
     * @param map {@link Map}
     */
    public void writeMapToExcel(Map<?, ?> map, File output) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet();
        int y = 0;
        HSSFRow rowhead = sheet.createRow(y);
        rowhead.createCell(0).setCellValue("Emplacement");
        rowhead.createCell(1).setCellValue("Ligne");
        rowhead.createCell(2).setCellValue("Méthode");
        rowhead.createCell(3).setCellValue("Nombre de return");
        y++;
        for (Map.Entry<?, ?> stringIntegerEntry : map.entrySet()) {
            rowhead = sheet.createRow(y++);
            Object key = stringIntegerEntry.getKey();
            Object value = stringIntegerEntry.getValue();
            String cellValue = String.valueOf(key);
            StringTokenizer stringTokenizer = new StringTokenizer(cellValue, "@@");
            HSSFCell cell = rowhead.createCell(0);

            cell.setCellValue(stringTokenizer.nextToken());
            rowhead.createCell(1).setCellValue(stringTokenizer.nextToken());
            rowhead.createCell(2).setCellValue(stringTokenizer.nextToken());
            rowhead.createCell(3).setCellValue(String.valueOf(value));

            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
            cell.setCellStyle(cellStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        FileOutputStream fileOut = new FileOutputStream(output);
        workbook.write(fileOut);
    }

    /**
     * gets the signature of the method
     *
     * @param methodDeclaration {@link MethodDeclaration}
     * @return {@link String}
     */
    private String getSignature(MethodDeclaration methodDeclaration) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodDeclaration.getName()).append('(');
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        List<String> parameterList = parameters.stream().map(
                p -> p.getType().toString()).
                collect(Collectors.toList());
        stringBuilder.append(String.join(",", parameterList))
                .append(')');
        return stringBuilder.toString();
    }
}

package musta.belmo.catchverifier.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import musta.belmo.catchverifier.beans.TryCatchDescriber;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CatchVerifier {

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


    public Set<TryCatchDescriber> verifyTryCatch(File src) throws IOException {
        Set<TryCatchDescriber> all = new HashSet<>();
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
                BlockStmt blockStmt = body.get();
                int y = 0;
                for (Statement statement : blockStmt.getStatements()) {
                    if (statement.isReturnStmt()) {
                        y++;
                    }
                }
                counter.put(src.getAbsolutePath() + " @@ " + methodDeclaration.getBegin().get().line + "@@" + getSignature(methodDeclaration), y);
            }
        });
        return counter;
    }

    private Set<TryCatchDescriber> countReturnStmtByMethodM(File src) throws IOException {
        final Set<TryCatchDescriber> counter = new LinkedHashSet<>();
        CompilationUnit compilationUnit = JavaParser.parse(src);
        compilationUnit.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
            Optional<BlockStmt> body = methodDeclaration.getBody();
            if (body.isPresent()) {
                BlockStmt blockStmt = body.get();

                for (Node statement : blockStmt.getChildNodes()) {
                    TryCatchDescriber tryCatchDescriber = new TryCatchDescriber();
                    if (statement.toString().contains("try")
                            && statement.toString().contains("catch")) {
                        TryStmt tryStmt = ((TryStmt) statement).asTryStmt();
                        NodeList<CatchClause> catchClauses = tryStmt.getCatchClauses();
                        for (CatchClause catchClause : catchClauses) {
                            if ("Exception".equals(catchClause.getParameter().getType().toString())) {
                                tryCatchDescriber.setValide(false);
                                break;
                            }
                        }
                        tryCatchDescriber.setEmplacement(src.getAbsolutePath());
                        tryCatchDescriber.setLigne(statement.getBegin().get().line);
                        counter.add(tryCatchDescriber);
                    }
                }
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
     * TODO: provide a description for this method
     *
     * @param t {@link T}
     */
    public <T> void writeObjectToExcel(T t, File output) throws Exception {
        Class<?> aClass = t.getClass();
        Method[] methods = aClass.getMethods();
        List<String> methodNames = Stream.of(methods).map(method -> method.getName()).collect(Collectors.toList());

        List<String> fieldNames = methodNames.stream()
                .filter(name -> name.startsWith("get")).collect(Collectors.toList());

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        int rowIndex = 0;

        for (String methodName : fieldNames) {
            HSSFRow rowHead = sheet.createRow(rowIndex++);
            rowHead.createCell(0).setCellValue(methodName);
        }

        rowIndex = 0;
        for (String methodName : methodNames) {
            HSSFRow rowHead = sheet.createRow(rowIndex++);
            rowHead.createCell(1).setCellValue(getValueByMethodName(t, methodName));
        }


        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        FileOutputStream fileOut = new FileOutputStream(output);
        workbook.write(fileOut);
    }

    private <T> String getValueByMethodName(T t, String methodName) throws Exception {
        Class<?> aClass = t.getClass();
        Method method = aClass.getMethod(methodName);
        return String.valueOf(method.invoke(t));
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

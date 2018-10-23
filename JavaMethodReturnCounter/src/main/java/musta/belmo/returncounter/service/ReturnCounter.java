package musta.belmo.returncounter.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.*;
import musta.belmo.returncounter.beans.MethodDescriber;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ReturnCounter {

    /**
     * @param src
     * @return
     * @throws IOException
     */
    public Set<MethodDescriber> countReturnStatements(File src) throws IOException {
        Set<MethodDescriber> methodDescribers = new LinkedHashSet<>();
        if (src.isDirectory()) {
            Collection<File> files = getJavaFilesInDir(src);
            for (File file : files) {
                methodDescribers.addAll(countReturnStmtByMethod(file));
            }
        } else {
            methodDescribers.addAll(countReturnStmtByMethod(src));
        }
        return methodDescribers;
    }

    public void countReturnStatements(String src, String dest) throws IOException {
        countReturnStatements(new File(src), new File(dest));
    }

    public void countReturnStatements(File src, File dest) throws IOException {
        Set<MethodDescriber> all = new HashSet<>();
        if (src.isDirectory()) {
            Collection<File> files = getJavaFilesInDir(src);
            for (File file : files) {

                all.addAll(countReturnStmtByMethod(file));
            }
        } else {
            all.addAll(countReturnStmtByMethod(src));
        }
        writeDescribersToExcel(all, dest);

    }

    private Collection<File> getJavaFilesInDir(File src) {
        return FileUtils.listFiles(src, new String[]{"java"}, true);
    }


    private Set<MethodDescriber> countReturnStmtByMethod(File src) throws IOException {
        final Set<MethodDescriber> counter = new LinkedHashSet<>();
        CompilationUnit compilationUnit = JavaParser.parse(src);
        compilationUnit.findAll(MethodDeclaration.class, methodDeclaration ->
                methodDeclaration.getParentNode().isPresent()
                        && !(methodDeclaration.getParentNode().get() instanceof ObjectCreationExpr))
                .forEach(methodDeclaration -> {
                    Optional<BlockStmt> body = methodDeclaration.getBody();
                    if (body.isPresent()) {

                        BlockStmt blockStmt = body.get();
                        int y = countInDepth(blockStmt);
                        MethodDescriber methodDescriber = new MethodDescriber();
                        methodDescriber.setEmplacement(src.getAbsolutePath());
                        methodDescriber.setLigne(methodDeclaration.getBegin().get().line);
                        methodDescriber.setName(getSignature(methodDeclaration));
                        methodDescriber.setNbReturns(y);
                        counter.add(methodDescriber);
                    }
                });
        return counter;
    }

    private int countInDepth(Node blockStmt) {
        int count = 0;
        if (blockStmt instanceof ReturnStmt) {
            count++;
        }

        List<Node> childNodes = blockStmt.getChildNodes();
        if (childNodes != null) {
            for (Node node : childNodes) {
                count += countInDepth(node);
            }
        }
        return count;
    }

    /**
     * @param methodDescribers
     * @param output
     * @throws IOException
     */
    private void writeDescribersToExcel(Collection<MethodDescriber> methodDescribers, File output) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("return counter");
        int rowCount = 0;
        HSSFRow rowHead = sheet.createRow(rowCount);

        rowHead.createCell(0).setCellValue("Emplacement");
        rowHead.createCell(1).setCellValue("Ligne");
        rowHead.createCell(2).setCellValue("Méthode");
        rowHead.createCell(3).setCellValue("Nombre de return");
        rowCount++;

        for (MethodDescriber methodDescriber : methodDescribers) {
            rowHead = sheet.createRow(rowCount++);
            rowHead.createCell(0).setCellValue(methodDescriber.getEmplacement());
            rowHead.createCell(1).setCellValue(methodDescriber.getLigne());
            rowHead.createCell(2).setCellValue(methodDescriber.getName());
            rowHead.createCell(3).setCellValue(methodDescriber.getNbReturns());
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
                .append(") : ")
                .append(methodDeclaration.getType().asString());
        return stringBuilder.toString();
    }
}

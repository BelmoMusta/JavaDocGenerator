package musta.belmo.enumhandler.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import musta.belmo.enumhandler.beans.EnumDescriber;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO : Compléter la description de cette classe
 */
public class EnumHandler {

    public CompilationUnit createEnum(EnumDescriber enumDescriber) throws Exception {
        CompilationUnit compilationUnit = new CompilationUnit();

        EnumDeclaration enumDeclaration = compilationUnit.addEnum(enumDescriber.getName());
        Map<String, String> enumEntries = enumDescriber.get();
        enumEntries.forEach((name, value) -> {
            EnumConstantDeclaration declaration = new EnumConstantDeclaration();
            declaration.setName(name);
            if (enumDescriber.isString()) {
                LiteralExpr expr = new StringLiteralExpr(value);
                declaration.addArgument(expr);
            } else {
                declaration.addArgument(value);
            }
            enumDeclaration.addEntry(declaration);
        });
        return compilationUnit;
    }

    /**
     * @param src {@link File}
     * @return {@link File}
     */
    private Collection<File> getJavaFilesInDir(File src) {
        return FileUtils.listFiles(src, new String[]{"java"}, true);
    }


    /**
     * Count in depth
     *
     * @param blockStmt {@link Node}
     * @return int
     */
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
     * gets the signature of the method
     *
     * @param methodDeclaration {@link MethodDeclaration}
     * @return Attribut {@link String}
     */
    private String getSignature(MethodDeclaration methodDeclaration) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodDeclaration.getName()).append('(');
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        List<String> parameterList = parameters.stream().map(p -> p.getType().toString()).collect(Collectors.toList());
        stringBuilder.append(String.join(",", parameterList)).append(") : ").append(methodDeclaration.getType().asString());
        return stringBuilder.toString();
    }
}

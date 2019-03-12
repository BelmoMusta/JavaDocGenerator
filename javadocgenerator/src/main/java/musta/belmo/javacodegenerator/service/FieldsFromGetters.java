package musta.belmo.javacodegenerator.service;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import musta.belmo.javacodecore.CodeUtils;

import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FieldsFromGetters extends AbstractJavaCodeGenerator {
    static final String PATH = "D:\\platformsg2_R_64\\workspace\\gk1emikrier_1_4_G56\\gk1emikrier-metier\\src\\main\\java\\fr\\msa\\agora\\gk1emikrier\\metier\\gk1mw5\\dto";

    public CompilationUnit generate(CompilationUnit compilationUnitSrc) {
        CompilationUnit compilationUnit = compilationUnitSrc.clone();
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(aClass -> {
            Predicate<MethodDeclaration> isGet = aMethod -> aMethod.getName().toString().startsWith("get");
            Predicate<MethodDeclaration> isIs = aMethod -> aMethod.getName().toString().startsWith("is");

            reverse(aClass.findAll(MethodDeclaration.class).stream())

                    .filter(isGet)
                    .forEach(aMethod -> {
                        String methodeName = aMethod.getName().toString().substring(3);
                        FieldDeclaration fieldDeclaration = CodeUtils.newField(aMethod.getType(),
                                "a" + methodeName,
                                Modifier.PRIVATE);

                        aClass.getMembers().add(0, fieldDeclaration);
                        ReturnStmt returnStmt = new ReturnStmt(fieldDeclaration.getVariable(0).getNameAsExpression());
                        BlockStmt blockStmt = new BlockStmt();
                        blockStmt.addStatement(returnStmt);
                        aMethod.setBody(blockStmt);
                        aClass.getMethodsByName("set" + methodeName).forEach(setterMethod -> {
                            BlockStmt blockStmt_ = new BlockStmt();
                            Expression assign = new AssignExpr(fieldDeclaration.getVariables().get(0).getNameAsExpression(),
                                    setterMethod.getParameter(0).getNameAsExpression(), AssignExpr.Operator.ASSIGN);
                            blockStmt_.addStatement(assign);
                            setterMethod.setBody(blockStmt_);
                        });
                    });

            reverse(aClass.findAll(MethodDeclaration.class).stream())
                    .filter(isIs)

                    .forEach(aMethod -> {
                        String methodeName = aMethod.getName().toString().substring(2);
                        FieldDeclaration fieldDeclaration = CodeUtils.newField(aMethod.getType(),
                                "a" + methodeName,
                                Modifier.PRIVATE);
                        ReturnStmt returnStmt = new ReturnStmt(fieldDeclaration.getVariable(0).getNameAsExpression());
                        BlockStmt blockStmt = new BlockStmt();
                        blockStmt.addStatement(returnStmt);
                        aMethod.setBody(blockStmt);
                    });
        });

        return compilationUnit;

    }

    private static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }


}

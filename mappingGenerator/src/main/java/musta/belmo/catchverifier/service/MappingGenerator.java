package musta.belmo.catchverifier.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import musta.belmo.catchverifier.beans.TryCatchDescriber;
import musta.belmo.javacodecore.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingGenerator {

    static CompilationUnit createMapper(CompilationUnit source, CompilationUnit destination) {
        String packageDeclaration = "";
        if (source.getPackageDeclaration().isPresent()) {
            packageDeclaration = source.getPackageDeclaration().get().getName().asString();
        }
        CompilationUnit result = new CompilationUnit();
        String srcClassName = source.findFirst(ClassOrInterfaceDeclaration.class)
                .get().getName().asString();
        ClassOrInterfaceDeclaration myClass = result.addClass(srcClassName + "Mapper").setPublic(true);
        MethodDeclaration mapperMethod = myClass.addMethod("map" + srcClassName);
        ClassOrInterfaceType destClassType = new ClassOrInterfaceType();
        ClassOrInterfaceType srcClassType = new ClassOrInterfaceType();
        destClassType.setName(destination.findFirst(ClassOrInterfaceDeclaration.class).get().getName().asString());
        srcClassType.setName(packageDeclaration + "." + srcClassName);
        mapperMethod.setType(destClassType);
        mapperMethod.addModifier(Modifier.PUBLIC);
        Parameter param = new Parameter(srcClassType, "p" + srcClassName);
        param.addModifier(Modifier.FINAL);
        mapperMethod.addParameter(param);
        Optional<BlockStmt> body = mapperMethod.getBody();
        if (body.isPresent()) {
            VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
            VariableDeclarator variableDeclarator = new VariableDeclarator();
            variableDeclarator.setType(destClassType);
            variableDeclarator.setName("l" + Utils.getSimpleClassName(destination.
                    findFirst(ClassOrInterfaceDeclaration.class)
                    .get().getName().asString()));
            variableDeclarationExpr.addVariable(variableDeclarator);
            AssignExpr objectDeclarationStmt = new AssignExpr(variableDeclarationExpr,
                    new NullLiteralExpr(), AssignExpr.Operator.ASSIGN);
            body.get().addStatement(objectDeclarationStmt);
            IfStmt ifStmt = new IfStmt();
            Expression checkNotNullExpression = new BinaryExpr(param.getNameAsExpression(),
                    new NullLiteralExpr(),
                    BinaryExpr.Operator.NOT_EQUALS);
            ifStmt.setCondition(checkNotNullExpression);
            ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(destClassType);
            AssignExpr objectCreationStmt = new AssignExpr(variableDeclarator.getNameAsExpression(),
                    objectCreationExpr, AssignExpr.Operator.ASSIGN);
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.addStatement(objectCreationStmt);
            ifStmt.setThenStmt(blockStmt);
            body.get().addStatement(ifStmt);
            source.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                MethodCallExpr call;
                if (methodDeclaration.getName().asString().startsWith("set")) {
                    call = createCallStmt(methodDeclaration, param, variableDeclarator, true);
                } else if (methodDeclaration.getName().asString().startsWith("get")
                        && (methodDeclaration.getType().asString().contains("List")
                        || methodDeclaration.getType().asString().contains("Collection"))) {
                    call = createCallStmt(methodDeclaration, param, variableDeclarator, false);
                } else {
                    return;
                }
                blockStmt.addStatement(call);
            });
            body.get().addStatement(new ReturnStmt(variableDeclarator.getNameAsExpression()));
        }
        return result;
    }

    private static MethodCallExpr createCallStmt(MethodDeclaration methodDeclaration,
                                                 Parameter param,
                                                 VariableDeclarator variableDeclarator,
                                                 boolean isSetter) {
        MethodCallExpr call = new MethodCallExpr(variableDeclarator.getNameAsExpression(),
                methodDeclaration.getName().asString());
        MethodCallExpr addAllMethod;
        MethodCallExpr retValue;
        String methodGetter = "get" + methodDeclaration.getName().asString().substring(3);
        MethodCallExpr getExpression = new MethodCallExpr(param.getNameAsExpression(), methodGetter);

        if (!isSetter) {
            addAllMethod = new MethodCallExpr(call, "addAll");
            addAllMethod.addArgument(getExpression);
            retValue = addAllMethod;
        } else {
            call.addArgument(getExpression);
            retValue = call;
        }
        return retValue;
    }

}
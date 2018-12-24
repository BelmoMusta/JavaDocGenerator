package musta.belmo.enumhandler.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import musta.belmo.enumhandler.beans.EnumAttribute;
import musta.belmo.enumhandler.beans.EnumDescriber;
import musta.belmo.enumhandler.beans.EnumValueHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * TODO : Compléter la description de cette classe
 */
public class EnumHandler {

    public CompilationUnit createEnum(EnumDescriber enumDescriber) throws Exception {
        CompilationUnit compilationUnit = new CompilationUnit();
        EnumDeclaration enumDeclaration = compilationUnit.addEnum(enumDescriber.getName());
        Map<String, List<EnumValueHolder>> enumEntries = enumDescriber.get();
        enumEntries.forEach((name, value) -> {
            EnumConstantDeclaration declaration = new EnumConstantDeclaration();
            declaration.setName(name);
            for (EnumValueHolder enumValueHolder : value) {
                if (EnumAttribute.STRING.equals(enumValueHolder.getType())) {
                    LiteralExpr expr = new StringLiteralExpr(enumValueHolder.getName());
                    declaration.addArgument(expr);
                } else {
                    declaration.addArgument(enumValueHolder.getName());
                }
            }
            enumDeclaration.addEntry(declaration);
        });
        List<EnumAttribute> enumAttributes = enumDescriber.getEnumAttributes();
        int i = 0;
        for (EnumAttribute enumAttribute : enumAttributes) {
            if (enumAttribute.getName() == null) {
                enumAttribute.setName(String.format("pArg%d", i++));
            }
        }
        for (EnumAttribute enumAttribute : enumAttributes) {
            enumDeclaration.addField(enumAttribute.getConcreteType(), enumAttribute.getName(), Modifier.PRIVATE);
        }
        ConstructorDeclaration constructorDeclaration = enumDeclaration.addConstructor();
        constructorDeclaration.setName(enumDescriber.getName());
        for (EnumAttribute enumAttribute : enumAttributes) {
            constructorDeclaration.addParameter(enumAttribute.getConcreteType(), enumAttribute.getName());
            Expression intialization = new ThisExpr();
            Expression fs = new FieldAccessExpr(intialization, enumAttribute.getName());
            Expression assign = new AssignExpr(fs, new NameExpr(enumAttribute.getName()), AssignExpr.Operator.ASSIGN);
            BlockStmt blockStmt;
            blockStmt = constructorDeclaration.getBody();
            blockStmt.addStatement(assign);
            // constructorDeclaration.setBody(blockStmt);
        }
        for (EnumAttribute enumAttribute : enumAttributes) {
            MethodDeclaration getter = createGetter(enumAttribute);
            enumDeclaration.addMember(getter);
        }
        return compilationUnit;
    }

    private MethodDeclaration createGetter(EnumAttribute attribute) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setType(attribute.getConcreteType());
        methodDeclaration.setName("get" + StringUtils.capitalize(attribute.getName()));
        methodDeclaration.addModifier(Modifier.PUBLIC);
        BlockStmt methodBody = new BlockStmt();
        Expression intialization = new ThisExpr();
        Expression fs = new FieldAccessExpr(intialization, attribute.getName());
        Statement returnStatement = new ReturnStmt(fs);
        methodBody.addStatement(returnStatement);
        methodDeclaration.setBody(methodBody);
        return methodDeclaration;
    }
}

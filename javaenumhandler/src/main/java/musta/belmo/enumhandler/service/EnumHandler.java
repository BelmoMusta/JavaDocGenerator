package musta.belmo.enumhandler.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import musta.belmo.enumhandler.beans.EnumDescriber;
import musta.belmo.enumhandler.beans.EnumAttribute;
import musta.belmo.enumhandler.beans.EnumValueHolder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

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
            enumDeclaration.addField(enumAttribute.getConcreteType(), enumAttribute.getName(),
                    Modifier.PRIVATE);
        }

        ConstructorDeclaration constructorDeclaration = enumDeclaration.addConstructor();
        constructorDeclaration.setName(enumDescriber.getName());


        for (EnumAttribute enumAttribute : enumAttributes) {
            constructorDeclaration.addParameter(enumAttribute.getConcreteType(), enumAttribute.getName());
            Expression intialization = new ThisExpr();
            Expression fs = new FieldAccessExpr(intialization, enumAttribute.getName());
            Expression assign = new AssignExpr(fs, new NameExpr(enumAttribute.getName()),
                    AssignExpr.Operator.ASSIGN);

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


    public MethodDeclaration createGetter(EnumAttribute attribute) {

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

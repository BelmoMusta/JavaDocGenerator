package musta.belmo.javacodecore;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

public class CodeUtils {

    private static final String STRING_BUILDER = "StringBuilder";

    public static ObjectCreationExpr objectCreationExpFromType(final ClassOrInterfaceType destClassType) {
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(destClassType);
        return objectCreationExpr;
    }

    public static VariableDeclarator variableDeclaratorFromType(final ClassOrInterfaceType destClassType) {
        return new VariableDeclarator().
                setType(destClassType);
    }

    public static VariableDeclarator variableDeclaratorFromType(final ClassOrInterfaceType destClassType, String name) {
        return variableDeclaratorFromType(destClassType)
                .setName(name);

    }


    public static VariableDeclarationExpr variableDeclarationExprFromVariable(final VariableDeclarator variableDeclarator) {
        return new VariableDeclarationExpr().
                addVariable(variableDeclarator);

    }

    public static IfStmt createIfStamtement(Expression condition, BlockStmt thenStatement, BlockStmt elseStatement) {
        return new IfStmt()
                .setCondition(condition)
                .setThenStmt(thenStatement)
                .setElseStmt(elseStatement);
    }


    public static boolean isCollectionType(Parameter methodDeclaration) {
        return methodDeclaration != null && isCollectionType(methodDeclaration.getType().asString());
    }

    public static boolean isCollectionType(MethodDeclaration methodDeclaration) {
        return methodDeclaration != null && isCollectionType(methodDeclaration.getType().asString());
    }

    public static AssignExpr createAssignExpression(Expression target, Expression value) {
        return new AssignExpr(target,
                value, AssignExpr.Operator.ASSIGN);
    }

    public static boolean isCollectionType(String methodReturnType) {
        boolean ret;
        int index = StringUtils.indexOf(methodReturnType, "<");
        if (index >= 0) {
            methodReturnType = methodReturnType.substring(0, index);
        }
        try {
            Class clazz = Class.forName("java.util." + methodReturnType);
            ret = java.util.Collection.class.isAssignableFrom(clazz) ||
                    java.util.Map.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            ret = false;
        }
        return ret;
    }

    public static void concatenationToAppend(Expression expression) {
        Expression temp = expression;
        LinkedList<Expression> literals = new LinkedList<>();

        while (temp.isBinaryExpr()) {
            BinaryExpr binaryExpr = temp.asBinaryExpr();
            temp = binaryExpr.getLeft();
            if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS) {
                literals.addFirst(binaryExpr.getRight());
            }
            if (temp.isLiteralExpr()) {
                literals.addFirst(temp);
            }
        }

        ObjectCreationExpr creationExpr = new ObjectCreationExpr();
        creationExpr.setType("StringBuilder");
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarationExpr.addVariable(variableDeclarator);
        variableDeclarator.setName("lStringBuilder");
        variableDeclarator.setType("StringBuilder");


        AssignExpr objectCreationStmt = new AssignExpr(variableDeclarationExpr,
                creationExpr, AssignExpr.Operator.ASSIGN);
        System.out.println(objectCreationStmt);
        MethodCallExpr callStmt = createStringBuilderAppendStmt(literals);
        System.out.println(callStmt);

    }

    private static MethodCallExpr createStringBuilderAppendStmt(LinkedList<Expression> literals) {
        MethodCallExpr call = new MethodCallExpr(new NameExpr(STRING_BUILDER), "append");
        call.addArgument(literals.get(0));

        for (int i = 1; i < literals.size(); i++) {
            call = new MethodCallExpr(call, "append");
            call.addArgument(literals.get(i));
        }
        return call;
    }

    private static boolean isMethodStartsWith(MethodDeclaration methodDeclaration, String prefix) {
        return methodDeclaration != null && methodDeclaration.getName().asString().startsWith(prefix);
    }

    public static boolean isSetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "set");
    }

    public static boolean isGetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "get");
    }

    public static boolean isIs(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "is") && methodDeclaration.getType()
                .toString()
                .equalsIgnoreCase("boolean");
    }
}

package musta.belmo.javacodecore;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.apache.commons.lang3.StringUtils;

public class CodeUtils {

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

    private static boolean isMethodStartsWith(MethodDeclaration methodDeclaration, String prefix) {
        return methodDeclaration != null && methodDeclaration.getName()
                .asString().startsWith(prefix);
    }

    public static boolean isGetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "get");
    }

    public static boolean isSetter(MethodDeclaration methodDeclaration) {
        return isMethodStartsWith(methodDeclaration, "set");

    }

    public static boolean isCollectionType(Parameter methodDeclaration) {
        return methodDeclaration != null && isCollectionType(methodDeclaration.getType().asString());
    }

    public static boolean isCollectionType(MethodDeclaration methodDeclaration) {
        return methodDeclaration != null && isCollectionType(methodDeclaration.getType().asString());
    }

    public static AssignExpr createAssignExpression(Expression target, Expression value) {
     return    new AssignExpr(target,
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
}

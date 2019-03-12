package musta.belmo.javacodecore;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocDescriptionElement;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

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

    public static MethodDeclaration cloneMethod(MethodDeclaration methodDeclaration, boolean isAbstract) {
        final MethodDeclaration lMethodDeclaration = methodDeclaration.clone();

        if (isAbstract) {
            lMethodDeclaration.setBody(null);
            lMethodDeclaration.setPublic(true); // an abstract method should be public in order to be overridden
        }
        return lMethodDeclaration;
    }



    /**
     *
     * @param classOrInterfaceDeclaration
     */
    public static void deletFields(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        classOrInterfaceDeclaration.getMembers().removeIf(member->member instanceof FieldDeclaration);
    }

    public static FieldDeclaration newField(Type type, String name, Modifier... modifiers) {
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        VariableDeclarator variable = new VariableDeclarator(type, name);
        fieldDeclaration.getVariables().add(variable);
        fieldDeclaration.setModifiers(Arrays.stream(modifiers)
                .collect(toCollection(() -> EnumSet.noneOf(Modifier.class))));
        return fieldDeclaration;
    }

    public static Comparator<FieldDeclaration> getFieldComparator() {
        return (o1, o2) -> {
            int compare = getFieldLevel(o2) -
                    getFieldLevel(o1);

            if (compare == 0)
                compare = o1.getVariables().get(0).getName().asString().compareTo(
                        o2.getVariables().get(0).getName().asString());
            return compare;
        };
    }

    public static void cloneFieldDeclaration(FieldDeclaration from, final FieldDeclaration to) {
        to.setModifiers(from.getModifiers());
        to.setVariables(from.getVariables());
        from.getComment().ifPresent((str) -> to.setBlockComment(str.getContent()));
        to.setAnnotations(from.getAnnotations());
    }

    public static int getFieldLevel(FieldDeclaration fieldDeclaration) {

        int level = 0;

        if (fieldDeclaration.isPublic() && fieldDeclaration.isStatic()) {
            level += 100000;
        } else if (fieldDeclaration.isPublic()) {
            level += 20;
        }
        if (fieldDeclaration.isStatic()) {
            level += 10000;
        }
        if (fieldDeclaration.isFinal()) {
            level += 1000;
        }
        if (fieldDeclaration.isProtected()) {
            level += 100;
        }
        if (fieldDeclaration.isPrivate()) {
            level += 10;
        }
        if (fieldDeclaration.isTransient()) {
            level += 1;
        }
        return level;

    }


    public static String removeUnusedFields(CompilationUnit compilationUnit) {
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            Optional<FieldDeclaration> aInstance = classOrInterfaceDeclaration.getFieldByName("aInstance");


            if (!aInstance.isPresent()) {
                aInstance = classOrInterfaceDeclaration.getFieldByName("instance");
            }

            if (aInstance.isPresent()) {
                FieldDeclaration fieldDeclaration = aInstance.get();
                classOrInterfaceDeclaration.remove(fieldDeclaration);
            }

        }
        return compilationUnit.toString();

    }

    public static String removeModifierForFields(CompilationUnit compilationUnit, Modifier modifier) {
        boolean changed = false;
        List<ClassOrInterfaceDeclaration> all = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : all) {
            List<FieldDeclaration> fields = classOrInterfaceDeclaration.findAll(FieldDeclaration.class);

            for (FieldDeclaration field : fields) {
                if (field.getModifiers().contains(modifier)) {
                    changed = true;
                    field.getModifiers().remove(modifier);
                }
            }

        }
        if (changed) {
            return compilationUnit.toString();
        }
        return null;

    }

    public  static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                .mapToObj(i -> temp[temp.length - i - 1]);
    }

    public static String getTypeDefaultValue(PrimitiveType primitiveType) {
        String defaultValue;
        switch (primitiveType.getType()) {

            case BOOLEAN:
                defaultValue = "false";
                break;
            case CHAR:
                defaultValue = "'0'";

                break;
            case BYTE:
                defaultValue = "0";

                break;
            case SHORT:
                defaultValue = "0";

                break;
            case INT:
                defaultValue = "0";

                break;
            case LONG:
                defaultValue = "0L";

                break;
            case FLOAT:
                defaultValue = "0f";
                break;
            case DOUBLE:
            default:
                defaultValue = "0.0d";
                break;
        }
        return defaultValue;
    }
}

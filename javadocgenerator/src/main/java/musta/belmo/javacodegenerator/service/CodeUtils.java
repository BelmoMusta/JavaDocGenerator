package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.printer.YamlPrinter;

import java.util.Comparator;

public class CodeUtils {

    public static Comparator<FieldDeclaration> getFieldComparator() {
        return new Comparator<FieldDeclaration>() {
            @Override
            public int compare(FieldDeclaration o1, FieldDeclaration o2) {
                int compare = getFieldLevel(o2) -
                        getFieldLevel(o1);

                if (compare == 0)
                    compare = o1.getVariables().get(0).getName().asString().compareTo(
                            o2.getVariables().get(0).getName().asString());
                return compare;
            }
        };
    }

    public static Expression creatAssignStement(String  left, String right, AssignExpr.Operator operator){
        return new AssignExpr(new NameExpr(left), new NameExpr(right), operator);

    }

    static public String printAsYaml(String code) {
        YamlPrinter yamlPrinter = new YamlPrinter(true);
        CompilationUnit compilationUnit = JavaParser.parse(code);
        String output = yamlPrinter.output(compilationUnit);
        return output;
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
}

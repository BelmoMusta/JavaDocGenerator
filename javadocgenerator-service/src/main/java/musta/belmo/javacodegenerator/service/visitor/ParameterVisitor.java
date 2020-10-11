package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.javadoc.Javadoc;

import java.util.List;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParameterVisitor extends AbstractCommonVisitor<MethodDeclaration> {
    private static final ParameterVisitor INSTANCE = new ParameterVisitor();

    public static ParameterVisitor getInstance() {
        return INSTANCE;
    }

    @Override
    public void visit(Parameter parameter, MethodDeclaration methodDeclaration) {
        final Javadoc javadoc = getOrCreateJavadoc(methodDeclaration);
        addBlockTag(parameter, javadoc);
        methodDeclaration.setJavadocComment(javadoc);
        super.visit(parameter, methodDeclaration);
    }

    public static void main(String[] args) {


        IntPredicate intPredicate = i -> i % 2 != 0;

        IntStream intStream = IntStream.range(1, 10)

                .filter(intPredicate)
                .map(i -> i * 2);
        List<Integer> result = intStream
                .boxed().collect(Collectors.toList());

        System.out.println(result);


    }
}

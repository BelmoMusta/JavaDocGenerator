package musta.belmo.mappinggenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import java.io.InputStream;

public class InterfaceDeriverTest {

    @Test
    public void testDeriveInterface() {
        InterfaceDeriver interfaceDeriver = new InterfaceDeriver();
        InputStream resourceAsStream = MappingGeneratorTest.class.getClassLoader().getResourceAsStream("Book.java");
        CompilationUnit compilationUnit = interfaceDeriver.deriveInterfaceFromClass(JavaParser.parse(resourceAsStream),"I");
        System.out.println(compilationUnit);


    }

}
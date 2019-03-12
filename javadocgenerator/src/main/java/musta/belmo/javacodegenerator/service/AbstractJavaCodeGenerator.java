package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractJavaCodeGenerator {

    public abstract CompilationUnit generate(CompilationUnit code);

    public CompilationUnit generate(java.io.File file) throws FileNotFoundException {
        return generate(JavaParser.parse(file));
    }

    public CompilationUnit generate(String code) {
        return generate(JavaParser.parse(code));

    }

    public CompilationUnit generate(Path code) throws IOException {
        return generate(JavaParser.parse(code));

    }

}

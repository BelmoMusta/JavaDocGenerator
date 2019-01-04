package musta.belmo.mappinggenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import musta.belmo.javacodecore.CodeUtils;

public class InterfaceDeriver {


    public CompilationUnit deriveInterfaceFromClass(CompilationUnit src, String interfaceName) {
        CompilationUnit lRet = src.clone();

        for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : lRet.findAll(ClassOrInterfaceDeclaration.class)) {
            classOrInterfaceDeclaration.setInterface(true);
            for (MethodDeclaration methodDeclaration : classOrInterfaceDeclaration.findAll(MethodDeclaration.class)) {
                methodDeclaration.setBody(null);
                methodDeclaration.setPublic(false);
            }
            CodeUtils.deletFields(classOrInterfaceDeclaration);
        }
        return lRet;
    }

    public CompilationUnit deriveInterfaceFromClass(String src, String interfaceName) {

        return deriveInterfaceFromClass(JavaParser.parse(src),interfaceName);
    }
}

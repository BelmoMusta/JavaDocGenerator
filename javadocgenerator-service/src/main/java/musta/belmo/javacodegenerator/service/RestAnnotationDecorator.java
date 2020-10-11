package musta.belmo.javacodegenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import musta.belmo.javacodegenerator.service.visitor.MethodAnnotationDeleterVisitor;
import musta.belmo.javacodegenerator.service.visitor.MethodDecoratorVisitor;

import java.io.File;

public class RestAnnotationDecorator {
	private static MethodDecoratorVisitor methodDecoratorVisitor = new MethodDecoratorVisitor();
	private static MethodAnnotationDeleterVisitor annotaionDeleter = new MethodAnnotationDeleterVisitor();
	
	public static void decorateFile(File file) throws Exception {
		
		CompilationUnit compilationUnit = JavaParser.parse(file);
		compilationUnit.accept(annotaionDeleter, compilationUnit);
		compilationUnit.accept(methodDecoratorVisitor, compilationUnit);
		
		System.out.println(compilationUnit);
	}
	
	public static void deleteAnnotations(File file) throws Exception {
		
		CompilationUnit compilationUnit = JavaParser.parse(file);
		compilationUnit.accept(methodDecoratorVisitor, compilationUnit);
		
		System.out.println(compilationUnit);
	}
	
	public static void main(String[] args) throws Exception {
	
	}
}

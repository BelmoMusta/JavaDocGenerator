package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.Iterator;
import java.util.List;

public class MethodAnnotationDeleterVisitor extends AbstractCommonVisitor<CompilationUnit> {
	private static final MethodAnnotationDeleterVisitor INSTANCE = new MethodAnnotationDeleterVisitor();
	
	public static MethodAnnotationDeleterVisitor getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void visit(MethodDeclaration src, CompilationUnit compilationUnit) {
		src.getAnnotations().clear();
		
		super.visit(src, compilationUnit);
	}
}

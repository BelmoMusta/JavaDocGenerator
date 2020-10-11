package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

public class ParamPathVariableVisitor extends AbstractCommonVisitor<MethodDeclaration> {
	private static final ParamPathVariableVisitor INSTANCE = new ParamPathVariableVisitor();
	
	public static ParamPathVariableVisitor getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void visit(Parameter src, MethodDeclaration methodDeclaration) {
		
		
		final MarkerAnnotationExpr getMapping = new MarkerAnnotationExpr();
		getMapping.setName("PathVariable");
		
		src.addAnnotation(getMapping);
		
		super.visit(src, methodDeclaration);
	}
}

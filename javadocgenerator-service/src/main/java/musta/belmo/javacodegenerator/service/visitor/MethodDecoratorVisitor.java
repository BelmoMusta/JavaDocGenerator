package musta.belmo.javacodegenerator.service.visitor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.stream.Collectors;

public class MethodDecoratorVisitor extends AbstractCommonVisitor<CompilationUnit> {
	private static final MethodDecoratorVisitor INSTANCE = new MethodDecoratorVisitor();
	
	public static MethodDecoratorVisitor getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void visit(MethodDeclaration src, CompilationUnit compilationUnit) {
	    
	    /*
	@GetMapping(value = "/recupererLaDeclarationParNumeroDuContrat/{contratId}", produces = "application/json")
    @apiOperation(value = "recupererLaDeclarationParNumeroDuContrat", notes =
    "recupererLaDeclarationParNumeroDuContrat")
    
	     */
		String params = src.getParameters().stream().map(Parameter::getNameAsString)
				.map(s -> "{" + s + "}")
				.collect(Collectors.joining("/"));
		final NormalAnnotationExpr getMapping = new NormalAnnotationExpr();
		getMapping.setName("GetMapping");
		
		getMapping.addPair("value", new NameExpr("\"/" + src.getNameAsString() + "/" + params + "\""));
		getMapping.addPair("produces", new NameExpr("\"application/json\""));
		
		
		final NormalAnnotationExpr apiOperation = new NormalAnnotationExpr();
		apiOperation.setName("ApiOperation");
		apiOperation.addPair("value", new NameExpr("\"" + src.getNameAsString() + " - OPERATION" + "\""));
		apiOperation.addPair("notes", new NameExpr("\"" + src.getNameAsString() + " - NOTES") + "\"");
		
		src.addAnnotation(getMapping);
		src.addAnnotation(apiOperation);
		src.accept(new ParamPathVariableVisitor(), src);
		super.visit(src, compilationUnit);
	}
}

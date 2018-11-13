package musta.belmo.enumhandler.service;

<<<<<<< HEAD
import com.github.javaparser.ast.CompilationUnit;
=======
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
>>>>>>> 563fe9be5a0b6517cae36798ba5b0c1aec8ca239
import musta.belmo.enumhandler.beans.EnumDescriber;
import musta.belmo.enumhandler.beans.EnumAttribute;
import musta.belmo.enumhandler.beans.EnumValueHolder;
import org.junit.Test;

<<<<<<< HEAD
=======
import java.io.File;
import java.util.ArrayList;
import java.util.List;

>>>>>>> 563fe9be5a0b6517cae36798ba5b0c1aec8ca239
public class EnumHandlerTest {

    @Test
    public void testGenerateEnum() throws Exception {

        EnumDescriber enumDescriber = new EnumDescriber("RcoEnum");
        enumDescriber.add(EnumAttribute.STRING);
        enumDescriber.addElement("RCO", new EnumValueHolder("002", EnumAttribute.STRING));
        enumDescriber.addElement("RCB", new EnumValueHolder("003", EnumAttribute.STRING));
        enumDescriber.addElement("RCA", new EnumValueHolder("004", EnumAttribute.STRING));

        EnumHandler enumHandler = new EnumHandler();
        CompilationUnit anEnum = enumHandler.createEnum(enumDescriber);
        System.out.println(anEnum);
    }

<<<<<<< HEAD
=======
    @Test
    public void testReadEnum() throws Exception {
        CompilationUnit compilationUnit = JavaParser.parse(new File("D:\\platformsg2_R_64\\workspace\\movalnsa_2_1_DEFAUT\\movalnsa-metier\\src\\main\\java\\fr\\msa\\agora\\movalnsa\\constante\\TypeRevenu.java"));
        for (EnumDeclaration enumDeclaration : compilationUnit.findAll(EnumDeclaration.class)) {
            for (EnumConstantDeclaration enumConstantDeclaration : enumDeclaration.getEntries()) {
                NodeList<Expression> arguments = enumConstantDeclaration.getArguments();
                List<Expression> newListArguments = new ArrayList<>();
                for (Expression argument : arguments) {
                    LiteralExpr expr = argument.asLiteralExpr();
                    expr.toString();
                    Expression target = new StringLiteralExpr(String.format("%04d",Integer.parseInt(expr.toString().replace("\"",""))));
                 //   System.out.println(target);
                    newListArguments.add(target);
                }
                arguments.clear();
                arguments.addAll(newListArguments);
            }
        }
        System.out.println(compilationUnit);
    }

>>>>>>> 563fe9be5a0b6517cae36798ba5b0c1aec8ca239
}
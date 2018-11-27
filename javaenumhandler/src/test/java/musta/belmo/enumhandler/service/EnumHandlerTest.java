package musta.belmo.enumhandler.service;

import com.github.javaparser.ast.CompilationUnit;
import musta.belmo.enumhandler.beans.EnumAttribute;
import musta.belmo.enumhandler.beans.EnumDescriber;
import musta.belmo.enumhandler.beans.EnumValueHolder;
import org.junit.Test;

public class EnumHandlerTest {

    @Test
    public void testGenerateEnum() throws Exception {

        EnumDescriber enumDescriber = new EnumDescriber("RcoEnum");
        enumDescriber.addElement("RCO", new EnumValueHolder("002", EnumAttribute.STRING));
        enumDescriber.addElement("RCB", new EnumValueHolder("003", EnumAttribute.STRING));
        enumDescriber.addElement("RCA", new EnumValueHolder("004", EnumAttribute.STRING));

        EnumHandler enumHandler = new EnumHandler();
        CompilationUnit anEnum = enumHandler.createEnum(enumDescriber);
        System.out.println(anEnum);
    }
    @Test
    public void testGenerateEnumCase2() throws Exception {

        EnumDescriber enumDescriber = new EnumDescriber("MyEnum");
        enumDescriber.addElement("RCO", new EnumValueHolder("002", new EnumAttribute("value","String")));
        enumDescriber.addElement("RCO", new EnumValueHolder("true", new EnumAttribute("tracked","boolean")));


        EnumHandler enumHandler = new EnumHandler();
        CompilationUnit anEnum = enumHandler.createEnum(enumDescriber);
        System.out.println(anEnum);
    }
}
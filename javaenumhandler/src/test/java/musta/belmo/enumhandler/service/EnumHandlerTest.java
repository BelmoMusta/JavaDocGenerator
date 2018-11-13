package musta.belmo.enumhandler.service;

import com.github.javaparser.ast.CompilationUnit;
import musta.belmo.enumhandler.beans.EnumDescriber;
import musta.belmo.enumhandler.beans.EnumAttribute;
import musta.belmo.enumhandler.beans.EnumValueHolder;
import org.junit.Test;

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

}
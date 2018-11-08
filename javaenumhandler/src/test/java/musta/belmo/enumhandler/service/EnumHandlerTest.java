package musta.belmo.enumhandler.service;

import com.github.javaparser.ast.CompilationUnit;
import musta.belmo.enumhandler.beans.EnumDescriber;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnumHandlerTest {

    @Test
    public void testGenerateEnum() throws Exception {

        EnumDescriber enumDescriber = new EnumDescriber("RcoEnum");
        enumDescriber.setString(true);
        enumDescriber.addElement("RCO", "001");
        enumDescriber.addElement("RCA", "002");
        enumDescriber.addElement("RCB", "003");
        EnumHandler enumHandler = new EnumHandler();
        CompilationUnit anEnum = enumHandler.createEnum(enumDescriber);
        System.out.println(anEnum);
    }

}
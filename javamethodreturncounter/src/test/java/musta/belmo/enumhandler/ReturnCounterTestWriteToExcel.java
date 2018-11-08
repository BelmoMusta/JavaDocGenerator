package musta.belmo.enumhandler;

import musta.belmo.enumhandler.beans.MethodDescriber;
import musta.belmo.enumhandler.service.ReturnCounter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Set;

public class ReturnCounterTestWriteToExcel {
    private Set<MethodDescriber> methodDescribers;

    @Before
    public void init() throws Exception {
        ReturnCounter returnCounter = new ReturnCounter();
        methodDescribers = returnCounter
                .countReturnStatements(getFile("CompilationUnit.java"));
    }

    private File getFile(String fileName) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

    @Test
    public void testCountReturnStatements() throws Exception {
        ReturnCounter returnCounter = new ReturnCounter();
        returnCounter.countReturnStatements(getFile("CompilationUnit.java"), new File("all.xls"));
    }
}
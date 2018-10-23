package musta.belmo.returncounter;

import musta.belmo.returncounter.gui.MethodDescriber;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    public MethodDescriber getMethodDescriberByName(String name) {
        Optional<MethodDescriber> first = methodDescribers.stream().
                filter(methodDescriber -> methodDescriber.getName()
                        .contains(name)).findFirst();
        return first.orElseGet(null);
    }

    @Test
    public void testCountReturnStatements() throws Exception {
        ReturnCounter returnCounter = new ReturnCounter();
        returnCounter.countReturnStatements(getFile("CompilationUnit.java"), new File("all.xls"));
    }
}
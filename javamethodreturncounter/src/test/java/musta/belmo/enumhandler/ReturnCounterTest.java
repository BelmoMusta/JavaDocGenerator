package musta.belmo.enumhandler;

import musta.belmo.enumhandler.beans.MethodDescriber;
import musta.belmo.enumhandler.service.ReturnCounter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReturnCounterTest {
    private Set<MethodDescriber> methodDescribers;

    @Before
    public void init() throws Exception {
        ReturnCounter returnCounter = new ReturnCounter();
        methodDescribers = returnCounter
                .countReturnStatements(getFile("CompilationUnit.java"));

    }

    private java.io.File getFile(String fileName) throws Exception {

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
    public void countZeroReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("zeroReturnMethod");
        assertNotNull(methodDescriber);
        assertEquals(0, methodDescriber.getNbReturns());
    }

    @Test
    public void countOneReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("oneReturnMethod");
        assertEquals(1, methodDescriber.getNbReturns());
    }

    @Test
    public void countTwoReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("twoReturnMethod");
        assertEquals(2, methodDescriber.getNbReturns());
    }

    @Test
    public void countThreeReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("threeReturnMethod");
        assertEquals(3, methodDescriber.getNbReturns());

    }

    @Test
    public void countTwoReturnMethodWithinForLoop() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("twoReturnMethodWithinForLoop");
        assertEquals(2, methodDescriber.getNbReturns());

    }

    @Test
    public void countMethodWithVoid() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("methodWithVoid");
        assertEquals(1, methodDescriber.getNbReturns());

    }

    @Test
    public void countMethodWithVoidWithSpace() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("methodWithVoidWithSpace");
        assertEquals(1, methodDescriber.getNbReturns());

    }

    @Test
    public void countMethodeWithOverridenAnonymousClass() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("methodeWithOverridenAnonymousClass");
        assertEquals(2, methodDescriber.getNbReturns());
    }
}
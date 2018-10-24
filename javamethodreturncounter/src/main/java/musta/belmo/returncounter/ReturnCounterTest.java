package musta.belmo.returncounter;

import musta.belmo.returncounter.beans.MethodDescriber;
import musta.belmo.returncounter.service.ReturnCounter;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO : Compléter la description de cette classe
 */
public class ReturnCounterTest {

    /**
     * L'attribut {@link #methodDescribers}.
     */
    private Set<MethodDescriber> methodDescribers;

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @throws Exception Exception levée si erreur.
     */
    @Before
    public void init() throws Exception {
        ReturnCounter returnCounter = new ReturnCounter();
        methodDescribers = returnCounter.countReturnStatements(getFile("CompilationUnit.java"));
    }

    /**
     * @param fileName {@link String}
     * @return Attribut {@link #file}
     * @throws Exception Exception levée si erreur.
     */
    private File getFile(String fileName) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

    /**
     * @param name {@link String}
     * @return Attribut {@link #methodDescriberByName}
     */
    public MethodDescriber getMethodDescriberByName(String name) {
        Optional<MethodDescriber> first = methodDescribers.stream().filter(methodDescriber -> methodDescriber.getName().contains(name)).findFirst();
        return first.orElseGet(null);
    }

    /**
     * Count zero return method
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countZeroReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("zeroReturnMethod");
        assertNotNull(methodDescriber);
        assertEquals(0, methodDescriber.getNbReturns());
    }

    /**
     * Count one return method
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countOneReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("oneReturnMethod");
        assertEquals(1, methodDescriber.getNbReturns());
    }

    /**
     * Count two return method
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countTwoReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("twoReturnMethod");
        assertEquals(2, methodDescriber.getNbReturns());
    }

    /**
     * Count three return method
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countThreeReturnMethod() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("threeReturnMethod");
        assertEquals(3, methodDescriber.getNbReturns());
    }

    /**
     * Count two return method within for loop
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countTwoReturnMethodWithinForLoop() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("twoReturnMethodWithinForLoop");
        assertEquals(2, methodDescriber.getNbReturns());
    }

    /**
     * Count method with void
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countMethodWithVoid() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("methodWithVoid");
        assertEquals(1, methodDescriber.getNbReturns());
    }

    /**
     * Count method with void with space
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countMethodWithVoidWithSpace() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("methodWithVoidWithSpace");
        assertEquals(1, methodDescriber.getNbReturns());
    }

    /**
     * Count methode with overriden anonymous class
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void countMethodeWithOverridenAnonymousClass() throws Exception {
        MethodDescriber methodDescriber = getMethodDescriberByName("methodeWithOverridenAnonymousClass");
        assertEquals(2, methodDescriber.getNbReturns());
    }
}

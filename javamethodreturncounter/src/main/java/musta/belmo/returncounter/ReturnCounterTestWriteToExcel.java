package musta.belmo.returncounter;

import musta.belmo.returncounter.beans.MethodDescriber;
import musta.belmo.returncounter.service.ReturnCounter;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * TODO : Compléter la description de cette classe
 */
public class ReturnCounterTestWriteToExcel {

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
     * Test count return statements
     *
     * @throws Exception Exception levée si erreur.
     */
    @Test
    public void testCountReturnStatements() throws Exception {
        ReturnCounter returnCounter = new ReturnCounter();
        returnCounter.countReturnStatements(getFile("CompilationUnit.java"), new File("all.xls"));
    }
}

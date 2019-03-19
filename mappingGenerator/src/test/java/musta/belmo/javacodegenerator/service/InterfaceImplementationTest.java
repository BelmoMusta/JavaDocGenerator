package musta.belmo.javacodegenerator.service;

import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.*;

public class InterfaceImplementationTest {

    @Test
    public void testImplementationOfInterface() throws Exception{

        InterfaceImplementation interfaceImplementation = new InterfaceImplementation();

        File file = new File("D:\\platformsg2_R_64\\workspace\\gk1emikrier_1_4_G56\\gk1emikrier-metier\\src\\main\\java\\fr\\msa\\agora\\gk1emikrier\\metier\\gk1mw5\\dto\\EnregistrementSecondaireG56Dto.java");
        CompilationUnit generate = interfaceImplementation.generate(file);

        IOUtils.write(String.valueOf(generate), new FileOutputStream(file), "UTF-8");
        System.out.println(generate);

    }

}
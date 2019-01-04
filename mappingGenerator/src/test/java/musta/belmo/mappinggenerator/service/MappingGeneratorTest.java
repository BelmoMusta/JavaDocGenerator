package musta.belmo.mappinggenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import musta.belmo.javacodecore.CodeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class MappingGeneratorTest {
    @Test
    public void createMapperTest() {
        MappingGenerator mappingGenerator = new MappingGenerator();
        InputStream resourceAsStream = MappingGeneratorTest.class.getClassLoader().getResourceAsStream("Book.java");
        CompilationUnit compilationUnit = JavaParser.parse(resourceAsStream);
        mappingGenerator.setSource(compilationUnit);
        mappingGenerator.setDestinationClassName("BookV2");
        mappingGenerator.setDestinationPackage("logic.book");
        mappingGenerator.setMappingMethodPrefix("map");
        mappingGenerator.setMapperClassPrefix("Mapper");
        mappingGenerator.setStaticMethod(true);
        mappingGenerator.setAccessCollectionByGetter(true);
        mappingGenerator.mapField("title", "titre");
        mappingGenerator.createMapper();

        CompilationUnit bookMapper = mappingGenerator.getResult();
        System.out.println(bookMapper);

    }

    @Test
    public void testIsCollectionType() {

        CodeUtils.isCollectionType("List");
        Assert.assertTrue(CodeUtils.isCollectionType("List"));
        Assert.assertTrue(CodeUtils.isCollectionType("List<OfSth>"));
        Assert.assertTrue(CodeUtils.isCollectionType("Collection"));
        Assert.assertTrue(CodeUtils.isCollectionType("ArrayList"));
        Assert.assertTrue(CodeUtils.isCollectionType("LinkedList"));
        Assert.assertTrue(CodeUtils.isCollectionType("Map"));
        Assert.assertTrue(CodeUtils.isCollectionType("Map<K,V>"));
        Assert.assertTrue(CodeUtils.isCollectionType("SortedMap"));
//
        Assert.assertFalse(CodeUtils.isCollectionType("String"));
        Assert.assertFalse(CodeUtils.isCollectionType("Object"));

    }

}
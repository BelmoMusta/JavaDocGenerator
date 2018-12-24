package musta.belmo.mappinggenerator.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
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

        mappingGenerator.createMapperV2();
        mappingGenerator.addJavaDocToResult();
        CompilationUnit bookMapper = mappingGenerator.getResult();
        System.out.println(bookMapper);

    }

    @Test
    public void testIsCollectionType() {

        MappingGenerator.isCollectionType("List");
        Assert.assertTrue(MappingGenerator.isCollectionType("List"));
        Assert.assertTrue(MappingGenerator.isCollectionType("List<OfSth>"));
        Assert.assertTrue(MappingGenerator.isCollectionType("Collection"));
        Assert.assertTrue(MappingGenerator.isCollectionType("ArrayList"));
        Assert.assertTrue(MappingGenerator.isCollectionType("LinkedList"));
        Assert.assertTrue(MappingGenerator.isCollectionType("Map"));
        Assert.assertTrue(MappingGenerator.isCollectionType("Map<K,V>"));
        Assert.assertTrue(MappingGenerator.isCollectionType("SortedMap"));
//
        Assert.assertFalse(MappingGenerator.isCollectionType("String"));
        Assert.assertFalse(MappingGenerator.isCollectionType("Object"));

    }

}
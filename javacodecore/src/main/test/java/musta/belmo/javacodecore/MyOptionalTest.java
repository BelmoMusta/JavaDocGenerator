package musta.belmo.javacodecore;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

public class MyOptionalTest {

    @Test
    public void testOrElseIfPredicate() {
        MyOptional<String> value = MyOptional.of("123");
        String expected = "OTHER";
        // if the a value is numeric return "OTHER"
        String actual = value.orElseIfPredicate(expected, StringUtils::isNumeric);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEmpty() {
        MyOptional<String> value = MyOptional.empty();
        String expected = "OTHER";
        // if the a value is null return "OTHER"
        String actual = value.orElseIfPredicate(expected, Objects::isNull);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testOfNullable() {
        MyOptional<String> value = MyOptional.ofNullable(null);
        String expected = "OTHER";
        // if the a value is null return "OTHER"
        String actual = value.orElseIfPredicate(expected, Objects::isNull);
        Assert.assertEquals(expected, actual);
    }
}

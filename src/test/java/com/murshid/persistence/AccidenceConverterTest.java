package com.murshid.persistence;

import com.google.common.collect.Lists;
import com.murshid.models.Accidence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class AccidenceConverterTest {

    @Test
    public void convertToDatabaseColumn() throws Exception {
        AccidenceConverter accidenceConverter = new AccidenceConverter();

        List<Accidence> testList= Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL_NUMBER);
        Object dbColumn = accidenceConverter.convertToDatabaseColumn(testList);

        assertNotNull(dbColumn);
        assertTrue(dbColumn instanceof String);
        assertEquals(dbColumn, "[MASCULINE] [PLURAL_NUMBER]");

    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        AccidenceConverter accidenceConverter = new AccidenceConverter();

        String testString = "[MASCULINE] [PLURAL_NUMBER]";
        Object appValue = accidenceConverter.convertToEntityAttribute(testString);

        List<Accidence> testList= Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL_NUMBER);

        assertNotNull(appValue);
        assertTrue(appValue instanceof List);
        assertEquals(appValue, testList);
    }

}

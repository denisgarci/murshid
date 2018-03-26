package com.murshid.persistence;

import com.google.common.collect.Lists;
import com.murshid.models.enums.Accidence;
import com.murshid.mysql.repo.AccidenceConverter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class AccidenceConverterTest {

    @Test
    public void convertToDatabaseColumn() throws Exception {
        AccidenceConverter accidenceConverter = new AccidenceConverter();

        List<Accidence> testList= Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL);
        Object dbColumn = accidenceConverter.convertToDatabaseColumn(testList);

        assertNotNull(dbColumn);
        assertTrue(dbColumn instanceof String);
        assertEquals(dbColumn, "[MASCULINE] [PLURAL]");

    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        AccidenceConverter accidenceConverter = new AccidenceConverter();

        String testString = "[MASCULINE] [PLURAL]";
        Object appValue = accidenceConverter.convertToEntityAttribute(testString);

        List<Accidence> testList= Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL);

        assertNotNull(appValue);
        assertTrue(appValue instanceof List);
        assertEquals(appValue, testList);
    }

}

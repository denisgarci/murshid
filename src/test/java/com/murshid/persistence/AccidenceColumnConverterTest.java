package com.murshid.persistence;

import com.google.common.collect.Lists;
import com.murshid.models.enums.Accidence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class AccidenceColumnConverterTest {

    @Test
    public void convertToDatabaseColumn() throws Exception {
        AccidenceColumnConverter accidenceConverter = new AccidenceColumnConverter();

        List<Accidence> testList= Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL);
        Object dbColumn = accidenceConverter.convertToDatabaseColumn(testList);

        assertNotNull(dbColumn);
        assertTrue(dbColumn instanceof String);
        assertEquals(dbColumn, "[MASCULINE] [PLURAL]");

    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        AccidenceColumnConverter accidenceConverter = new AccidenceColumnConverter();
        String testString = "[MASCULINE] [PLURAL]";
        Object appValue = accidenceConverter.convertToEntityAttribute(testString);

        List<Accidence> testList= Lists.newArrayList(Accidence.MASCULINE, Accidence.PLURAL);

        assertNotNull(appValue);
        assertTrue(appValue instanceof List);
        assertEquals(appValue, testList);
    }

}

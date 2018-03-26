package com.murshid.mysql.repo;

import com.murshid.models.enums.Accidence;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Converter
public class AccidenceConverter implements AttributeConverter<List<Accidence>, String>{

    @Override
    public String convertToDatabaseColumn(List<Accidence> appData) {
        if (appData == null){
            return null;
        }

        if (appData.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        appData.forEach(acc -> sb.append("[" + acc.name() + "] "));
        return sb.delete(sb.length() -1, sb.length()).toString();
    }

    @Override
    public List<Accidence> convertToEntityAttribute(String appData) {
        if (appData == null){
            return null;
        }

        Pattern p = Pattern.compile("\\[(.*?)\\]"); //locates all tokens between []
        Matcher m = p.matcher(appData);

        List<Accidence> result = new ArrayList<>();
        while(m.find()) {
            result.add(Accidence.valueOf(m.group(1)));
        }
        return result;
    }
}

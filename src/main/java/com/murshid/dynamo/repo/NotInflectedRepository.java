package com.murshid.dynamo.repo;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.murshid.dynamo.domain.NotInflected;
import com.murshid.models.converters.DynamoAccessor;
import com.murshid.models.converters.NotInflectedConverter;

import javax.inject.Named;
import java.util.Iterator;
import java.util.Optional;

@Named
public class NotInflectedRepository {

    public Optional<NotInflected> findOne(String hindi, int hindiIndex){
        Table table = DynamoAccessor.dynamoDB.getTable("not_inflected");

        PrimaryKey primaryKey = new PrimaryKey().addComponent("hindi", hindi)
                .addComponent("hindi_index", hindiIndex);
        Item item = table.getItem(primaryKey);
        if (item == null){
            return Optional.empty();
        }else{
            return Optional.of(NotInflectedConverter.convert(item));
        }
    }

    public Iterator<Item> findByHindi(String hindi){
        Table table = DynamoAccessor.dynamoDB.getTable("not_inflected");

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("hindi = :v_param")
                .withValueMap(new ValueMap().withString(":v_param", hindi));

        ItemCollection<QueryOutcome> items = table.query(spec);
        Iterator<Item> iter = items.iterator();
        return iter;
    }

    public void save(NotInflected notInflected){
        Table table = DynamoAccessor.dynamoDB.getTable("not_inflected");
        Item item = NotInflectedConverter.convert(notInflected);
        table.putItem(item);
    }

}

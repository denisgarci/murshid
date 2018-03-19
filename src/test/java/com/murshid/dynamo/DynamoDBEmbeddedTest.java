package com.murshid.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class demonstrates how to use DynamoDBEmbedded in JUnit tests
 *
 * @author Alexander Pivovarov
 */
public class DynamoDBEmbeddedTest {

    @Test
    public void createTableTest() {
        AmazonDynamoDB ddb = DynamoDBEmbedded.create().amazonDynamoDB();
        try {
            String tableName = "Movies";
            String hashKeyName = "film_id";
            CreateTableResult res = createTable(ddb, tableName, hashKeyName);

            TableDescription tableDesc = res.getTableDescription();
            assertEquals(tableName, tableDesc.getTableName());
            assertEquals("[{AttributeName: " + hashKeyName + ",KeyType: HASH}]", tableDesc.getKeySchema().toString());
            assertEquals("[{AttributeName: " + hashKeyName + ",AttributeType: S}]",
                         tableDesc.getAttributeDefinitions().toString());
            assertEquals(Long.valueOf(1000L), tableDesc.getProvisionedThroughput().getReadCapacityUnits());
            assertEquals(Long.valueOf(1000L), tableDesc.getProvisionedThroughput().getWriteCapacityUnits());
            assertEquals("ACTIVE", tableDesc.getTableStatus());
            assertEquals("arn:aws:dynamodb:ddblocal:000000000000:table/Movies", tableDesc.getTableArn());

            ListTablesResult tables = ddb.listTables();
            assertEquals(1, tables.getTableNames().size());
        } finally {
            ddb.shutdown();
        }
    }

    private static CreateTableResult createTable(AmazonDynamoDB ddb, String tableName, String hashKeyName) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S));

        List<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
        ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH));

        ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);

        CreateTableRequest request =
                new CreateTableRequest()
                        .withTableName(tableName)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withKeySchema(ks)
                        .withProvisionedThroughput(provisionedthroughput);

        return ddb.createTable(request);
    }
}

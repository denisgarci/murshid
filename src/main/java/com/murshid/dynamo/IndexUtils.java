package com.murshid.dynamo;

import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.murshid.models.converters.DynamoAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to create / delete indexes
 */
@SuppressWarnings("unused")
public class IndexUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexUtils.class);

    @SuppressWarnings("unused")
    public static void createIndex() throws InterruptedException{
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        Index index = table. createGSI(
                new CreateGlobalSecondaryIndexAction()
                        .withIndexName("idx-master_dictionary_id")
                        .withKeySchema(
                                new KeySchemaElement("master_dictionary_id", KeyType.HASH))
                        .withProvisionedThroughput(
                                new ProvisionedThroughput(3L, 3L))
                        .withProjection(
                                new Projection()
                                        .withProjectionType(ProjectionType.KEYS_ONLY)),
                new AttributeDefinition("master_dictionary_id",
                        ScalarAttributeType.N));
        index.waitForActive();
        LOGGER.info("index created");

    }

    @SuppressWarnings("unused")
    public static void deleteIndex() {
        Table table = DynamoAccessor.dynamoDB.getTable("inflected");

        Index index = table.getIndex ("idx-master_dictionary_id");
        index.deleteGSI();
        LOGGER.info("index deleted");

    }
}

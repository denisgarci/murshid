package com.murshid.dynamo;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyTest {

    @Test
    public void useDB() throws Exception {
        AWSCredentials awsCredentials = new BasicAWSCredentials("key1", "key2");
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(awsCredentials);

        amazonDynamoDB.setEndpoint("http://localhost:8000/");

        ListTablesResult result = amazonDynamoDB.listTables();

        assertNotNull(result);
    }
}

package com.murshid.models.converters;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoAccessor {

    public static DynamoDB dynamoDB;

    public static AmazonDynamoDB client;

    static {
        AWSCredentials awsCredentials = new BasicAWSCredentials("key1", "key2");
        client = new AmazonDynamoDBClient(awsCredentials);
        client.setEndpoint("http://localhost:8000/");
        dynamoDB = new DynamoDB(client);
    }


}

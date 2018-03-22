package com.murshid.dynamo;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoAccessor {

    public static DynamoDB dynamoDB;

    static {
        AWSCredentials awsCredentials = new BasicAWSCredentials("key1", "key2");
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(awsCredentials);
        amazonDynamoDB.setEndpoint("http://localhost:8000/");
        dynamoDB = new DynamoDB(amazonDynamoDB);
    }


}

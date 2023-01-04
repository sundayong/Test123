//package com.sundayong;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.ServerApi;
//import com.mongodb.ServerApiVersion;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.MongoIterable;
//
//public class MongodbDemo {
//
//    public static void main(String[] args) {
//
//
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://sundayong:sundayong123@cluster0.z0uyl8g.mongodb.net/?retryWrites=true&w=majority");
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .serverApi(ServerApi.builder()
//                        .version(ServerApiVersion.V1)
//                        .build())
//                .build();
//        MongoClient mongoClient = MongoClients.create(settings);
//        MongoDatabase database = mongoClient.getDatabase("test");
//        MongoIterable<String> collectionNames = database.listCollectionNames();
//
//        database.createCollection("test");
//
//        for (String collectionName : collectionNames) {
//            System.out.println(collectionName);
//        }
//
//    }
//
//
//}

//package com.sundayong;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//
//import static com.mongodb.client.model.Filters.eq;
//
//public class MongodbConnectDemo {
//
//
//    public static void main( String[] args ) {
//        // Replace the uri string with your MongoDB deployment's connection string
//        String uri = "mongodb+srv://sundayong:sundayong123@cluster0.z0uyl8g.mongodb.net/?retryWrites=true&w=majority";
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
//            MongoCollection<Document> collection = database.getCollection("movies");
//            Document doc = collection.find(eq("title", "Back to the Future")).first();
//            System.out.println(doc.toJson());
//        }
//    }
//}

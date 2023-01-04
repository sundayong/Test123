//package com.sundayong;
//
//import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
//import static com.mongodb.client.model.Filters.eq;
//import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
//import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
//
//import org.bson.codecs.configuration.CodecProvider;
//import org.bson.codecs.configuration.CodecRegistry;
//import org.bson.codecs.pojo.PojoCodecProvider;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//public class QuickStartPojoExample {
//
//    public static void main(String[] args) {
//        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
//        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
//
//        // Replace the uri string with your MongoDB deployment's connection string
//        String uri = "mongodb+srv://sundayong:sundayong123@cluster0.z0uyl8g.mongodb.net/?retryWrites=true&w=majority";
//
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            MongoDatabase database = mongoClient.getDatabase("sample_mflix").withCodecRegistry(pojoCodecRegistry);
//            MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
//
//            Movie movie = collection.find(eq("title", "Back to the Future")).first();
//            System.out.println(movie);
//        }
//    }
//}
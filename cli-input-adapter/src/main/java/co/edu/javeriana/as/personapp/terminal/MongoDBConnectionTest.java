package co.edu.javeriana.as.personapp.terminal;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnectionTest {

    public static void main(String[] args) {
        System.out.println("Starting MongoDB connection test...");

        try {
            // Define credentials and connection details
            String username = "persona_db";
            String password = "persona_db";
            String authDb = "persona_db"; // Authentication database
            String host = "localhost";
            int port = 27017;

            // Create connection string with authentication
            ConnectionString connectionString = new ConnectionString(
                    "mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=" + authDb);

            // Configure MongoDB client settings
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();

            // Create MongoClient
            try (MongoClient mongoClient = MongoClients.create(settings)) {
                System.out.println("Connection to MongoDB successful!");

                // List databases
                System.out.println("\nAvailable databases:");
                mongoClient.listDatabaseNames().forEach(System.out::println);

                // List collections in persona_db
                MongoDatabase personaDb = mongoClient.getDatabase("persona_db");
                System.out.println("\nCollections in persona_db:");
                personaDb.listCollectionNames().forEach(System.out::println);

                // Count documents in collections
                System.out.println("\nDocument counts:");
                
                MongoCollection<Document> personaCollection = personaDb.getCollection("persona");
                System.out.println("persona: " + personaCollection.countDocuments());
                
                MongoCollection<Document> profesionCollection = personaDb.getCollection("profesion");
                System.out.println("profesion: " + profesionCollection.countDocuments());
                
                MongoCollection<Document> estudiosCollection = personaDb.getCollection("estudios");
                System.out.println("estudios: " + estudiosCollection.countDocuments());
                
                MongoCollection<Document> telefonoCollection = personaDb.getCollection("telefono");
                System.out.println("telefono: " + telefonoCollection.countDocuments());
            }
        } catch (Exception e) {
            System.err.println("MongoDB connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nTest completed!");
    }
} 
package co.edu.javeriana.as.personapp.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
// import com.mongodb.MongoCredential; // No longer needed
// import com.mongodb.ServerAddress; // No longer needed
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

// import java.util.Collections; // No longer needed

@Configuration
@EnableMongoRepositories(basePackages = "co.edu.javeriana.as.personapp.mongo.repository")
public class MongoConfig {
    
    // Try to use URI if directly provided
    @Value("${spring.data.mongodb.uri:}")
    private String mongoUri;
    
    // Individual connection parameters
    @Value("${spring.data.mongodb.host:localhost}")
    private String host;
    
    @Value("${spring.data.mongodb.port:27017}")
    private int port;
    
    @Value("${spring.data.mongodb.database:persona_db}")
    private String database;
    
    @Value("${spring.data.mongodb.username:}")
    private String username;
    
    @Value("${spring.data.mongodb.password:}")
    private String password;
    
    @Value("${spring.data.mongodb.authentication-database:admin}")
    private String authenticationDatabase;
    
    @Bean
    public MongoClient mongoClient() {
        String connectionString;
        
        // If a URI is explicitly provided, use it
        if (mongoUri != null && !mongoUri.trim().isEmpty()) {
            connectionString = mongoUri;
        } else {
            // Otherwise, build URI from individual properties
            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s", 
                    username, password, host, port, database, authenticationDatabase);
            } else {
                // Without authentication
                connectionString = String.format("mongodb://%s:%d/%s", host, port, database);
            }
        }
        
        System.out.println("Using MongoDB connection URI for MongoClient: " + connectionString);
        ConnectionString connString = new ConnectionString(connectionString);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .build();
        return MongoClients.create(mongoClientSettings);
    }
    
    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        // The database name from the URI will be used if not specified otherwise.
        // However, SimpleMongoClientDatabaseFactory constructor requires a database name.
        // If the URI contains the database, it's often parsed by the factory.
        // Explicitly passing `database` ensures it uses the @Value injected one.
        return new SimpleMongoClientDatabaseFactory(mongoClient(), database);
    }
    
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }
} 
package yuio;

import org.bson.Document;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.LogManager;

public class QuestionPaperGenerator {
    public static void main(String[] args) {
        // Disable MongoDB driver logging
        LogManager.getLogManager().reset();

        // Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter the number of questions for each category
        System.out.print("Enter the number of easy questions: ");
        int easyCount = scanner.nextInt();

        System.out.print("Enter the number of medium questions: ");
        int mediumCount = scanner.nextInt();

        System.out.print("Enter the number of hard questions: ");
        int hardCount = scanner.nextInt();

        // Replace the connection string with your MongoDB Atlas connection string
        String connectionString = "mongodb+srv://rvcpg:rvcpg@rvk1.oi5nl.mongodb.net/?retryWrites=true&w=majority";

        // Create a connection string object
        ConnectionString connString = new ConnectionString(connectionString);

        // Create MongoClientSettings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();

        // Create MongoClient
        MongoClient mongoClient = MongoClients.create(settings);

        // Replace "<your-database-name>" with your actual database name
        String databaseName = "qp";

        // Get the database
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        // Replace "<your-collection-name>" with your actual collection name
        String collectionName = "questions";

        // Get the collection
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // Randomly select questions from the database based on the category counts
        List<Document> easyQuestions = getRandomQuestions(collection, "easy", easyCount);
        List<Document> mediumQuestions = getRandomQuestions(collection, "medium", mediumCount);
        List<Document> hardQuestions = getRandomQuestions(collection, "hard", hardCount);

        // Print the retrieved questions
        System.out.println("\nEasy Questions:");
        for (Document document : easyQuestions) {
            System.out.println(document.getString("question"));
        }

        System.out.println("\nMedium Questions:");
        for (Document document : mediumQuestions) {
            System.out.println(document.getString("question"));
        }

        System.out.println("\nHard Questions:");
        for (Document document : hardQuestions) {
            System.out.println(document.getString("question"));
        }
        
        System.out.println("\nMade by Ravi");

        // Close the MongoClient
        mongoClient.close();

        // Close the scanner
        scanner.close();
    }

    private static List<Document> getRandomQuestions(MongoCollection<Document> collection, String category, int count) {
        // Perform a random sampling of questions from the specified category
        List<Document> sampledQuestions = new ArrayList<>();

        // Create an aggregation pipeline to randomly sample the documents
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", new Document("category", category)));
        pipeline.add(new Document("$sample", new Document("size", count)));

        // Execute the aggregation pipeline
        collection.aggregate(pipeline).into(sampledQuestions);

        return sampledQuestions;
    }
}

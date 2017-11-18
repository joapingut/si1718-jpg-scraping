package web.scraping.jsoup;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DataBaseConnector {
	
	private static MongoCollection<Document> collection;
	private static MongoClient mongoClient;
	
	public static void openConnection() {
		MongoClientURI connectionString = new MongoClientURI("mongodb");
		MongoClient mongoClient = new MongoClient(connectionString);
		MongoDatabase database = mongoClient.getDatabase("si1718-jpg-publications");
		MongoCollection<Document> collection = database.getCollection("articles");
		DataBaseConnector.mongoClient = mongoClient;
		DataBaseConnector.collection = collection;
	}
	
	public static void closeConnection() {
		mongoClient.close();
	}
	
	public static void insertListOfArticles(List<Article> articlesList) {
		if (collection == null) {
			openConnection();
		}
		List<Document> insertList = new ArrayList<Document>();
		for(Article art:articlesList) {
			insertList.add(Article.articleToDocument(art));
		}
		if (insertList != null && !insertList.isEmpty()) {
			collection.insertMany(insertList);
		}
	}

}

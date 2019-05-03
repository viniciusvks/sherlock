package dev.viniciusvks.sherlock;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.viniciusvks.sherlock.document.Document;
import dev.viniciusvks.sherlock.document.DocumentException;
import dev.viniciusvks.sherlock.search.SearchResponse;
import dev.viniciusvks.sherlock.search.SearchResult;
import dev.viniciusvks.sherlock.search.cse.CseSearchClient;

public class App {
	
	private static final Logger log = LogManager.getLogger(App.class);
	private static final String TEST_FILE_RELATIVE_PATH = "/src/main/resources/test-file.pdf";
	private static final String PROPERTIES_FILE_RELATIVE_PATH = "/src/main/resources/app.properties";
	
	public static void main(String[] args) {
		
		Properties properties = loadProperties();
		if(properties == null) {
			return;
		}
		
		String API_KEY = properties.getProperty("key");
		String API_CX = properties.getProperty("cx");
		
		CseSearchClient searchClient = new CseSearchClient()
				.setKey(API_KEY)
				.setCx(API_CX);
		
		String filePath = System.getProperty("user.dir") + TEST_FILE_RELATIVE_PATH;
		
		log.info("Loading file: {}", filePath);
		
		try (Document doc = new Document(filePath)){
			
			String query = doc.getPage(10);
			String[] queryLines = query.split("\n");
			query = String.join(" ", new String[] { queryLines[1], queryLines[2],  queryLines[3]});
			
			log.info("Searching query: "+query);
			
			SearchResponse searchResponse = searchClient.search(query);
			
			log.info("Search finished. Total results: " + searchResponse.getTotalResults());			
			logSearchResults(searchResponse.getResults());
			
		} catch (DocumentException | IOException e) {
			log.error(e.getMessage());
		}
		
	}
	
	private static Properties loadProperties() {
		
		try {
			
			String rootPath = System.getProperty("user.dir");
			String appConfigPath = rootPath + PROPERTIES_FILE_RELATIVE_PATH;
			
			Properties appProps = new Properties();
			appProps.load(new FileInputStream(appConfigPath));
			
			return appProps;
			
		} catch (IOException e) {
			log.error("error loading app properties file: " + e.getMessage());
			return null;
		}
	
	}
	
	private static void logSearchResults(List<SearchResult> results) {
		
		log.debug("+----------------------------------------------------------------------------------------------------------");
		
		results.forEach(result -> {
			
			String[] snippetLines = result.getSnippet().split("\n");
			log.debug("| link: " + result.getLink());
			log.debug("| snippet: ");
			for(int i = 0; i < snippetLines.length; i++) {
				log.debug("| "+snippetLines[i]);
			}
			log.debug("+----------------------------------------------------------------------------------------------------------");
		});
		
	}
	
}

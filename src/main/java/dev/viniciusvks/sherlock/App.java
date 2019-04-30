package dev.viniciusvks.sherlock;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import dev.viniciusvks.sherlock.search.SearchResponse;
import dev.viniciusvks.sherlock.search.SearchResult;
import dev.viniciusvks.sherlock.search.cse.CseSearchClient;

public class App {
	
	private static final String PROPERTIES_FILE_RELATIVE_PATH = "/src/main/resources/app.properties";
	
	public static void main(String[] args) {
		
		String query = "O aprendizado automático ou a aprendizagem automática ou também aprendizado de máquina ou "
				+ "aprendizagem de máquina é um subcampo da ciência da computação que evoluiu do estudo de reconhecimento de padrões e "
				+ "da teoria do aprendizado computacional em inteligência artificial.";
		
		Properties properties = null;
		
		try {
			properties = loadProperties();
		} catch (IOException e) {
			System.out.println("error loading app properties file: " + e.getMessage());
			return;
		}
		
		String API_KEY = properties.getProperty("key");
		String API_CX = properties.getProperty("cx");
		
		SearchResponse searchResponse = new CseSearchClient()
				.setKey(API_KEY)
				.setCx(API_CX)
				.search(query);
		
		System.out.println("Total Results: "+searchResponse.getTotalResults());
		logSearchResults(searchResponse.getResults());
		
	}
	
	private static Properties loadProperties() throws FileNotFoundException, IOException {
		
		String rootPath = System.getProperty("user.dir");
		String appConfigPath = rootPath + PROPERTIES_FILE_RELATIVE_PATH;
		
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));
		
		return appProps;
		
	}
	
	private static void logSearchResults(List<SearchResult> results) {
		
		results.forEach(result -> {
			
			String[] snippetLines = result.getSnippet().split("\n");
			System.out.println("| link: " + result.getLink());
			System.out.println("| snippet: ");
			for(int i = 0; i < snippetLines.length; i++) {
				System.out.println("| "+snippetLines[i]);
			}
			System.out.println("+----------------------------------------------------------------------------------------------------------");
		});
		
	}
	
}

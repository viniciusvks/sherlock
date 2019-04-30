package dev.viniciusvks.sherlock.search.scraper;

import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.viniciusvks.sherlock.search.SearchClient;
import dev.viniciusvks.sherlock.search.SearchResponse;
import dev.viniciusvks.sherlock.search.SearchResult;

public class ScraperSearchClient implements SearchClient {
	
	private static final Logger log = LogManager.getLogger(ScraperSearchClient.class);

	@Override
	public SearchResponse search(String query) {
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		SearchResponse searchResponse = new SearchResponse();
		ScraperSearch search = new ScraperSearch(query);
		
		for(search.execute();  !search.hasErrors() && search.hasNextPage(); search.nextPage()) {
			searchResults.addAll(search.getResults());
		}
		
		log.info("Search finished.");
		
		if(search.hasErrors()) {
			searchResponse.setErrors(search.getErrors());
		}
		
		searchResults.addAll(search.getResults());
		searchResponse.setResults(searchResults);
		return searchResponse;
	}

}

package dev.viniciusvks.sherlock.search.cse;

import java.util.ArrayList;

import dev.viniciusvks.sherlock.search.SearchClient;
import dev.viniciusvks.sherlock.search.SearchResponse;
import dev.viniciusvks.sherlock.search.SearchResult;

public class CseSearchClient implements SearchClient {
	
	private String key;
	private String cx;
	
	public CseSearchClient setKey(String key) {
		this.key = key;
		return this;
	}
	
	public CseSearchClient setCx(String cx) {
		this.cx = cx;
		return this;
	}
	
	public SearchResponse search(String query) {
		
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		SearchResponse searchResponse = new SearchResponse();
		CseSearch search = new CseSearch(query);
		
		search.setKey(key)
			  .setCx(cx);
		
		for(search.execute(); search.hasNextPage(); search.nextPage()) {
			searchResults.addAll(search.getResults());
		}
		
		if(search.hasErrors()) {
			searchResponse.setErrors(search.getErrors());
		}
		
		searchResults.addAll(search.getResults());
		searchResponse.setResults(searchResults);
		return searchResponse;
		
	}
		
}

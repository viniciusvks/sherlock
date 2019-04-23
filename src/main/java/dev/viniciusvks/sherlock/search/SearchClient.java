package dev.viniciusvks.sherlock.search;

import java.util.ArrayList;

public class SearchClient {
	
	private String key;
	private String cx;
	
	public SearchClient setKey(String key) {
		this.key = key;
		return this;
	}
	
	public SearchClient setCx(String cx) {
		this.cx = cx;
		return this;
	}
	
	public SearchResponse search(String query) {
		
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		SearchResponse searchResponse = new SearchResponse();
		Search search = new Search(query);
		
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

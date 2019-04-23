package dev.viniciusvks.sherlock.search;

import java.util.ArrayList;

public class SearchResponse {
	
	private Integer totalResults;
	private ArrayList<SearchResult> searchResults = new ArrayList<>();
	private SearchError searchErrors;
	
	public Integer getTotalResults() {
		return totalResults;
	}
	
	public ArrayList<SearchResult> getResults() {
		return searchResults;
	}
	
	public void setResults(ArrayList<SearchResult> searchResults) {
		this.searchResults = searchResults;
		totalResults = searchResults.size();
	}
	
	public boolean hasErrors() {
		return searchErrors != null;
	}
	
	public SearchError getErrors() {
		return searchErrors;
	}
	
	public void setErrors(SearchError searchErrors) {
		this.searchErrors = searchErrors;
	}

}

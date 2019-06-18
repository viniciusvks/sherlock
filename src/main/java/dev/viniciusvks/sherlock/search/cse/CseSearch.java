package dev.viniciusvks.sherlock.search.cse;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse.List;
import com.google.api.services.customsearch.model.Query;
import com.google.gson.Gson;

import dev.viniciusvks.sherlock.search.Search;
import dev.viniciusvks.sherlock.search.SearchError;
import dev.viniciusvks.sherlock.search.SearchResult;

public class CseSearch implements Search {
	
	private static final Logger log = LogManager.getLogger(CseSearch.class);

	private Customsearch customSearch;
	private Long pageIndex = 1L;
	 
	private ArrayList<SearchResult> results;
	private SearchError errors;
	private List list;
	private Query nextPage;
	
	public CseSearch(String query, Customsearch customSearch) {
		
		log.info("New search query: {}", query);

		this.customSearch = customSearch;
		results = new ArrayList<SearchResult>();
		
		try {
			
			list = this.customSearch.cse().list(query);
						
		} catch (IOException e) {
			log.error("Error instantiating search object: {}", e.getMessage());
			clearData();
			extractSearchErrors(e);
			nextPage = null;
		}
	}
	
	public CseSearch(String query) {
		this(query, new Customsearch(new NetHttpTransport(), new JacksonFactory(), null));
	}

	public CseSearch setKey(String key) {
		list.setKey(key);
		return this;
	}
	
	public CseSearch setCx(String cx) {
		list.setCx(cx);
		return this;
	}
	
	public void execute() {
		
		if(hasErrors()) {
			log.warn("Search has errors. aborting execution");
			return;
		}

		try {
			com.google.api.services.customsearch.model.Search cseSearch = list.setStart(pageIndex).execute();
			clearData();
			extractSearchResults(cseSearch);
			getNextPage(cseSearch);
		} catch (IOException e) {
			clearData();
			extractSearchErrors(e);
			nextPage = null;
		}
		
	}
	
	private void extractSearchResults(com.google.api.services.customsearch.model.Search search) {
		search.getItems().forEach(cseResult -> {
			SearchResult searchResult = new SearchResult(cseResult.getLink(), cseResult.getSnippet());
			results.add(searchResult);
		});
	}
	
	private void extractSearchErrors(IOException e) {

		String jsonObject = extractJsonObject(e.getMessage());

		if(jsonObject.isEmpty()) {

			SearchError searchError = new SearchError();
			searchError.setMessage(e.getMessage());
			errors = searchError;

		} else {
			errors = new Gson().fromJson(jsonObject, SearchError.class);
		}
	}

	private String extractJsonObject(String message) {
		int start = message.indexOf("{");
		int end = message.lastIndexOf("}") + 1;
		return (start >= 0 && end > 0) ? message.substring(start, end) : "";
	}
	
	private void clearData() {
		results.clear();
		errors = null;
	}
	
	private void getNextPage(com.google.api.services.customsearch.model.Search search) {
		java.util.List<Query> nextPageItems = search.getQueries().get("nextPage"); 
		nextPage = nextPageItems != null ? nextPageItems.get(0) : null;
	}
	
	public boolean hasErrors() {
		return errors != null;
	}
	
	public SearchError getErrors() {
		return errors;
	}
	
	public boolean hasNextPage() {
		return nextPage != null;
	}
	
	public void nextPage() {
		if(hasNextPage()) {
			pageIndex = nextPage.getStartIndex().longValue();
			execute();
		}
	}
	
	public ArrayList<SearchResult> getResults() {
		return this.results;
	}
	
}

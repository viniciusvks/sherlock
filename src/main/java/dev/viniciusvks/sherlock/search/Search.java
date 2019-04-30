package dev.viniciusvks.sherlock.search;

import java.util.ArrayList;

public interface Search {

	public void execute();
	public boolean hasErrors();
	public SearchError getErrors();
	public boolean hasNextPage();
	public void nextPage();
	public ArrayList<SearchResult> getResults();
	
}

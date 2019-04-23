package dev.viniciusvks.sherlock.search;

public class SearchResult {
	
	private final String link;
	private final String snippet;
	
	public SearchResult(String link, String snippet){
		this.link = link;
		this.snippet = snippet;
	}

	public String getLink() {
		return link;
	}

	public String getSnippet() {
		return snippet;
	}

}

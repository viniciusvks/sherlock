package dev.viniciusvks.sherlock.search.scraper;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import dev.viniciusvks.sherlock.search.SearchResult;

public class ScraperSearchTest {

	@Test
	public void searchShouldFetchResultsCorrectly() {
		
		String query = "ano 2001 eu estava prestes abandonar carreira gerente projetos " + 
				"software. Eu não aguentava mais aquilo. Era escopo sempre mudava. prazo " + 
				"custo sempre estouravam cliente nunca sabia queria correria " + 
				"fim projeto. Fins semana ";
		
		ScraperSearch scraperSearch = new ScraperSearch(query);
		scraperSearch.execute();
		ArrayList<SearchResult> results = scraperSearch.getResults();
		assertFalse(results.isEmpty());
		logSearchResults(results);
		
	}

	private void logSearchResults(ArrayList<SearchResult> results) {
		System.out.println("Results");			
		System.out.println("+----------------------------------------------------------------------------------------------------------");
		for(SearchResult result : results) {
			System.out.println("|link: "+ result.getLink());
			System.out.println("|snippet: "+ result.getSnippet());
			System.out.println("+----------------------------------------------------------------------------------------------------------");
		}
	}

}

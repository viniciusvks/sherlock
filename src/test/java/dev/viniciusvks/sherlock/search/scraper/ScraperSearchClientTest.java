package dev.viniciusvks.sherlock.search.scraper;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import dev.viniciusvks.sherlock.search.SearchResponse;
import dev.viniciusvks.sherlock.search.SearchResult;

public class ScraperSearchClientTest {

	@Test
	public void clientShouldReturnValidResponse() {
		
		String query = "ano 2001 eu estava prestes abandonar carreira gerente projetos " + 
				"software. Eu não aguentava mais aquilo. Era escopo sempre mudava. prazo " + 
				"custo sempre estouravam cliente nunca sabia queria correria " + 
				"fim projeto. Fins semana ";
		
		ScraperSearchClient searchClient = new ScraperSearchClient();
		SearchResponse response = searchClient.search(query);
		ArrayList<SearchResult> results = response.getResults();
		
		assertFalse(results.isEmpty());
		assertFalse(response.hasErrors());
		
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

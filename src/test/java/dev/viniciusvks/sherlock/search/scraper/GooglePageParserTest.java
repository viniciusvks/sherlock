package dev.viniciusvks.sherlock.search.scraper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dev.viniciusvks.sherlock.search.SearchResult;

public class GooglePageParserTest {
	
	private static final String SCHEME = "https";
	private static final String HOST = "www.google.com";
	private static final String PATH = "/search";
	
	@Test
	public void parserShouldReturnValidResultsWhenHtmlPageIsValid() {
		
		String query = "ano 2001 eu estava prestes abandonar carreira gerente projetos " + 
				"software. Eu não aguentava mais aquilo. Era escopo sempre mudava. prazo " + 
				"custo sempre estouravam cliente nunca sabia queria correria " + 
				"fim projeto. Fins semana ";
				
		try(WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			
			String pageUrl = resolveUrl(query);
			HtmlPage page = webClient.getPage(pageUrl);
			GooglePageParser parser = new GooglePageParser();
			parser.parse(page);
			ArrayList<SearchResult> results = parser.getResults();
			assertFalse(results.isEmpty());
			logSearchResults(results);
			
		} catch (URISyntaxException  | FailingHttpStatusCodeException | PageParseException | IOException e) {
			fail(e.getMessage());
		}
		
	}
	
	public String resolveUrl(String query) throws MalformedURLException, URISyntaxException {
		return new URIBuilder()
				.setScheme(SCHEME)
				.setHost(HOST)
				.setPath(PATH)
				.setParameter("safe", "off")
				.setParameter("source", "hp")
				.setParameter("btnK", "Google Search")
				.setParameter("q", query)
				.setParameter("start", "1")
				.build()
				.toURL()
				.toString();
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

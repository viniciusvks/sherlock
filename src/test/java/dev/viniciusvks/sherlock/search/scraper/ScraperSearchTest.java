package dev.viniciusvks.sherlock.search.scraper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dev.viniciusvks.sherlock.search.SearchResult;

public class ScraperSearchTest {

	private static final String SAMPLE_PAGE = "file://home/vinicius/Dev/sherlock/src/test/resources/sample-google-page.html";

	private static final String SAMPLE_QUERY = "ano 2001 eu estava prestes abandonar carreira gerente projetos " + 
			"software. Eu não aguentava mais aquilo. Era escopo sempre mudava. prazo " + 
			"custo sempre estouravam cliente nunca sabia queria correria " + 
			"fim projeto. Fins semana ";

	@Mock
	private WebClient webClient;

	@Mock
	private GooglePageParser googlePageParser;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void searchShouldFetchResultsWithNoErrors() throws FailingHttpStatusCodeException, MalformedURLException, IOException, PageParseException {
		
		when(webClient.getPage(any(String.class))).thenReturn(samplePage());
		
		try(ScraperSearch scraperSearch = new ScraperSearch(SAMPLE_QUERY, webClient, new GooglePageParser())) {

			scraperSearch.execute();
			ArrayList<SearchResult> results = scraperSearch.getResults();

			int expectedResultsCount = 10;

			assertEquals(expectedResultsCount, results.size());
		}
		
	}

	@SuppressWarnings("resource")
	@Test
	public void searchShouldHaveErrorWhenFailingHttpStatusCodeExceptionIsThrown() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		WebResponse response = new StringWebResponse("test", new URL(SAMPLE_PAGE));
		when(webClient.getCurrentWindow()).thenReturn(new WebClient().getCurrentWindow());
		when(webClient.getPage(any(String.class))).thenThrow(new FailingHttpStatusCodeException(response));

		try(ScraperSearch scraperSearch = new ScraperSearch(SAMPLE_QUERY, webClient, new GooglePageParser())) {

			scraperSearch.execute();
			ArrayList<SearchResult> results = scraperSearch.getResults();

			assertTrue(results.isEmpty());
			assertTrue(scraperSearch.hasErrors());

		}

	}

	@Test
	public void searchShouldHaveErrorWhenIOExceptionIsThrown() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		when(webClient.getPage(any(String.class))).thenThrow(new IOException());

		try(ScraperSearch scraperSearch = new ScraperSearch(SAMPLE_QUERY, webClient, new GooglePageParser())) {

			scraperSearch.execute();
			ArrayList<SearchResult> results = scraperSearch.getResults();

			assertTrue(results.isEmpty());
			assertTrue(scraperSearch.hasErrors());

		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void searchShouldHaveErrorWhenPageParseExceptionIsThrown() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		when(webClient.getPage(any(String.class))).thenThrow(PageParseException.class);

		try(ScraperSearch scraperSearch = new ScraperSearch(SAMPLE_QUERY, webClient, new GooglePageParser())) {

			scraperSearch.execute();
			ArrayList<SearchResult> results = scraperSearch.getResults();

			assertTrue(results.isEmpty());
			assertTrue(scraperSearch.hasErrors());

		}

	}

	public HtmlPage samplePage() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		try(WebClient client = new WebClient())  {
			return client.getPage(SAMPLE_PAGE);
		}
	}

}

package dev.viniciusvks.sherlock.search.scraper;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;

import dev.viniciusvks.sherlock.search.Search;
import dev.viniciusvks.sherlock.search.SearchError;
import dev.viniciusvks.sherlock.search.SearchResult;

public class ScraperSearch implements Search, Closeable {
	
	private static final Logger log = LogManager.getLogger(ScraperSearch.class);
	private static final String BASE_URL = "https://www.google.com";
	
	private String pageUrl;
	private GooglePageParser pageParser;
	private WebClient webClient;
	private ArrayList<SearchResult> results;
	private SearchError errors;
	
	public ScraperSearch(String query) {
		this(query, new WebClient(BrowserVersion.BEST_SUPPORTED), new GooglePageParser());
	}

	public ScraperSearch(String query, WebClient webClient, GooglePageParser pageParser) {
		log.info("New search query: {}", query);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		buildPageUrl(query);
		this.pageParser = pageParser;
		this.webClient = webClient;
		this.webClient.setCssErrorHandler(new SilentCssErrorHandler());
		this.webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
		results = new ArrayList<>();
	}
	
	private void buildPageUrl(String query) {
		
		try {
			
			pageUrl = new URIBuilder(BASE_URL)
					.setPath("search")
					.setParameter("safe", "off")
					.setParameter("source", "hp")
					.setParameter("btnK", "Google Search")
					.setParameter("q", query)
					.build()
					.toURL()
					.toString();
			
		} catch (MalformedURLException | URISyntaxException e) {
			log.error("Error parsing build page url: {}", e.getMessage());
			extractSearchErrors(e);
		}
	}

	@Override
	public void execute() {
		
		if(hasErrors()) {
			log.warn("Search with errors, cancelling request.");
			return;
		}

		try {
			clearData();
			log.info("Fetching page with url: {}", pageUrl);
			HtmlPage page = webClient.getPage(pageUrl);
			pageParser.parse(page);
			log.info("Page fetched with no errors");
			results = pageParser.getResults();
		} catch (FailingHttpStatusCodeException e) {
			handleFailedRequest(e.getResponse(), webClient.getCurrentWindow());
			clearData();
			extractSearchErrors(e);
		} catch (IOException e) {
			log.error("Error fetching page: {}", e.getMessage());
			clearData();
			extractSearchErrors(e);
		} catch (PageParseException e) {
			log.error("Error parsing page: {}", e.getMessage());
			clearData();
			extractSearchErrors(e);
		}

	}
	
	
	private void handleFailedRequest(WebResponse response, WebWindow window) {
		try {
			HtmlPage page = HTMLParser.parseHtml(response, window);
			if (pageParser.hasRecaptcha(page.getDocumentElement())) {
				log.error("Error fetching page: reCaptcha page returned");
				log.error("referrer: ", response.getResponseHeaderValue("referrer"));
			}
		} catch (IOException e) {
			log.error("Error fetching page:", response.getStatusMessage());
		}
	}
	
	private void extractSearchErrors(Exception e) {
		errors = new SearchError();
		errors.setMessage(e.getMessage());
	}
	
	private void clearData() {
		results.clear();
		errors = null;
	}

	@Override
	public boolean hasErrors() {
		return errors != null;
	}

	@Override
	public SearchError getErrors() {
		return errors;
	}

	@Override
	public boolean hasNextPage() {
		return pageParser.getNextPageLink() != null;
	}

	@Override
	public void nextPage() {
		if(hasNextPage()) {
			log.debug("Building next page url");
			pageUrl = BASE_URL + pageParser.getNextPageLink();
			execute();
		}
	}

	@Override
	public ArrayList<SearchResult> getResults() {
		return results;
	}

	@Override
	public void close() {
		webClient.close();
	}

}

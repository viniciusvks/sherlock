package dev.viniciusvks.sherlock.search.scraper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dev.viniciusvks.sherlock.search.SearchResult;

public class GooglePageParserTest {
	
	private static final String SAMPLE_PAGE = "file://home/vinicius/Dev/sherlock/src/test/resources/sample-google-page.html";
	private static final String INVALID_PAGE = "file://home/vinicius/Dev/sherlock/src/test/resources/invalid-google-page.html";
	private static final String CAPTCHA_PAGE = "file://home/vinicius/Dev/sherlock/src/test/resources/captcha.html";
	
	@Test
	public void parserShouldReturnValidResultsWhenHtmlPageIsValid() {

		try(WebClient webClient = new WebClient()) {
		
			HtmlPage page = webClient.getPage(SAMPLE_PAGE);
			GooglePageParser parser = new GooglePageParser();

			parser.parse(page);
			ArrayList<SearchResult> results = parser.getResults();
			assertFalse(results.isEmpty());
			
		} catch (FailingHttpStatusCodeException | PageParseException | IOException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test(expected=PageParseException.class)
	public void parserShouldThrowExceptionWhenPageIsInvalid() throws PageParseException {

		try(WebClient webClient = new WebClient()) {

			HtmlPage page = webClient.getPage(INVALID_PAGE);
			GooglePageParser parser = new GooglePageParser();

			parser.parse(page);
			ArrayList<SearchResult> results = parser.getResults();
			assertTrue(results.isEmpty());

		} catch (FailingHttpStatusCodeException | IOException e) {
			fail(e.getMessage());
		}

	}
	
	@Test
	public void parserShouldDetectPageWithCaptcha() {

		try(WebClient webClient = new WebClient()) {

			HtmlPage page = webClient.getPage(CAPTCHA_PAGE);
			GooglePageParser parser = new GooglePageParser();

			parser.parse(page);
			ArrayList<SearchResult> results = parser.getResults();
			assertTrue(results.isEmpty());

		}catch(PageParseException e) {
			assertEquals("Recaptcha page detected", e.getMessage());
		}catch (FailingHttpStatusCodeException | IOException e) {
			fail(e.getMessage());
		}

	}

}

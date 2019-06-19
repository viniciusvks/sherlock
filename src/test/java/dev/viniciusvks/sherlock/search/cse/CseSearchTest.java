package dev.viniciusvks.sherlock.search.cse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse;
import com.google.api.services.customsearch.Customsearch.Cse.List;
import com.google.api.services.customsearch.model.Query;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.google.gson.Gson;

import dev.viniciusvks.sherlock.search.Error;
import dev.viniciusvks.sherlock.search.SearchError;
import dev.viniciusvks.sherlock.search.SearchResult;

public class CseSearchTest {

	private static final String QUERY = "O aprendizado automático ou a aprendizagem automática ou também aprendizado de máquina ou "
									  + "aprendizagem de máquina é um subcampo da ciência da computação que evoluiu do estudo de "
									  + "reconhecimento de padrões e da teoria do aprendizado computacional em inteligência artificial.";

	@Mock
	private Customsearch customSearch;

	@Mock
	private Cse cse;

	@Mock
	private List list;

	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void searchShouldReturnErrorsWhenIOExceptionIsThrownAtExecution() throws IOException {

		Error error = new Error();
		error.setMessage("message");
		error.setReason("reason");
		error.setDomain("domain");

		SearchError expectedSearchError = new SearchError();
		expectedSearchError.setCode(403);
		expectedSearchError.setErrors(new Error[] {error});

		String message = new Gson().toJson(expectedSearchError);
		IOException ioException = new IOException(message);

		when(list.setStart(any(Long.class))).thenCallRealMethod();
		when(list.execute()).thenThrow(ioException);
		when(cse.list(QUERY)).thenReturn(list);
		when(customSearch.cse()).thenReturn(cse);

		CseSearch search = new CseSearch(QUERY, customSearch);
		search.execute();
		
		int expectedNumberOfErrors = 1;
		
		SearchError actualErrors = search.getErrors();

		assertTrue(search.hasErrors());
		assertFalse(search.hasNextPage());
		assertNotNull(actualErrors);

		Error[] errors = actualErrors.getErrors();
		assertEquals(expectedNumberOfErrors, errors.length);
		assertEquals(error.getDomain(), errors[0].getDomain());
		assertEquals(error.getMessage(), errors[0].getMessage());
		assertEquals(error.getReason(), errors[0].getReason());
		
	}
	
	@Test
	public void searchShouldReturnErrorsWhenIOExceptionIsThrownAtConstructor() throws IOException {
		
		IOException ioException = new IOException("error");
		
		when(cse.list(QUERY)).thenThrow(ioException);
		when(customSearch.cse()).thenReturn(cse);
		
		CseSearch search = new CseSearch(QUERY, customSearch);
		search.execute();
		
		assertTrue(search.hasErrors());
		SearchError errors = search.getErrors();
		assertEquals(ioException.getMessage(), errors.getMessage());
		
	}
	
	@Test
	public void searchShouldReturnResults() throws IOException {
		
		Result item = new Result();
		item.setLink("link");
		item.setSnippet("snippet");
		
		ArrayList<Result> items = new ArrayList<Result>();
		items.add(item);
		
		Map<String, java.util.List<Query>> queries = new HashMap<>();
		queries.put("nextPage", null);
		
		Search search = new Search();
		search.setItems(items);
		search.setQueries(queries);
		
		when(list.setStart(any(Long.class))).thenCallRealMethod();
		when(list.execute()).thenReturn(search);
		when(cse.list(QUERY)).thenReturn(list);
		when(customSearch.cse()).thenReturn(cse);
		
		CseSearch cseSearch = new CseSearch(QUERY, customSearch);
		cseSearch.setCx("cx")
			.setKey("key")
			.execute();
		
		ArrayList<SearchResult> results = cseSearch.getResults();
		
		assertFalse(cseSearch.hasNextPage());
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(item.getLink(), results.get(0).getLink());
		assertEquals(item.getSnippet(), results.get(0).getSnippet());
		
	}

}

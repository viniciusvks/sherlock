package dev.viniciusvks.sherlock.search;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class SearchTest {
	
	private static final String PROPERTIES_FILE_RELATIVE_PATH = "/src/main/resources/app.properties";
	private static final String QUERY = "O aprendizado automático ou a aprendizagem automática ou também aprendizado de máquina ou "
									  + "aprendizagem de máquina é um subcampo da ciência da computação que evoluiu do estudo de "
									  + "reconhecimento de padrões e da teoria do aprendizado computacional em inteligência artificial.";

	@Test
	public void searchShouldReturnErrorsWhenKeyIsMissing() {
		
		Properties properties = null;	
		
		try {
			properties = loadProperties();
		} catch (IOException e) {
			fail("error loading app properties file");
		} 
		
		String cx = properties.getProperty("cx");
		
		Search search = new Search(QUERY);
		search.setCx(cx).execute();
		
		SearchError searchError = search.getErrors();
		Error[] errors = searchError.getErrors();
		String expectedReason = "dailyLimitExceededUnreg";
		int expectedNumberOfErrors = 1;
		
		assertTrue(search.hasErrors());
		assertFalse(search.hasNextPage());
		assertNotNull(searchError);
		assertEquals(new Integer(403), searchError.getCode());
		assertNotNull(errors);
		assertEquals(expectedNumberOfErrors, errors.length);
		assertEquals(expectedReason, errors[0].getReason());		
		
	}
	
	@Test
	public void searchShouldReturnErrorsWhenCxIsMissing() {
		
		Properties properties = null;	
		
		try {
			properties = loadProperties();
		} catch (IOException e) {
			fail("error loading app properties file");
		} 
		
		String cx = properties.getProperty("key");
		
		Search search = new Search(QUERY);
		search.setCx(cx).execute();
		
		SearchError searchError = search.getErrors();
		Error[] errors = searchError.getErrors();
		String expectedReason = "dailyLimitExceededUnreg";
		int expectedNumberOfErrors = 1;
		
		assertTrue(search.hasErrors());
		assertFalse(search.hasNextPage());
		assertNotNull(searchError);
		assertEquals(new Integer(403), searchError.getCode());
		assertNotNull(errors);
		assertEquals(expectedNumberOfErrors, errors.length);
		assertEquals(expectedReason, errors[0].getReason());		
		
	}
	
	@Test
	public void searchShouldReturnErrorsWhenKeyIsInvalid() {
		
		Properties properties = null;	
		
		try {
			properties = loadProperties();
		} catch (IOException e) {
			fail("error loading app properties file");
		} 
		
		String cx = properties.getProperty("cx");
		
		Search search = new Search(QUERY)
								.setKey("INVALID KEY")
								.setCx(cx);
		search.execute();
		
		SearchError searchError = search.getErrors();
		Error[] errors = searchError.getErrors();
		String expectedReason = "keyInvalid";
		int expectedNumberOfErrors = 1;
		
		assertTrue(search.hasErrors());
		assertFalse(search.hasNextPage());
		assertNotNull(searchError);
		assertEquals(new Integer(400), searchError.getCode());
		assertNotNull(errors);
		assertEquals(expectedNumberOfErrors, errors.length);
		assertEquals(expectedReason, errors[0].getReason());		
		
	}
	
	@Test
	public void searchShouldReturnErrorsWhenCxIsInvalid() {
		
		Properties properties = null;	
		
		try {
			properties = loadProperties();
		} catch (IOException e) {
			fail("error loading app properties file");
		} 
		
		String key = properties.getProperty("key");
		
		Search search = new Search(QUERY)
								.setKey(key)
								.setCx("INVALID CX");
		search.execute();
		
		SearchError searchError = search.getErrors();
		Error[] errors = searchError.getErrors();
		String expectedReason = "invalid";
		int expectedNumberOfErrors = 1;
		
		assertTrue(search.hasErrors());
		assertFalse(search.hasNextPage());
		assertNotNull(searchError);
		assertEquals(new Integer(400), searchError.getCode());
		assertNotNull(errors);
		assertEquals(expectedNumberOfErrors, errors.length);
		assertEquals(expectedReason, errors[0].getReason());		
		
	}
	
	private Properties loadProperties() throws FileNotFoundException, IOException {
		
		String rootPath = System.getProperty("user.dir");
		String appConfigPath = rootPath + PROPERTIES_FILE_RELATIVE_PATH;
		
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));
		
		return appProps;
		
	}

}

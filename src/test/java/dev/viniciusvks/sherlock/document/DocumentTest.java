package dev.viniciusvks.sherlock.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class DocumentTest {
	
	private static final String TEST_FILE_REL_PATH = "/src/test/resources/test-file.pdf";
	private static final String ENCRYPTED_FILE_REL_PATH = "/src/test/resources/encrypted.pdf";
	private static final String OUT_OF_BOUNDS_MSG = "Page index out of bounds";
	private static final String INCORRECT_PASSWORD_MSG = "Cannot decrypt PDF, the password is incorrect";
	private static final int EXPECTED_NUMBER_OF_PAGES = 3;
	private static final String FIRST_PAGE_CONTENT = "First page";
	private static final String SECOND_PAGE_CONTENT = "Second page";
	private static final String THIRD_PAGE_CONTENT = "Third page";
	
	@Test
	public void documentShouldBeParsedCorrectlyWhenFileIsValid() {
		
		String filePath = System.getProperty("user.dir") + TEST_FILE_REL_PATH;
		
		try (Document doc = new Document(filePath)) {
			
			assertEquals(EXPECTED_NUMBER_OF_PAGES, doc.getNumberOfPages());
			assertEquals(FIRST_PAGE_CONTENT, doc.getPage(1));
			assertEquals(SECOND_PAGE_CONTENT, doc.getPage(2));
			assertEquals(THIRD_PAGE_CONTENT, doc.getPage(3));
			
		} catch (IOException | DocumentException e) {
			fail(e.getMessage());
		} 
		
	}
	
	@Test(expected=DocumentException.class)
	public void shouldThrowExceptionWhenPageIndexIsOutOfBounds() throws IOException, DocumentException {
		
		String filePath = System.getProperty("user.dir") + TEST_FILE_REL_PATH;
		try (Document doc = new Document(filePath)) {
			int numberOfPages = doc.getNumberOfPages();
			doc.getPage(numberOfPages + 1);
		} catch (DocumentException e) {
			assertEquals(OUT_OF_BOUNDS_MSG, e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=IOException.class)
	public void shouldThrowExceptionWhenFileDoesNotExists() throws IOException, DocumentException {
		
		String filePath = "wrongpath";
		try (Document doc = new Document(filePath)) {
		} catch (IOException e) {
			assertEquals(filePath + " (No such file or directory)", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=DocumentException.class)
	public void shouldThrowExceptionWhenLoadingEncryptedFile() throws IOException, DocumentException {
		
		String filePath = System.getProperty("user.dir") + ENCRYPTED_FILE_REL_PATH;
		try (Document doc = new Document(filePath)) {
		} catch (DocumentException e) {
			assertEquals(INCORRECT_PASSWORD_MSG, e.getMessage());
			throw e;
		}
	}

}

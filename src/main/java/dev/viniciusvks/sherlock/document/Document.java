package dev.viniciusvks.sherlock.document;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

public class Document implements Closeable {
	
	private int numberOfPages;
	PDDocument document;
	PDFTextStripper textStripper;
	
	public Document(File file) throws DocumentException, IOException {
		try{
			document = PDDocument.load(file);
			numberOfPages = document.getNumberOfPages();
			textStripper = new PDFTextStripper();
		} catch (InvalidPasswordException e) {
			throw new DocumentException(e.getMessage());
		}
	}
	
	public String getPage(int index) throws IOException, DocumentException {
		if(index < 1 || index > numberOfPages) {
			throw new DocumentException("Page index out of bounds");
		}
		textStripper.setStartPage(index);
		textStripper.setEndPage(index);
		return textStripper.getText(document).trim();
	}
	
	public int getNumberOfPages() {
		return numberOfPages;
	}
	
	@Override
	public void close() throws IOException {
		document.close();
	}

}

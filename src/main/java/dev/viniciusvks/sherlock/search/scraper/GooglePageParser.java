package dev.viniciusvks.sherlock.search.scraper;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dev.viniciusvks.sherlock.search.SearchResult;

public class GooglePageParser {
	
	private final Logger log = LogManager.getLogger(GooglePageParser.class);
	
	private enum PageNode {
		
		ROOT("#search"),
		RESULTS("div.srg > div.g"),
		LINK("div.r > a"),
		SNIPPET("span.st"),
		NEXT_PAGE("#pnnext"),
		RECAPTCHA("#recaptcha");
		
		private String selector;
		
		PageNode(String query){
			this.selector = query;
		}
		
		public String getSelector() {
			return selector;
		}
		
	}
	
	private ArrayList<SearchResult> results = new ArrayList<SearchResult>();
	private String nextPageLink;
	
	public void parse(HtmlPage page) throws PageParseException {
	
		log.info("Parsing google page");
		HtmlElement document = page.getDocumentElement();
		DomNode rootNode = null;
		
		try {

			rootNode = extractNode(document, PageNode.ROOT);
			if(rootNode.hasChildNodes()) {
				extractResultNodes(rootNode);
			}
			extractNextPageLink(document);

		} catch(PageParseException e) {
			log.error("Error parsing page: {}", e.getMessage());
			if(hasRecaptcha(document)) {
				throw new PageParseException("Recaptcha page detected");
			}

			throw e;
		}
	}
	
	public boolean hasRecaptcha(DomNode root) {
		return root.querySelector(PageNode.RECAPTCHA.getSelector()) != null;
	}

	private void extractNextPageLink(HtmlElement document) {
		log.debug("Extracting node for next page link");
		DomElement nextPageLinkNode = (DomElement) document.querySelector(PageNode.NEXT_PAGE.getSelector());
		
		if(nextPageLinkNode != null) {
			nextPageLink = nextPageLinkNode.getAttribute("href");
		} else {
			nextPageLink = null;
			log.debug("Next page link not found");
		}
	}

	private void extractResultNodes(DomNode rootNode) throws PageParseException {
		
		log.debug("Extracting page results nodes");
		
		DomNodeList<DomNode> resultNodes = extractNodeList(rootNode, PageNode.RESULTS);
		
		for(DomNode resultNode : resultNodes) {
			
			DomNode snippetNode = extractNode(resultNode, PageNode.SNIPPET);
			DomElement linkNode = (DomElement) extractNode(resultNode, PageNode.LINK);
			
			String link = linkNode.getAttribute("href");
			String snippet = snippetNode.asText().replaceAll(" ... ", " ");
			
			SearchResult searchResult = new SearchResult(link, snippet);
			results.add(searchResult);
			
		}
	}
	
	private DomNode extractNode(DomNode root, PageNode nodeToExtract) throws PageParseException {
		
		log.debug("Extracting page node: {}", nodeToExtract.toString());
		
		DomNode extractedNode = root.querySelector(nodeToExtract.getSelector());
		
		if(extractedNode == null) {
			throw new PageParseException("Node not found: " + nodeToExtract.toString());
		}
		
		return extractedNode;
	}
	
	private DomNodeList<DomNode> extractNodeList(DomNode root, PageNode nodesToExtract) throws PageParseException {
		
		log.debug("Extracting page list of nodes: {}", nodesToExtract.toString());
		
		DomNodeList<DomNode> extractedNodes = root.querySelectorAll(nodesToExtract.getSelector());
		if(extractedNodes.isEmpty()) {
			throw new PageParseException("Nodes not found: "+nodesToExtract.toString());
		}
		return extractedNodes;
		
	}
	
	public ArrayList<SearchResult> getResults() {
		return results;
	}
	
	public String getNextPageLink() {
		return nextPageLink;
	}

}

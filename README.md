# Sherlock - An app for plagiarism detection

This project combines the power of [Google Custom Search API](https://developers.google.com/custom-search/) with the [Cosine Similarity](https://en.wikipedia.org/wiki/Cosine_similarity) 
to find signs of plagiarism in a text document by splitting it into small text snippets and googling them in the internet.
At the end, the app calculates the similarity between each search result with the queried snippet and creates a report with
the most relevant findings.

## Getting Started

This app uses the Google Custom Search API to search the text in the internet. To make it work, you must have a google account
and create a project, an API key e and a Custom Search Engine ID. Also, there's a limit of 100 free queries per day. If you need more queries, you can buy a higher quota. You can check all this information [here](https://developers.google.com/custom-search/v1/overview).

After that, you must place your API key and engine ID (cx) in the app.properties file. It's located in the resources folder with 
a .example extension, just rename it to app.properties and fill in the values.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

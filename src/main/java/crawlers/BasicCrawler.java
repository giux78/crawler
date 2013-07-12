package crawlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private String[] myCrawlDomains;

	private List<String> customDomains = new ArrayList<String>();

	private String customUniqueUrl;

	private String dirName;

	@Override
	public void onStart() {
		myCrawlDomains = (String[]) myController.getCustomData();

		for (String domains : myCrawlDomains) {
			if (domains.startsWith("dom:")) {
				customDomains.add(domains.split(":")[1]);
				System.out.println(customDomains);
			} else if (domains.startsWith("url:")) {
				customUniqueUrl = domains.split(":")[1];
				System.out.println(customUniqueUrl);
			} else if (domains.startsWith("dir:")) {
				dirName = domains.split(":")[1];
			}
		}

	}

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		System.out.println(href);
		if (FILTERS.matcher(href).matches()) {
			return false;
		}

		// if (!href.contains("opinioni") && !href.contains("opinione")) {
		// return false;
		// }

		// if (!href.contains("opinione")) {
		// return false;
		// }

		if (!href.contains(customUniqueUrl)) {
			return false;
		}

		for (String crawlDomain : customDomains) {
			if (href.contains(crawlDomain)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		int parentDocid = page.getWebURL().getParentDocid();

		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Docid of parent page: " + parentDocid);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());

			System.out.println(url);
			String filePath = url.replace("http://", "");
			String[] fileNameArray = filePath.split("/");
			String fileName = fileNameArray[fileNameArray.length - 1];
			System.out.println("FileName : " + fileName);

			File file = new File("///Users/alessandroercolani/crawler/"
					+ dirName + "/" + fileName + ".html");
			File fileTxt = new File("///Users/alessandroercolani/crawler/"
					+ dirName + "/" + fileName + ".txt");
			try {
				FileUtils.writeStringToFile(file, html);
				FileUtils.writeStringToFile(fileTxt, text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println("=============");
	}
}
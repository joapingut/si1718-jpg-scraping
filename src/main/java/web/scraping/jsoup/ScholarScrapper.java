package web.scraping.jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScholarScrapper {

	
	public static void main(String[] args) {
		try {
			extractScholar();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void extractScholar() throws MalformedURLException, IOException {
		Document doc = Jsoup.parse(new URL("https://scholar.google.es/citations?user=2UiKKocAAAAJ&hl=en"), 10000);
		Element div = doc.getElementById("gsc_art");
		Elements links = div.select("a.gsc_a_at");
		Set<String> linksList = new LinkedHashSet<String>();
		for(Element link:links) {
			try {
				String jour = extractJournal(link.absUrl("data-href"));
				if(jour != null) {
					linksList.add(jour);
				}
			} catch (SocketTimeoutException e) {
				System.out.println("TIMEOUT " + link.absUrl("data-href"));
			}
		}
		System.out.println(linksList);
	}
	
	public static String extractJournal(String userUrl) throws MalformedURLException, IOException {
		Document doc = Jsoup.parse(new URL(userUrl), 10000);
		Elements elements = doc.select("div.gsc_vcd_field");
		String journal = null;
		if(elements != null && elements.size() != 0) {
			for(Element ele:elements) {
				if("Journal".equals(ele.text())) {
					Element jour = ele.nextElementSibling();
					journal = jour.text();
				}
			}
		}
		return journal;
	}
}

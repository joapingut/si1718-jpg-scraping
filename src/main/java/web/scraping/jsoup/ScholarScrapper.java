package web.scraping.jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScholarScrapper {

	private static final String URL_BASE = "https://scholar.google.es/citations?hl=en&user=";
	
	public static void main(String[] args) {
		try {
			extractScholar("2UiKKocAAAAJ");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<ScholarArticle> extractScholar(String idUser) throws MalformedURLException, IOException {
		Document doc = Jsoup.parse(new URL(URL_BASE + idUser), 10000);
		if(doc == null) {
			return null;
		}
		Element div = doc.getElementById("gsc_art");
		if(div == null) {
			return null;
		}
		Elements links = div.select("a.gsc_a_at");
		if(links == null) {
			return null;
		}
		List<ScholarArticle> linksList = new ArrayList<ScholarArticle>();
		for(Element link:links) {
			try {
				ScholarArticle scholar = extractJournal(link.absUrl("data-href"));
				if(scholar != null) {
					linksList.add(scholar);
				}
			} catch (SocketTimeoutException e) {
				System.out.println("TIMEOUT " + link.absUrl("data-href"));
			}
		}
		System.out.println(linksList);
		return(linksList);
	}
	
	public static ScholarArticle extractJournal(String userUrl) throws MalformedURLException, IOException {
		Document doc = Jsoup.parse(new URL(userUrl), 10000);
		if(doc == null) {
			return null;
		}
		Elements elements = doc.select("div.gsc_vcd_field");
		if(elements == null) {
			return null;
		}
		ScholarArticle article = new ScholarArticle();
		if(elements != null && elements.size() != 0) {
			String title = doc.select("a.gsc_vcd_title_link").text();
			article.setTitle(title);
			boolean isJournal = false;
			for(Element ele:elements) {
				Element jour = ele.nextElementSibling();
				if("Journal".equals(ele.text())) {
					article.setJournal(jour.text());
					isJournal = true;
				} else if("Authors".equals(ele.text())) {
					article.setAuthors(jour.text());
				} else if("Publication date".equals(ele.text())) {
					article.setPublicationDate(jour.text());
				} else if("Volume".equals(ele.text())) {
					article.setVolume(jour.text());
				} else if("Pages".equals(ele.text())) {
					article.setPages(jour.text());
				} else if("Publisher".equals(ele.text())) {
					article.setPublisher(jour.text());
				} else if("Description".equals(ele.text())) {
					article.setDescription(jour.text());
				}
			}
			if(!isJournal) {
				return null;
			}
		}
		return article;
	}
}

package web.service;

import java.util.List;
import java.io.IOException;

import org.bson.Document;
import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import web.scraping.jsoup.ScholarArticle;
import web.scraping.jsoup.ScholarScrapper;

@RestController
public class ServingLayerController {

	@RequestMapping("/scholar")
	public Object serve(
			@RequestParam(value = "id", required = true, defaultValue = "null") String tag)
			throws JsonParseException, JsonMappingException, IOException {
		if(tag == null || tag.isEmpty() || tag.equals("null")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		try {
			List<ScholarArticle> result = ScholarScrapper.extractScholar(tag);
			if(result == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			if(result.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			}
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			System.out.println("Error");
			if(e instanceof HttpStatusException) {
				HttpStatusException execption = (HttpStatusException) e;
				Document menssage = new Document("message", "Google returns: " + execption.getStatusCode());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(menssage);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}
	


}
package dev.obrienlabs.biometric.nbi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
	
	   @Value("${google.maps.api.key:}")
	   private String mapsApiKey;
	   @RequestMapping(value = "/index")
	   public String index() {
		   return "index";
	   }
	   
	   @GetMapping(value = "/heart", produces = MediaType.TEXT_HTML_VALUE)
	   public ResponseEntity<String> heart() throws IOException {
	      ClassPathResource resource = new ClassPathResource("static/heart.html");
	      String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
	      html = html.replace("GOOGLE_MAPS_API_KEY", mapsApiKey);
	      return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
	   }
}
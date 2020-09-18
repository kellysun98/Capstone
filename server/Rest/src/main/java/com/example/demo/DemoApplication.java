package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	class nodeController{
		@GetMapping('/')
		public List<Float> {
			List<List<Float>> InitNode = new ArrayList<>();
			InitNode.add(
				[@RequestParam('longitude'), @RequestParam('latitude')], //requesting param from users' input for starting coordinates
				[@RequestParam('end_long'), @RequestParam('end_lat')]) //ending coordinates
			return InitNode
		
		}
	}

}

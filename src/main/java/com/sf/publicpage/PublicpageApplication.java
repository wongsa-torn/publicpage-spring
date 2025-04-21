package com.sf.publicpage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PublicpageApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicpageApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(String[] args) {
		// Print on console when run app.
		return runner -> {
			System.out.println("Publicpage Start...");
		};
	}

//	@RestController
//	class HelloController {
//	 
//	    @RequestMapping("/hello/{name}")
//	    String hello(@PathVariable String name) {
//	 
//	        return "Hi " + name + " !";
//	 
//	    }
//	}
	
}

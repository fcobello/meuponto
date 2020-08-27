package br.com.cobello.meuponto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Application 
{
	@Autowired
	RestService s;
	
	public static void main (final String[] args) 
	{
		SpringApplication.run(Application.class, args);
	}
	
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> 
//		{
//
//			System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//			s.hitTime();
//		};
//	}
}
package PlayForward.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		System.out.println("HELLOW");
		SpringApplication.run(DemoApplication.class, args);
	}
}



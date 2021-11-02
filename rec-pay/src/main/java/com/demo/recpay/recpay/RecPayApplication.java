package com.demo.recpay.recpay;

import com.demo.recpay.recpay.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class RecPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecPayApplication.class, args);
	}

}

package com.project.course.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class CourseSubscriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseSubscriptionApplication.class, args);
	}

}

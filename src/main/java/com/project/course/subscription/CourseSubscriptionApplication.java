package com.project.course.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
public class CourseSubscriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseSubscriptionApplication.class, args);
	}

}

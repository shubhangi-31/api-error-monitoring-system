package com.example.errorprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;

@EnableKafka
@EnableKafkaRetryTopic
@SpringBootApplication
public class ErrorProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErrorProcessorApplication.class, args);
	}
}

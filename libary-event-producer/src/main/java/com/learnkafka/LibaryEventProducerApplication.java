package com.learnkafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@SpringBootApplication
public class LibaryEventProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibaryEventProducerApplication.class, args);
	}

}

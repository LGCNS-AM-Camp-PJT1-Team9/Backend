package com.team9.jobbotdari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@RefreshScope
@EnableAsync
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class JobbotdariApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobbotdariApplication.class, args);
	}

}

package com.officeflow.backend;

import com.officeflow.backend.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@MapperScan("com.officeflow.backend.mapper")
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// Temporary Test: Run this after context loads
	@Bean
	CommandLineRunner testDatabase(UserMapper userMapper) {
		System.out.println("加密密码：" + new BCryptPasswordEncoder().encode("123456"));
		return args -> {
			System.out.println("---- Testing MyBatis-Plus Connection ----");
			userMapper.selectList(null).forEach(System.out::println);
			System.out.println("---- Test Finished ----");
		};
	}
}

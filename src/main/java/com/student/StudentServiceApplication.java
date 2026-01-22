package com.student;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import io.r2dbc.spi.ConnectionFactory;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import reactor.core.publisher.Hooks;

@SpringBootApplication(
		nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
		exclude = {RedisAutoConfiguration.class}
		//,exclude = SecurityAutoConfiguration.class
)
@ComponentScan(
		basePackages = {"com.student", "com.student.config"},
		nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@OpenAPIDefinition(info = @Info(
	    title = "WebFlux API Example",
	    version = "1.0",
	    description = "Documentation for the WebFlux API"
	))
public class StudentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentServiceApplication.class, args);
		Hooks.enableAutomaticContextPropagation();
	}
	
	@Bean
	public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		
		CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("database/data.sql")));
		initializer.setDatabasePopulator(populator);
		
		return initializer;
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	

}

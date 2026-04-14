package com.borderless.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Borderless Hospital Appointment System.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 *   - @Configuration      → This class can define Spring beans
 *   - @EnableAutoConfiguration → Spring Boot auto-configures based on classpath
 *   - @ComponentScan      → Scans this package (and sub-packages) for components
 *
 * HOW IT WORKS:
 *   1. JVM calls main()
 *   2. SpringApplication.run() bootstraps the Spring context
 *   3. Spring scans for @Component, @Service, @Repository, @Controller etc.
 *   4. Hibernate creates/updates DB tables based on @Entity classes
 *   5. DataSeeder.run() executes to seed doctor data
 *   6. Embedded Tomcat starts listening on port 8080
 */
@SpringBootApplication
public class HospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
    }
}

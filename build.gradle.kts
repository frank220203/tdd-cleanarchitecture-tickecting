import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.boot.gradle.tasks.bundling.BootJar

//2024-07-04
//Jar파일명에 날짜 14자리 추가
tasks.named<BootJar>("bootJar"){
	val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"))
	archiveFileName.set("tdd-cleanarchitecture-ticketing-${version}-$currentDateTime.jar")
}

plugins {
	java
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "frankproject"
version = "0.0.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

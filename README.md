# biometric-backend
Biometric Backend

## Architecture
### Spring Boot
Add pom.xml
```
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.5.15</version> <!-- maximum swagger version -->
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>dev.obrienlabs.biometric</groupId>
	<artifactId>biometric-nbi</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>biometric-nbi</name>
	<description>Biometric backend</description>
	<properties>
		<java.version>17</java.version>
		<jersey.version>2.27</jersey.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- 2023 -->
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <!--artifactId>spring-boot-starter-data-jpa</artifactId-->
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
            <!-- avoid restarts -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
  <!-- swagger 3 for spring boot 3 -->    
    <!-- dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>3.0.0</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>3.0.0</version>
    </dependency>
    <dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency-->
        
<!-- swagger 2.9 for spring boot 2 -->    
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.9.2</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.9.2</version>
    </dependency>
    <dependency>
      <groupId>io.springfox</groupId>
      <artifactId>springfox-schema</artifactId>
      <version>2.9.2</version>
    </dependency>
<!--  use jakarta -->   
	</dependencies>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

```

Add ApplicationService interface
```
public interface ApplicationServiceLocal {
	String health();
}
```

Add ApplicationService implementation
```
@Service
public class ApplicationService implements ApplicationServiceLocal {
	@Override
	public String health() {
		return "OK";
	}
}
```
Add application.properties under resources
```
# required when running as a spring boot jar in alpine - as opposed to a tomcat container
server.servlet.context-path=/nbi
logging.level.org.springframework.web=DEBUG
#springfox.documentation.swagger.v2.path=/api-docs
server.port=8080
```
Add default Application 
```
@SpringBootApplication
public class BiometricNbiApplication {
	public static void main(String[] args) {
		SpringApplication.run(BiometricNbiApplication.class, args);
	}
}

```
### OpenAPI 3 / Swagger Model


Add SwaggerConfig
```
@Configuration
@EnableSwagger2 // without spring boot
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();				
	}
}
```
### Spring Data JPA
pom.xml
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

```
#### References
- https://spring.io/guides/gs/accessing-data-jpa

### MySQL Database Backend
### Lombok
### Kafka
### Javascript AJAX frontend with three.js
### Google Angular 2 Backend


## Building
### Generate GCP Credentials
```
gcloud auth application-default login 
cp ~/.config/gcloud/application_default_credentials.json $TARGET_DIR
```


## Deployment

## Debugging

http://127.0.0.1:8080/nbi/api


<img width="1712" alt="Screenshot 2024-05-05 at 18 57 33" src="https://github.com/ObrienlabsDev/biometric-backend/assets/24765473/a373fec1-a001-4bf4-ad9a-562dd4f1d321">

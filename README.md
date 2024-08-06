# biometric-backend
Biometric Backend
- reference https://github.com/cloud-quickstart/reference-architecture
- see https://github.com/ObrienlabsDev/doppler-radar-ml/issues

## Architecture
### Deployment
#### Kubernetes Cluster
- https://github.com/ObrienlabsDev/biometric-backend/issues/11
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
- see https://github.com/ObrienlabsDev/biometric-backend/issues/3
- https://github.com/ObrienlabsDev/biometric-backend/commit/80b18e2077b26b30e26b1e6a8ec81dbee68ca668
- 
pom.xml
```
database
 docker run --name mysql-dev0 -v mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=... -d -p 3506:3306 arm64v8/mysql:8.0.38

CONTAINER ID   IMAGE                  COMMAND                  CREATED      STATUS         PORTS                               NAMES
f4b8fc9ce4d0   arm64v8/mysql:8.0.38   "docker-entrypoint.s…"   4 days ago   Up 6 minutes   33060/tcp, 0.0.0.0:3506->3306/tcp   mysql-dev0

	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-jpa</artifactId>
	</dependency>
        <dependency>
        	<groupId>mysql</groupId>
        	<artifactId>mysql-connector-java</artifactId>
        	<!-- version>5.1.42</version--><!-- for 5.7.44 - use 8.0.33 for 8.0.38 -->
        	<scope>runtime</scope>
    </dependency>
```

Repositories
```
@Repository
public class RecordRepositoryImpl {// implements RecordRepository {
	  @PersistenceContext
	  private EntityManager entityManager;
	  //Record findById(long id);
	  //@Override
	  @Transactional
	  public void persist(Record record) {
		  entityManager.persist(record);
	  }
}

```
JPA Entities

```
@MappedSuperclass
public class DataObject {
    @Version
    private Long version;
}

@MappedSuperclass
public class IdentifiableDataObject extends DataObject {
    @Id
    @Column(name="IDENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)//.AUTO)   
    @XmlElement
    private Long id;
}

@Entity
@Table(name="gps_record")
@Access(value = AccessType.FIELD)
@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Record extends IdentifiableDataObject {
    @XmlElement
    @Column(name="userId", nullable=false)
    private Long userId;
    // 20240713: https://stackoverflow.com/questions/25283198/spring-boot-jpa-column-name-annotation-ignored
    //@Column(name="ts_start")
    @XmlElement 
    private Long tsStart;
}
```

For the problem where the column names were auto converting from tsStart to ts_start - gemini was no help - stackoverflow as usually was.
https://stackoverflow.com/questions/25283198/spring-boot-jpa-column-name-annotation-ignored

```
# https://docs.spring.io/spring-boot/docs/1.1.0.M1/reference/html/howto-database-initialization.html
#spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.hibernate.ddl-auto=none
#spring.jpa.properties.hibernate.globally_quoted_identifiers=true
spring.datasource.url=jdbc:mysql://127.0.0.1:3506/biometric
spring.datasource.username=root
spring.datasource.password=...
# deprecated
#spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver

# override auto conversion of tsStart to ts_start
# see https://stackoverflow.com/questions/25283198/spring-boot-jpa-column-name-annotation-ignored
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```
The use of EntityManager.persist(entity) over CrudRepository.save(entity, Long) - required @EnableJpaRepositories("com.delivery.repository") on the application class to pick up a non interface repository
```
@SpringBootApplication
@EnableJpaRepositories("dev.obrienlabs.biometric.repository")
public class BiometricNbiApplication {
	public static void main(String[] args) {
		SpringApplication.run(BiometricNbiApplication.class, args);
	}

for
@Repository
public class RecordRepositoryImpl {// implements RecordRepository {
	  @PersistenceContext
	  private EntityManager entityManager;
	  //Record findById(long id);
	  //@Override
	  @Transactional
	  public void persist(Record record) {
		  entityManager.persist(record);
	  }
}
```

## Working

```
http://127.0.0.1:8080/nbi/swagger-ui.html#/api-controller/getGpsUsingGET

7-13 18:32:15.043 DEBUG 95337 --- [nio-8080-exec-5] o.s.web.servlet.DispatcherServlet        : GET "/nbi/api/getGps?ac=0&action=u2&al=0&arx=0&ary=0&arz=0&be=0&grx=0&gry=0&grz=0&gsx=0&gsy=0&gsz=0&hr1=0&hr2=0&hrd1=0&hrd2=0&hu=0&lax=0&lay=0&laz=0&lg=0&li=0&lt=0&mfx=0&mfy=0&mfz=0&p=0&pr=0&px=0&rvx=0&rvy=0&rvz=0&s=0&te=0&ts=0&u=202407130&up=0", parameters={masked}
2024-07-13 18:32:15.044 DEBUG 95337 --- [nio-8080-exec-5] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to dev.obrienlabs.biometric.nbi.controller.ApiController#getGps(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, HttpServletRequest)
2024-07-13 18:32:15.079 DEBUG 95337 --- [nio-8080-exec-5] m.m.a.RequestResponseBodyMethodProcessor : Using 'text/plain', given [*/*] and supported [text/plain, */*, text/plain, */*, application/json, application/*+json, application/json, application/*+json]
2024-07-13 18:32:15.079 DEBUG 95337 --- [nio-8080-exec-5] m.m.a.RequestResponseBodyMethodProcessor : Writing ["Record(id=15458116,uid=202407130,ssq=0,rsq=0,hr1=0,hr2=0,lat=0.0,lon=0.0,bea=0,alt=0.0,tst0,tsp=1720 (truncated)..."]
2024-07-13 18:32:15.081 DEBUG 95337 --- [nio-8080-exec-5] o.s.web.servlet.DispatcherServlet        : Completed 200 OK

select * from biometric.gps_record where userId="202407130"
'15458114','0','0','0','0.0000000000','0.0000000000','0',NULL,'0','0','0','0','0','0','0','0','0','0','0','0.0000000000','0','0','0','0','0.0000000000','0','0','0','0','0','0','0','0','0','0','0','0','0','0','1720909791966','202407130','0'

Insert again


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

```

```

## Debugging

http://127.0.0.1:8080/nbi/api


<img width="1712" alt="Screenshot 2024-05-05 at 18 57 33" src="https://github.com/ObrienlabsDev/biometric-backend/assets/24765473/a373fec1-a001-4bf4-ad9a-562dd4f1d321">

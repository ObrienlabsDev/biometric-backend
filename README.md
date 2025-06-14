# biometric-backend
Biometric Backend
- reference https://github.com/cloud-quickstart/reference-architecture
- see https://github.com/ObrienlabsDev/doppler-radar-ml/issues

## Architecture
### Deployment
#### Helm
#### Kubernetes
see https://github.com/ObrienlabsDev/biometric-backend/tree/main/biometric-nbi/src/kubernetes

The following script runs both the mysql and biometric-nbi springboot containers

```
kubernetes % ./deploy.sh
(venv-t214) michaelobrien@mbp8 kubernetes % kubectl get pods -n mysql                                    
NAME                    READY   STATUS    RESTARTS   AGE
mysql-9fbfc4867-bj4gz   1/1     Running   0          5m39s
(venv-t214) michaelobrien@mbp8 kubernetes % kubectl get services -n mysql                                
NAME    TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)    AGE
mysql   ClusterIP   None         <none>        3306/TCP   31m
(venv-t214) michaelobrien@mbp8 kubernetes % kubectl exec -it mysql-9fbfc4867-bj4gz -n mysql  -- /bin/bash
bash-5.1# mysql -p

mysql> CREATE DATABASE IF NOT EXISTS biometric;
Query OK, 1 row affected (0.01 sec)

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| biometric          |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
6 rows in set (0.00 sec)

mysql> use biometric;
Database changed
mysql> exit
Bye
bash-5.1# exit
exit
(venv-t214) michaelobrien@mbp8 kubernetes % 

```

test outside by port forwarding

```
(venv-t214) michaelobrien@mbp8 kubernetes % kubectl port-forward mysql-9fbfc4867-bj4gz -n mysql 3306:3306
Forwarding from 127.0.0.1:3306 -> 3306
Forwarding from [::1]:3306 -> 3306
```

If you prefer direct access without port forwarding, update `mysql-service.yaml`
to use a `NodePort` service. The example in this repository exposes port `3306`
on node port `30306`:

```
spec:
  type: NodePort
  ports:
    - protocol: TCP
      port: 3306
      targetPort: 3306
      nodePort: 30306
```
After applying the updated service you can connect using `<node-ip>:30306`.

![Image](https://github.com/user-attachments/assets/943d18d8-2cfc-478e-91ed-a7cd7b1dcf23)

#### Docker Desktop
- see https://github.com/ObrienlabsDev/biometric-backend/issues/6
```
sudo apt install docker.io
sudo reboot now
sudo usermod -aG docker ubuntu
```
##### Create network
```
# create network
docker network create --driver=bridge mysql
3f194fa29ef5d8808ef0cbc41406e0c6a37bc0866be864b26ba38244de8ed0ef

# list network
ubuntu@mini5:~$ docker network list | grep mysql
3f194fa29ef5   mysql     bridge    local

```
##### Create persistent volume
Volumes in docker are created several ways - here we use the run command with -v - see https://docs.docker.com/engine/storage/volumes/
```
# create volume

# list existing volume

```
##### Deploy MySQL
```
# create container
docker run --name mysql-dev0 -v mysql-data:/var/lib/mysql --network="mysql" -e MYSQL_ROOT_PASSWORD=root -d -p 3506:3306 mysql:8.0.38

# restart container
ubuntu@mini5:~/obrienlabsdev/biometric-backend$ docker start mysql-dev0
mysql-dev0
ubuntu@mini5:~/obrienlabsdev/biometric-backend$ docker ps
CONTAINER ID   IMAGE          COMMAND                  CREATED        STATUS         PORTS                                                  NAMES
35b726a994a5   mysql:8.0.38   "docker-entrypoint.s…"   7 months ago   Up 4 seconds   33060/tcp, 0.0.0.0:3506->3306/tcp, :::3506->3306/tcp   mysql-dev0
```
##### Deploy Spring Boot REST JPA Application
```
# create container

# restart container
ubuntu@mini5:~/obrienlabsdev/biometric-backend$ docker start biometric-nbi
biometric-nbi
ubuntu@mini5:~/obrienlabsdev/biometric-backend$ docker ps
CONTAINER ID   IMAGE                                 COMMAND                  CREATED        STATUS         PORTS                                                  NAMES
5329c8017261   obrienlabs/biometric-nbi:0.0.2-ia64   "java -Djava.securit…"   5 months ago   Up 3 seconds   0.0.0.0:8888->8080/tcp, :::8888->8080/tcp              biometric-nbi
35b726a994a5   mysql:8.0.38                          "docker-entrypoint.s…"   7 months ago   Up 2 minutes   33060/tcp, 0.0.0.0:3506->3306/tcp, :::3506->3306/tcp   mysql-dev0
```

##### Verify API is visible from outside
```
2025-03-30 17:35:49.067 DEBUG 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : GET "/nbi/api/getGps?&u=20250330&de=iph10&pr=18.299999&lg=-75.940366&lt=45.343882&al=105.475247&ac=15.579314&be=136&s=-1.000000&grx=0.000000&gry=0.000000&grz=0.000000&arx=0.083786&ary=-0.241714&arz=-0.847626&lax=0.083786&lay=-0.241714&laz=-0.847626&rvx=-0.187668&rvy=0.254351&rvz=0.077352&ts=1743356148869&mfx=-45.306244&mfy=-65.799034&mfz=-21.953857&up=5", parameters={masked}
2025-03-30 17:35:49.069 DEBUG 1 --- [nio-8080-exec-1] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to dev.obrienlabs.biometric.nbi.controller.ApiController#getGps(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, HttpServletRequest)
f21czytgm0qb
2025-03-30 17:35:49.118 DEBUG 1 --- [nio-8080-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Using 'text/plain', given [*/*] and supported [text/plain, */*, text/plain, */*, application/json, application/*+json, application/json, application/*+json]
2025-03-30 17:35:49.118 DEBUG 1 --- [nio-8080-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Writing ["OK:f21czytgm0qb:Record(15778365,20250330,5,0,null,null,45.343882,-75.940366,136,105.475247,174335614 (truncated)..."]
2025-03-30 17:35:49.119 DEBUG 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
```
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

For the problem where the column names were auto converting from tsStart to ts_start.
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

If you encounter warnings about restricted native access when running Maven with Java 21 or newer, create a `.mvn/jvm.config` file containing:
```
--enable-native-access=ALL-UNNAMED
```
This allows Maven to load the native library used by Jansi without warnings.


## Deployment

```

```

## Debugging

http://127.0.0.1:8080/nbi/api


<img width="1712" alt="Screenshot 2024-05-05 at 18 57 33" src="https://github.com/ObrienlabsDev/biometric-backend/assets/24765473/a373fec1-a001-4bf4-ad9a-562dd4f1d321">

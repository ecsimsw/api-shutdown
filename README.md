# api-shutdown

API는 정의되었지만 더 이상 사용되지 않거나 임시로 막아둔 API 의 응답을 자동으로 생성한다.    

latest version : 0.0.4

## 기능들 
- 조건을 만족하면 정의된 핸들러의 HttpMethod 와 RequestPath 에 맞춰 ShutDown 응답을 반환한다.
- 조건은 Profile 정보, Property 존재 여부, 빈 존재 여부로 정의한다.
- 컨트롤러별 ShutDown 시 응답 메시지, 상태 코드, 컨텐츠 타입을 지정할 수 있다.
- 전역 설정으로 기본 응답 형식들을 설정할 수 있다.

## 미리 보기

``` java
@ShutDown(
    conditionOnActiveProfile = "backup",
    message = "This API is currently unavailable.",
    status = HttpStatus.SERVICE_UNAVAILABLE,
    contentType = MediaType.APPLICATION_JSON_VALUE
)
@RestController
class ShutDownController {

    @GetMapping("/api/a")
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("Hi");
    }

    @PostMapping("/api/b")
    public ResponseEntity<String> hey() {
        return ResponseEntity.ok("Hey");
    }
}
```

Active profile 이 backup 인 상황에서 핸들러에 정의된 API 는 정의된 shutDown 상태를 응답한다.                      
```
HTTP status : 503, SERVICE_UNAVAILABLE
Content type : application/json
Message : This API is currently unavailable.
```

## 사용 방법

### build.gradle 

라이브러리 의존성을 추가한다.     
javax 를 사용하는 JDK 16 이하에선 javax-X.X.X 를 사용한다.
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ecsimsw:api-shutdown:0.0.4'
    // implementation 'com.github.ecsimsw:api-shutdown:javax-0.0.4'       // for Versions lower than java17
}
```

### @EnableShutDown 추가

@EnableShutDown 로 라이브러리 사용을 활성화한다.     

``` java
@EnableShutDown
@SpringBootApplication
public class MyApplication {}
```

### ShutDown 정의 

컨트롤러에 @ShutDown 어노테이션을 명시한다.     

ShutDown 시 정의된 핸들러들의 HttpMethod 와 Request path 의 요청에 @ShutDown 정보로 응답이 반환된다.

``` java
@ShutDown(
    conditionOnActiveProfile = "backup",
    message = "This API is currently unavailable.",
    status = HttpStatus.SERVICE_UNAVAILABLE,
    contentType = MediaType.APPLICATION_JSON_VALUE
)
@RestController
class ShutDownController {

    @GetMapping("/api/shutDownGet")
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("Hi");
    }

    @PostMapping("/api/shutDownPost")
    public ResponseEntity<String> hey() {
        return ResponseEntity.ok("Hey");
    }
}
```

핸들러는 `@GetMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`, `@RequestMapping` 를 지원하고 여러 UrlPath, HttpMethod 를 지정할 수 있다.    

``` java
@RequestMapping(
    value = {
        TestApis.API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_1,
        TestApis.API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_2
    },
    method = { RequestMethod.GET, RequestMethod.POST }
)
public ResponseEntity<String> api1() {
    return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
}
```

## 추가 기능 

### 1. Default 설정 변경 - Global configuration

ShutDownGlobalConfig 빈을 등록하는 것으로 기본값을 설정할 수 있다.
@ShutDown 에 직접 값을 입력하는 경우 해당 값이 우선시 되고, 값을 입력하지 않는 경우 Global config 에 정의된 값으로 설정된다.         

``` java
@EnableShutDown
@Configuration
public class ShutDownConfig {

    @Bean
    public ShutDownGlobalConfig shutDown() {
        return new ShutDownGlobalConfigBuilder()
            .message("Global config message")
            .status(HttpStatus.TEMPORARY_REDIRECT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
```

### 2. 설정 가능한 ShutDown 조건들

ShutDown 조건을 설정할 수 있다.    

- conditionOnActiveProfile : 정의된 Profile 들이 모두 Activate 되어 있는 경우 ShutDown 된다.
- conditionOnProperties : 정의된 Property 들이 모두 Application property 로 등록되어 있는 경우 ShutDown 된다.
- conditionOnBean : 정의된 Type 들이 모두 빈으로 등록되어 있는 경우 ShutDown 된다.
- conditionOnMissingBean : 정의된 Type 들이 모두 빈으로 등록되어 있지 않은 경우 ShutDown 된다.
- force : 앞선 어떤 조건들과 상관없이 true 면 해당 핸들러들은 모두 ShutDown 된다.

``` java
public @interface ShutDown {

    // Shut down when all the profiles are activated
    String[] conditionOnActiveProfile() default {};

    // Shut down when all the properties exist
    String[] conditionOnProperties() default {};

    // Shut down when all the beans exist
    Class<?>[] conditionOnBean() default {};

    // Shut down when all the beans not exist
    Class<?>[] conditionOnMissingBean() default {};

    // Force shutdown ignoring other conditions
    boolean force() default false;
}
```

### 3. ShutDown filter

BeanFactoryPostProcessor 가 실행되는 과정에서 ShutDown 조건을 확인하고 이에 부합하면 Filter 를 생성하게 된다.    

해당 Filter 의 Order와 Name 설정은 ShutDownGlobalConfig 으로 기본 값을 변경할 수 있다.

``` java
@Configuration
public class ShutDownConfig {

    @Bean
    public ShutDownGlobalConfig shutDown() {
        return new ShutDownGlobalConfigBuilder()
            .filterOrder(1)
            .filterPrefix("myShutDownFilter")
            .build();  
    }
}
```

## 추가 예정 사항

#### 1. @ShutDown 핸들러

컨트롤러만이 아닌 핸들러를 기준으로도 어노테이션이 동작할 수 있도록 한다.    

``` java
@ShutDown(
  message = "서버 상태 이상으로 현재 사용 불가능한 API 입니다.",
  status = HttpStatus.INTERNAL_SERVER_ERROR
)
@RestController
class ShutDownController {

    @ShutDown(
      message = "hi는 더이상 사용되지 않습니다.",
      status = HttpStatus.PERMANENT_REDIRECT
    )
    @GetMapping("/api/shutDownGet")
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("Hi");
    }

    @PostMapping("/api/shutDownPost")
    public ResponseEntity<String> hey() {
        return ResponseEntity.ok("Hey");
    }
}
```

#### 2. 보다 구체적인 ShutDown 조건, Filter match 조건

- Params
- Headers
- Consumes
- Produces

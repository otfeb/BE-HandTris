# BE-HandTris
👉 핸드 모션 캡처로 즐기는 실시간 1:1 대결 테트리스

## 프로젝트 아키텍쳐
<img width="960" alt="핸드트릭스_중간발표_아키텍처" src="https://github.com/user-attachments/assets/0d824576-b7b4-4656-83c9-8c07e36b06e2">


## ERD
![image](https://github.com/HandTris/BE-HandTris/raw/dev/docs/images/ERD.png)

## Use Case
![image](https://github.com/HandTris/BE-HandTris/raw/dev/docs/images/UseCase.png)

# 0. 개발 환경, 언어, 도구

- Intellij
- Data JPA
- JDK 21
- Mysql
- Redis (Cache DB)
- Docker
- Terraform
- Github Action
- Junit
- Mockito
- Prometheus
- Grafana
- PostMan (API Test)

# 1. 코딩 컨벤션

## Entity Class

- [x] 필드명은 전부 `camelCase` 로 작성
- [x] Class Name은 `PascalCase` 로 작성

## Package Naming

- `Domain` 별로 패키지를 분리한다.
  - ex.) `회원`, `채팅방`이 존재한다면 `member`,`chat`등의 큰 단위의 Domain으로 분리한다.
- Controller는 `api`,Service는 `application`,Repository는 `repo`로 Naming하여 기능별로 분리
  - Service의 경우 `impl`과 `service`로 나누어 `DIP` 준수
- DTO와 Entity는 별도의 패키지에서 관리하며, DTO내부는 Request와 Response 용도에 따라 패키지를 따로 두어 분리한다.
  - DTO는 `Inner Class` 방식을 사용한다.
  - Naming의 경우 `MemberDTO`의 경우, 회원가입과 정보조회 DTO는 다음과 같이 `SignUp`,`CheckInfo`로 정정한다.
- **ex)**

```markdown
root
├── README.md
├── build
│   ├── classes
│   │   └── java
│   │       └── main
│   │           └── jungle
│   │               └── HandTris
│   │                   ├── HandTrisApplication.class
│   │                   ├── application
│   │                   │   ├── impl
│   │                   │   │   ├── GameRoomServiceImpl.class
│   │                   │   │   ├── MemberProfileServiceImpl.class
│   │                   │   │   ├── MemberRecordServiceImpl.class
│   │                   │   │   ├── MemberServiceImpl.class
│   │                   │   │   ├── S3UploaderServiceImpl.class
│   │                   │   │   └── TetrisServiceImpl.class
│   │                   │   └── service
│   │                   │       ├── ExpirationListener.class
│   │                   │       ├── GameRoomService.class
│   │                   │       ├── MemberProfileService.class
│   │                   │       ├── MemberRecordService.class
│   │                   │       ├── MemberService.class
│   │                   │       ├── S3UploaderService.class
│   │                   │       └── TetrisService.class
│   │                   ├── domain
│   │                   │   ├── GameMember.class
│   │                   │   ├── GameRoom.class
│   │                   │   ├── GameStatus.class
│   │                   │   ├── Member.class
│   │                   │   ├── MemberRecord.class
│   │                   │   ├── exception
│   │                   │   │   ├── AccessTokenExpiredException.class
│   │                   │   │   ├── ConnectedMemberNotFoundException.class
│   │                   │   │   ├── DuplicateNicknameException.class
│   │                   │   │   ├── DuplicateUsernameException.class
│   │                   │   │   ├── FileConversionException.class
│   │                   │   │   ├── GameRoomNotFoundException.class
│   │                   │   │   ├── ImageUploadException.class
│   │                   │   │   ├── InvalidImageTypeException.class
│   │                   │   │   ├── InvalidTokenFormatException.class
│   │                   │   │   ├── MemberNotFoundException.class
│   │                   │   │   ├── MemberRecordNotFoundException.class
│   │                   │   │   ├── NicknameNotChangedException.class
│   │                   │   │   ├── ParticipantLimitedException.class
│   │                   │   │   ├── PasswordMismatchException.class
│   │                   │   │   ├── PlayingGameException.class
│   │                   │   │   ├── RefreshTokenExpiredException.class
│   │                   │   │   ├── TokenNotProvideException.class
│   │                   │   │   ├── UnauthorizedAccessException.class
│   │                   │   │   └── UserNotFoundException.class
│   │                   │   └── repo
│   │                   │       ├── GameMemberRepository.class
│   │                   │       ├── GameRoomRepository.class
│   │                   │       ├── MemberRecordRepository.class
│   │                   │       └── MemberRepository.class
│   │                   ├── global
│   │                   │   ├── config
│   │                   │   │   ├── FilterConfig.class
│   │                   │   │   ├── JasyptConfig.class
│   │                   │   │   ├── RedisConfig.class
│   │                   │   │   ├── S3Config.class
│   │                   │   │   ├── WebConfig$1.class
│   │                   │   │   ├── WebConfig.class
│   │                   │   │   ├── log
│   │                   │   │   │   ├── CachedBodyRequestWrapper.class
│   │                   │   │   │   └── CachedBodyServletInputStream.class
│   │                   │   │   ├── security
│   │                   │   │   │   └── SecurityConfig.class
│   │                   │   │   └── ws
│   │                   │   │       └── StompConfig.class
│   │                   │   ├── discord
│   │                   │   │   ├── DiscordAppender.class
│   │                   │   │   ├── DiscordWebHook.class
│   │                   │   │   └── model
│   │                   │   │       ├── Author.class
│   │                   │   │       ├── EmbedObject.class
│   │                   │   │       ├── Field.class
│   │                   │   │       ├── Footer.class
│   │                   │   │       ├── Image.class
│   │                   │   │       ├── JsonObject.class
│   │                   │   │       └── Thumbnail.class
│   │                   │   ├── dto
│   │                   │   │   ├── ResponseEnvelope.class
│   │                   │   │   ├── WhiteListURI.class
│   │                   │   │   └── discord
│   │                   │   │       └── DiscordConstants.class
│   │                   │   ├── exception
│   │                   │   │   ├── DiscordException.class
│   │                   │   │   ├── DiscordLogException.class
│   │                   │   │   ├── ErrorCode.class
│   │                   │   │   ├── GlobalExceptionHandler$FieldError.class
│   │                   │   │   └── GlobalExceptionHandler.class
│   │                   │   ├── filter
│   │                   │   │   ├── JWTFilter.class
│   │                   │   │   ├── MDCFilter.class
│   │                   │   │   └── ServletWrappingFilter.class
│   │                   │   ├── handler
│   │                   │   │   ├── JWTAccessDeniedHandler.class
│   │                   │   │   ├── JWTAuthenticateDeniedHandler.class
│   │                   │   │   └── StompHandler.class
│   │                   │   ├── jwt
│   │                   │   │   └── JWTUtil.class
│   │                   │   ├── util
│   │                   │   │   ├── ApiCallUtil.class
│   │                   │   │   ├── HttpRequestUtil.class
│   │                   │   │   ├── MDCUtil.class
│   │                   │   │   └── StringUtil.class
│   │                   │   └── validation
│   │                   │       ├── EnumValidator.class
│   │                   │       ├── UserNameFromJwtResolver.class
│   │                   │       ├── UserNicknameFromJwt.class
│   │                   │       └── ValidEnum.class
│   │                   └── presentation
│   │                       ├── AuthController.class
│   │                       ├── GameRoomController.class
│   │                       ├── HealthCheckController.class
│   │                       ├── MemberController.class
│   │                       ├── MemberRecordController.class
│   │                       ├── TetrisController.class
│   │                       ├── TokenController.class
│   │                       └── dto
│   │                           ├── request
│   │                           │   ├── GameMemberEssentialDTO.class
│   │                           │   ├── GameResult.class
│   │                           │   ├── GameResultReq.class
│   │                           │   ├── GameRoomDetailReq.class
│   │                           │   ├── MemberRequest.class
│   │                           │   ├── MemberUpdateReq.class
│   │                           │   ├── RoomMemberNicknameDTO.class
│   │                           │   ├── RoomStateReq.class
│   │                           │   └── TetrisMessageReq.class
│   │                           └── response
│   │                               ├── CustomMemberDetails.class
│   │                               ├── GameRoomDetailRes.class
│   │                               ├── MemberDetailRes.class
│   │                               ├── MemberDetailResWithTokenRes.class
│   │                               ├── MemberProfileDetailsRes.class
│   │                               ├── MemberProfileUpdateDetailsRes.class
│   │                               ├── MemberRecordDetailRes.class
│   │                               ├── RoomOwnerRes.class
│   │                               └── RoomStateRes.class
│   ├── resources
│   │   └── main
│   │       ├── application-S3.yml
│   │       ├── application-dev.yml
│   │       ├── application-jwt.yml
│   │       ├── application-prod.yml
│   │       ├── application.yml
│   │       ├── console-appender.xml
│   │       ├── db
│   │       │   └── migration
│   │       │       └── V1__init.sql
│   │       ├── discord-appender.xml
│   │       ├── logback-dev.xml
│   │       └── logback-prod.xml
├── build.gradle
├── docker
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── monitor
│       ├── grafana
│       │   └── dashboards
│       │       └── dashboards.yml
│       └── prometheus
│           └── prometheus.yml
├── docs
│   ├── API_Documentation.md
│   ├── ERD.md
│   ├── UseCase.md
│   └── images
│       ├── ApiDocumentation.png
│       ├── ERD.png
│       └── UseCase.png
├── infra
│   ├── alb.tf
│   ├── ec2.tf
│   ├── handtris.pem
│   ├── network.tf
│   ├── provider.tf
│   ├── rds.tf
│   ├── route.tf
│   ├── variable.tf
│   └── version.tf
├── out
│   ├── production
│   │   ├── classes
│   │   │   └── jungle
│   │   │       └── HandTris
│   │   │           ├── HandTrisApplication.class
│   │   │           ├── application
│   │   │           │   ├── impl
│   │   │           │   │   ├── GameRoomServiceImpl.class
│   │   │           │   │   ├── MemberProfileServiceImpl.class
│   │   │           │   │   ├── MemberRecordServiceImpl.class
│   │   │           │   │   ├── MemberServiceImpl.class
│   │   │           │   │   ├── S3UploaderServiceImpl.class
│   │   │           │   │   └── TetrisServiceImpl.class
│   │   │           │   └── service
│   │   │           │       ├── ExpirationListener.class
│   │   │           │       ├── GameRoomService.class
│   │   │           │       ├── MemberProfileService.class
│   │   │           │       ├── MemberRecordService.class
│   │   │           │       ├── MemberService.class
│   │   │           │       ├── S3UploaderService.class
│   │   │           │       └── TetrisService.class
│   │   │           ├── domain
│   │   │           │   ├── GameMember.class
│   │   │           │   ├── GameRoom.class
│   │   │           │   ├── GameStatus.class
│   │   │           │   ├── Member.class
│   │   │           │   ├── MemberRecord.class
│   │   │           │   ├── exception
│   │   │           │   │   ├── AccessTokenExpiredException.class
│   │   │           │   │   ├── ConnectedMemberNotFoundException.class
│   │   │           │   │   ├── DestinationUrlNotFoundException.class
│   │   │           │   │   ├── DuplicateNicknameException.class
│   │   │           │   │   ├── DuplicateUsernameException.class
│   │   │           │   │   ├── FileConversionException.class
│   │   │           │   │   ├── GameRoomNotFoundException.class
│   │   │           │   │   ├── ImageUploadException.class
│   │   │           │   │   ├── InvalidImageTypeException.class
│   │   │           │   │   ├── InvalidTokenFormatException.class
│   │   │           │   │   ├── MemberNotFoundException.class
│   │   │           │   │   ├── MemberRecordNotFoundException.class
│   │   │           │   │   ├── NicknameNotChangedException.class
│   │   │           │   │   ├── ParticipantLimitedException.class
│   │   │           │   │   ├── PasswordMismatchException.class
│   │   │           │   │   ├── PlayingGameException.class
│   │   │           │   │   ├── RefreshTokenExpiredException.class
│   │   │           │   │   ├── TokenNotProvideException.class
│   │   │           │   │   ├── UnauthorizedAccessException.class
│   │   │           │   │   └── UserNotFoundException.class
│   │   │           │   └── repo
│   │   │           │       ├── GameMemberRepository.class
│   │   │           │       ├── GameRoomRepository.class
│   │   │           │       ├── MemberRecordRepository.class
│   │   │           │       └── MemberRepository.class
│   │   │           ├── global
│   │   │           │   ├── config
│   │   │           │   │   ├── FilterConfig.class
│   │   │           │   │   ├── JasyptConfig.class
│   │   │           │   │   ├── RedisConfig.class
│   │   │           │   │   ├── S3Config.class
│   │   │           │   │   ├── WebConfig$1.class
│   │   │           │   │   ├── WebConfig.class
│   │   │           │   │   ├── log
│   │   │           │   │   │   ├── CachedBodyRequestWrapper.class
│   │   │           │   │   │   └── CachedBodyServletInputStream.class
│   │   │           │   │   ├── security
│   │   │           │   │   │   └── SecurityConfig.class
│   │   │           │   │   └── ws
│   │   │           │   │       ├── StompConfig.class
│   │   │           │   │       └── User.class
│   │   │           │   ├── discord
│   │   │           │   │   ├── DiscordAppender.class
│   │   │           │   │   ├── DiscordWebHook.class
│   │   │           │   │   └── model
│   │   │           │   │       ├── Author.class
│   │   │           │   │       ├── EmbedObject.class
│   │   │           │   │       ├── Field.class
│   │   │           │   │       ├── Footer.class
│   │   │           │   │       ├── Image.class
│   │   │           │   │       ├── JsonObject.class
│   │   │           │   │       └── Thumbnail.class
│   │   │           │   ├── dto
│   │   │           │   │   ├── ResponseEnvelope.class
│   │   │           │   │   ├── WhiteListURI.class
│   │   │           │   │   └── discord
│   │   │           │   │       └── DiscordConstants.class
│   │   │           │   ├── exception
│   │   │           │   │   ├── DiscordException.class
│   │   │           │   │   ├── DiscordLogException.class
│   │   │           │   │   ├── ErrorCode.class
│   │   │           │   │   ├── GlobalExceptionHandler$FieldError.class
│   │   │           │   │   └── GlobalExceptionHandler.class
│   │   │           │   ├── filter
│   │   │           │   │   ├── JWTFilter.class
│   │   │           │   │   ├── MDCFilter.class
│   │   │           │   │   └── ServletWrappingFilter.class
│   │   │           │   ├── handler
│   │   │           │   │   ├── JWTAccessDeniedHandler.class
│   │   │           │   │   ├── JWTAuthenticateDeniedHandler.class
│   │   │           │   │   └── StompHandler.class
│   │   │           │   ├── jwt
│   │   │           │   │   └── JWTUtil.class
│   │   │           │   ├── util
│   │   │           │   │   ├── ApiCallUtil.class
│   │   │           │   │   ├── HttpRequestUtil.class
│   │   │           │   │   ├── MDCUtil.class
│   │   │           │   │   └── StringUtil.class
│   │   │           │   └── validation
│   │   │           │       ├── EnumValidator.class
│   │   │           │       ├── UserNameFromJwtResolver.class
│   │   │           │       ├── UserNicknameFromJwt.class
│   │   │           │       └── ValidEnum.class
│   │   │           └── presentation
│   │   │               ├── AuthController.class
│   │   │               ├── GameRoomController.class
│   │   │               ├── HealthCheckController.class
│   │   │               ├── MemberController.class
│   │   │               ├── MemberRecordController.class
│   │   │               ├── TetrisController.class
│   │   │               ├── TokenController.class
│   │   │               └── dto
│   │   │                   ├── request
│   │   │                   │   ├── GameMemberEssentialDTO.class
│   │   │                   │   ├── GameResult.class
│   │   │                   │   ├── GameResultReq.class
│   │   │                   │   ├── GameRoomDetailReq.class
│   │   │                   │   ├── MemberRequest.class
│   │   │                   │   ├── MemberUpdateReq.class
│   │   │                   │   ├── RoomMemberNicknameDTO.class
│   │   │                   │   ├── RoomStateReq.class
│   │   │                   │   └── TetrisMessageReq.class
│   │   │                   └── response
│   │   │                       ├── CustomMemberDetails.class
│   │   │                       ├── GameRoomDetailRes.class
│   │   │                       ├── MemberDetailRes.class
│   │   │                       ├── MemberDetailResWithTokenRes.class
│   │   │                       ├── MemberProfileDetailsRes.class
│   │   │                       ├── MemberProfileUpdateDetailsRes.class
│   │   │                       ├── MemberRecordDetailRes.class
│   │   │                       ├── RoomOwnerRes.class
│   │   │                       └── RoomStateRes.class
│   │   └── resources
│   │       ├── application-S3.yml
│   │       ├── application-dev.yml
│   │       ├── application-jwt.yml
│   │       ├── application-prod.yml
│   │       ├── application.yml
│   │       ├── console-appender.xml
│   │       ├── db
│   │       │   ├── locust_dummy.sql
│   │       │   └── migration
│   │       │       ├── V1__init.sql
│   │       │       └── V2__delete_avg_time_member_record.sql
│   │       ├── discord-appender.xml
│   │       ├── logback-dev.xml
│   │       └── logback-prod.xml
│   └── test
│       ├── classes
│       │   ├── generated_tests
│       │   └── jungle
│       │       └── HandTris
│       │           ├── GameRoom
│       │           │   └── MockGameRoomServiceTests.class
│       │           ├── HandTrisApplicationTests.class
│       │           └── JasyptTest.class
│       └── resources
│           └── application.yml
└── src
    ├── main
    │   ├── generated
    │   ├── java
    │   │   └── jungle
    │   │       └── HandTris
    │   │           ├── HandTrisApplication.java
    │   │           ├── application
    │   │           │   ├── dto
    │   │           │   ├── impl
    │   │           │   │   ├── AuthServiceImpl.java
    │   │           │   │   ├── BCryptPasswordServiceImpl.java
    │   │           │   │   ├── CustomNicknameServiceImpl.java
    │   │           │   │   ├── GameRoomServiceImpl.java
    │   │           │   │   ├── MemberProfileServiceImpl.java
    │   │           │   │   ├── MemberRecordServiceImpl.java
    │   │           │   │   ├── ReissueServiceImpl.java
    │   │           │   │   ├── S3UploaderServiceImpl.java
    │   │           │   │   └── TetrisServiceImpl.java
    │   │           │   └── service
    │   │           │       ├── AuthService.java
    │   │           │       ├── BCryptPasswordService.java
    │   │           │       ├── CustomNicknameService.java
    │   │           │       ├── CustomOAuth2MemberService.java
    │   │           │       ├── ExpirationListener.java
    │   │           │       ├── GameRoomService.java
    │   │           │       ├── MemberProfileService.java
    │   │           │       ├── MemberRecordService.java
    │   │           │       ├── ReissueService.java
    │   │           │       ├── S3UploaderService.java
    │   │           │       └── TetrisService.java
    │   │           ├── domain
    │   │           │   ├── GameMember.java
    │   │           │   ├── GameRoom.java
    │   │           │   ├── GameStatus.java
    │   │           │   ├── Member.java
    │   │           │   ├── MemberRecord.java
    │   │           │   ├── exception
    │   │           │   │   ├── AccessTokenExpiredException.java
    │   │           │   │   ├── ConnectedMemberNotFoundException.java
    │   │           │   │   ├── DestinationUrlNotFoundException.java
    │   │           │   │   ├── DuplicateNicknameException.java
    │   │           │   │   ├── DuplicateUsernameException.java
    │   │           │   │   ├── FileConversionException.java
    │   │           │   │   ├── GameRoomNotFoundException.java
    │   │           │   │   ├── ImageUploadException.java
    │   │           │   │   ├── InvalidImageTypeException.java
    │   │           │   │   ├── InvalidSocialLoginExcepion.java
    │   │           │   │   ├── InvalidTokenFormatException.java
    │   │           │   │   ├── MemberNotFoundException.java
    │   │           │   │   ├── MemberRecordNotFoundException.java
    │   │           │   │   ├── NicknameNotChangedException.java
    │   │           │   │   ├── ParticipantLimitedException.java
    │   │           │   │   ├── PasswordMismatchException.java
    │   │           │   │   ├── PlayingGameException.java
    │   │           │   │   ├── RefreshTokenExpiredException.java
    │   │           │   │   ├── TokenNotProvideException.java
    │   │           │   │   ├── UnauthorizedAccessException.java
    │   │           │   │   └── UserNotFoundException.java
    │   │           │   └── repo
    │   │           │       ├── GameMemberRepository.java
    │   │           │       ├── GameRoomRepository.java
    │   │           │       ├── MemberRecordRepository.java
    │   │           │       └── MemberRepository.java
    │   │           ├── global
    │   │           │   ├── config
    │   │           │   │   ├── FilterConfig.java
    │   │           │   │   ├── JasyptConfig.java
    │   │           │   │   ├── RedisConfig.java
    │   │           │   │   ├── S3Config.java
    │   │           │   │   ├── WebConfig.java
    │   │           │   │   ├── log
    │   │           │   │   │   ├── CachedBodyRequestWrapper.java
    │   │           │   │   │   └── CachedBodyServletInputStream.java
    │   │           │   │   ├── security
    │   │           │   │   │   └── SecurityConfig.java
    │   │           │   │   └── ws
    │   │           │   │       ├── StompConfig.java
    │   │           │   │       └── User.java
    │   │           │   ├── discord
    │   │           │   │   ├── DiscordAppender.java
    │   │           │   │   ├── DiscordWebHook.java
    │   │           │   │   └── model
    │   │           │   │       ├── Author.java
    │   │           │   │       ├── EmbedObject.java
    │   │           │   │       ├── Field.java
    │   │           │   │       ├── Footer.java
    │   │           │   │       ├── Image.java
    │   │           │   │       ├── JsonObject.java
    │   │           │   │       └── Thumbnail.java
    │   │           │   ├── dto
    │   │           │   │   ├── ResponseEnvelope.java
    │   │           │   │   ├── WhiteListURI.java
    │   │           │   │   └── discord
    │   │           │   │       └── DiscordConstants.java
    │   │           │   ├── exception
    │   │           │   │   ├── DiscordException.java
    │   │           │   │   ├── DiscordLogException.java
    │   │           │   │   ├── ErrorCode.java
    │   │           │   │   └── GlobalExceptionHandler.java
    │   │           │   ├── filter
    │   │           │   │   ├── JWTFilter.java
    │   │           │   │   ├── MDCFilter.java
    │   │           │   │   └── ServletWrappingFilter.java
    │   │           │   ├── handler
    │   │           │   │   ├── JWTAccessDeniedHandler.java
    │   │           │   │   ├── JWTAuthenticateDeniedHandler.java
    │   │           │   │   ├── OAuth2FailureHandler.java
    │   │           │   │   ├── OAuth2SuccessHandler.java
    │   │           │   │   └── StompHandler.java
    │   │           │   ├── jwt
    │   │           │   │   └── JWTUtil.java
    │   │           │   ├── util
    │   │           │   │   ├── ApiCallUtil.java
    │   │           │   │   ├── HttpRequestUtil.java
    │   │           │   │   ├── MDCUtil.java
    │   │           │   │   └── StringUtil.java
    │   │           │   └── validation
    │   │           │       ├── EnumValidator.java
    │   │           │       ├── UserNameFromJwtResolver.java
    │   │           │       ├── UserNicknameFromJwt.java
    │   │           │       └── ValidEnum.java
    │   │           └── presentation
    │   │               ├── AuthController.java
    │   │               ├── GameRoomController.java
    │   │               ├── HealthCheckController.java
    │   │               ├── MemberProfileController.java
    │   │               ├── MemberRecordController.java
    │   │               ├── ReissueController.java
    │   │               ├── TetrisController.java
    │   │               └── dto
    │   │                   ├── request
    │   │                   │   ├── GameMemberEssentialDTO.java
    │   │                   │   ├── GameResult.java
    │   │                   │   ├── GameResultReq.java
    │   │                   │   ├── GameRoomDetailReq.java
    │   │                   │   ├── MemberRequest.java
    │   │                   │   ├── MemberUpdateReq.java
    │   │                   │   ├── RoomStateReq.java
    │   │                   │   └── TetrisMessageReq.java
    │   │                   └── response
    │   │                       ├── CustomMemberDetails.java
    │   │                       ├── CustomOAuth2Member.java
    │   │                       ├── GameRoomDetailRes.java
    │   │                       ├── MemberDetailRes.java
    │   │                       ├── MemberDetailResWithTokenRes.java
    │   │                       ├── MemberProfileDetailsRes.java
    │   │                       ├── MemberProfileRes.java
    │   │                       ├── MemberProfileUpdateDetailsRes.java
    │   │                       ├── MemberRecordDetailRes.java
    │   │                       ├── OAuth2MemberDetailRes.java
    │   │                       ├── ReissueTokenRes.java
    │   │                       ├── RoomOwnerRes.java
    │   │                       └── RoomStateRes.java
    │   └── resources
    │       ├── application-S3.yml
    │       ├── application-dev.yml
    │       ├── application-jwt.yml
    │       ├── application-oauth-dev.yml
    │       ├── application-oauth-prod.yml
    │       ├── application-prod.yml
    │       ├── application.yml
    │       ├── console-appender.xml
    │       ├── db
    │       │   └── migration
    │       │       ├── V1__init.sql
    │       │       ├── V2__delete_avg_time_member_record.sql
    │       │       └── V3__delete_game_status.sql
    │       ├── discord-appender.xml
    │       ├── logback-dev.xml
    │       └── logback-prod.xml
```
  
## File Naming

- YML파일은 `Kebab Case` 로 Naming한다.
- DB 설정 및  프로젝트에 필요한 설정들은 `application.yml` 에 저장하여준다.
- jwt,aws,mail등을 별도의 yml파일을 만들어 관리한다.

## API 요청 URl

- 동사를 사용하지 않는다.
- RestFul한 네이밍을 사용한다.
- ex.) ${domain_name}s/

## DB & CI/CD
- AWS EC2에 운영서버를 운영하며 Github Action과 Docker를 이용하여 CI/CD를 구축
- Mysql DB는 RDS에 운영중이며, 같은 VPC가 아니라면 접근불가

# 2. 레파지토리 관리 기법

Organization에서 BackEnd 별도의 레파지토리에서 관리합니다.

### **⚙️** 브랜치 관리 전략

### **⚙️ Git-flow**
![trunked_base_development](https://www.optimizely.com/contentassets/569ac3ee0b124da19a5ac9ea2e8b2b4d/trunk-based-development.png)

**⚙️ 브랜치 구성**
- `feat` 브랜치는 하나의 기능을 개발하기 위한 브랜치입니다. 부모는`develop`이며, 개발이 완료되면`develop`에 merge합니다. 브랜치 이름은 보통`feature/*`이 됩니다.
- `dev` 브랜치는 개발을 위한 브랜치입니다. 여러`feature`들이 merge되는 장소이며, 아직 release되지 않은 기능들이 모여 있게 됩니다.
- `main`브랜치는 실제 운영 중인 서비스의 브랜치입니다.
- `hotfix`브랜치는 서비스에 문제가 발생했을 때 핫픽스에 해당하는 브랜치입니다. 기능 개발(`feature`) 등과 달리 빠르게 대처해야 할 필요가 있기 때문에,`master`브랜치에 직접 merge하는 전략을 취합니다.`dev`과의 차이가 발생하기 때문에, 나중에 차이를 merge할 필요가 있습니다.

## 🛠 Git Branch workflow
![image](https://github.com/DY-WhatSong/BE-What_Song/assets/39437170/70a022bc-ff52-48cd-b8a5-994d6b2b15f6)

- `Squash and Merge` 방법을 차용하여 `Issue`,`Feature` 단위 커밋을 통해 Merge시 진행 사항 구조 손쉽게 파악하는 것을 목적으로한다.
- 각 branch PR시 `이슈화`하여 코드에 대한 피드백 및 토론 후 상대방이 `Merge`

### 브랜치 네이밍

**⚙️ 네이밍 패턴**

`#이슈번호`

**Ex)** 이슈번호가 67인 '로그인 기능' 이슈를 구현하는 브랜치를 생성하는 경우, 브랜치 이름을`#67`로 작성한다.

### 커밋 메시지

**⚙ 메시지 구조**

`Type : 제목 #이슈번호`

`본문`

**Ex)**이슈번호가 67인 이슈의 기능을 구현한 뒤 커밋을 하는 상황이라면 커밋 메시지의 제목을`feat : A기능 구현 #67`으로 작성한다.

**⚙ Type**

- `feat` : 새로운 기능에 대한 커밋
- `fix`	: 버그 수정에 대한 커밋
- `docs` : 문서 수정에 대한 커밋
- `style` : 코드 스타일 혹은 포맷 등에 관한 커밋
- `refactor` : 코드 리팩토링에 대한 커밋
- `test` : 테스트 코드 수정에 대한 커밋
- `chore` : 패키지 관련 및 빌드코드 수정

# 3. 소개 포스터
- 사진


# 개발기간

2024 06 20 ~ 2024 07 20


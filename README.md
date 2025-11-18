# AI 기반 재난 예측 API 서버 (Spring Boot)

이 프로젝트는 3-Tier 아키텍처(React-Spring Boot-Flask)의 **메인 API 서버**입니다. React 클라이언트의 요청을 받아 인증/인가를 처리하고, 외부 Flask AI 서버와 통신하여 예측 결과를 반환하는 핵심 허브 역할을 수행합니다.

# 1. 프로젝트 개요

* **보안 (Security)**: Spring Security와 JWT (JSON Web Token)를 이용한 강력한 인증/인가 시스템을 제공합니다.
* **AI 예측**: 외부 Flask AI 서버와 연동하여 특정 데이터(예: 위도, 경도)를 기반으로 재난(지진 등) 발생 가능성을 예측하는 회귀(Regression) 모델 결과를 제공합니다.
* **사용자 관리**: 회원 가입, 로그인, 소셜 로그인(OAuth2), 비밀번호 변경 등 필수적인 사용자 관리 기능을 API 형태로 제공합니다.

전체적으로 **Stateless한 RESTful API 서버**로 설계되었으며, React와 같은 최신 프론트엔드 프레임워크와의 연동을 고려하여 CORS(Cross-Origin Resource Sharing) 정책이 설정되어 있습니다.

## 2. 시스템 아키텍처

프로젝트는 역할 분리를 위해 계층형 아키텍처(Layered Architecture)를 따릅니다.

* **Controller Layer**: HTTP 요청을 수신하고 응답을 처리하는 API의 진입점 (DTO 사용)
* **Service Layer**: 핵심 비즈니스 로직 처리, 트랜잭션 관리, 외부 서비스(AI 서버)와 통신
* **Repository Layer**: Spring Data JPA를 사용하여 데이터베이스와 상호작용
* **Domain Layer**: @Entity 어노테이션을 가진 JPA 엔티티 클래스
* **Security (Filter/Util)**: JWT 토큰 생성, 검증 및 요청 필터링을 통해 API 접근 제어

```text
    Client (React)
          |
          |  HTTP Request (JSON)
          v
+-----------------------------------------------------------------+
|  Spring Boot API Server                                         |
|                                                                 |
|  [Controller]  <-  [Security Filters (JWT Check)]  <- Request   |
|      |                                                          |
|      v                                                          |
|  [Service]     <-- (AI Prediction) -->  [External AI Server (Flask)]
|      |                                                          |
|      v                                                          |
|  [Repository (JPA)]                                             |
|      |                                                          |
|      v                                                          |
|  [Database (MariaDB/MongoDB)]                                   |
|                                                                 |
+-----------------------------------------------------------------+

## 3. 핵심 기능 및 흐름
* **3.1.** JWT 기반 인증 및 인가 흐름
Stateless 환경을 위해 JWT를 사용한 토큰 기반 인증 시스템을 구현했습니다.

로그인 및 토큰 발급 (/generateToken): 사용자가 ID/PW로 로그인을 요청하면 APILoginFilter가 요청을 가로채 인증에 성공하면 APILoginSuccessHandler가 Access/Refresh Token을 생성하여 클라이언트에 전달합니다.

API 요청 및 토큰 검증: 클라이언트는 API 요청 시 Authorization: Bearer <Access Token> 헤더를 포함하여 전송합니다. TokenCheckFilter가 /api/로 시작하는 모든 요청을 가로채 Access Token의 유효성을 검증합니다.

Access Token 만료 및 재발급 (/refreshToken): Access Token이 만료되면, 클라이언트는 Refresh Token을 사용하여 /refreshToken 엔드포인트로 새로운 Access Token 발급을 요청합니다.

주요 구현 코드: CustomSecurityConfig.java, JWTUtil.java, TokenCheckFilter.java

* **3.2.** AI 회귀 예측 기능 흐름
외부 Flask 서버와 연동하여 AI 예측 기능을 제공합니다.

예측 요청 (/api/regression/predict): 클라이언트가 예측에 필요한 데이터(DTO)를 POST 요청으로 보냅니다. RegressionController가 이 요청을 받아 RegressionService에 처리를 위임합니다.

외부 AI 서버 통신: RegressionServiceImpl은 OkHttpClient를 사용하여 Flask 서버 URL (/predict/regression)로 JSON 형식의 요청을 보냅니다.

결과 반환: Flask 서버로부터 받은 응답을 RegressionResponseDTO로 파싱하여 컨트롤러에 반환하고, 클라이언트는 최종 예측 결과를 받게 됩니다.

주요 구현 코드: RegressionController.java, RegressionServiceImpl.java

## 4. 사용된 주요 기술 스택
언어: Java 17

프레임워크: Spring Boot 3

데이터베이스: MariaDB (JPA), MongoDB

보안: Spring Security, JWT, OAuth2 (소셜 로그인)

API 문서화: SpringDoc (Swagger UI)

HTTP 클라이언트: OkHttp

빌드 도구: Gradle

기타: Lombok, ModelMapper, QueryDSL

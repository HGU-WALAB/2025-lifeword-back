<div align="center">

# 라이프워드 백엔드 (LifeWord Backend) 🛠️

[<img src="https://img.shields.io/badge/프로젝트 기간-2025.01~-fab2ac?style=flat&logo=&logoColor=white" />]()

</div>

## 📝 소개

'라이프워드'는 교회의 설교와 성경 말씀을 효율적으로 관리하고 공유할 수 있는 웹 서비스입니다. 목회자들은 설교를 작성하고 관리할 수 있으며, 성도들은 설교를 열람하고 성경을 쉽게 찾아볼 수 있습니다.

### 주요 기능

- 설교 작성 및 관리
- 설교 버전 관리 시스템
- 성경 구절 빠른 검색
- 설교 북마크 기능
- 관리자 페이지를 통한 사용자/설교 관리
- 소셜 로그인 (카카오, 구글)
- 자체 로그인 시스템

<br />

## 📂 프로젝트 구조

Domain-Driven Design, DDD 방식으로 설계

도메인 기반 계층 구조:

```
bibly-be/
├── src/
│   ├── main/
│   │   └── java/com/project bibly_be/
│   │       ├── admin/
│   │       │   ├── ...
│   │       ├── bible/
│   │       │   ├── ...
│   │       ├── bookmark/
│   │       │   ├── ...
│   │       ├── bookmarklist/
│   │       │   ├── ...
│   │       ├── sermon/
│   │       │   ├── ...
│   │       ├── text/
│   │       │   ├── ...
│   │       ├── user/
│   │       │   ├── ...
│   │       ├── BiblyBeApplication.java
│   │       └── ServletInitializer.java
│   └── test/
│       └── java/com/project/bibly_be/
│           └── ...
├── build.gradle
├── settings.gradleㅍ
└── README.md
```

<br />

## ⚙ 기술 스택

### Back-end

<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Java.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringBoot.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Mysql.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Swagger.png?raw=true" width="80">
</div>

### Tools

<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Github.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Notion.png?raw=true" width="80">
</div>

<br />

## 📌 주요 기능 (백엔드)

- 사용자 회원가입/로그인 (JWT 기반 인증)
- 소셜 로그인 (카카오, 구글)
- 설교 작성 / 수정 / 삭제 / 버전 관리
- 성경 구절 검색 API
- 설교 북마크 기능
- 관리자 기능 (유저, 설교 관리)
- RESTful API 설계
- Swagger를 통한 API 문서 자동화

<br />

## 💁‍♂️ 프로젝트 팀원

|                    Frontend                    |                    Backend                     |                    Backend                    |                 Backend                 |
| :--------------------------------------------: | :--------------------------------------------: | :-------------------------------------------: | :-------------------------------------: |
| ![](https://github.com/hjkim0905.png?size=120) | ![](https://github.com/Diggydogg.png?size=120) | ![](https://github.com/naim-kim.png?size=120) |                                         |
|     [김현중](https://github.com/hjkim0905)     |     [윤동혁](https://github.com/Diggydogg)     |     [김나임](https://github.com/naim-kim)     | [곽서원](https://github.com/seowon1112) |

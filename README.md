# [Survey Link](https://www.notion.so/teamsparta/14-Survey-Link-2492dc3ef514801b87c0cf81781f6d0a#2532dc3ef51480318f67d79381813058)

![img.png](images/surveylink.png)

---
# Survey Link

## 왜 Survey Link를 만들었나요?

많은 사람들이 구글 폼이나 네이버 폼 같은 도구로 설문을 진행합니다.  
편리하지만 실제로는 이런 한계가 있습니다:

- 참여자는 매번 이름·이메일 같은 개인정보를 입력해야 해서 번거롭습니다.
- 설문 결과를 확인하려면 데이터를 내려받아 직접 가공해야 하므로 실시간 분석이 어렵습니다.

우리는 고민했습니다.  
**“설문을 만드는 사람도, 참여하는 사람도 더 쉽고 재미있게 접근할 수는 없을까?”**

그 답이 바로 **Survey Link**입니다.

---

## Survey Link는 어떤 플랫폼인가요?

Survey Link는 **설문 참여를 게임처럼 즐길 수 있는 웹 기반 설문 플랫폼**입니다.

-  **참여자가 응답할 때, 설문 생성자는 설문을 생성 • 종료할 때마다 포인트를 얻고, 등급을 올리며 성취감을 느낄 수 있습니다.**
-  **설문 생성자는 등록된 유저 프로필(연령대·관심사 등)을 기반으로 원하는 타깃에게 효과적으로 설문을 배포할 수 있습니다.**
-  **수집된 데이터는 자동으로 통계에 반영되어, 실시간 분석과 빠른 의사결정을 지원합니다.**

---

## Survey Link가 만드는 경험

Survey Link는 단순히 “설문을 만드는 도구”를 넘어,  
**참여자에게는 성장과 성취의 경험을, 설문 발행자에게는 효율적인 데이터 수집과 분석 환경**을 제공합니다.

✨ **더 쉽고, 더 빠르고, 더 즐겁게.**  
Survey Link는 설문의 새로운 방식을 제안합니다.

---
## 👥 팀원 소개

<div align="center">

| 이름 | 역할 | 담당                                       | GitHub |
|------|------|------------------------------------------|--------|
| **유진원** | 팀장 | 통계 도메인<br>아키텍처 설계<br>클라우드 인프라 구축         | [GitHub](https://github.com/Jindnjs) |
| **이동근** | 팀원 | 유저 도메인<br>Spring Security + JWT<br>OAuth | [GitHub](https://github.com/DG0702) |
| **장경혁** | 팀원 | 참여 도메인                                   | [GitHub](https://github.com/kcc5107) |
| **이준영** | 부팀장 | 설문 도메인<br>인프라 구축                         | [GitHub](https://github.com/LJY981008) |
| **최태웅** | 팀원 | 프로젝트 도메인                                 | [GitHub](https://github.com/taeung515) |
| **김도연** | 팀원 | 공유 도메인                                   | [GitHub](https://github.com/easter1201) |

</div>

---
## 핵심 서비스 플로우
![img_1.png](images/핵심서비스플로우.png)

---
# 도메인별 주요 기능
<details>
<summary>사용자 시스템</summary>

#### ✨ 로그인 플로우
![img.png](images/로그인플로우.png)

- 사용자가 서비스를 이용하기 위해 로그인을 하는 기능
- 로컬 로그인과 OAuth(카카오, 네이버, 구글) 로그인으로 나누어져 있음

</details>

---
<details>
<summary>프로젝트 시스템</summary>

#### ✨ 프로젝트 생성, 검색 플로우
![img.png](images/프로젝트_플로우.png)
- 프로젝트를 생성
    - Period 주기에 따라 상태변경 스케줄링
- 프로젝트 검색
    - trigram Index를 통한 빠른 keyword 검색
    - NoOffset 페이지네이션

</details>

---
<details>
<summary>설문 시스템</summary>

#### ✨ 설문 생성, 조회 플로우
![img.png](images/설문_플로우.png)

- 프로젝트의 담당자 또는 작성 권한 보유자가 설문을 생성하는 서비스
    - 설문 생성 시 읽기모델 동기화
    - 지연 이벤트를 통한 설문 시작/종료 컨트롤
- 읽기 모델을 사용한 빠른 조회 서비스
- 스케줄링을 통한 참여자 수 갱신

</details>

---
<details>
<summary>공유 시스템</summary>

</details>

---
<details>
<summary>참여 시스템</summary>

#### ✨ 설문 응답 제출 플로우
![img.png](images/설문_응답제출_플로우.png)

- 사용자가 특정 설문에 대한 답변을 제출하는 핵심 기능
- 설문 응답을 저장하면 설문 제출 이벤트를 발행

</details>

---
<details>
<summary>통계 시스템</summary>

#### ✨ 통계 집계, 조회 플로우
![img.png](images/통계_플로우.png)

- 통계 집계시, 이벤트로부터 통계 데이터 → Elastic 색인
- 통계 조회시 ElasticSearch Aggregation으로 데이터 반환

</details>


## 🛠 기술 스택
![img.png](images/기술스택.png)


## 기술적 의사결정

- [스케줄링 방식 vs 개별 처리 방식 의사 결정 방식](https://www.notion.so/teamsparta/vs-2542dc3ef51480d18141d940af62388e?source=copy_link)


- [조회 모델을 위한 기술적 의사 결정 → MongoDB](https://www.notion.so/teamsparta/MongoDB-2542dc3ef514802389aff6fb59470acb?source=copy_link)


- [외부 API 호출로 인한 스레드 병목 현상 개선](https://www.notion.so/teamsparta/API-2542dc3ef5148037ac82e307365c1f72?source=copy_link)


- [EC2 vs ECS](https://www.notion.so/teamsparta/EC2-vs-ECS-2542dc3ef51480b18997ca8eeb090a88?source=copy_link)


- [전략 패턴 도입](https://www.notion.so/teamsparta/EC2-vs-ECS-2542dc3ef51480b18997ca8eeb090a88?source=copy_link)


- [웹 PUSH 알림 웹 소켓](https://www.notion.so/teamsparta/2552dc3ef51480b4abfac5763b3ffe05?source=copy_link)

## 성능 최적화

- [설문 응답 제출 성능 최적화](https://www.notion.so/teamsparta/Redis-2542dc3ef514809bbd55c5fae2e1e08a?source=copy_link)


- [프로젝트 검색 API 성능 검증 및 NoOffset 도입](https://www.notion.so/teamsparta/API-NoOffset-2542dc3ef51480afaf75f539d821afe4?source=copy_link)


- [테이블 비정규화로 설문 제출 성능 개선](https://www.notion.so/teamsparta/2542dc3ef51480609d96d9cd20ab9d8c?source=copy_link)


- [회원탈퇴 구조적 문제 개선](https://www.notion.so/teamsparta/2542dc3ef51480dca912c246719869bf?source=copy_link)


- [PostgreSQL의 GIN Index를 통한 검색성능 향상](https://www.notion.so/teamsparta/PostgreSQL-GIN-Index-2542dc3ef5148058b9bfef04a4864633?source=copy_link)

## 트러블 슈팅

- [스케줄링 시 데이터가 누락되는 문제 해결](https://www.notion.so/teamsparta/2542dc3ef51480dea65dcc813544ca12?source=copy_link)


- [공유 구조 변경](https://www.notion.so/teamsparta/2552dc3ef51480a997dbd8965800621e?source=copy_link)


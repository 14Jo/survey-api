# [Survey Link](https://www.notion.so/teamsparta/14-Survey-Link-2492dc3ef514801b87c0cf81781f6d0a#2532dc3ef51480318f67d79381813058)

![img.png](images/surveylink.png)

---
## 프로젝트 개요
- Survey Link는 설문 참여를 게임처럼 즐길 수 있는 설문 플랫폼입니다.
  참여자는 설문에 응답할 때마다 포인트를 얻고, 등급을 올리며 성취감을 느낄 수 있습니다.
  설문 생성자는 등록된 유저 프로필(연령대·관심사 등)을 기반으로 원하는 타깃에게 효과적으로 설문을 배포할 수 있습니다.
- 참여자는 매번 개인정보를 입력할 필요 없이, 익명화된 프로필을 통해 간편하게 참여할 수 있습니다.
  수집된 데이터는 자동으로 통계에 반영되어 분석이 즉시 가능하며,
  참여자는 설문 참여 자체가 곧 성장 경험이 되는 새로운 방식을 제공합니다.

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

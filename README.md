# RunningTracker

실시간 위치 추적 및 운동 기록 관리를 위한 Android 애플리케이션입니다. Jetpack Compose와 Clean Architecture를 기반으로 구축되었으며, 성능 최적화와 확장성을 고려하여 설계되었습니다.

## 🚀 주요 기능

- **실시간 경로 추적**: 포그라운드 서비스를 통해 사용자의 위치를 지속적으로 수집하고 지도 위에 실시간으로 경로(Polyline)를 그립니다.
- **운동 기록 관리**: 운동 종료 후 이동 거리, 시간, 고도 등을 계산하여 로컬 데이터베이스에 저장하고 목록으로 확인합니다.
- **상태 알림**: 알림창을 통해 현재 운동 상태를 표시하며, 앱이 백그라운드로 전환되어도 위치 추적을 중단하지 않습니다.
- **화면 유지 기능**: 운동 중에는 사용자의 편의를 위해 화면이 꺼지지 않도록 유지합니다.
- **데이터 유실 방지**: 시스템에 의해 서비스가 강제 종료 후 복구될 때 데이터를 안전하게 유지하는 전략이 적용되어 있습니다.

## 🛠 기술 스택

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Async/State**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html), [Flow](https://kotlinlang.org/docs/flow.html)
- **Maps**: Google Maps SDK, Naver Maps SDK
- **Collections**: `kotlinx-collections-immutable` (성능 최적화를 위한 불변 컬렉션 사용)
- **Lifecycle**: `androidx.lifecycle.compose.collectAsStateWithLifecycle` 적용으로 스마트한 리소스 관리

## 🏗 프로젝트 구조

```text
app/src/main/java/com/survivalcoding/runningtracker/
├── core/             # 공통 유틸리티 및 기본 클래스
├── data/             # 데이터 레이어 (Entity, Database, Repository 구현부)
├── domain/           # 도메인 레이어 (Model, Repository 인터페이스)
└── presentation/     # 프리젠테이션 레이어 (UI, ViewModels, States)
```

## ⚙️ 멀티 플레이버(Flavor) 설정

본 프로젝트는 지도 SDK와 환경에 따라 다양한 플레이버를 지원합니다.

### SDK Dimension
- `google`: Google Maps SDK 사용
- `naver`: Naver Maps SDK 사용

### Environment Dimension
- `dev`: 개발 환경 (Base URL: dev.api...)
- `staging`: 검증 환경 (Base URL: staging.api...)
- `prod`: 운영 환경 (Base URL: api...)

## ⚡ 성능 최적화 (Recomposition)

Compose의 성능을 극대화하기 위해 다음과 같은 최적화 기법이 적용되었습니다:
- **`ImmutableList` 사용**: `List` 대신 `ImmutableList`를 사용하여 Compose 컴파일러가 리스트의 불변성을 인식하고 불필요한 Recomposition을 건너뛸 수 있도록(Skiping) 처리했습니다.
- **ViewModel 상태 계산**: UI에서 매번 계산하던 유도 상태(Derived State)를 ViewModel 레이어에서 미리 계산하여 주입함으로써 UI Recomposition 부담을 줄였습니다.
- **`collectAsStateWithLifecycle`**: 앱의 라이프사이클에 맞춰 상태 구독을 최적화하고 에너지를 절약합니다.
- **LazyColumn Key 설정**: 리스트 아이템에 고유 ID를 부여하여 아이템 변경 시 최소한의 업데이트만 발생하도록 최적화했습니다.
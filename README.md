# Gentoo Floating Button SDK 통합 가이드

이 가이드는 **Android Native App**에 Gentoo Floating Button SDK를 통합하는 방법을 설명합니다. 이 SDK를 사용하면 웹사이트에 쉽게 **채팅 서비스를 추가**할 수 있습니다. 아래 단계를 따라 설정을 완료하고 기능을 적용하세요.

# 시작하기

## Requirements

Gentoo SDK의 최소 요구 사항:

- **Android 8.0 (API level 26)** 이상

## SDK 설치

### 레포지토리 구성

`settings.gradle` 파일에 **JitPack 레포지토리**를 추가합니다.

```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

### Gentoo 의존성 추가

사용하려는 모듈에 Gentoo SDK 의존성을 추가합니다.

```kotlin
dependencies {
    implementation("com.github.waddle-corp:gentoo-sdk-aos:1.0.1")
}
```

## Gentoo SDK 초기화

다른 함수를 호출하기 전에 **초기화**가 반드시 필요합니다. `Application` 클래스에서 SDK를 초기화하거나, 초기화에 필요한 데이터를 모두 확보할 수 있는 시점에 즉시 초기화하세요.

```kotlin
Gentoo.initialize(
    InitializeParams(
        clientId = "YOUR_CLIENT_ID",
        authCode = "YOUR_AUTH_CODE",
        udid = "USER_DEVICE_ID"
    )
)
```

> YOUR_CLIENT_ID, YOUR_AUTH_CODE, USER_DEVICE_ID를 실제 값으로 교체해야 합니다.
>

## Floating 버튼 추가

Gentoo SDK는 `GentooFloatingActionButton` 클래스를 제공합니다. **XML**이나 **프로그래밍 방식**으로 추가할 수 있으며, 아래는 XML을 사용하는 예입니다.

```xml
<!-- XML 예시 -->
<androidx.constraintlayout.widget.ConstraintLayout
		...
    <com.waddle.gentoo.ui.GentooFloatingActionButton
        android:id="@+id/gentooFloatingActionButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="50dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
		...
</androidx.constraintlayout.widget.ConstraintLayout>
```

## ViewModel 추가 및 바인딩

Gentoo SDK는 **MVVM 아키텍처**에 최적화되어 있습니다. Activity 또는 Fragment에서 ViewModel을 생성하고 Gentoo SDK와 **바인딩**하는 과정이 필요합니다.

### ViewModel 생성

`GentooHomeViewModel`과 `GentooDetailViewModel` 두 가지 ViewModel이 있으며, 각각은 홈 화면과 상세 화면에서 사용됩니다.

```kotlin
class HomeActivity : AppCompatActivity() {
    val viewModel: GentooHomeViewModel by viewModels()
}

class DetailActivity : AppCompatActivity() {
    val viewModel: GentooDetailViewModel by viewModels {
        val itemId = intent.getStringExtra(EXTRA_ITEM_ID) ?: ""
        GentooDetailViewModelFactory(itemId)
    }

    companion object {
        const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
    }
}
```

### View & ViewModel 바인딩

ViewModel의 데이터가 View에 반영되도록, Gentoo View와 ViewModel을 바인딩합니다.

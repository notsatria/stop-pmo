# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew testDebugUnitTest      # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew test                   # Run all tests
```

Build files use Kotlin DSL (`.gradle.kts`). Version catalog at `gradle/libs.versions.toml`.

## Architecture

Single-module Jetpack Compose app (`dev.notsatria.stop_pmo`) using MVVM with a thin domain layer.

**Package structure:**
- `data/` — Room database, DAO, entities, DataStore preferences, repository implementations
- `domain/` — Repository interfaces, domain models, entity-to-domain mappers
- `di/` — Koin module (single `appModule` in `AppModule.kt`)
- `navigation/` — Type-safe Compose Navigation with `Screen` sealed class destinations
- `ui/screen/` — Each screen has its own `*Screen.kt`, `*ViewModel.kt`, `*State.kt` (UiState data class)
- `ui/components/` — Shared reusable composables
- `ui/theme/` — Custom `CompositionLocal`-based theming (`LocalTheme`) with light/dark palettes
- `utils/` — Date formatting, streak calculation, notification helpers, debug tools
- `worker/` — WorkManager `StreakCheckWorker` for daily streak milestone notifications

**Key patterns:**
- ViewModels expose state via `MutableStateFlow<UiState>` (no underscore prefix on the state property)
- Each screen follows: `Route` composable → `ViewModel` → `UiState` data class
- Navigation uses `@Serializable` `Screen` sealed class with `NavHost` in `PMONavHost.kt`
- Repository pattern: interface in `domain/repository/`, implementation in `data/repository/`
- Entity mapping via `toDomainModel()` extension in `domain/Mapper.kt`
- Koin DI uses standard DSL (`viewModelOf`, `single`, `factory`) — not annotation-based
- WorkManager workers registered via `worker { }` Koin DSL

## Navigation

Bottom nav bar shows on Dashboard, History, Analytics, Settings. Streak screen is parameterized (`Screen.Streak(streakCount: Int)`) with slide animation, no bottom bar. Deep linking from notifications uses intent extras (`nav_target`, `streak_count`).

## Theming

Custom theme system via `CompositionLocal` — uses `LocalTheme` with `CustomTheme` data class containing light/dark `ColorScheme` palettes. Not using Material3's built-in dynamic color. MaterialKolor library for color generation.

## Conventions

- No comments or documentation in code — code should be self-explanatory
- `@Preview` composables use `AppThemePreview` wrapper
- Break large views into small, single-purpose composable components
- Use `LaunchedEffect` for side effects, `remember`/`derivedStateOf` for performance
- Kotlin 2.2.10, KSP for annotation processing, Java 11 target
- SDK: compile 36, target 36, min 24

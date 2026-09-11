# Cake List

Android app built for the Waracle mobile coding exercise.

## Tech

- Kotlin
- Jetpack Compose
- MVVM
- Hilt
- Retrofit + Gson
- Coil
- Coroutines / StateFlow / SharedFlow

## Setup

1. Clone the repository.
2. Open the project in Android Studio.
3. Run the `app` configuration on an emulator or Android device with internet access.

## Architecture

The app uses a lightweight MVVM structure:

Compose UI  
→ ViewModel  
→ GetCakesUseCase  
→ CakeRepository  
→ CakeApi

`GetCakesUseCase` owns the cake-list business rules:
- remove duplicate cakes
- sort cakes alphabetically by title

The repository is responsible for fetching and mapping remote data.

## AI Assistance

AI assistants (ChatGPT and Codex) were used during development to support code review, discuss implementation approaches, suggest test scenarios, and review documentation.

I made the final implementation and architectural decisions, reviewed the generated suggestions, ran the automated tests, and manually verified the application behaviour.

## Testing

Run:

```bash
./gradlew :app:testDebugUnitTest
```
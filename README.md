
# Participate
## Citizen Science Android App

A mobile application that integrates with SciStarter's API to help users discover, participate in, and track their contributions to citizen science projects, unlocking achievements as they make meaningful impacts on real scientific research.


##  Key Features

- **SciStarter API Integration**: Real-time access to thousands of citizen science projects
- **OAuth Authentication**: Secure user login with SciStarter credentials
- **Achievement System**: Progressive badge unlocking based on participation milestones
- **Confetti Animations**: Celebratory visual feedback using custom Canvas animations
- **Persistent Data Storage**: Room database for offline achievement and project tracking
- **Responsive UI**: Material Design 3 with custom eco-friendly theming
- **WebView Integration**: Seamless in-app browsing of project details and dashboards

##  Technical Skills Demonstrated

### Android Development
- **Jetpack Compose**: Modern declarative UI framework
- **Material Design 3**: Contemporary design patterns and components
- **Architecture Components**: ViewModel, LiveData, StateFlow
- **Navigation**: Multi-screen app flow with state management

### Data Management
- **Room Database**: Local data persistence with SQLite
- **Kotlin Coroutines**: Asynchronous programming and background tasks
- **Flow & StateFlow**: Reactive data streams and state management
- **Repository Pattern**: Clean architecture with data layer separation

### API Integration
- **RESTful API Consumption**: OkHttp client for network requests
- **OAuth 2.0**: Third-party authentication implementation
- **JSON Parsing**: Gson for data serialization/deserialization
- **WebView Management**: Cookie handling and JavaScript bridge communication

### UI/UX Design
- **Custom Animations**: Canvas-based confetti particle system
- **Responsive Layouts**: Adaptive UI for different screen sizes
- **Theming**: Custom color schemes and typography
- **User Feedback**: Progress indicators, dialogs, and status messages

### Code Quality
- **MVVM Architecture**: Separation of concerns and testability
- **Kotlin Best Practices**: Null safety, extension functions, sealed classes
- **Dependency Injection**: Manual DI with factory patterns
- **Error Handling**: Try-catch blocks and graceful failure states

##  Screenshots

<img src="Screenshot_20260315-143922_Participate" alt="Description of the screenshot" width="150"> <img src="./assets/screenshot.png" alt="Description of the screenshot" width="150"> <img src="./assets/screenshot.png" alt="Description of the screenshot" width="150"> <img src="./assets/screenshot.png" alt="Description of the screenshot" width="150"> <img src="./assets/screenshot.png" alt="Description of the screenshot" width="150">


##  Architecture
```
app/
├── data/
│   ├── Database.kt          # Room database and DAOs
│   └── models/              # Data classes and entities
├── ui/
│   ├── MainActivity.kt      # Main app entry point
│   ├── theme/               # Material Design theming
│   └── components/          # Reusable composables
├── viewmodel/
│   └── MainViewModel.kt     # Business logic and state management
└── utils/
    ├── Achievements.kt      # Achievement definitions
    ├── ConfettiAnimation.kt # Custom animation logic
    └── AchievementDialog.kt # UI components
```

##  Technologies Used

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room (SQLite)
- **Networking**: OkHttp, Gson
- **Architecture**: MVVM with Repository Pattern
- **Async**: Kotlin Coroutines & Flow
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

##  Dependencies
```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.activity:activity-compose:1.8.2")

// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Networking
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.google.code.gson:gson:2.10.1")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")
```

##  Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17 or higher
- Android SDK 34
- SciStarter API Key ([Get one here](https://scistarter.org/api))

### Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/participate.git
cd participate
```

2. Add your SciStarter API key
   - Open `MainActivity.kt`
   - Replace `YOUR_API_KEY_HERE` with your actual API key in two locations:
     - Line ~320 (login URL)
     - Line ~450 (fetchProjects function)

3. Build and run
```bash
./gradlew assembleDebug
```

Or open in Android Studio and click Run 

##  How to Use

1. **Discover Projects**: Browse SciStarter's project finder to explore citizen science opportunities
2. **Login**: Authenticate with your SciStarter account to sync your participation
3. **Track Progress**: Click the ✓ button when you participate in a project
4. **Unlock Achievements**: Earn badges as you contribute to more projects:
   - 🌱 Seedling (1 project)
   - 🌿 Sprout (5 projects)
   - 🌳 Young Tree (10 projects)
   - 🌲 Forest Guardian (20 projects)
   - 🌍 Earth Hero (50 projects)
5. **View Stats**: Check your achievements tab to see your progress

##  Design Decisions

### Gamification
The achievement system was designed to encourage continued engagement through progressive milestones. The confetti animation provides immediate positive feedback, creating a dopamine loop that reinforces participation behavior.

### Data Architecture
Room database was chosen for offline-first architecture, ensuring users can track progress without constant internet connectivity. The ViewModel pattern separates business logic from UI, making the codebase more maintainable and testable.

### User Experience
Material Design 3 provides familiarity while the eco-themed color scheme (green on black) reinforces the environmental mission of citizen science. The WebView integration allows seamless access to full project details without leaving the app.


## Learning Outcomes

This project demonstrates proficiency in:
- Modern Android development with Jetpack Compose
- RESTful API integration and OAuth flows
- Local database management with Room
- Asynchronous programming with Kotlin Coroutines
- MVVM architecture and state management
- Custom animations and Canvas drawing
- Material Design principles
- Version control with Git

## Author

**Dana Harper**
- GitHub: (https://github.com/danaharper151)

## License

This project is open source and available under the [MIT License](LICENSE).

## Acknowledgments

- [SciStarter](https://scistarter.org/) for providing the API and mission
- Android Developers community for comprehensive documentation
- Jetpack Compose samples for UI inspiration

---

*Built as a Capstone Project for my BSCS to demonstrate mobile development skills*

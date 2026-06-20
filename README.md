QuickApply

An Android app that automates the job application grind — draft once, attach your resume, enhance it with AI, and send personalized applications via Gmail in one tap.

<img width="1280" height="853" alt="1781855192190" src="https://github.com/user-attachments/assets/5ae756fd-0b12-494a-acb4-4d6dfda6424b" />

Why I built this

A friend of mine, Muhammad Maaz, was applying to dozens of jobs a week and kept repeating the same routine: open Gmail, find the resume file, write a near-identical email with a different name swapped in, attach the file, send. He asked if I could build something to take the repetitive parts off his hands — that became QuickApply.

Features

  Draft templates — write a message once with [HR Name] and [Company Name] placeholders; QuickApply personalizes it automatically before sending
  Resume management — upload up to 5 CVs/resumes (PDF or DOCX), preview them, and pick which one to attach per application
  AI-powered enhancement — uses an LLM (via the Groq API) to rewrite your resume or draft message for clarity, tone, and ATS optimization, while preserving every fact (dates, companies, job titles). You always review the result before anything is overwritten — original and enhanced versions are both kept, and you choose which to send
  Direct Gmail sending — sends applications straight from the app using the Gmail API with OAuth2, attaching the resume automatically
  Application history — tracks the last 20 sent applications (HR name, company, email, timestamp) so you're never guessing who you've already contacted
  Light/dark theme with persisted user preference

Tech Stack
Language & UI
  Kotlin
  Jetpack Compose (Material 3)

Architecture
  MVVM with a clean Data → Domain → Presentation layering
  Use-case pattern for business logic (AddDraftUseCase, AuthUseCase, etc.)
  Repository pattern abstracting Firebase/local data sources

Backend & Auth
  Firebase Authentication (Google Sign-In via OAuth2)
  Firebase Firestore (drafts, sent application history)
  Firebase Remote Config (secure runtime API key delivery — no secrets baked into the APK)

AI Integration
  Groq API (Llama 3.3 70B) for resume and cover-letter enhancement
  Structured prompt design to preserve factual content while improving wording

Document Processing
  PDFBox (Android) — PDF text extraction and generation
  Apache POI — DOCX text extraction

Networking & Async
  Retrofit + Gson
  Kotlin Coroutines & Flow

Local Storage
  Jetpack DataStore (Preferences) for CV metadata and theme settings

Email
  Gmail API (google-api-services-gmail) with JavaMail for MIME message construction and attachments

Build & Release
  ProGuard/R8 with custom rules for release-build obfuscation and minification

Architecture Overview
com.t.quickapply
├── data/
│   ├── local/           
│   ├── model/            
│   ├── remote/
│   │   ├── ai/           
│   │   ├── config/       
│   │   ├── firebase/    
│   │   └── gmail/       
│   └── repository/     
├── domain/
│   ├── model/             
│   ├── repository/        
│   └── usecase/          
└── presentation/
    ├── auth/              
    ├── cv/               
    ├── draft/            
    ├── home/               
    ├── applications/     
    ├── profile/           
    ├── navigation/         
    ├── theme/             
    └── components/      

The app follows a strict dependency direction: presentation depends on domain, domain depends on nothing (pure Kotlin interfaces/models), and data implements the domain interfaces. This keeps the business logic testable and decoupled from Android/Firebase specifics.

Setup

Prerequisites
  Android Studio (latest stable)
  A Firebase project with Authentication, Firestore, and Remote Config enabled
  A free Groq API key from console.groq.com

1. Clone the repo
  bashgit clone https://github.com/<your-username>/QuickApply.git
  cd QuickApply

2. Add Firebase config
  Download google-services.json from your Firebase project and place it in app/.

3. Configure local properties
  Create a local.properties file in the project root (this is gitignored) with:
  propertiessdk.dir=/path/to/your/android/sdk

4. Set up the Groq API key
The app fetches the Groq API key at runtime via Firebase Remote Config (no key is bundled in the APK):
  In Firebase Console → Remote Config, add a parameter named groq_api_key
  Set its value to your Groq API key
  Publish the change

5. Add your release SHA-1 (for Google Sign-In)
  bashkeytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
  Add the resulting SHA-1 fingerprint to Firebase Console → Project Settings → Your Android app.

6. Build and run
  Open the project in Android Studio and run on a device/emulator with API 26+.

Permissions
  PermissionReasonINTERNETFirebase, Gmail API, Groq API calls
  READ_EXTERNAL_STORAGE (API ≤32)Picking resume files on older Android versions

Roadmap
 Cover letter generation
 Export enhanced resume as a downloadable file from in-app preview
 Application status tracking (applied → interview → offer)

Acknowledgements
  Thanks to Muhammad Maaz for the original idea and for being the first (very patient) tester.

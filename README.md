# Login & Signup App

A native Android authentication app built with Kotlin and Firebase Authentication as part of the Syntecxhub Android Internship — Project 1 (Task 4).

---

## Features

- Email and password signup with full name capture
- Email and password login with session persistence
- Password reset via email link sent by Firebase
- Protected profile screen — only accessible when logged in
- Automatic redirect to login if session expires
- Form validation with clear error messages for every field
- Clean error mapping for all Firebase error codes
- Logout with confirmation dialog

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM (ViewModel + StateFlow) |
| Authentication | Firebase Authentication |
| Async | Kotlin Coroutines |
| UI | XML layouts + Material Components |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

---

## Project Structure

```
app/src/main/java/com/chrisarsene/loginapp/
├── AuthRepository.kt          # wraps all Firebase Auth calls and maps errors
├── AuthState.kt               # sealed class: Idle / Loading / Success / Error / ResetEmailSent
├── AuthViewModel.kt           # form validation + calls repository + exposes StateFlow
├── AuthViewModelFactory.kt    # passes repository into ViewModel
├── LoginActivity.kt           # email + password login screen
├── SignupActivity.kt          # name + email + password + confirm password screen
├── ProfileActivity.kt         # protected screen shown only to logged-in users
└── ResetPasswordActivity.kt   # sends Firebase password reset email

app/src/main/res/
├── layout/
│   ├── activity_login.xml
│   ├── activity_signup.xml
│   ├── activity_profile.xml
│   └── activity_reset_password.xml
├── drawable/
│   ├── bg_input.xml
│   └── ic_arrow_back.xml
└── values/
    ├── colors.xml
    ├── strings.xml
    └── styles.xml
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 8 or higher (bundled with Android Studio)
- A Firebase account — free at [console.firebase.google.com](https://console.firebase.google.com)

### Firebase Setup (required before building)

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add project** and give it a name
3. Inside the project click the **Android icon**
4. Enter the package name: `com.chrisarsene.loginapp`
5. Download `google-services.json`
6. Replace the placeholder file at `app/google-services.json` with the downloaded file
7. In Firebase console go to **Authentication → Sign-in method → Email/Password → Enable**

### Build and Run

```powershell
# Clone or unzip the project
cd LoginSignupApp

# Open in Android Studio
# File → Open → select the LoginSignupApp folder

# Sync Gradle (Android Studio does this automatically on open)
# File → Sync Project with Gradle Files

# Run on device or emulator
# Press the green Play button or Shift + F10
```

---

## How Authentication Works

```
App opens
    ↓
LoginActivity checks FirebaseAuth.currentUser
    ↓
null → show login screen
not null → go directly to ProfileActivity

User submits login form
    ↓
AuthViewModel validates fields locally
    ↓
AuthRepository calls Firebase signInWithEmailAndPassword
    ↓
Success → navigate to ProfileActivity
Error   → show mapped error message under the form
```

---

## Form Validation Rules

| Field | Rule |
|---|---|
| Full name | Required, minimum 2 characters |
| Email | Required, must be valid email format |
| Password | Required, minimum 6 characters |
| Confirm password | Must match password field exactly |

All validation runs locally in `AuthViewModel` before any Firebase call is made — no unnecessary network requests for obviously invalid input.

---

## Error Handling

Firebase returns raw error codes. `AuthRepository.mapError()` converts them into readable messages:

| Firebase error | User-facing message |
|---|---|
| `user-not-found` | No account found with this email |
| `wrong-password` | Incorrect password |
| `email-already-in-use` | An account with this email already exists |
| `weak-password` | Password must be at least 6 characters |
| `invalid-email` | Please enter a valid email address |
| Network failure | No internet connection |

---

## Security Notes

- Passwords are never stored locally — Firebase handles all credential storage
- Firebase issues a secure token on login — the app never handles raw passwords after submission
- `ProfileActivity` checks `currentUser` on both `onCreate` and `onResume` to prevent unauthorized access
- The back button is disabled on `ProfileActivity` to prevent navigating back to the login screen after a successful login
- `google-services.json` should be added to `.gitignore` before pushing to a public repository

---

## Adding to .gitignore

```gitignore
# Firebase config — contains sensitive project credentials
app/google-services.json

# Android standard ignores
*.iml
.gradle/
local.properties
/.idea/
.DS_Store
/build/
app/build/
```

---

## Built By

**Ndongozi Chris Arsene**
Full-Stack and Android Developer — Kigali, Rwanda
GitHub: [NdongoziChrisArsene](https://github.com/NdongoziChrisArsene)
Portfolio: [ndongozi-portfolio.vercel.app](https://ndongozi-portfolio.vercel.app)

Internship: Syntecxhub Android Development Internship

# 📝 Modern To-Do App (Jetpack Compose)

A clean, modern Android application for managing daily tasks, built entirely with **Kotlin** and **Jetpack Compose**. This app utilizes the **MVVM architecture** and persists data locally using **Room Database**.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue?style=flat&logo=android)
![Room Database](https://img.shields.io/badge/Database-Room-green?style=flat&logo=sqlite)

## ✨ Features

*   **Add Tasks:** Create new tasks with a Title, Category Label (Personal, Work, Study), and Priority level.
*   **Priority System:** Assign Low, Medium, or High priority using an interactive slider.
*   **Sorting:** Sort your list dynamically by:
    *   📅 Date (Newest/Oldest)
    *   ⚡ Priority (High/Low)
*   **Task Management:** Mark tasks as complete (strikethrough visual) or delete them permanently.
*   **Persistence:** All data is saved locally on the device using Room Database, so tasks remain after closing the app.
*   **Material 3 Design:** Follows modern Android design guidelines with a clean UI.


## 🛠️ Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetbrains/compose) (Material3)
*   **Database:** [Room Persistence Library](https://developer.android.com/training/data-storage/room)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Navigation:** Jetpack Compose Navigation
*   **Concurrency:** Coroutines & Flows
*   **State Management:** `StateFlow` and `collectAsState`

## 📂 Project Structure
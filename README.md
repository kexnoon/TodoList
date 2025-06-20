# Notes App – Showcase Project

## 📘 Overview

This project is a simple note-taking application inspired by the default notes app found on most smartphones. While its features are minimal by design, the architecture is deliberately more complex to showcase my skills as a developer. This includes practices and design patterns that would typically be considered over-engineering for an app of this size but serve to highlight capabilities in modular architecture, clean code, and modern Android development.

## 🛠 Tech Stack

- **Kotlin** – with Coroutines and Flows
- **Jetpack Compose**
- **Koin**
- **Room**

## 🧭 Project Structure

![Modules Diagram](docs/modules-diagram.png)

```
:app               → Base application module
:app-example       → Demo app showcasing UI-layer architecture and navigation
:core-ui           → Shared UI components (navigation, design system, etc.)
:feature-main      → Main feature module for viewing, creating, and editing notes
:feature-example   → Sample feature used in :app-example
:component-notes   → Business logic for notes management
:storage           → Data storage layer (currently using Room)
```

## 🎯 Current Goal

The primary focus is building a minimum viable version of the application that supports:

- Displaying a list of notes (currently as simple TODO items)
- Full CRUD operations (Create, Read, Update, Delete)

## 🧩 Upcoming Features

- 🔍 **Search** – Quickly find notes by keywords
- 🗂 **Folders** – Organize notes into custom categories

## 🌟 Potential Enhancements

- 🔐 **Authentication & Sync** – Firebase integration for login and cloud storage
- 🖼 **Rich Notes** – Support for text, images, media files, etc.
- ⏰ **Reminders** – Add alarms or time-based notifications
- 🧠 **More Tools** – Calendar, tagging, prioritization, and more

---

## 🤝 Contributing

At this time, the project is in an early development phase and not open for external contributions. However, feedback and suggestions are always welcome!

## 📄 License

[MIT License](./LICENSE) – feel free to use or adapt the codebase for your own projects.

## 🙋‍♂️ Author

Developed with ❤️ by Telma Ewelina Popova

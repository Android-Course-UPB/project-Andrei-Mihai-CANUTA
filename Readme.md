# My Bookshelf App

###### CANUTA Andrei-Mihai, SCPD-1

## Overview

**My Bookshelf** is an Android application developed in **Kotlin** using **Jetpack Compose**. It helps users organize and manage their book collections. The application integrates with the **Google Books API** for book search and utilizes **Room database** for offline persistence.

## Features

- **Home Screen**: View the list of saved books from the local database.
- **Search Screen**: Search for books using the Google Books API.
- **Book Details**: View book details in a popup (title, author, cover, description, rating).
- **Add Book**: Add books from the search screen to the local database.
- **Update Status**: Change the status of a book (Should Read, Reading, Read).
- **Delete Book**: Delete books with confirmation prompt.
- **Filter**: Filter books by status using chips.

## Technologies Used

- **Kotlin**
- **Jetpack Compose**
- **Navigation Component**
- **Room Database**
- **Retrofit**
- **Google Books API**
- **MVVM Architecture**

## Book Statuses

Books can have one of three statuses:

- `Should Read` – default when adding a book
- `Reading`
- `Read`

## How to Run

1. Clone the repository.
2. Open in Android Studio.
3. Sync Gradle.
4. Run the application on an emulator or real device.

## Known Issues

- You must **increment the Room database version** when changing the schema.
- Ensure **Internet permission** is enabled for API access.
- Not loading book covers
# Unicorner - The Student Marketplace

Unicorner is an Android marketplace platform built specifically for post-secondary students. It aims to create a trusted, local community for buying and selling items, promoting reusability and helping students save money.Users can browse listings, find great deals on campus, sell their own items, and connect with other students through a built-in chat system.

## Table of Contents

- [Core Features](#core-features)
- [Tech Stack & Architecture](#tech-stack-and-architecture)
- [Key Architectural Concepts](#key-architectural-concepts)
- [Getting Started](#getting-started))
- [Project Structure](#project-structure)
- [Future Work](#future-work))

## Core Features

- **Home Feed**: A dynamic "For You" feed showing all available items, excluding the user's own listings.
- **Detailed Product View**: Tap on any item to see more details, including description, price, condition, and seller information.
- **Search & Categories**: A dedicated search page and clickable category filters to easily find specific items.
- **User Profile**: A clean profile page displaying the user's name, rating, and a preview of their listings.
- **Listings Management**:
  - **Sell Item**: An intuitive form to create new listings with a title, price, condition, category, and description.
  - **Edit Listing**: Seamlessly edit existing listings. 
  - **Status Control**: Mark items as "Pending" or "Sold" via a simple popup menu. 
  - **Delete Listing**: Remove listings with a confirmation dialog.
- **Integrated Chat**:
  - Start a conversation directly from a product page.
  - View all ongoing conversations in a clean chat list.
  - Real-time-style messaging with a simulated auto-reply for a better user experience.
- **Favorites**: Users can "like" items and view them all in a dedicated Favorites screen.
- **Review System**: After a product is marked as "Sold", users can leave a review for the seller.

## Tech Stack & Architecture

- **Language**: Java
- **UI Toolkit**: Android XML with Material Design Components 3 (```MaterialButton```, ```CardView```, ```Chip```, ```BottomNavigationView```).
- **Architecture**: Model-View-Adapter / Activity-centric architecture.
- **Data Management**: A Singleton `ProductRepository` acts as a centralized, in-memory data store for all products, users, and chat data. This simulates a local database and ensures data consistency across all activities.
- **Asynchronous Operations**: ```Handler``` is used for simulating delayed network responses in the chat.
- **Image Loading**: ```Glide``` for efficient loading and caching of product images into ImageViews.
- **Navigation**: Android ```Intents``` are used for navigating between activities, with data passed via ```putExtra```.

## Key Architectural Concepts

- **Singleton Repository** (```ProductRepository.java```): This is the heart of the app's data layer. By creating a single, globally accessible instance, we ensure that every activity (Home, Profile, Edit, etc.) is always working with the same, up-to-date data. It prevents data inconsistencies between screens.
- **RecyclerViews with Adapters**: The app extensively uses RecyclerView to display dynamic lists efficiently.
  - ```ProductAdapter```: For the main product feed.
  - ```UserListingAdapter```: For the seller's view of their own listings (with edit/delete buttons).
  - ```ConversationListAdapter```: For the list of chats.
  - ```ChattingAdapter```: For displaying sent and received messages within a chat.
  - ```CategoryAdapter```: For the horizontal list of categories.
- **Activity Lifecycle for UI Refresh** (```onResume```): To solve the "stale data" problem, activities like ```UserListingsActivity``` and ```HomeActivity``` use the ```onResume()``` lifecycle method. This forces the UI to refresh its data from the ```ProductRepository``` every time the screen becomes visible, ensuring that edits or deletions made in other activities are immediately reflected.
- **Interface Callbacks for Loose Coupling**: The ```ProductAdapter``` uses an ```OnFavoriteClickListener``` interface to communicate clicks on the favorite button back to the ```HomeActivity```. This is a clean pattern that prevents the adapter from having a direct, rigid dependency on the activity.

## Getting Started

1. **Clone the repository**:
```bash
    git clone https://github.com/your-username/cosc341-group16-project.git
```
2. **Open in Android Studio**: Open Android Studio, select `File` > `Open`, and navigate to the cloned project directory.
3. **Build the Project**: Let Android Studio sync the Gradle files. This should happen automatically. If not, click the "Sync Project with Gradle Files" button.
4. **Run the App**: Select an emulator or connect a physical device and click the "Run" button.

## Project Structure
A brief overview of the most important files and packages:
```bash
app/src/main/java/com/example/myapplication/
│
├── activities/         (Suggestion: Move all activities here)
│   ├── HomeActivity.java       # Main screen, shows products
│   ├── ProfilePage.java        # User's profile and their listings preview
│   ├── UserListingsActivity.java # Shows all of a user's listings
│   ├── EditListingActivity.java  # Form to edit an existing product
│   ├── SellActivity.java         # Form to create a new product
│   ├── BuyItemActivity.java      # Detailed view of a single product
│   ├── ChatListActivity.java     # List of all conversations
│   └── ChattingActivity.java     # The actual chat screen
│
├── adapters/           (Suggestion: Move all adapters here)
│   ├── ProductAdapter.java
│   └── UserListingAdapter.java
│
├── data/               (Suggestion: Move model/repository here)
│   ├── ProductRepository.java    # Singleton data source
│   ├── Product.java              # Model for a product
│   ├── User.java                 # Model for a user
│   └── ChatManager.java          # Singleton for chat data
│
└── res/
    ├── layout/                 # All XML layout files for activities and items
    ├── drawable/               # All images and icons
    └── menu/                   # Bottom navigation menu, status popup menu
```

## Future Work

- Persistent Storage: Replace the singleton ProductRepository with a real database solution like Room or SQLite to persist data between app launches.
- Firebase Integration: Use Firebase for backend services:
  - Authentication: For real user login and registration.
  - Firestore: As a real-time NoSQL database for products and chats.
  - Storage: To upload and host user images and videos.
- Real-time Chat: Replace the Handler-based fake reply with a real-time solution using Firebase Realtime Database or Firestore listeners.
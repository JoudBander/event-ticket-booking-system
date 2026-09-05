# Event Ticket Booking System

A role-based JavaFX desktop application for booking event tickets, backed by an Oracle database.

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-0288D1?style=flat-square)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=flat-square&logo=oracle&logoColor=white)

Built as a 4-person team project for a university Advanced Programming course (CS313). I worked across the database layer, the controllers, and the UI, alongside three teammates.

## Overview

The system supports three roles — Customer, Event Manager, and Admin — each with its own dashboard and permission set. Customers browse events and book tickets, managers create and track their own events, and admins manage user accounts and oversee activity across the platform. Everything runs against a three-table Oracle schema (`USERS`, `EVENTS`, `BOOKINGS`) connected by real foreign keys.

## Features

**Customer** — browse and search events, book tickets, view booking history, cancel upcoming bookings.

**Event Manager** — create, edit, and delete events, track ticket sales and remaining capacity, see who's booked into each event.

**Admin** — manage user accounts across all roles, oversee events and bookings platform-wide.

## Screenshots

**Customer**

![Login](docs/screenshots/login.png)
![Customer dashboard](docs/screenshots/customer-dashboard.png)
![Available events](docs/screenshots/available-events.png)

**Event Manager**

![Manager dashboard](docs/screenshots/manager-dashboard.png)
![My events](docs/screenshots/manager-my-events.png)

**Admin**

![Admin dashboard](docs/screenshots/admin-dashboard.png)
![User management](docs/screenshots/admin-user-management.png)
![All events](docs/screenshots/admin-all-events.png)

## Design

Class diagram covering the `User` hierarchy, `Event`, `Booking`, and the `Searchable` interface:

![Class diagram](docs/screenshots/class-diagram.png)

Screen navigation flow from login through each role's dashboard:

![Navigation flow](docs/screenshots/navigation-flow.png)

## Database

| Table | Key columns |
|---|---|
| `USERS` | USER_ID (PK), USERNAME, PASSWORD, FULL_NAME, EMAIL, PHONE, USER_TYPE |
| `EVENTS` | EVENT_ID (PK), EVENT_NAME, EVENT_TYPE, EVENT_DATE, LOCATION, TICKET_PRICE, TOTAL_SEATS, AVAILABLE_SEATS, MANAGER_ID (FK → USERS) |
| `BOOKINGS` | BOOKING_ID (PK), CUSTOMER_ID (FK → USERS), EVENT_ID (FK → EVENTS), BOOKING_DATE, NUM_TICKETS, TOTAL_AMOUNT, STATUS |

Full DDL is in [`database/schema.sql`](database/schema.sql).

## Tech stack

Java 25, JavaFX (FXML + CSS) for the UI, Oracle Database via the JDBC thin driver, and Apache Ant for the build (it's a NetBeans project). The DAO layer is built around a generic `DatabaseOperations<T>` interface, so the three DAO classes share one CRUD contract instead of each defining their own from scratch.

## Project structure

```
EventTicketBookingSystemproject/
├── src/
│   ├── controllers/     # 11 controllers — login, dashboards, CRUD pages
│   ├── database/        # DatabaseConnection + DAO classes
│   ├── helper/          # AlertHelper, ValidationHelper
│   ├── interfaces/      # DatabaseOperations<T>, Searchable
│   ├── models/          # User, Admin, Customer, EventManager, Event, Booking
│   └── views/           # FXML layouts + style.css
├── database/
│   └── schema.sql
├── lib/
│   └── ojdbc8.jar
├── docs/
│   └── screenshots/
├── build.xml
└── README.md
```

## Running it

You'll need NetBeans, JDK 25 with JavaFX configured, and a local Oracle instance (XE works fine).

1. Clone the repo and open it in NetBeans (File → Open Project)
2. Run `database/schema.sql` against your Oracle instance
3. Update the connection details in `src/database/DatabaseConnection.java` if yours differ
4. Run the project (Shift+F6, or `ant run` from the command line)

## Worth pointing out

`BookingDB.insert()` wraps the booking insert and the seat-count update in a single database transaction, so if either step fails, both roll back — seat counts can't drift out of sync with actual bookings. Every DAO class uses `PreparedStatement`, so there's no SQL injection surface anywhere in the data layer.

## What's not finished

The `Admin` class has a few statistics methods (`generateStatistics()`, `generateRevenueStats()`) that turned out to be redundant — the live stats actually shown on the User Management page (total users, events, bookings, revenue) are computed directly in the controller instead. Worth cleaning up as dead code rather than a missing feature. Testing was manual, screen by screen, not automated. DB credentials are hardcoded for local development, which I'd externalize for anything beyond a class project.

## Author

Joud Al Thonayan
Computer Science student, Princess Nourah University
[LinkedIn](https://www.linkedin.com/in/joud-al-thonayan-bb126a431) • [GitHub](https://github.com/JoudBander)

# Dont-Wreck-My-House
Welcome to "Don't Wreck My House" a Java application for managing guest accommodations akin to the services provided by Airbnb. This application simplifies the process of pairing guests with hosts, ensuring that finding the perfect place to stay is as smooth as a breeze. Whether you're an accommodation administrator looking to manage reservations efficiently or you're learning the nuances of Java Fundamentals through a real-world application, you've come to the right place!

#Overview
This program is intended to provide a seamless interface for accommodation admins to manage reservations. From viewing existing bookings to creating new ones, editing details, or canceling future reservations, this application equips you with everything you need to ensure your guests have a pleasant stay without any hiccups.

# Current Features
- View Existing Reservations: Effortlessly browse through all the reservations associated with a host.
- Create Reservations: Pair guests with hosts and book accommodations for specific dates, provided the chosen location is available.
- Edit Reservations: Need to change the booking details? No problem! Edit the reservation details as needed.
- Cancel Reservations: Plans change, and when they do, cancelling future reservations is just a few clicks away.

# Technical Details
- Maven Project: This is a Maven-based project, ensuring easy management of dependencies and project lifecycle.
- Spring Dependency Injection: Configured with XML or annotations for managing components and services.
- Data Representation: Uses BigDecimal for financial calculations and LocalDate for date management. All data is represented through models within the application.
- Unique Identifiers: Reservation identifiers are unique per host. The combination of a reservation identifier and a host identifier uniquely identifies a reservation.

# Testing
- Thorough testing has been conducted to ensure reliability. This includes:
- Data component testing.
- Domain component testing using test doubles.
- User interface testing is not part of the requirements.

# Installation
1. Clone this repository to your local machine.
2. Navigate to the directory where you cloned the project.
3. Compile the code using Maven. If you don't have Maven installed, follow the instructions here to set it up.
4. Once compiled, you can run the application using Java.

# Usage
To start the application, run the following command in your terminal:
- java -jar path/to/your/compiled/jarfile.jar

Navigating the application is straightforward:
You'll be greeted by the Main Menu upon startup. Use the numbers [0-4] to navigate through the options.
Follow the prompts on the screen to view reservations, make a new reservation, edit an existing one, or cancel a future reservation.

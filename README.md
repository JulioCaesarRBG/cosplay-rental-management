# Rental Cosplay Management System

A modern Java Swing-based desktop application for managing costume rental business operations, now with improved architecture, security, and code quality.

## Features

- **User Authentication**: Secure login system with input validation and password hashing
- **Costume Management**: Add, edit, delete, and view costume inventory
- **Customer Management**: Manage customer information and contacts
- **Rental Operations**: Process costume rentals with date tracking
- **Return Management**: Handle costume returns with late fee calculations
- **Reporting**: Print rental receipts and reports
- **MVC Architecture**: Clean separation of Model, View, and Controller
- **Service Layer**: Business logic separated from UI
- **Logging System**: Application events and errors are logged
- **Input Validation**: Comprehensive validation and sanitization for all user input

## Technology Stack

- **Language**: Java
- **GUI Framework**: Java Swing
- **Database**: MySQL
- **Build Tool**: Apache Ant (NetBeans project)
- **Libraries**: 
  - JCalendar for date picking
  - MySQL Connector/J for database connectivity
  - rs2xml for table data binding
  - Java Logging API for application logging

## Database Schema

The application uses a MySQL database with the following main tables:
- `user` - System users and authentication (with password hashing and salt)
- `kostum` - Costume inventory management  
- `pelanggan` - Customer information
- `rental` - Rental transaction records
- `pengembalian` - Return transaction records

## Project Structure

```
src/
├── code/
│   ├── model/                # Entity models (User, Costume, Customer, Rental)
│   ├── service/              # Business logic (UserService, CostumeService, etc)
│   ├── util/                 # Utilities (logging, validation, constants)
│   ├── Login.java            # User authentication (View/Controller)
│   ├── Costume.java          # Costume management (View/Controller)
│   ├── Pelanggan.java        # Customer management (View/Controller)
│   ├── SewaKostum.java       # Rental operations (View/Controller)
│   └── Pengembalian.java     # Return operations (View/Controller)
├── Gambar/
│   └── logo.png              # Application logo
lib/
├── jcalendar-1.4.jar
├── mysql-connector-j-8.0.33.jar
└── rs2xml.jar
```

## Setup Instructions

1. **Database Setup**:
   - Install MySQL server
   - Create database `rental_cosplay`
   - Set up required tables and user account
   - Use password hashing for user table (see SECURITY_IMPROVEMENTS.md)

2. **Application Setup**:
   - Open project in NetBeans IDE
   - Ensure all JAR libraries are in classpath
   - Configure database connection settings in `src/config/database.properties`
   - Build and run the application

## Security & Code Quality

- All database operations use PreparedStatement to prevent SQL injection
- Passwords are hashed and salted before storage
- Input is validated and sanitized throughout the application
- Centralized database connection management
- Logging system for error and event tracking
- MVC pattern and service layer for maintainable code

See [`SECURITY_IMPROVEMENTS.md`](SECURITY_IMPROVEMENTS.md) for details.

## License

This project is for educational purposes.

## Contributing

This is a learning project. Suggestions for improvements are welcome!
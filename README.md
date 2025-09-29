# Rental Cosplay Management System

A Java Swing-based desktop application for managing costume rental business operations.

## Features

- **User Authentication**: Secure login system
- **Costume Management**: Add, edit, delete, and view costume inventory
- **Customer Management**: Manage customer information and contacts
- **Rental Operations**: Process costume rentals with date tracking
- **Return Management**: Handle costume returns with late fee calculations
- **Reporting**: Print rental receipts and reports

## Technology Stack

- **Language**: Java
- **GUI Framework**: Java Swing
- **Database**: MySQL
- **Build Tool**: Apache Ant (NetBeans project)
- **Libraries**: 
  - JCalendar for date picking
  - MySQL Connector/J for database connectivity
  - rs2xml for table data binding

## Database Schema

The application uses a MySQL database with the following main tables:
- `user` - System users and authentication
- `kostum` - Costume inventory management  
- `pelanggan` - Customer information
- `rental` - Rental transaction records
- `pengembalian` - Return transaction records

## Project Structure

```
src/
├── code/
│   ├── Login.java          # User authentication
│   ├── Costume.java        # Costume management
│   ├── Pelanggan.java      # Customer management
│   ├── SewaKostum.java     # Rental operations
│   └── Pengembalian.java   # Return operations
├── Gambar/
│   └── logo.png           # Application logo
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

2. **Application Setup**:
   - Open project in NetBeans IDE
   - Ensure all JAR libraries are in classpath
   - Configure database connection settings
   - Build and run the application

## Security Notes

⚠️ **Important**: This application is currently in development and contains security vulnerabilities that should be addressed before production use:
- SQL injection vulnerabilities
- Plain text password storage
- Hard-coded database credentials

## License

This project is for educational purposes.

## Contributing

This is a learning project. Suggestions for improvements are welcome!
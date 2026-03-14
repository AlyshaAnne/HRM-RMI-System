# HRM-RMI-System

Java RMI-based Distributed Human Resource Management System  
Course: CT024-3-3 Distributed Computer Systems

---

## System Overview

This project implements a distributed HR Management System using:

- Java RMI (Remote Method Invocation)
- JavaFX (Client GUI)
- PostgreSQL (Database)
- JDBC (Database connectivity)

Architecture flow:

Client (JavaFX UI)  
⬇ RMI  
Server (HRMServiceImpl)  
⬇ JDBC  
PostgreSQL Database  

---

## Project Structure

HRM-RMI-System  
├── src  
│   ├── client  
│   ├── server  
│   └── shared  
├── lib  
├── bin  
└── .vscode  

---

## Features Implemented

### HR Module

- Login authentication (database-based)
- Submit password reset request
- View reset requests
- Approve / Reject reset requests
- Account activation / deactivation

### Employee Module

- (Teammate implementation if applicable)

---

## Setup Instructions

### 1. Requirements

Install:

- JDK 17 or above
- PostgreSQL
- JavaFX SDK (matching JDK version)
- VS Code with Extension Pack for Java

---

### 2. Database Setup

1. Open pgAdmin.
2. Create a database (example: hrm_db).
3. Create required tables:
   - accounts
   - reset_requests
   - employees (if applicable)
4. Update database credentials inside:
   src/server/service/DatabaseConnection.java

---

### 3. JavaFX Setup

Download JavaFX SDK and extract it.

Example path:
C:\javafx-sdk-25.0.2\lib

Update .vscode/launch.json and ensure the VM arguments include:

--module-path "PATH_TO_YOUR_JAVAFX_LIB"
--add-modules javafx.controls,javafx.fxml

Each team member must update the JavaFX path according to their own machine.

---

### 4. Running the System

Step 1 – Start Server  
Run:
HRMServer.java  

Wait until the console shows:
Server ready...

Step 2 – Start Client  
Run:
Main.java  

The login screen should appear.

---

## Important Notes

- Always start the Server before the Client.
- If RMI method mismatch errors occur:
  1. Delete the bin folder.
  2. Recompile the project.
  3. Restart the server.
- If JavaFX classes (Stage, Scene, etc.) cannot be resolved:
  - Verify JavaFX path in launch.json
  - Restart VS Code
  - Clean Java Language Server workspace

---

## Distributed Justification

This system uses a distributed architecture to:

- Separate client UI from server logic
- Centralize business logic on the server
- Allow multiple remote clients
- Improve scalability and maintainability

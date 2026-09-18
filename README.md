# Hospital Appointment Management System

A console-based Java application for managing patients, doctors, and appointments at a small clinic. Built for flipped course evaluation project for Vityarthi.

---

## Project Overview

City Clinic's Hospital Appointment System lets clinic staff:
- Register and manage patient records
- Add doctors with specializations and time slots
- Book appointments (with double-booking prevention)
- Cancel appointments and restore time slots
- Mark appointments complete and generate itemized consultation bills

All data persists between runs via plain text (pipe-delimited) files — no database required.

---

## Features

| Module | Feature |
|---|---|
| **Patients** | Register, view, search (by name/ID), update |
| **Doctors** | Add doctors with specialization, fee, and slots |
| **Appointments** | Book (slot-validated), cancel, view (all/filtered) |
| **Billing** | Auto-generate bill with GST when appointment is completed |
| **Persistence** | Data saved to `data/` folder as `.txt` files |

---

## Technologies Used

- **Language:** Java 17+ (pure core Java)
- **I/O:** `java.io.BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`
- **No external libraries, no database, no GUI**

---

## Project Structure

```
HospitalAppointmentSystem/
├── src/
│   ├── Main.java                          ← Console menu entry point
│   ├── model/
│   │   ├── Person.java                    ← Abstract base class
│   │   ├── Patient.java                   ← Extends Person
│   │   ├── Doctor.java                    ← Extends Person
│   │   ├── Appointment.java               ← Appointment model
│   │   └── Billable.java                  ← Interface for bill generation
│   ├── service/
│   │   └── HospitalService.java           ← All business logic
│   ├── storage/
│   │   └── FileHandler.java               ← File I/O persistence layer
│   └── exceptions/
│       ├── PatientNotFoundException.java
│       ├── SlotUnavailableException.java
│       └── InvalidAppointmentException.java
├── data/
│   ├── doctors.txt                        ← Persisted doctor records
│   ├── patients.txt                       ← Persisted patient records
│   └── appointments.txt                   ← Persisted appointment records
├── README.md
└── statement.md
```

---

## OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| **Inheritance** | `Patient` and `Doctor` extend abstract `Person` |
| **Abstraction** | `Person.getDisplayInfo()` is abstract; each subclass implements it |
| **Encapsulation** | All fields are `private`; accessed via `public` getters/setters |
| **Interface** | `Billable` interface implemented by `HospitalService` |
| **Custom Exceptions** | `PatientNotFoundException`, `SlotUnavailableException`, `InvalidAppointmentException` |
| **Polymorphism** | `getDisplayInfo()` behaves differently for `Patient` vs `Doctor` |

---

## How to Compile & Run

### Prerequisites
- Java JDK 17 or higher installed
- `javac` and `java` available in your PATH

### Step 1 — Navigate to the project root

```bash
cd path/to/project
```

### Step 2 — Compile all Java source files

```bash
javac -d out src/model/*.java src/exceptions/*.java src/storage/*.java src/service/*.java src/Main.java
```

### Step 3 — Run the application

```bash
java -cp out Main
```


## How to Test

### Quick Test Walkthrough

1. **Run the app** → main menu appears
2. **Option 5** → View Doctors — you'll see 5 pre-loaded doctors
3. **Option 2** → View Patients — 3 pre-loaded patients shown
4. **Option 6** → Book Appointment
   - Patient ID: `P003`, Doctor ID: `D003`, Slot: `10:00`
   - Should succeed 
5. **Option 6** → Book same slot again (D003, 10:00) → `SlotUnavailableException` shown 
6. **Option 8** → View Appointments — see all entries including the new one
7. **Option 9** → Complete Appointment — enter the new appointment ID → Bill printed 
8. **Option 7** → Try to cancel the completed appointment → `InvalidAppointmentException` 
9. **Option 10** → Exit and re-run — data should persist 

---

## Console Menu

```
MAIN MENU
  1.  Register Patient
  2.  View / Search Patients
  3.  Update Patient Details
  4.  Add Doctor
  5.  View Doctors & Availability
  6.  Book Appointment
  7.  Cancel Appointment
  8.  View Appointments
  9.  Complete Appointment & Generate Bill
  10. Exit
```

---

## Sample Bill Output

```

CONSULTATION BILL

Appointment ID : A003 
Date / Slot    : 2026-09-17 at 10:00

Patient        : Rahul Patel
Patient ID     : P003

Doctor         : Dr. Anjali Verma
Specialization : Orthopedic Surgeon

Consultation Fee: Rs. 1200.00
GST (18%)      : Rs. 216.00
TOTAL PAYABLE  : Rs. 1416.00

  Thank you for visiting City Clinic!
```

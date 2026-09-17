# Problem Statement

## Title
**Hospital Appointment Management System**

## Problem Statement

Small clinics and private hospitals often struggle with manual appointment tracking — paper logs get lost, double-bookings happen, and generating accurate bills takes time. There is a need for a simple, lightweight software solution that can manage patient records, doctor schedules, and appointments without requiring expensive software or internet connectivity.

This project addresses that need by providing a **console-based Java application** that a clinic receptionist can run on any computer to manage the clinic's daily operations.

---

## Scope

The system handles the following within its scope:

| In Scope | Out of Scope |
|---|---|
| Patient registration and record management | Online/web access or mobile app |
| Doctor schedule and slot management | Integration with external healthcare systems |
| Appointment booking with double-booking prevention | Advanced reporting / analytics |
| Appointment cancellation with slot restoration | Multi-clinic / multi-branch support |
| Consultation bill generation with GST | Payment gateway integration |
| File-based data persistence between runs | Database / cloud storage |

---

## Target Users

| User | Role |
|---|---|
| **Clinic Receptionist** | Primary user — registers patients, books/cancels appointments, generates bills |
| **Clinic Administrator** | Adds/manages doctor records and specializations |

---

## High-Level Features

### 1. Patient Registration & Records
- Register new patients with name, age, gender, contact, and medical history notes
- Search patients by name (partial match) or Patient ID
- Update patient details any time

### 2. Doctor & Schedule Management
- Add doctors with specialization, consultation fee, and available time slots
- View all doctors and their current slot availability
- Slots are automatically removed on booking and restored on cancellation

### 3. Appointment Booking & Billing
- Book an appointment by selecting patient, doctor, and an available slot
- Double-booking prevention enforced at the service layer
- Cancel a scheduled appointment (slot is automatically freed)
- View all appointments, or filter by Patient ID or Doctor ID
- Mark an appointment as complete and auto-generate an itemized bill (fee + 18% GST)

### 4. Data Persistence
- All data (patients, doctors, appointments) is saved to plain-text pipe-delimited files
- Data is automatically loaded on startup — no data loss between sessions

---

## Technologies & Concepts

- **Language:** Java 17+
- **OOP Principles:** Inheritance (`Person → Patient/Doctor`), Encapsulation, Abstraction, Polymorphism
- **Interface:** `Billable` interface for bill generation
- **Custom Exceptions:** `PatientNotFoundException`, `SlotUnavailableException`, `InvalidAppointmentException`
- **File I/O:** `java.io` package — `BufferedReader`, `BufferedWriter`
- **Input Handling:** `java.util.Scanner` for console menu

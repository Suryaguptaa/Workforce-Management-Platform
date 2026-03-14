# Workforce Management System

A backend REST API built with Spring Boot that helps managers track employee work, manage team hierarchies, and get weekly summaries of team activity.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-23-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-purple?style=flat-square)

> Backend only — API-first design. No frontend included.

---

## What it does

Managers in a company often have no clear way to see who is working on what, without using heavy tools like Jira or invasive monitoring software. This project provides a simple, structured backend where:

- Employees log what they worked on each day
- Managers can see weekly summaries for their entire team
- The system flags inactive employees or those who are overloaded
- Every important action is recorded in an audit log
- A scheduled job runs every Monday to generate weekly team reports

---

## Features

- **Employee Management** — Create employees with roles (Employee, Manager, Admin)
- **Hierarchy Management** — Assign managers to employees with validation (no self-assignment, no circular hierarchy)
- **Work Logging** — Employees log daily work with type, description, start time and end time
- **Weekly Summaries** — Aggregate work hours per employee and per team for any given week
- **Audit Logging** — Append-only log of every critical action (who did what and when)
- **Weekly Scheduler** — Runs every Monday at 9 AM to generate and store team reports
- **AI Summary Layer** — Converts weekly data into plain English summaries (mocked, replaceable with any AI API)

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 23 | Core language |
| Spring Boot 3.x | Backend framework |
| Spring Data JPA | Database access |
| Hibernate | ORM implementation |
| MySQL 8 | Relational database |
| Spring Scheduler | Weekly background jobs |
| Bean Validation | Input validation |

---

## Run Locally

**Prerequisites:** Java 23, MySQL 8, Maven

```bash
# Clone the repo
git clone https://github.com/Suryaguptaa/Workforce-Management-Platform.git
cd Workforce-Management-Platform

# Create the database
mysql -u root -p
CREATE DATABASE work_intelligence;
exit

# Update your MySQL credentials in src/main/resources/application.properties

# Run
mvn spring-boot:run
```

Server starts at `http://localhost:8080`

---

## API Reference

### Employees

```
POST   /employees                          Create a new employee
GET    /employees                          Get all employees
POST   /employees/assign-manager           Assign a manager to an employee
```

**Create employee request:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "EMPLOYEE"
}
```

Valid roles: `EMPLOYEE` · `MANAGER` · `ADMIN`

---

### Work Logs

```
POST   /work-logs                          Log a work entry
GET    /work-logs/employee/{id}/weekly     Get employee weekly summary
GET    /work-logs/manager/{id}/weekly      Get full team weekly summary
```

**Log work request:**
```json
{
  "employeeId": 5,
  "workType": "FEATURE",
  "description": "Built the weekly summary API",
  "startTime": "2026-01-27T10:00:00",
  "endTime": "2026-01-27T13:00:00"
}
```

Valid work types: `FEATURE` · `BUG` · `RESEARCH` · `SUPPORT`

---

### Manager Assignment Validations

When assigning a manager the system checks:
- Target employee must have the `MANAGER` role
- An employee cannot be assigned as their own manager
- Circular hierarchies are blocked
- The entire operation runs inside a single `@Transactional` block

---

### Weekly Scheduler

A scheduled job fires every Monday at 9:00 AM:

```java
@Scheduled(cron = "0 0 9 ? * MON")
```

It aggregates the previous week's work data per team, generates a plain-English summary, and stores it. The scheduling, business logic, and AI layers are fully separated.

---

### Audit Log

Every critical action is recorded with:

| Field | Description |
|---|---|
| `actorId` | Who triggered the action |
| `actorType` | USER / MANAGER / SYSTEM |
| `entityType` | What was affected (EMPLOYEE, WORK_LOG, etc.) |
| `entityId` | ID of the affected record |
| `action` | What happened (CREATE, ASSIGN_MANAGER, etc.) |
| `timestamp` | Exact time |

Audit logs are append-only — no updates, no deletes.

---

## Project Structure

```
src/main/java/
├── entity/         Employee, WorkLog, AuditLog, WeeklySummary
├── repository/     Spring Data JPA repositories
├── service/        Business logic + @Transactional
├── controller/     REST endpoints
├── dto/            Request and response objects
├── scheduler/      Weekly background job
└── ai/             Summary generation layer (mocked)
```

---

## Author

**Surya Dev Gupta**
6th Semester — Lakshmi Narain College of Technology Excellence

---

## License

MIT License — see [LICENSE](LICENSE) for details.
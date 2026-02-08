# Multi-Tenant Onboarding Notification Platform

An event-driven, multi-tenant notification system designed to handle onboarding and lifecycle reminders at scale.

This project demonstrates backend architecture patterns commonly used in SaaS platforms, including scheduling, event-driven processing, idempotency, and per-tenant rate limiting.

---

## Problem Statement

Companies often need to notify employees or managers at specific points in an onboarding or review workflow (e.g. document submission reminders, probation check-ins).

This becomes complex when:
- Multiple tenants (companies) exist
- Each tenant has different rules and schedules
- Notifications must be sent reliably and without duplication
- Delivery must be rate-limited per tenant

This project solves those problems in a clean, extensible way.

---

## Key Features

- Multi-tenant architecture
- Configurable notification rules per tenant
- Scheduled job to determine eligible notifications
- Event-driven processing using RabbitMQ
- Idempotent message handling (dedupe keys)
- Per-tenant rate limiting using Resilience4j
- Task lifecycle tracking (PENDING / SENT / FAILED)
- REST APIs for triggering jobs and inspecting state

---

## High-Level Architecture

1. A scheduled job scans tenants and employees to determine which notifications are due.
2. Eligible notifications are published as events to RabbitMQ.
3. Consumers process events, create notification tasks, and ensure idempotency.
4. A delivery service sends notifications with per-tenant rate limiting.
5. Tasks are marked as SENT or FAILED for observability and retries.

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- RabbitMQ
- H2 (in-memory database)
- Resilience4j (rate limiting)
- Maven

---

## Running Locally

### Prerequisites
- Java 21
- Maven
- RabbitMQ (local)

### Start RabbitMQ
```bash
docker run -d --hostname rabbit --name rabbit \
  -p 5672:5672 -p 15672:15672 rabbitmq:3-management

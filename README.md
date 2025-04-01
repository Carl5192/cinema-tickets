# Ticket Service Implementation

## Overview
This project implements the `TicketService` interface for the DWP coding exercise. It processes ticket purchases for Adult, Child, and Infant tickets, calculating total costs and seat reservations while enforcing business rules. Built with Java 21, it emphasises clean, reusable, and well-tested code.

## Business Rules
- **Ticket Types and Prices:**
  - Adult: £25
  - Child: £15
  - Infant: £0 (no payment, no seat)
- **Constraints:**
  - Maximum 25 tickets per purchase.
  - Child/Infant tickets require at least one Adult ticket.
  - Infants cannot exceed Adults (sit on laps).
- **Services:**
  - Uses `TicketPaymentService` for payments.
  - Uses `SeatReservationService` for seat allocation.

## Implementation Details
- **Structure:** Single `TicketServiceImpl` class with private methods:
  - `calculateTotals`: Computes cost and seats in one pass.
  - `validatePurchase`: Enforces all rules set out in the brief.
- **Optimisations:**
  - Conditional service calls (`if > 0`) avoid unnecessary third-party requests.
  - Infants tracked only for validation, per "no pay, no seat."
- **Assumptions:** Relies on third-party services being defect-free, account IDs > 0 valid.

## Setup
1. **Requirements:**
   - Java 21
   - Maven
   - JUnit 5 & Mockito (for tests)
2. **Build:**
   ```bash
   mvn clean install
3. **Test**
   ```bash
   mvn -Dtest=TicketServiceImplTest test



   

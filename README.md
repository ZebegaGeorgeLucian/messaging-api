# Messaging API

A multi-channel messaging REST API built with Spring Boot, AWS SQS, and AWS S3.
I built this project to deepen my understanding of how messaging platforms work
under the hood and to get hands-on experience with AWS for the first time.

## What it does

The API simulates how a real messaging platform handles outbound messages across
multiple channels — SMS, MMS, WhatsApp, and Facebook. When a message is sent, it
gets queued in AWS SQS and processed asynchronously by a background worker, which
mirrors how production messaging systems decouple delivery from the API layer.
MMS messages support media attachments stored in AWS S3, served via presigned URLs
that expire after one hour for secure access.

## Architecture
Client → REST API → H2 Database (QUEUED)
→ AWS SQS Queue
↓
Message Worker (polls every 5s)
↓
H2 Database (SENT)

The API never blocks waiting for delivery — it saves the message, pushes it to SQS,
and returns 201 immediately. The worker runs independently on a schedule, picks up
messages from the queue, updates their status, and deletes them from SQS only after
successful processing so nothing gets lost if the worker crashes mid-run.

## Tech stack

- **Java 17**
- **Spring Boot 3.2** — web, data JPA, scheduling
- **H2** — in-memory database for message persistence
- **AWS SQS** — async message queue
- **AWS S3** — media storage for MMS attachments
- **Lombok** — reduces boilerplate
- **Maven**

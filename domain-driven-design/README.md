# DDD Architect Agent

An AI agent specialized in Domain-Driven Design principles, patterns, and practices for designing software systems.

## Purpose

The DDD Architect agent helps teams design systems using strategic and tactical DDD patterns:

### Strategic Design
- Bounded Context identification and mapping
- Domain classification (Core, Supporting, Generic)
- Context relationships (Customer-Supplier, ACL, etc.)
- Ubiquitous Language establishment

### Tactical Design
- Aggregate design with invariants
- Entity and Value Object identification
- Domain Events catalog
- Domain Services specification
- Repository patterns

### Architectural Patterns
- Hexagonal Architecture
- CQRS (when justified)
- Event Sourcing
- Saga Pattern
- Anti-Corruption Layers

## When to Use

Use this agent when:
- Designing a new system using DDD principles
- Analyzing existing codebases for DDD compliance
- Identifying bounded contexts in complex domains
- Creating architectural diagrams and context maps
- Defining aggregates, entities, and value objects

## Examples

The `examples/` folder contains:

### insurance-domain/
A comprehensive insurance policy management system design:
- `insurance-policy-management-system.md` - Complete DDD blueprint including:
  - 10 bounded contexts
  - Context map with relationships
  - Customer journey sequence diagrams
  - Aggregate class diagrams
  - State machine diagrams
  - 130+ business rules
  - CQRS read models
  - Saga definitions
  - Payment and subscription models

## Knowledge Base

- `ddd-expert-knowledge-base.md` - Comprehensive DDD reference covering:
  - Eric Evans' original concepts
  - Martin Fowler's patterns
  - Microsoft implementation guidance
  - Real-world case studies

## Agent Definition

- `ddd-architect-agent.md` - The full agent definition

## Related Resources

- Domain-Driven Design by Eric Evans
- Implementing Domain-Driven Design by Vaughn Vernon
- Patterns of Enterprise Application Architecture by Martin Fowler

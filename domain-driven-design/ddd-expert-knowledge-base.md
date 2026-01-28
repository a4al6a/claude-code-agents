# Domain-Driven Design Expert Knowledge Base

This document contains comprehensive summaries of Domain-Driven Design content from Martin Fowler's website (martinfowler.com) and Microsoft's MSDN Magazine. It serves as the foundational knowledge base for a DDD expert agent.

---

## Table of Contents

1. [Core Concepts](#core-concepts)
   - [Domain-Driven Design (Overview)](#1-domain-driven-design-overview)
   - [Ubiquitous Language](#2-ubiquitous-language)
   - [Bounded Context](#3-bounded-context)
   - [Evans Classification (Entities, Value Objects, Services)](#4-evans-classification)
   - [DDD Aggregates](#5-ddd-aggregates)
   - [Value Objects](#6-value-objects)
2. [Patterns and Anti-Patterns](#patterns-and-anti-patterns)
   - [Anemic Domain Model (Anti-Pattern)](#7-anemic-domain-model-anti-pattern)
   - [CQRS](#8-cqrs-command-query-responsibility-segregation)
   - [Eager Read Derivation](#9-eager-read-derivation)
   - [Contextual Validation](#10-contextual-validation)
3. [Domain Modeling Challenges](#domain-modeling-challenges)
   - [Type Instance Homonym](#11-type-instance-homonym)
   - [Contradictory Observations](#12-contradictory-observations)
   - [Currency As Value](#13-currency-as-value)
4. [Applied DDD Examples](#applied-ddd-examples)
   - [Customer Loyalty Software](#14-customer-loyalty-software)
5. [Strategic Design at Scale](#strategic-design-at-scale)
   - [Data Mesh Architecture](#15-data-mesh-architecture)
   - [Decentralizing Architecture at Xapo Bank](#16-decentralizing-architecture-at-xapo-bank)
6. [Implementation Guide (Microsoft)](#implementation-guide-microsoft)
   - [The Platonic Model Philosophy](#17-the-platonic-model-philosophy)
   - [Entities in Practice](#18-entities-in-practice)
   - [Value Objects in Practice](#19-value-objects-in-practice)
   - [Aggregate Roots in Practice](#20-aggregate-roots-in-practice)
   - [Domain Services vs Application Services](#21-domain-services-vs-application-services)
   - [Repositories](#22-repositories)
   - [Anti-Corruption Layers](#23-anti-corruption-layers)
   - [Modules and Organization](#24-modules-and-organization)
7. [Design Principles](#design-principles)
   - [Single Responsibility Principle in DDD](#25-single-responsibility-principle-in-ddd)
   - [Law of Demeter](#26-law-of-demeter)
   - [Layer Supertype Pattern](#27-layer-supertype-pattern)
8. [Practical Guides (ThoughtWorks)](#practical-guides-thoughtworks)
   - [Strategic vs Tactical Design](#28-strategic-vs-tactical-design)
   - [Problem and Solution Spaces](#29-problem-and-solution-spaces)
   - [Tactical Design Constructs](#30-tactical-design-constructs)
   - [Four-Stage Learning Path](#31-four-stage-learning-path)
   - [DDD for Services Architecture](#32-ddd-for-services-architecture)

---

## Core Concepts

### 1. Domain-Driven Design (Overview)

**Source:** https://martinfowler.com/bliki/DomainDrivenDesign.html

#### Definition
Domain-Driven Design (DDD) is a software development methodology that centers development on programming a domain model with a rich understanding of the processes and rules of a domain. The approach originated from Eric Evans's 2003 book and has evolved through contributions from a growing practitioner community.

#### Key Principles

- **Model-Centric Development:** Unlike earlier approaches that treated models as preliminary artifacts, DDD advocates for building and refining models throughout the software's lifecycle
- **Ubiquitous Language:** Domain terminology is embedded directly into software systems rather than keeping models confined to documentation
- **Living Models:** Models evolve continuously as understanding deepens

#### Essential Conceptual Elements

1. **Evans Classification:** Organizes objects into three categories:
   - Entities
   - Value Objects
   - Service Objects

2. **Aggregates:** Help structure domain logic into cohesive clusters

3. **Bounded Contexts:** Address organizing complex domains at the strategic level—a solution previously lacking in software architecture discourse

#### Applicability

While rooted in object-oriented thinking, DDD's fundamental concepts transcend specific programming paradigms. The strategic design aspects particularly apply across diverse development approaches.

#### Recommended Resources

- Eric Evans's original book (essential reading, though challenging)
- Vaughn Vernon's 2013 work (accessible follow-up emphasizing strategic design)

---

### 2. Ubiquitous Language

**Source:** https://martinfowler.com/bliki/UbiquitousLanguage.html

#### Definition
Ubiquitous Language describes the practice of establishing a shared, precise vocabulary between developers and domain experts. The language must be grounded in the Domain Model used within the software system.

#### Core Principles

1. **Rigor is Required:** Software doesn't cope well with ambiguity—the language must be precise

2. **Continuous Testing Through Dialogue:** Using the language in conversations with domain experts serves as a validation mechanism for both the language and the underlying domain model

3. **Evolutionary Development:** The language and model should mature as the team deepens its understanding of the business domain

#### Key Responsibilities

| Role | Responsibility |
|------|----------------|
| **Domain Experts** | Challenge terminology or structures that inadequately represent domain knowledge |
| **Developers** | Identify ambiguities and inconsistencies that could compromise system design |

#### Goal

By consistently applying model-based language until it achieves fluency, teams develop "a model that is complete and comprehensible, made up of simple elements that combine to express complex ideas."

#### Benefits

- Bridges the communication gap between technical and business stakeholders
- Creates shared understanding essential for effective software design
- Validates the domain model through regular use

---

### 3. Bounded Context

**Source:** https://martinfowler.com/bliki/BoundedContext.html

#### Core Concept

Bounded Context is a foundational pattern that addresses the challenge of managing large, complex software systems. Rather than attempting to create a single unified model for an entire organization, this pattern divides large domains into smaller, self-contained contexts, each with its own cohesive model.

#### The Problem It Solves

Large organizations struggle with terminology inconsistencies across departments. For example, "meter" might carry different meanings depending on the department in a utility company. These linguistic variations, called **polysemes**, create confusion in software design because "total unification of the domain model for a large system will not be feasible or cost-effective."

#### Key Principles

1. **Unified Models Within Contexts:** Each bounded context maintains internal consistency within its own language and conceptual framework, allowing different groups to use the same terms with specialized meanings

2. **Shared and Distinct Concepts:** Contexts may share common concepts (like products and customers) but model them differently. Integration requires explicit mapping mechanisms between these varying interpretations

3. **Boundary Determination:**
   - Human culture and communication patterns primarily shape context boundaries
   - Models function as a Ubiquitous Language
   - Technical factors also influence boundaries (e.g., distinguishing between in-memory and database representations)

#### Strategic Relationships

DDD's strategic design explores various relationship patterns between bounded contexts, best visualized through **context maps** that depict their interactions and dependencies.

#### Practical Value

- Teams can work autonomously within their domains
- System integration is maintained through carefully managed interfaces between contexts
- Reduces complexity by containing it within bounded areas

---

### 4. Evans Classification

**Source:** https://martinfowler.com/bliki/EvansClassification.html

Eric Evans' taxonomy from *Domain Driven Design* identifies three fundamental types of domain objects:

#### The Three Categories

| Category | Definition | Examples | Key Characteristic |
|----------|------------|----------|-------------------|
| **Entities** | Objects with distinct identities persisting through time and various representations | Customer, Ship, Rental Agreement | Maintain uniqueness regardless of attribute changes |
| **Value Objects** | Objects lacking independent identity, defined solely by their attributes | Date, Money, Database Query | Two instances with identical attributes are equivalent; **must be immutable** |
| **Services** | Standalone operations within a domain context | Database connections, Messaging gateways, Repositories, Product factories | Typically exist as singletons within execution context |

#### Key Distinctions

The most concrete differentiator between entities and values involves **equality**:
- Value Objects override equality and hash methods
- Entities typically don't override these methods
- Multiple identical value instances are acceptable
- Duplicate entity representations create problems

#### Implementation Considerations

- Service singularity usually applies within processing contexts (one per thread in multithreaded environments)
- Implementation details should remain hidden from other domain objects

#### Terminology Warning

These terms carry significant overload in software engineering:
- "Entity" conflicts with database entities
- "Service" conflicts with service-oriented architecture and service layers
- Context-specific usage requires clarification

---

### 5. DDD Aggregates

**Source:** https://martinfowler.com/bliki/DDD_Aggregate.html

#### Definition

A DDD aggregate is a cluster of domain objects that can be treated as a single unit. The pattern helps organize related entities into cohesive groups that maintain integrity together.

#### Key Components

Each aggregate contains an **aggregate root**—a designated component object that serves as the entry point.

**Critical Rule:** Any references from outside the aggregate should only go to the aggregate root. This ensures the root can protect the aggregate's consistency.

#### Data Transfer and Transactions

- Aggregates function as atomic units for persistence operations
- The framework treats them as complete loads and saves rather than individual objects
- **Transactions should not cross aggregate boundaries**—this establishes clear transactional limits

#### Common Misconception

Aggregates differ fundamentally from collection classes like lists or maps:
- Collections are generic structures
- Aggregates embody specific domain concepts (orders, clinic visits, playlists)
- An aggregate typically encompasses multiple collections alongside simple fields

#### Terminology Note

The term "aggregate" appears across various contexts including UML, but those applications don't represent the same concept as DDD aggregates.

---

### 6. Value Objects

**Source:** https://martinfowler.com/bliki/ValueObject.html

#### Core Definition

Value objects are compound objects whose equality is determined by their property values rather than object identity. "Objects that are equal due to the value of their properties...are called value objects."

#### Value vs. Reference Objects

| Type | Equality Based On | Example |
|------|------------------|---------|
| **Value Objects** | Content/properties | Two `Point(2,3)` instances are equivalent |
| **Reference Objects** | Identity/memory reference | Sales orders identified by unique order numbers |

#### Practical Examples

- Coordinates
- Monetary amounts
- Date ranges

#### Critical Principle: Immutability

The most important characteristic is **immutability**. Fowler illustrates an "aliasing bug" where modifying one reference unexpectedly affects another.

> "Value objects should be immutable."

#### Implementation Guidance

1. Override equality methods appropriately for each language
2. **Never provide setter methods**
3. Use getters for read-only access
4. Consider using language-specific immutability features

#### Benefits

- Enables richer domain modeling
- Prevents subtle aliasing bugs
- Can simplify complex codebases through thoughtful refactoring

---

## Patterns and Anti-Patterns

### 7. Anemic Domain Model (Anti-Pattern)

**Source:** https://martinfowler.com/bliki/AnemicDomainModel.html

#### Definition and Characteristics

An anemic domain model appears legitimate at first glance—it contains domain-related objects with proper relationships and structure. However, the critical flaw emerges when examining behavior: these objects lack substantive functionality, functioning primarily as **"bags of getters and setters."**

Domain logic is deliberately excluded from objects and instead concentrated in separate service classes.

#### Core Problem

This pattern is fundamentally contrary to object-oriented principles, which emphasize combining data with behavior. Fowler notes that "this is really just a procedural style design" disguised as object-oriented architecture.

#### Economic Argument Against Anemia

The pattern creates a paradoxical situation:
- Organizations incur the substantial costs of domain modeling (particularly complex database mapping layers)
- They don't receive corresponding benefits
- By extracting behavior into services, developers essentially create transaction scripts

> "All of the costs of a domain model, without yielding any of the benefits."

#### Proper Service Layer Usage

A critical distinction exists:

| Service Layer Type | Characteristics |
|-------------------|-----------------|
| **Legitimate** | Sits atop behaviorally rich domain models, coordinating tasks while keeping itself thin |
| **Anemic** | Contains all domain logic while domain objects are data-only |

**Domain logic—validations, calculations, business rules—should reside within domain objects, not service classes.**

#### Root Causes

1. Practitioners lacking experience with genuine domain models
2. Data-centric backgrounds
3. Technologies like J2EE Entity Beans that encourage this antipattern

---

### 8. CQRS (Command Query Responsibility Segregation)

**Source:** https://martinfowler.com/bliki/CQRS.html

#### Core Concept

CQRS is a pattern introduced by Greg Young that separates the model used for updating data from the model used for reading it.

> "You can use a different model to update information than the model you use to read information."

#### Traditional Approach vs. CQRS

| Aspect | Traditional | CQRS |
|--------|-------------|------|
| Model | Single conceptual model for CRUD | Separate models for commands and queries |
| Optimization | General-purpose | Each model optimized for its purpose |
| Architecture | Single database | Potentially separate databases, processes, hardware |

#### Key Architectural Benefits

CQRS pairs well with complementary patterns:
- Task-based user interfaces
- Event-based programming and Event Sourcing
- Eventual consistency approaches
- Separate optimization strategies for reads and writes

#### When to Use It

**Caution:** CQRS is "a significant mental leap" unsuitable for most systems.

Legitimate use cases:
1. **Complex domains** where command and query logic diverges substantially
2. **High-performance scenarios** where read/write loads differ significantly, enabling independent scaling

**CQRS should apply only to specific bounded contexts, not entire systems.**

#### Critical Warning

Improper implementation adds dangerous complexity without justification in many cases. This pattern is not a default choice—it's a specialized solution for specific problems.

---

### 9. Eager Read Derivation

**Source:** https://martinfowler.com/bliki/EagerReadDerivation.html

#### Core Concept

An architectural pattern introduced by Greg Young that separates read and write operations in high-transaction systems by pre-computing derived data.

#### Domain Logic Categories

Domain logic divides into three categories:
1. Validations
2. Consequences
3. Derivations

Eager Read Derivation applies differently to writes versus reads.

#### How It Works

1. An update enters the system (e.g., a birth record)
2. Domain model validates and processes consequences
3. Core data updates the primary database
4. Domain model runs derivation logic and sends messages to populate reporting databases
5. Read requests directly query reporting databases—no additional logic needed

> "Reads don't touch the main database at all. Instead we have one or more ReportingDatabases that are structured in the same way as our read requests."

#### Key Trade-offs

| Benefit | Cost |
|---------|------|
| Improved performance | Eventual consistency (not strong consistency) |
| Better scalability | Users may experience brief delays seeing updates |
| Simpler read path | More complex write path |

#### Applicability

This technique "mostly becomes attractive as you deal with high demand distributed cases," particularly for systems requiring high transaction throughput serving numerous concurrent users.

---

### 10. Contextual Validation

**Source:** https://martinfowler.com/bliki/ContextualValidation.html

#### Core Thesis

Validation should be tied to specific contexts rather than treated as a universal property of objects.

**Instead of:** `isValid()`
**Use:** `isValidForCheckIn()`

#### Key Problems with Context-Free Validation

- Creates confusion about object validity
- Over-restricts saving functionality
- Assumes "context-free validity" should prevent database persistence

#### Main Principles

1. **Context Binding:** "Validation as something that's bound to a context—typically an action that you want to do"

2. **Interrogate Constraints:** Each validation rule should be questioned: should it genuinely block an operation, particularly database saves?

3. **User-Centered Design:** Alan Cooper's philosophy—incomplete information shouldn't prevent users from saving work

#### Practical Implications

Recognizing that database persistence is itself an action helps developers distinguish between:
- **Truly blocking constraints** (must be satisfied)
- **Merely informative validations** (nice to have)

This contextual approach enables more flexible systems that accommodate incomplete states when appropriate.

---

## Domain Modeling Challenges

### 11. Type Instance Homonym

**Source:** https://martinfowler.com/bliki/TypeInstanceHomonym.html

#### Core Concept

A single word can simultaneously reference two distinct but related concepts—the "type-instance homonym."

#### Classic Example: "Book"

| Usage | Refers To | Class |
|-------|-----------|-------|
| "'War and Peace' is a wonderful book" | Literary work | `LiteraryWork` |
| "This book has a tattered cover" | Physical object | `PhysicalCopy` |

Unlike true homonyms (like "right"), these concepts maintain a close relationship—the physical copy carries a rendering of the literary work.

#### Key Principles

1. **Awareness is Essential:** Programmers and domain modelers must recognize these homonyms and represent them as distinct entities in code

2. **Natural Language Tolerance:** "Humans seem to have no trouble at all switching between the multiple concepts." Attempting to enforce precision in everyday conversation sounds unnatural

3. **Selective Application:** Use disambiguating terms consistently in software while acknowledging the homonym remains a natural synonym

#### Practical Guidance

- Scan for type-instance homonyms early when working in new domains, particularly in central terminology
- Create domain-expert-comprehensible names for each concept
- Use informal class diagrams to identify these ambiguities
- Apply precision "with a light touch" rather than completely eliminating the original term

---

### 12. Contradictory Observations

**Source:** https://martinfowler.com/bliki/ContradictoryObservations.html

#### Core Concept

Challenges the conventional assumption that computer systems must maintain perfect data consistency. Sometimes systems should record contradictory data and help humans deal with that, particularly in healthcare contexts where conflicting information reflects real-world uncertainty.

#### The Observation Pattern

Rather than storing data as simple attributes, treat information as first-class entities called **observations**. Each observation includes metadata:
- When it was recorded
- Who recorded it
- How it was obtained

This enables systems to retain multiple contradictory claims while maintaining an audit trail.

#### Key Features

| Feature | Description |
|---------|-------------|
| **Capturing Absence** | Observations can document what is *not* true (valuable in diagnostics) |
| **Rejection Mechanism** | When evidence proves an observation wrong, mark it as rejected rather than deleting it |
| **Evidence Chains** | Observations form interconnected webs; rejecting one necessitates reviewing dependent ones |

#### Practical Implications

- Essential in healthcare but applies broadly
- Works wherever multiple conflicting data sources exist
- Domain experts (like clinicians) often recognize complexities that developers initially overlook

---

### 13. Currency As Value

**Source:** https://martinfowler.com/bliki/CurrencyAsValue.html

#### Core Topic

Designing currency as a software abstraction—examining when simple value objects suffice and when more complex patterns become necessary.

#### Standard Approach: Value Objects for Currency

Currency is an exemplary value object. The internationally recognized currency code (such as USD) typically provides adequate representation. Value objects are immutable and excellent for straightforward financial systems.

#### Exception Case: Mutable Currency Attributes

Fowler describes a system requiring a "pip value" feature that could change over time and powered UI nudge buttons for currency adjustments. This mutability requirement violated standard value object principles.

#### The Dual-Pattern Solution

| Class | Type | Purpose |
|-------|------|---------|
| `CurrencyValue` | Value Object | Holds immutable data like currency codes |
| `CurrencyReference` | Reference Object | Maintains mutable, relatively static data (like pip value) |

The value object delegated certain operations (like pip value retrieval) to the reference object, which was managed in a lookup table indexed by currency code.

#### Key Insight

This "bi-semantic behavior" is exceptionally rare. Most systems successfully employ simple value object patterns for currency management, making this dual-pattern approach an edge case rather than standard practice.

---

## Applied DDD Examples

### 14. Customer Loyalty Software

**Source:** https://martinfowler.com/bliki/CustomerLoyaltySoftware.html

#### Core Framework: Accounting Perspective

Loyalty systems should be understood through an accounting lens. "The core of a loyalty system is a system to keep track of points (or miles)," fundamentally operating as accounting infrastructure.

This perspective simplifies many seemingly complex problems, particularly when handling manual interventions or ad-hoc adjustments.

#### Key Architectural Insights

| Concept | Implementation |
|---------|---------------|
| Multiple points types | Managed as different currencies within accounting model |
| Potential points (future miles) | Treated using accounts payable concepts |
| Complex scenarios | Clarified through Test-Driven Development |

#### Two Distinct System Concerns

1. **Accounting Side:**
   - Records transactions
   - Manages liabilities
   - Handles posting rules triggered by domain events

2. **Customer Experience Side:**
   - Analyzes behavior patterns
   - Measures promotional effectiveness
   - Data mining operations

#### Operational Approaches

- **Agreement Dispatchers:** Patterns mapping events to posting rules provide flexible frameworks for handling constantly changing program requirements while maintaining historical accuracy

#### Future Architecture Vision

Decouple accounting and analytics into separate systems fed by a shared event stream, with independent "miners" transforming event logs into specialized analytical structures.

---

## Strategic Design at Scale

### 15. Data Mesh Architecture

**Source:** https://martinfowler.com/articles/data-monolith-to-mesh.html

#### Overview

Zhamak Dehghani proposes a fundamental paradigm shift: rather than maintaining centralized data lakes, organizations should adopt a **data mesh**—a distributed ecosystem where business domains own and serve their data as products.

#### Current Architecture Failures

| Problem | Description |
|---------|-------------|
| **Centralization & Monolithic Design** | Attempting to ingest all enterprise data into one platform creates bottlenecks |
| **Coupled Pipeline Decomposition** | Architectures decompose around technical functions (ingestion, cleansing, aggregation) rather than business domains |
| **Siloed Hyper-Specialized Teams** | Centralized data teams become isolated from both sources and consumers, lacking domain expertise |

#### Data Mesh: The Solution

Converges three disciplines:

**1. Distributed Domain-Driven Architecture**
- Domains become the primary architectural quantum
- **Source-oriented data:** Operational domains host and serve datasets as immutable event streams
- **Consumer-oriented data:** Consuming domains create specialized datasets suited to their use cases
- Pipelines become internal domain implementation details

**2. Product Thinking Applied to Data**

Domain datasets must function as products:
- **Discoverable:** Centralized catalog enables users to find datasets
- **Addressable:** Global naming conventions allow programmatic access
- **Trustworthy:** Owners define SLOs for data quality
- **Self-describing:** Schemas and sample data enable independent consumption
- **Interoperable:** Global standardization enables cross-domain correlation
- **Secure:** Centrally-defined access control policies

**3. Self-Serve Data Infrastructure Platform**

Domain-agnostic platform providing:
- Polyglot storage options
- Data encryption and access control
- Schema management and versioning
- Pipeline orchestration
- Automated catalog registration
- Data quality monitoring

#### Key Paradigm Shifts

| Old Language | New Language |
|--------------|--------------|
| "Ingesting" | "Serving" |
| "Extracting and loading" | "Discovering and using" |
| Centralized data flow | "Publishing events as streams" |
| Monolithic platforms | "Ecosystem of data products" |

#### Implementation Reality

Technology readiness isn't the limiting factor. The challenge lies in organizational willingness to abandon the legacy "big data platform" paradigm and adopt distributed responsibility for data quality and governance at the domain level.

---

### 16. Decentralizing Architecture at Xapo Bank

**Source:** https://martinfowler.com/articles/xapo-architecture-experience.html

#### Overview

How Xapo Bank transformed from a centralized architecture function to a decentralized model using:
- Architecture Advice Forum (AAF)
- Architecture Decision Records (ADRs)
- Architecture Advice Process

#### The Problem with Centralized Architecture

- Knowledge remains siloed within architects
- Long feedback loops delay delivery
- Architecture function becomes a bottleneck
- Remote/distributed teams struggle with async knowledge sharing

Xapo experienced these challenges as a globally distributed company across 40+ countries and 25+ time zones.

#### Foundational Changes

Before implementing the AAF:
1. **Domain-Driven Design adoption:** Reorganized around business domains
2. **Team Topologies:** Created Stream-Aligned Teams (SATs) aligned with business subdomains
3. **Developer experience focus:** Refactored infrastructure and tooling for team autonomy
4. **Core principles:** Emphasized "team autonomy and reduction in hand-offs"

#### How the Architecture Advice Forum Works

**Key Principle:** "Anyone can then take an architectural decision" without requiring consensus or approval from higher authority.

**Core Components:**
- Weekly synchronous meetings (30 minutes)
- Asynchronous pre-work and post-work through documentation
- ADRs tracked in Jira: `draft → proposed → accepted → adopted`
- Curated attendee list including product, delivery, regulatory, infra, and executive voices

**Standing Agenda:**
- Spikes and in-play ADRs
- Team-coupling issues
- DORA metrics tracking
- Cloud spend
- ADR progress discussions

#### Key Innovations

**Jira-based ADR System Benefits:**
- Automatic timestamps at status transitions
- Data on decision velocity
- Integration with Slack for transparency
- Connection between ADRs and implementation work

**Metrics:** "Lead time for decisions" (draft to adopted) proved "a reliable indicator of process and system health"

#### Benefits Realized

| Benefit | Description |
|---------|-------------|
| **Faster decisions** | Weeks/months reduced to days |
| **Distributed ownership** | Collective responsibility |
| **Improved alignment** | Explicit capture of cross-functional requirements |
| **Knowledge acceleration** | Domain language increased across teams |
| **Business alignment** | Product representation in decisions |

#### Critical Lessons

**Mindset Shift Challenges:**
- Teams unconsciously still sought consensus
- Initial pursuit of "perfect" solutions needed redirecting toward pragmatic trade-offs
- Explicit documentation of compromises in ADRs helped teams understand decisions

**Ongoing Maintenance Required:**
- Continuous curation
- Active internal marketing
- Dedicated personnel to sustain momentum

> "Creating a forum or structure alone is not enough to ensure its continued success."

**Supporting Ecosystem Matters:** AAF's success depended on Team Topologies restructuring, continuous delivery infrastructure, DORA metrics, and strong organizational culture.

---

## Implementation Guide (Microsoft)

**Source:** https://learn.microsoft.com/en-us/archive/msdn-magazine/2009/february/best-practice-an-introduction-to-domain-driven-design

This section provides practical implementation guidance from David Laribee's comprehensive MSDN Magazine article on DDD.

### 17. The Platonic Model Philosophy

#### Core Concept

Software is a "shadow" or manifestation of an ideal Form, not the Form itself. This philosophical foundation drives the entire DDD approach.

#### Model-Driven Design

- Understanding of the model evolves **in code** rather than through heavy documentation
- The goal is to bring code closer to the true Form over time through "crunching knowledge into models"
- Models are living artifacts that grow with understanding

#### Knowledge Crunching

The process of iteratively refining the domain model through:
- Conversations with domain experts
- Experimentation in code
- Continuous validation and refinement

---

### 18. Entities in Practice

#### Definition

Entities are "things" in the system with both **identity** and **lifecycle**. They are the nouns of your domain: people, places, things.

#### Characteristics

- Can be accessed by identifier (e.g., customer number)
- Have internal state changes and behavior
- Should be **behavioral units**, not data units
- Persist through time regardless of attribute changes

#### Method Injection Pattern

A technique for providing dependencies to entities without requiring an IoC container:

```csharp
public class Policy {
    public void Renew(IAuditNotifier notifier) {
        // do internal state-related things, validation, etc.
        notifier.ScheduleAuditFor(this);
    }
}
```

**Benefits:**
- No IoC container required to create entities
- Dependencies visible at method level
- Encapsulation maintained
- Clear collaboration points

---

### 19. Value Objects in Practice

#### Definition

Value objects are descriptors or properties of entities that have **no identity**. They are defined entirely by their attributes.

#### Key Principles

1. **Immutability is mandatory** - Value objects must never change after creation
2. **Side-effect free** - Operations return new instances rather than modifying existing ones
3. **More intention-revealing than primitives** - `Money` is clearer than `decimal`

#### Implementation Example

```csharp
public class Money {
    public readonly Currency Currency;
    public readonly decimal Amount;

    public Money(decimal amount, Currency currency) {
        Amount = amount;
        Currency = currency;
    }

    public Money AddFunds(Money fundsToAdd) {
        // Exchange to normalize currency if needed
        var exchange = ServiceLocator.Find<IMoneyExchange>();
        var normalizedMoney = exchange.CurrentValueFor(fundsToAdd, this.Currency);
        var newAmount = this.Amount + normalizedMoney.Amount;
        // Return NEW instance - never modify this
        return new Money(newAmount, this.Currency);
    }
}

public enum Currency { USD, GBP, EUR, JPY }
```

#### Design Guidelines

- Use `readonly` fields or private setters
- Return new instances from operations
- Implement value equality (override `Equals` and `GetHashCode`)
- Consider implementing `IEquatable<T>`

---

### 20. Aggregate Roots in Practice

#### Definition

Aggregate roots are special entities that serve as the **only entry point** to a cluster of related objects. Consumers should only reference aggregate roots directly.

#### Core Principle

> "Guard subordinate entities zealously"

Child entities cannot exist without the parent aggregate root. External code should never hold references to child entities.

#### Correct vs. Incorrect Usage

```csharp
// CORRECT: Ask the aggregate to do the work
Policy.Renew();  // Aggregate figures out internal details

// INCORRECT: Violates Law of Demeter, couples to internal structure
Policy.CurrentPeriod().Renew();
```

#### Benefits

- Avoids over-coupling between components
- Reduces rigid models and cascade changes
- Better for unit testing (black-box, state-testing paradigm)
- Enforces transactional boundaries

#### Testing Aggregate Roots

Test aggregates as black boxes, verifying state changes rather than interactions:

```csharp
public class When_renewing_an_active_policy_that_needs_renewal {
    Policy ThePolicy;
    DateTime OriginalEndingOn;

    [SetUp]
    public void Context() {
        ThePolicy = new Policy(new DateTime(1/1/2009));
        var somePayroll = new CompanyPayroll();
        ThePolicy.Covers(somePayroll);
        ThePolicy.Write();
        OriginalEndingOn = ThePolicy.EndingOn;
    }

    [Test]
    public void Should_create_a_new_period() {
        ThePolicy.EndingOn.ShouldEqual(OriginalEndingOn.AddYears(1));
    }
}
```

---

### 21. Domain Services vs Application Services

#### Domain Services

Operations or processes that don't naturally belong to any entity. They capture domain concepts (verbs from the Ubiquitous Language).

**Characteristics:**
- Stateless
- Highly cohesive (typically single public method)
- Named after verbs or business activities
- Part of the domain model

**Example:**

```csharp
public class PolicyRenewalProcessor {
    private readonly IAuditNotifier _notifier;

    public PolicyRenewalProcessor(IAuditNotifier notifier) {
        _notifier = notifier;
    }

    public void Renew(Policy policy) {
        policy.Renew();
        _notifier.ScheduleAuditFor(policy);
    }
}
```

#### Application Services

Broader, shallower services that provide layered architecture. They map between the domain model and client requirements (DTOs, external systems).

**Characteristics:**
- Coordinate domain operations
- Handle cross-cutting concerns (logging, transactions)
- Translate between domain and external representations
- Should remain thin

**Example:**

```csharp
public interface IPolicyService {
    void Renew(PolicyRenewalDTO renewal);
    void Terminate(PolicyTerminationDTO termination);
    void Write(QuoteDTO quote);
}

public class PolicyService : Service {
    private readonly ILogger _logger;
    private readonly IPolicyRepository _policies;

    public PolicyService(ILogger logger, IPolicyRepository policies) {
        _logger = logger;
        _policies = policies;
    }

    public void Renew(PolicyRenewalDTO renewal) {
        var policy = _policies.Find(renewal.PolicyID);
        policy.Renew();
        var logMessage = string.Format(
            "Policy {0} was successfully renewed by {1}.",
            policy.Number, renewal.RequestedBy);
        _logger.Log(logMessage);
    }
}
```

#### Key Distinction

| Aspect | Domain Service | Application Service |
|--------|---------------|-------------------|
| **Focus** | Domain logic | Coordination |
| **Language** | Ubiquitous Language verbs | Technical concerns |
| **Dependencies** | Domain objects | Infrastructure + Domain |
| **Thickness** | Thin, focused | Thin, broad |

---

### 22. Repositories

#### Definition

Repositories represent an **in-memory collection** of aggregate roots. They abstract persistence concerns from the domain model.

#### Core Principles

- One repository per aggregate root
- Never for child entities (access through aggregate root)
- Separate persistence concerns from domain logic

#### Generic Repository Pattern

```csharp
public interface IRepository<T> where T : IEntity {
    T Find(int id);
    IEnumerable<T> Find(Query<T> query);
    void Save(T entity);
    void Delete(T entity);
}
```

#### Implementation Guidelines

- Use Layer Supertype pattern for code reuse
- Prevent SQL, ORM constructs, and stored procedures from contaminating the model
- Keep the interface domain-focused, not technology-focused

---

### 23. Anti-Corruption Layers

#### Definition

Anti-Corruption Layers (ACLs) are gatekeepers that prevent non-domain concepts from leaking into domain models. They create "seams" for introducing changes to legacy code.

#### Purpose

- Translate between different bounded contexts
- Protect domain model purity
- Isolate legacy system integration
- Enable gradual modernization

#### Repositories as ACLs

Repositories are a type of anti-corruption layer—they prevent database concepts (SQL, ORMs, stored procedures) from polluting the domain model.

#### Integration Scenarios

Particularly useful when integrating with:
- Legacy systems with different models
- External third-party services
- Systems in other bounded contexts

---

### 24. Modules and Organization

#### Definition

Modules are mini-boundaries within a single bounded context that group cohesive concepts. In .NET, they are implemented as **namespaces**.

#### Purpose

- Prevent "Small Balls of Mud" at the model level
- Group highly cohesive domain concepts
- Improve discoverability

#### Benefits

- Cleaner IntelliSense/autocomplete experience
- Better static analysis capabilities
- Logical organization matching domain structure
- Easier navigation for developers

#### Bounded Context Example: Insurance System

An insurance policy management system might have these bounded contexts:
- Quoting and sales
- General policy workflow (renewals, terminations)
- Auditing payroll estimation
- Quarterly self-estimates
- Setting and managing rates
- Issuing commissions
- Billing customers
- Accounting
- Underwriting (determining acceptable exposures)

Each context owns its own Ubiquitous Language and model.

---

## Design Principles

### 25. Single Responsibility Principle in DDD

#### Natural SRP Through Patterns

DDD patterns provide Single Responsibility Principle adherence "for free":

| Pattern | Single Responsibility |
|---------|----------------------|
| Entity | Business identity and behavior for one concept |
| Value Object | Immutable representation of one attribute |
| Repository | Persistence for one aggregate type |
| Domain Service | One domain operation |
| Application Service | Coordination for one use case |

#### Benefits

- Encourages high cohesion
- Makes code easier to discover, reuse, and maintain
- Prevents "fattening" of class profiles

---

### 26. Law of Demeter

#### The Rule

Methods should only invoke methods on:
1. Itself
2. Its parameters
3. Objects it creates/instantiates
4. Direct component objects

#### The Problem: Deep Dotting

```csharp
// BAD: Couples to internal structure
var name = order.GetCustomer().GetAddress().GetCity().GetName();

// GOOD: Ask, don't reach
var name = order.GetShippingCityName();
```

#### Consequences of Violation

- Introduces unnecessary coupling
- Causes "death by a thousand cuts" maintenance nightmares
- Makes refactoring difficult
- Breaks encapsulation

#### DDD Application

This principle reinforces why external code should only access aggregate roots, not reach into child entities.

---

### 27. Layer Supertype Pattern

**Source:** https://martinfowler.com/eaaCatalog/layerSupertype.html

#### Definition

A type that acts as the supertype for all types in its layer, consolidating shared functionality.

#### Purpose

Addresses code duplication when multiple objects in the same layer require identical methods or functionality.

#### Implementation

```csharp
// Layer Supertype for Entities
public abstract class Entity {
    public int Id { get; protected set; }

    public override bool Equals(object obj) {
        if (obj is not Entity other) return false;
        return Id == other.Id;
    }

    public override int GetHashCode() => Id.GetHashCode();
}

// Layer Supertype for Value Objects
public abstract class ValueObject<T> where T : ValueObject<T> {
    public abstract override bool Equals(object obj);
    public abstract override int GetHashCode();

    public static bool operator ==(ValueObject<T> a, ValueObject<T> b) {
        if (a is null && b is null) return true;
        if (a is null || b is null) return false;
        return a.Equals(b);
    }

    public static bool operator !=(ValueObject<T> a, ValueObject<T> b) => !(a == b);
}
```

#### Benefits

- Reduces duplication
- Improves maintainability through single-point-of-change
- Enforces consistency across layer implementations
- Simplifies architecture with clear hierarchical relationships

---

## Summary: Key DDD Principles for Expert Agents

### Strategic Design Principles

1. **Divide and Conquer:** Use Bounded Contexts to manage complexity
2. **Language Matters:** Establish and maintain Ubiquitous Language
3. **Context Mapping:** Explicitly define relationships between bounded contexts
4. **Domain Ownership:** Teams own their domains and data (Data Mesh thinking)

### Tactical Design Principles

1. **Rich Domain Models:** Avoid anemic models—combine data with behavior
2. **Evans Classification:** Properly categorize Entities, Value Objects, and Services
3. **Aggregate Boundaries:** Respect transactional boundaries; access through aggregate roots
4. **Immutability:** Value Objects must be immutable to prevent aliasing bugs

### Validation and Modeling Principles

1. **Contextual Validation:** Validate for specific actions, not universal validity
2. **Handle Contradictions:** Sometimes record conflicting data rather than enforcing false consistency
3. **Watch for Homonyms:** Identify type-instance homonyms early in domain modeling

### Architectural Patterns

1. **CQRS:** Separate read and write models when justified—not by default
2. **Eager Read Derivation:** Pre-compute derived data for high-throughput systems
3. **Event Streams:** Use events for loose coupling between domains

### Organizational Principles

1. **Decentralize Decisions:** Enable teams to make architectural decisions
2. **Document Decisions:** Use ADRs to capture context and rationale
3. **Measure Decision Velocity:** Track how quickly decisions move from draft to adopted

---

## Practical Guides (ThoughtWorks)

### 28. Strategic vs Tactical Design

**Source:** https://www.thoughtworks.com/en-au/insights/blog/evolutionary-architecture/domain-driven-design-in-10-minutes-part-one

#### Definition

DDD is "an approach to designing and building software where the focus is on accurately expressing business rules in isolation from other technical concerns."

#### Two Interconnected Disciplines

| Discipline | Focus | Scope |
|------------|-------|-------|
| **Strategic Design** | Big picture understanding | Business problems, competitive advantages, team relationships, what to build |
| **Tactical Design** | Implementation patterns | Building complex domain models effectively |

#### The Climber Metaphor

> "Strategic Design is to how a climber plans their route up a cliff and Tactical Design is to the tools they use."

Strategic design determines where you're going; tactical design determines how you get there.

#### Foundational Ideas

1. **Model-Driven Design:** Business processes should be modeled in self-contained domain models, prioritizing these over database design and architecture

2. **Ubiquitous Language:** All stakeholders communicate using consistent, unambiguous business terminology to eliminate translation overhead

---

### 29. Problem and Solution Spaces

**Source:** https://www.thoughtworks.com/en-au/insights/blog/evolutionary-architecture/domain-driven-design-in-10-minutes-part-one

#### Problem Space

The problem space consists of **subdomains**—distinct problem areas within a business. Understanding these requires collaboration with domain experts.

**Example (Warehouse-as-a-Service Company):**
- Delivery/Collection
- Inventory
- Sales
- Finance

#### Solution Space

The solution space maps current enterprise architecture against identified problems. This reveals:
- Misalignments between systems and business needs
- Opportunities for improvement
- Areas requiring refactoring

#### Domain Classification

| Type | Definition | Investment Strategy |
|------|------------|-------------------|
| **Core Domain** | Provides competitive advantage | Maximum investment, full tactical design |
| **Supporting Subdomain** | Necessary but not differentiating | Moderate investment |
| **Generic Subdomain** | Commodity functions (e.g., Finance) | Minimal custom development, use off-the-shelf |

#### Strategic Prioritization

Focus resources on **differentiation** rather than commoditized functions. Core domains deserve the best developers and most rigorous DDD application.

---

### 30. Tactical Design Constructs

**Source:** https://www.thoughtworks.com/en-au/insights/blog/evolutionary-architecture/domain-driven-design-part-two

#### Core Principle

Encapsulate **all business logic within Domain Models** rather than scattering it across application layers.

#### Building Blocks Summary

| Construct | Purpose | Identity | Mutability |
|-----------|---------|----------|------------|
| **Entity** | Objects with persistent identity | Has identity independent of properties | Mutable |
| **Value Object** | Descriptors defined by values | No independent identity | Immutable |
| **Aggregate** | Cluster enforcing validation rules | Root has identity | Controlled mutation |
| **Domain Event** | Significant business occurrences | Event-specific | Immutable (record) |

#### Entity vs Value Object Metaphor

- **Entity:** A human remains the same person despite name changes (identity persists)
- **Value Object:** The color brown cannot change without becoming a different color (defined by value)

#### Aggregate Rules

1. Only the **Aggregate Root** can be mutated directly
2. Aggregates reference each other **only through Root identifiers**
3. Changes must go through the root to maintain invariants

#### Supporting Components

| Component | Role | Guideline |
|-----------|------|-----------|
| **Repository** | Persistence operations | Operate exclusively on Aggregate Roots |
| **Application Service** | External request routing | Keep "dumb"—thin orchestration only |
| **Domain Service** | Cross-aggregate orchestration | Use when Domain Events are insufficient |

#### Key Insight

> Keep Application Services "dumb" to maintain business logic concentration in the Domain Model.

---

### 31. Four-Stage Learning Path

**Source:** https://www.thoughtworks.com/en-au/insights/blog/domain-driven-design-neednt-be-hard-heres-how-start

A practical pathway to understanding and implementing DDD without being overwhelmed by theoretical frameworks.

#### Stage 1: Understand the Business Problem

- Focus on comprehending what problem your software solves
- Use simple diagrams and modeling
- Don't worry about bounded contexts or ubiquitous language yet
- **Goal:** Clear understanding of the business domain

#### Stage 2: Tighten Code-Model Loop

- Keep sketches and code synchronized
- Create a dedicated "model" package for domain logic
- Separate from framework infrastructure
- Accept that framework elements will initially contaminate this space
- **Goal:** Domain logic has a clear home in the codebase

#### Stage 3: Collaborate with Domain Experts

- Work closely with people who understand the real-world system
- Use their terminology for both **nouns** (entities) and **verbs** (operations)
- Let experts validate whether your model accurately reflects their understanding
- This shared vocabulary becomes essential
- **Goal:** Model validated by domain experts

#### Stage 4: Handle Model Fragmentation

- When a single model cannot solve multiple problems, split it
- Different models can address different aspects
- **Map analogy:** London Underground maps serve different purposes than street maps
- Duplication across models is acceptable
- **Goal:** Right-sized models for specific problems

#### Success Indicator

> "You're practicing DDD when domain experts understand your code (setting aside syntax) and your model aligns with their mental models of the business."

---

### 32. DDD for Services Architecture

**Source:** https://www.thoughtworks.com/en-au/insights/blog/domain-driven-design-services-architecture

#### Core Concept

DDD principles guide the decomposition of complex systems into manageable service applications. The emphasis is **not on service size, but on business capabilities**.

#### Key Insight

> "The emphasis is not on the size, but instead on the business capabilities."

This represents **"SOA done right"**—aligning technical architecture with business domain understanding.

#### Problem Decomposition Approach

Like planning a visa application, complex software challenges benefit from breaking into understandable pieces:
- Don't detail the whole process at once
- Try to understand the overall journey first
- Identify natural boundaries

#### Bounded Contexts as Services

DDD's bounded contexts become the foundation for service separation:
- Each bounded context represents an independent business area
- Each can become its own service application
- Boundaries are driven by business, not technology

#### Architectural Evolution Path

| Stage | Characteristics |
|-------|-----------------|
| 1. Monolithic | Single application with database coupling |
| 2. Service-Integrated | Applications with hidden database interactions |
| 3. Domain-Modeled | Identifying tangled responsibilities |
| 4. Bounded Context Services | Reflecting both domain and technical boundaries |

#### Practical Guidance

1. **Build domain models first** to guide separation rather than reverse-engineering from existing code

2. **Narrow the gap** between business and development teams through domain discussion

3. **Prioritize meaningful separation** over granularity—don't split just to have smaller services

4. **Let boundaries emerge** from understanding the business, not from technical preferences

---

## Getting Started with DDD

### Learning Path

1. Study **S.O.L.I.D. principles** as foundation
2. Read Eric Evans' *Domain-Driven Design: Tackling Complexity in the Heart of Software*
3. Alternative entry: InfoQ's smaller version of the DDD book
4. Vaughn Vernon's *Implementing Domain-Driven Design* for practical guidance
5. Join community discussions and practice

### Practical Approach

- Adapt philosophies and patterns that make sense for your situation
- Not all elements apply universally
- Ubiquitous Language and Bounded Contexts are more important than perfect implementation
- **Design for value**: Business value consciousness over dogma adherence

### Core Domain Identification

Focus DDD investment on areas bringing most business value:
- Usually aligns with client's core competencies
- Where custom software typically lives
- Senior developers should master fundamental principles relevant to core domain

---

## References

### Martin Fowler's Website
- https://martinfowler.com/tags/domain%20driven%20design.html

### Microsoft MSDN Magazine
- https://learn.microsoft.com/en-us/archive/msdn-magazine/2009/february/best-practice-an-introduction-to-domain-driven-design

### ThoughtWorks
- https://www.thoughtworks.com/en-au/insights/blog/evolutionary-architecture/domain-driven-design-in-10-minutes-part-one
- https://www.thoughtworks.com/en-au/insights/blog/evolutionary-architecture/domain-driven-design-part-two
- https://www.thoughtworks.com/en-au/insights/blog/domain-driven-design-neednt-be-hard-heres-how-start
- https://www.thoughtworks.com/en-au/insights/blog/domain-driven-design-services-architecture

### Primary Books
- Eric Evans, *Domain-Driven Design: Tackling Complexity in the Heart of Software* (2003)
- Vaughn Vernon, *Implementing Domain-Driven Design* (2013)

### Related Resources
- Dan North on User Stories: https://dannorth.net/whats-in-a-story
- Big Ball of Mud: https://laputan.org/mud
- Robert C. Martin on SRP: https://objectmentor.com/resources/articles/srp.pdf
- Law of Demeter: https://cmcrossroads.com/bradapp/docs/demeter-intro.html
- Layer Supertype Pattern: https://martinfowler.com/eaaCatalog/layersupertype.html

---
name: design-principles
description: Canonical reference for software design principles (SOLID, GRASP, DRY, KISS, YAGNI, Law of Demeter, Tell-Don't-Ask, Command-Query Separation, composition over inheritance, encapsulation, immutability, pure functions). Referenced by clean-coder, code-smell-detector, refactoring-advisor, and ddd-assessor to avoid divergent definitions.
---

# Design Principles — Canonical Reference

This skill is the single source of truth for design principles across ALF agents. When a finding or recommendation cites a principle, cite it from here so that clean-coder, code-smell-detector, refactoring-advisor, and ddd-assessor stay consistent.

Each principle has: **Name** → **Core rule** → **Common violations** → **Typical refactoring that resolves the violation**.

## SOLID

### S — Single Responsibility Principle (SRP)

**Core rule**: A module/class/function should have one reason to change. One cohesive responsibility.

**Violations**:
- Classes mixing persistence + business rules + presentation
- Functions doing parsing + calculation + I/O
- Modules exporting unrelated utilities
- "Manager" / "Helper" / "Util" classes holding disparate logic

**Refactorings**: Extract Class, Extract Method, Move Method, Split Module.

### O — Open/Closed Principle (OCP)

**Core rule**: Open for extension, closed for modification. Add new behavior by adding new code, not by editing existing code paths.

**Violations**:
- Long if/else or switch on a type discriminator that grows every time a new type is added
- Hardcoded branch list in an algorithm
- Framework extension requiring core changes

**Refactorings**: Replace Conditional with Polymorphism, Strategy pattern, Template Method, Chain of Responsibility, plugin architecture.

### L — Liskov Substitution Principle (LSP)

**Core rule**: Subtypes must be substitutable for their base types without altering correctness. Preconditions cannot be strengthened; postconditions cannot be weakened; invariants must hold.

**Violations**:
- Subclass throws on methods the parent supports (`NotImplementedException`)
- Subclass returns a narrower set of values than documented in the parent
- "Rectangle/Square" or "Bird that can't fly" inheritance

**Refactorings**: Replace Inheritance with Delegation, Extract Interface, Pull Up/Push Down Method.

### I — Interface Segregation Principle (ISP)

**Core rule**: Clients should not depend on methods they do not use. Small, role-specific interfaces over broad ones.

**Violations**:
- "Fat" interfaces that clients implement partially, dropping unused methods
- Client coupling to methods they never call
- Cross-cutting interfaces forcing unused implementations

**Refactorings**: Split Interface, Extract Role Interface.

### D — Dependency Inversion Principle (DIP)

**Core rule**: Depend on abstractions, not concretions. High-level modules should not depend on low-level modules; both should depend on abstractions.

**Violations**:
- Business code directly instantiating a database driver, HTTP client, or file system
- Tests impossible to write without touching real infrastructure
- Module imports reaching "downward" into implementation details

**Refactorings**: Introduce Interface, Dependency Injection, Ports and Adapters (Hexagonal).

## GRASP

GRASP (General Responsibility Assignment Software Patterns) — Craig Larman. Guide responsibility assignment.

| Principle | Rule | Violation signal |
|---|---|---|
| Information Expert | Assign a responsibility to the class that has the information needed | Methods that fetch data from another object and do work on it (Feature Envy) |
| Creator | Assign creation responsibility to the class that aggregates, contains, records, closely uses, or has initializing data for the created object | `new X()` scattered across codebase without locality |
| Controller | A non-UI class handles system events (use-case controller) | UI classes (views/components) contain business logic |
| Low Coupling | Minimize dependencies between classes | Wide-reaching imports; changes in one class ripple into many |
| High Cohesion | Responsibilities should be strongly related within a class | Class methods operating on disjoint subsets of fields |
| Polymorphism | Use polymorphism to handle type variation | Type-check-then-branch (`if isinstance(...)`) |
| Pure Fabrication | Invent a class with no direct domain counterpart when it gives high cohesion + low coupling (e.g. repositories, factories) | Domain classes bloated with persistence/formatting logic |
| Indirection | Introduce a mediator to decouple two classes | Direct coupling between volatile components |
| Protected Variations | Wrap points of predicted change in stable interfaces | Every new requirement requires broad edits across call sites |

## Simplicity principles

### DRY — Don't Repeat Yourself

**Core rule**: Each piece of knowledge should have a single, unambiguous, authoritative representation in a system.

**Nuance**: DRY applies to *knowledge*, not to *code that looks similar*. Two pieces of code that happen to have the same shape for different reasons should **not** be deduplicated — they will diverge and you'll end up with an awkward abstraction.

**Violations**: same constant repeated, same validation rule in UI + backend + DB, same business rule duplicated across services.

**Refactorings**: Extract Method, Extract Constant, Extract Shared Library, Shared Event Schema.

### KISS — Keep It Simple

**Core rule**: Solutions should be as simple as possible, no simpler. Complexity is a cost paid forever.

**Violations**: abstraction layers with one implementation; clever one-liners; custom DSLs for small domains; design patterns imposed where a function would suffice.

**Refactorings**: Inline (Method/Class/Variable), Replace Pattern with Direct Call, Collapse Hierarchy.

### YAGNI — You Aren't Gonna Need It

**Core rule**: Don't build for speculative future requirements. Build what is needed now; re-design when the future arrives.

**Violations**: interfaces with one implementation "in case we need to swap"; configurable parameters no one configures; unused extension points; premature plugin architectures.

**Refactorings**: Inline Interface, Remove Parameter, Remove Dead Code.

## Coupling and cohesion

### Law of Demeter (LoD) — "don't talk to strangers"

**Core rule**: A method should only call methods of: itself; its parameters; objects it creates; its direct fields. Not `a.b.c.d.e()` chains.

**Violations**: "train wreck" chains; knowledge of multiple levels of object structure; fragile to reshaping of internals.

**Refactorings**: Hide Delegate, Add Parameter, Tell-Don't-Ask.

### Tell, Don't Ask

**Core rule**: Don't ask an object for data to then act on it — tell it what to do.

**Violations**: getters followed by external decision-making; anemic domain models where services orchestrate state changes.

**Refactorings**: Move Method, Introduce Behavior on Entity, Replace Getter with Command.

### Command-Query Separation (CQS)

**Core rule**: Methods are either commands (mutate state, return nothing) or queries (return value, no side effects). Not both.

**Violations**: "getter" methods that mutate state; "save()" returning a richly decorated object; boolean-returning functions that also send emails.

**Refactorings**: Split into Query + Command; Return Immutable Result; Separate Concern.

### Composition over Inheritance

**Core rule**: Prefer `has-a` (composition) over `is-a` (inheritance) for behavior reuse.

**Violations**: deep inheritance hierarchies; "template method" abuse; inheritance for code reuse where no IS-A relationship exists.

**Refactorings**: Replace Inheritance with Delegation, Extract Class, Strategy.

## Data and state

### Encapsulation

**Core rule**: Hide internal state; expose behavior.

**Violations**: public fields; "anemic" classes with getters/setters and no behavior; clients mutating internal collections returned from getters.

**Refactorings**: Replace Data Value with Object, Replace Field with Query, Return Defensive Copy.

### Immutability (favor)

**Core rule**: Prefer unchangeable values. Makes reasoning easier, enables safe sharing, prevents entire classes of concurrency bugs.

**Violations**: mutable shared state; setters on domain entities; collections passed by reference without defensive copy.

**Refactorings**: Change Reference to Value, Freeze Object, Use Persistent Data Structures.

### Pure functions (favor)

**Core rule**: Deterministic output; no side effects; no hidden state.

**Violations**: functions reading env vars / global state; functions with hidden I/O; functions whose result depends on wall-clock time or randomness.

**Refactorings**: Parameterize Method (inject time, random); Separate Pure from Impure.

## Meta-principles

### Separation of Concerns

**Core rule**: Different concerns (persistence, domain logic, presentation, I/O, cross-cutting) should live in different modules.

### Modularity

**Core rule**: Break systems into substitutable, testable, independently-deployable units with clear interfaces.

### Robustness (Postel's Law) — use with care

**Core rule**: "Be conservative in what you send, liberal in what you accept."

**Caveat**: overuse makes systems accept malformed input silently, causing downstream bugs. Prefer strict validation at boundaries with clear errors; only loosen when compatibility demands it.

## Priority and tension

Principles sometimes conflict. Order of precedence when they do:

1. **Correctness** over any principle
2. **Simplicity (KISS/YAGNI)** over DRY when "DRY" forces a premature abstraction
3. **Single Responsibility** over "fewer files" — more small units beats fewer god objects
4. **Explicit over clever** — a longer obvious form beats a shorter obscure one
5. **Test-driven clarity** — if a principle makes code harder to test in isolation, the principle is being misapplied

Principles guide judgement, they don't substitute for it.

## Principle → Refactoring quick map

| Violation | Principle | Canonical refactoring |
|---|---|---|
| God class | SRP, High Cohesion | Extract Class |
| Feature Envy | Information Expert | Move Method |
| Switch on type | OCP | Replace Conditional with Polymorphism |
| Fat interface | ISP | Split Interface |
| Hardcoded concrete dependency | DIP | Introduce Interface + DI |
| Train wreck (a.b.c.d) | LoD | Hide Delegate |
| Anemic entity | Tell-Don't-Ask | Move Method onto entity |
| Duplicate code | DRY (with care) | Extract Method / Class |
| Deep hierarchy | Composition over inheritance | Replace Inheritance with Delegation |
| Mutable shared state | Immutability | Change Reference to Value |
| Getter-then-mutate from outside | Encapsulation | Replace Getter with Command |

## How agents reference this skill

- `alf-clean-coder`: applies these principles during refactoring. Principle priority list (above) resolves conflicts.
- `alf-code-smell-detector`: maps detected smells to principle violations using the Principle → Refactoring quick map.
- `alf-refactoring-advisor`: recommends refactorings keyed to principle violations.
- `alf-ddd-assessor`: uses encapsulation, Tell-Don't-Ask, SRP, and Information Expert to evaluate aggregate and entity design.

When citing a principle, always use the canonical name above (e.g., "Law of Demeter", not "Principle of Least Knowledge" — same thing, one name).

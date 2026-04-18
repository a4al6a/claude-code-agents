---
name: smell-catalog-solid-grasp-detection
description: Detection signals and grep/tool commands for SOLID and GRASP violations, including per-principle patterns (SRP, OCP, LSP, ISP, DIP, Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations). Load when the analysis phase targets principle-level violations.
---

# SOLID / GRASP Violation Detection

Canonical principle *definitions* live in the shared `design-principles` skill — cite them from there and keep this skill focused on detection signals.

## Detection signal table

| Principle | Detection signal | Primary patterns |
|---|---|---|
| SRP | Large Class / Divergent Change / God Object / "and"-in-name | Mixed concerns (UI+logic+persistence); imports from many domains |
| OCP | Switch-on-type / instanceof-branching / hardcoded instantiation | `switch(type)`, `if isinstance(x, ...)`, `new ConcreteClass()` without factory/DI |
| LSP | Refused Bequest / Strengthened preconditions / Subclass exceptions | `@Override` throws `UnsupportedOperationException`; subclass returns null for documented-non-null |
| ISP | Fat Interface (>10 methods) / "And"-in-interface-name / empty implementations | Clients implementing partial interfaces |
| DIP | Direct instantiation in constructors / concrete imports / static utility calls | Business code `import X.Y.impl.*`; DB/FS calls in domain layer |
| Information Expert | Feature Envy / Data Class / Anemic Domain Model | Methods using more fields of other classes; getter chains |
| Creator | Creation in unrelated classes | Factory-like logic outside container/creator |
| Controller | Bloated controllers (>200 LOC, >15 methods) / UI in controllers | Business logic in `*Controller` classes |
| Low Coupling | Message chains / intimate classes / global singletons | `a.b.c.d.e` chains; static cross-module references |
| High Cohesion | Utility classes (all static) / mixed-abstraction classes | `Util` / `Helper` / `Manager` holding disjoint logic |
| Polymorphism | Type-switching / instance-of-then-cast | `if isinstance(x, Foo)` replacing dispatch |
| Pure Fabrication missing | Domain logic in DAO/repo/UI | Business rules in persistence or presentation |
| Indirection missing | Tight coupling between layers | No interface between volatile boundary |
| Protected Variations missing | Hardcoded protocols/formats | Business rules in `if` chains rather than strategies |

## Grep commands (fallback when AST tools unavailable)

### SOLID violations
```bash
# SRP — Large classes
find . -name "*.java" -exec wc -l {} + | awk '$1 > 500' | sort -nr
find . -name "*.py"   -exec wc -l {} + | awk '$1 > 300' | sort -nr

# OCP — Switch statements / type checks
grep -r "switch\s*("       --include="*.java" --include="*.js" --include="*.ts"
grep -r "instanceof\|typeof" --include="*.java" --include="*.js"

# LSP — Refused bequest
grep -r "UnsupportedOperationException\|NotImplementedException" .
grep -r "throw.*not.*implement\|throw.*support" -i .

# ISP — Fat interfaces
grep -rA 20 "interface.*{" --include="*.java" | grep -c "public.*("

# DIP — Direct instantiation / concrete imports
grep -r "new [A-Z].*("    --include="*.java" --include="*.js" --include="*.ts"
grep -r "import.*\.[A-Z]" --include="*.java"
```

### GRASP violations
```bash
# Information Expert — Feature Envy / getter chains
grep -r "\.get.*\.get.*\.get" .

# Low Coupling — Message chains
grep -r "\.[a-zA-Z]*\.[a-zA-Z]*\.[a-zA-Z]*" --include="*.java" --include="*.py"

# High Cohesion — Utility classes
grep -r "class.*Util\|class.*Helper" .
grep -r "public static.*public static.*public static" --include="*.java"

# Controller — Bloated controllers
find . -name "*Controller*" -exec wc -l {} + | awk '$1 > 200'
grep -r "@Controller\|@RestController" -A 50 --include="*.java" | grep -c "public.*("

# Polymorphism — Type checking
grep -r "if.*instanceof\|if.*typeof" .
grep -r "switch.*getClass\|switch.*type"  .
```

### Cross-language
```bash
# Global state
grep -r "static.*=\|global " .
grep -r "singleton\|Singleton" -i .

# Hardcoded dependencies
grep -r "new File\|new Date\|new URL" .

# Missing abstraction
grep -r "class.*extends.*Concrete\|class.*implements.*Impl" .
grep -r "import.*\.impl\.\|import.*\.concrete\." .
```

## Tool-first (prefer over grep)

| Tool | Language | Detects |
|---|---|---|
| `semgrep` with custom rule packs | polyglot | AST-level smells (feature envy, deeply nested, switch-on-type) |
| `radon cc` / `radon mi` | Python | cyclomatic complexity, maintainability index |
| `lizard` | polyglot | cyclomatic, CCN, LOC per function, NLOC |
| `gocyclo` | Go | cyclomatic |
| `eslint` + `sonarjs` rules | JS/TS | cognitive complexity, switch-on-type, feature envy |
| `pmd` + `spotbugs` | Java | full catalog of rule-based smells |
| `rubocop` metrics | Ruby | complexity, class length, method length |
| `clippy` | Rust | many smells built-in |
| `jscpd` / `simian` | polyglot | duplicate code (copy-paste detection) |

Prefer these first; record tool invocations in the methodology section. A tool-derived finding has **high confidence**; a grep-derived finding has **medium confidence**.

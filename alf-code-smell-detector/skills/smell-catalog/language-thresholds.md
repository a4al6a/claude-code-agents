---
name: smell-catalog-language-thresholds
description: Language-specific detection thresholds and idiomatic smell patterns for Python, JavaScript/TypeScript, Java, Go, and Rust. Load once the primary language is identified so thresholds like "Long Method > N lines" are calibrated to language idiom.
---

# Language-Specific Detection Thresholds

Load this skill after language detection. Apply the matching language's thresholds to avoid false positives (e.g., flagging a 40-line Go function as "too long").

## Python
- **Long Method**: >20 lines (Pythonic threshold)
- **Magic Number**: Hardcoded values not in CONSTANTS
- **Global Data**: module-level variables, excessive use of `global`
- **Primitive Obsession**: strings for dates instead of `datetime`; dicts for structured records instead of dataclasses/NamedTuples

## JavaScript / TypeScript
- **Callback Hell**: >3 levels of nested callbacks
- **Feature Envy**: excessive use of other object properties
- **Magic Number**: hardcoded numbers not in `const` declarations
- **Dead Code**: unreachable code after `return`; `if (false)` branches

## Java
- **Large Class**: >500 lines or >20 methods
- **Long Parameter List**: >3 parameters (considering overloading)
- **Refused Bequest**: `@Override` that throws `UnsupportedOperationException`
- **Primitive Obsession**: `String`/`int` where a custom type would be better (e.g., `String email`)

## Go
- **Long Method**: >50 lines (Go idiom accommodates longer functions)
- **Global Data**: package-level variables without clear necessity
- **Error handling**: ignored errors (`_ = f()`) or excessive defensive checks

## Rust
- **Primitive Obsession**: basic types where the newtype pattern would convey intent
- **Clone abuse**: unnecessary `.clone()` calls bypassing borrow discipline
- **Unwrap overuse**: `.unwrap()` in non-test paths instead of proper error propagation

## Polyglot notes
When a codebase mixes languages, apply thresholds per-file based on extension. Don't average thresholds across languages in a single finding — cite the language explicitly.

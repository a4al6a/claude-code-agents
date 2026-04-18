---
name: refactoring-risk-and-language-caveats
description: Risk assessment rubric, complexity factors, and per-language caveats (Java checked exceptions, JS/TS `this` binding, Python `@property`, C# async, Rust borrow checker, Go structural interfaces) for refactoring recommendations. Load when assessing risk or adapting mechanics to a specific language.
---

# Risk Assessment & Language Caveats

## Risk buckets

### Low Risk
- Rename Method, Extract Variable, Replace Magic Number with Symbolic Constant
- Mitigation: automated IDE support, compile-time verification

### Medium Risk
- Extract Method, Move Method, Extract Class
- Mitigation: comprehensive test coverage, incremental changes

### High Risk
- Replace Conditional with Polymorphism, Replace Inheritance with Delegation, Form Template Method, Duplicate Observed Data, Replace Type Code with Subclasses/State
- Mitigation: thorough planning, extensive testing, rollback strategy

## Complexity factors

| Factor | Impact |
|---|---|
| Legacy code without tests | Significantly increases complexity — add characterization tests first |
| High coupling | Moderate increase |
| Multiple inheritance | High increase |
| Complex business logic | Moderate increase |

## Language-specific caveats

| Language | Caveat |
|---|---|
| Java | Checked exceptions may cascade when extracting methods; plan exception specs. |
| JavaScript/TS | `this` context is re-bound when methods move; use arrow functions or `.bind()` deliberately. |
| Python | `@property` decorators can silently change behavior when fields are replaced with methods. |
| C# | Async method extraction needs to preserve `async Task` chain; avoid `async void`. |
| Rust | Borrow checker may fight extractions; prefer Extract Method with cloned/referenced parameters. |
| Go | Interface satisfaction is structural; extracting interfaces may need no changes to implementers. |

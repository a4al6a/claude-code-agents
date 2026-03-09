# alf-error-handling-reviewer

Evaluates exception handling consistency, resilience patterns, and failure mode coverage.

## What It Analyzes

- **Exception Patterns**: Catch-all, empty catch, swallowed exceptions, specific catches
- **Error Propagation**: Consistent error types across layers, information loss
- **Resilience Patterns**: Retry logic, circuit breakers, timeouts, fallbacks
- **Missing Error Handling**: Unprotected I/O, network calls, parsing operations
- **Logging on Failure**: Context sufficiency, sensitive data exposure
- **User-Facing Errors**: Generic vs helpful messages, error codes for API consumers

## Usage

```
Analyze the codebase at /path/to/project for error handling quality.
Write your JSON output to /path/to/results/alf-error-handling-reviewer-data.json
```

## Output

- Markdown report to stdout with findings grouped by category
- Structured JSON with scored findings, resilience pattern inventory, and recommendations

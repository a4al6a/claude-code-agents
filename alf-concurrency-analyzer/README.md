# alf-concurrency-analyzer

Detects concurrency issues, performance anti-patterns, and scalability risks.

## What It Analyzes

- **Thread Safety**: Shared mutable state, missing synchronization, lock ordering
- **Race Conditions**: TOCTOU, unprotected concurrent access, file system races
- **Async/Await Issues**: Blocking in async paths, missing awaits, fire-and-forget
- **N+1 Queries**: ORM lazy loading in loops, unbatched API calls
- **Memory Leaks**: Unbounded caches, listener accumulation, circular references
- **Resource Management**: Connection pool config, unclosed handles, leak patterns

## Usage

```
Analyze the codebase at /path/to/project for concurrency issues.
Write your JSON output to /path/to/results/alf-concurrency-analyzer-data.json
```

## Output

- Markdown report to stdout with findings by concurrency dimension
- Structured JSON with scored findings, pattern inventory, and recommendations

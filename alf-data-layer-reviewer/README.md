# alf-data-layer-reviewer

Evaluates database access patterns, schema hygiene, and data layer correctness.

## What It Analyzes

- **Migration Hygiene**: Sequential, reversible, no unsafe destructive operations
- **ORM Usage**: Lazy loading traps, over-fetching, missing eager loading
- **Raw SQL Safety**: Injection risk, parameterized query usage
- **Transaction Boundaries**: Missing transactions, scope issues, rollback paths
- **Index Coverage**: Missing indexes for query patterns, unused indexes
- **N+1 Detection**: ORM-specific loop query patterns
- **Connection Management**: Pool configuration, leak potential

## Usage

```
Analyze the codebase at /path/to/project for data layer quality.
Write your JSON output to /path/to/results/alf-data-layer-reviewer-data.json
```

## Output

- Markdown report to stdout with data layer findings by category
- Structured JSON with scored findings, ORM-specific issues, and recommendations

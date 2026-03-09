# alf-api-design-reviewer

Evaluates API contracts for consistency, usability, and evolution readiness.

## What It Analyzes

- **Endpoint Inventory**: Complete discovery of all routes/endpoints
- **Naming Consistency**: Resource naming, casing, RESTful vs RPC-style
- **HTTP Method Correctness**: Proper verb usage for operations
- **Error Response Uniformity**: Consistent error shape across all endpoints
- **Versioning Strategy**: URL, header, or none; consistency
- **Pagination Patterns**: Cursor vs offset, consistency across list endpoints
- **Backward Compatibility**: Breaking change risks

## Usage

```
Analyze the codebase at /path/to/project for API design quality.
Write your JSON output to /path/to/results/alf-api-design-reviewer-data.json
```

## Output

- Markdown report to stdout with endpoint inventory and consistency analysis
- Structured JSON with scored dimensions, violations, and recommendations

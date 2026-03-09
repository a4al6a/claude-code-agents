# alf-consistency-checker

Evaluates adherence to project conventions, naming patterns, and structural uniformity.

## What It Analyzes

- **Naming Conventions**: Variable/function/file/class casing consistency
- **Project Structure**: Similar things in similar places, structural outliers
- **Import Ordering**: Grouping, sorting, relative vs absolute style
- **Logging Uniformity**: Structured vs printf, consistent levels, logger patterns
- **Configuration Approach**: Consistent config access across modules
- **Error Handling Style**: Consistent exception types and wrapping
- **Code Formatting**: Indentation, quotes, line length consistency
- **Pattern Adherence**: Consistent use of architectural patterns

## Usage

```
Analyze the codebase at /path/to/project for consistency.
Write your JSON output to /path/to/results/alf-consistency-checker-data.json
```

## Output

- Markdown report to stdout with convention analysis and deviation inventory
- Structured JSON with scored dimensions, dominant conventions, and recommendations

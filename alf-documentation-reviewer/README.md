# alf-documentation-reviewer

Assesses documentation coverage, accuracy, and alignment with code complexity.

## What It Analyzes

- **Coverage vs Complexity**: Are the hardest parts documented?
- **Public API Docs**: Docstring/JSDoc coverage of public interfaces
- **Stale Comments**: Comments contradicting code, outdated TODOs
- **README Quality**: Setup instructions, completeness, freshness
- **ADRs**: Presence, currency, coverage of key decisions
- **Changelog**: Existence, format, consistency
- **Onboarding Path**: Clone-to-running time, dev dependency docs

## Usage

```
Analyze the codebase at /path/to/project for documentation quality.
Write your JSON output to /path/to/results/alf-documentation-reviewer-data.json
```

## Output

- Markdown report to stdout with documentation gaps and quality assessment
- Structured JSON with scored dimensions, gaps, and recommendations

# alf-dependency-auditor

Assesses the health, risk, and hygiene of project dependencies.

## What It Analyzes

- **Outdated Dependencies**: Version drift severity (major/minor/patch)
- **Abandoned Dependencies**: Last publish date, maintenance signals
- **License Compliance**: Copyleft contamination, incompatible licenses
- **Transitive Risk**: Dependency tree depth, conflicting versions, phantom deps
- **Duplicate Functionality**: Multiple libraries solving the same problem
- **Pinning Strategy**: Exact pins, ranges, lock file freshness
- **Supply Chain Signals**: Typosquatting risk, install scripts

## Usage

```
Analyze the codebase at /path/to/project for dependency health.
Write your JSON output to /path/to/results/alf-dependency-auditor-data.json
```

## Output

- Markdown report to stdout with dependency inventory and risk analysis
- Structured JSON with scored findings, outdated/abandoned lists, and recommendations

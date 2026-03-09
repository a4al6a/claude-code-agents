# alf-ownership-analyzer

Analyzes knowledge distribution, bus factor, and collaboration health using git history.

## What It Analyzes

- **Bus Factor**: Files/modules with single contributor (knowledge silos)
- **Hotspot Analysis**: High churn + high complexity = risk magnifiers
- **Knowledge Concentration**: Gini coefficient, top-N contributor ownership
- **Orphan Code**: Files with no recent committer (> 12 months)
- **Team Coupling**: Frequently co-changed files, merge conflict hotspots
- **Churn Velocity**: Areas changing too fast (instability) or never (staleness)

## Usage

```
Analyze the codebase at /path/to/project for ownership patterns.
Write your JSON output to /path/to/results/alf-ownership-analyzer-data.json
```

## Output

- Markdown report to stdout with ownership map, hotspots, and bus factor analysis
- Structured JSON with scored findings, per-module ownership, and recommendations

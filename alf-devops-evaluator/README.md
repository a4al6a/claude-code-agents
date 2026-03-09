# alf-devops-evaluator

Evaluates CI/CD pipeline quality, build reproducibility, and deployment readiness.

## What It Analyzes

- **CI/CD Pipeline**: Stages, parallelism, caching, failure handling
- **Build Reproducibility**: Lock files, pinned tools, deterministic builds
- **Environment Parity**: Dev/staging/prod config drift
- **Secret Management**: Storage method, rotation, hardcoded risks
- **Deployment Strategy**: Blue/green, canary, rolling; rollback capability
- **Containerization**: Dockerfile best practices, image security, layer efficiency
- **Infrastructure as Code**: Tool, coverage, state management
- **Branch Protection**: Required reviews, CI checks, signed commits

## Usage

```
Analyze the codebase at /path/to/project for DevOps maturity.
Write your JSON output to /path/to/results/alf-devops-evaluator-data.json
```

## Output

- Markdown report to stdout with DevOps maturity assessment per dimension
- Structured JSON with scored dimensions, configuration analysis, and recommendations

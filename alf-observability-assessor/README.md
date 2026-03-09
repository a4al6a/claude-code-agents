# alf-observability-assessor

Assesses logging, tracing, metrics, and monitoring instrumentation for production readiness.

## What It Analyzes

- **Logging Coverage**: Key decision points, errors, service boundaries
- **Structured Logging**: JSON/structured vs printf-style adoption
- **Log Level Appropriateness**: DEBUG in prod paths, ERROR for non-errors
- **Distributed Tracing**: Span coverage, context propagation, correlation IDs
- **Metric Emission**: Request, error, latency, business, and infrastructure metrics
- **Health Checks**: Liveness, readiness, dependency, startup probes
- **Alert Readiness**: Failure mode signals, SLI/SLO definitions, runbooks
- **Sensitive Data**: PII/secrets in log entries

## Usage

```
Analyze the codebase at /path/to/project for observability readiness.
Write your JSON output to /path/to/results/alf-observability-assessor-data.json
```

## Output

- Markdown report to stdout with observability assessment per dimension
- Structured JSON with scored findings, instrumentation inventory, and recommendations

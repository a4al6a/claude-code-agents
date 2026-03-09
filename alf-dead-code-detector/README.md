# alf-dead-code-detector

Finds unreachable code, unused exports, orphaned files, and feature drift.

## What It Analyzes

- **Unreachable Code**: Dead branches, post-return statements, impossible conditions
- **Unused Exports**: Public functions/classes never imported elsewhere
- **Orphaned Files**: Files not reachable from any entry point
- **Stale Feature Flags**: Toggles always true/false, never evaluated
- **Unused Configuration**: Config keys defined but never read
- **Zombie Dependencies**: Declared but never imported
- **Commented-Out Code**: Large blocks of commented source code
- **Dead Routes**: Endpoints defined but unreachable

## Usage

```
Analyze the codebase at /path/to/project for dead code.
Write your JSON output to /path/to/results/alf-dead-code-detector-data.json
```

## Output

- Markdown report to stdout with dead code inventory and removal candidates
- Structured JSON with scored findings, dead code ratio, and recommendations

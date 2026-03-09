# alf-security-assessor

Detects security vulnerabilities and assesses the overall security hygiene of a codebase.

## What It Analyzes

- **OWASP Top 10**: Injection, broken auth, XSS, CSRF, misconfig, and more
- **Hardcoded Secrets**: API keys, tokens, passwords, private keys in source
- **Input Validation**: Coverage of entry points with proper sanitization
- **Auth Patterns**: Consistency and completeness of authentication/authorization
- **Dependency CVEs**: Known vulnerabilities in declared dependencies
- **Cryptographic Usage**: Weak algorithms, hardcoded salts, improper key management

## Usage

```
Analyze the codebase at /path/to/project for security vulnerabilities.
Write your JSON output to /path/to/results/alf-security-assessor-data.json
```

## Output

- Markdown report to stdout with findings grouped by OWASP category
- Structured JSON with scored findings, risk distribution, and prioritized recommendations

---
name: audit-methodology
description: Scoring approach, severity classification, finding format, evidence collection patterns, and report templates for software system audits
---

# Audit Methodology

This skill contains the scoring methodology, severity classification, evidence collection patterns, finding format, and report template. Load during Phase 3 (COLLECT) and Phase 4 (ANALYZE).

## Severity Classification

Based on CVSS v4.0 severity ranges and OWASP Risk Rating Methodology.

| Level | Label | Description | Action Required | Examples |
|---|---|---|---|---|
| 5 | Critical | Active exploitation risk or direct regulatory violation | Immediate remediation | Hardcoded secrets in repo, PII in plaintext logs, no authentication on sensitive endpoints |
| 4 | High | Significant vulnerability or compliance gap | Remediate within sprint | High-severity CVEs in dependencies, missing encryption at rest, no audit logging |
| 3 | Medium | Notable weakness or partial compliance | Remediate within quarter | Outdated dependencies (not vulnerable), incomplete logging, missing rate limiting |
| 2 | Low | Minor issue or improvement opportunity | Address in backlog | Missing security headers, verbose error messages, minor config improvements |
| 1 | Informational | Observation or best practice recommendation | Consider for future | Documentation gaps, style inconsistencies, optional hardening measures |

### Risk Scoring Formula

```
Audit Risk Score = Severity (1-5) x Likelihood (1-5) x Business Impact (1-5)
```

**Severity**: Based on CVSS base score or control deficiency impact
**Likelihood**: Exploitability, exposure, environmental factors (1=unlikely, 5=actively exploitable)
**Business Impact**: Data sensitivity, regulatory consequences, operational effect (1=minimal, 5=catastrophic)

**Risk score interpretation**:
| Score Range | Priority |
|---|---|
| 75-125 | Immediate action required |
| 40-74 | High priority -- next sprint |
| 15-39 | Medium priority -- within quarter |
| 5-14 | Low priority -- backlog |
| 1-4 | Informational |

### Confidence Levels

Every finding includes a confidence assessment:

| Level | Meaning | When to Apply |
|---|---|---|
| High | Evidence directly confirms the finding | File:line reference shows the exact issue; tool output confirms vulnerability |
| Medium | Evidence strongly suggests the finding | Pattern detected but edge cases possible; configuration implies but does not prove |
| Low | Evidence is indirect or circumstantial | Absence of evidence (missing control); inference from related artifacts |

When confidence is Low, note this explicitly and recommend manual verification.

## Finding Format

Every finding uses this structure:

```markdown
### FINDING-{NNN}: {Brief Title}

**Severity**: {Critical|High|Medium|Low|Informational}
**Confidence**: {High|Medium|Low}
**Risk Score**: {N} (Severity {N} x Likelihood {N} x Impact {N})
**Location**: {file:line} or {file} or "Not found (missing control)"
**Framework Mapping**: {Framework} {Control ID} -- {Control Name}

**Evidence**:
{What was observed -- code snippet, config value, tool output, or absence of expected control}

**Criteria**:
{What standard or best practice was applied -- specific regulation article, OWASP rule, CIS benchmark}

**Impact**:
{Potential consequences if unaddressed -- regulatory, security, operational}

**Recommendation**:
{Specific remediation guidance with effort estimate}

**Effort**: {S|M|L|XL}
```

## Evidence Collection Patterns

### Access Control Dimension

**Grep patterns** (adapt to detected language/framework):
```bash
# Authentication mechanisms
grep -rn "authenticate\|login\|signIn\|passport\|jwt\|bearer\|oauth" --include="*.{js,ts,py,java,go,rb,rs}"
grep -rn "bcrypt\|argon2\|scrypt\|pbkdf2\|sha256\|sha512" --include="*.{js,ts,py,java,go,rb,rs}"
grep -rn "mfa\|totp\|two.factor\|2fa\|multi.factor" -i --include="*.{js,ts,py,java,go,rb,rs,yml,yaml,json}"

# Authorization patterns
grep -rn "role\|permission\|authorize\|rbac\|abac\|policy\|isAdmin\|hasRole\|canAccess" --include="*.{js,ts,py,java,go,rb,rs}"
grep -rn "@Secured\|@PreAuthorize\|@RolesAllowed\|@login_required\|@requires_auth" --include="*.{js,ts,py,java,go,rb}"

# Session management
grep -rn "session\|cookie\|httpOnly\|secure\|sameSite\|maxAge\|expires" --include="*.{js,ts,py,java,go,rb,rs}"
```

### Encryption Dimension

```bash
# Encryption at rest
grep -rn "AES\|encrypt\|decrypt\|cipher\|crypto\|KMS\|keyVault\|secretManager" --include="*.{js,ts,py,java,go,rb,rs}"
grep -rn "ssl\|tls\|https\|certificate\|cert\|HSTS" -i --include="*.{js,ts,py,java,go,yml,yaml,conf,cfg}"

# Weak cryptography
grep -rn "MD5\|SHA1\|DES\|RC4\|ECB\|ssl.*v2\|ssl.*v3\|tls.*1\.0\|tls.*1\.1" -i --include="*.{js,ts,py,java,go,rb,rs,yml,yaml,conf}"
```

### Secrets Detection

```bash
# Hardcoded secrets
grep -rn "password\s*=\s*['\"]" --include="*.{js,ts,py,java,go,rb,rs,yml,yaml,properties,env}"
grep -rn "api[_-]?key\s*=\s*['\"]" -i --include="*.{js,ts,py,java,go,rb,rs,yml,yaml,json}"
grep -rn "secret\s*=\s*['\"]" --include="*.{js,ts,py,java,go,rb,rs,yml,yaml,properties}"
grep -rn "AKIA[0-9A-Z]\{16\}" .  # AWS access keys
grep -rn "ghp_[a-zA-Z0-9]\{36\}" .  # GitHub tokens
grep -rn "-----BEGIN.*PRIVATE KEY-----" .  # Private keys
```

### Logging Dimension

```bash
# Logging implementation
grep -rn "logger\|log\.\|logging\|winston\|bunyan\|pino\|log4j\|slf4j\|logrus\|slog\|tracing" --include="*.{js,ts,py,java,go,rb,rs}"
grep -rn "audit.log\|audit_log\|auditLog\|AuditEvent\|audit.trail" --include="*.{js,ts,py,java,go,rb,rs}"

# PII in logs (potential violation)
grep -rn "log.*email\|log.*password\|log.*ssn\|log.*credit.card\|log.*token" -i --include="*.{js,ts,py,java,go,rb,rs}"
```

### Supply Chain Dimension

```bash
# Run package manager audit tools
npm audit --json 2>/dev/null || true
pip audit --format json 2>/dev/null || true
cargo audit --json 2>/dev/null || true
bundle audit check 2>/dev/null || true
mvn dependency-check:check 2>/dev/null || true

# Dependency age assessment
npm outdated --json 2>/dev/null || true
pip list --outdated --format json 2>/dev/null || true

# License detection
find . -name "LICENSE*" -o -name "LICENCE*" -o -name "COPYING" 2>/dev/null
grep -rn "license\|licence" --include="package.json" --include="*.toml" --include="*.gemspec" --include="pom.xml"
```

### Configuration Security Dimension

```bash
# Secrets in environment/config files
find . -name ".env" -o -name ".env.*" -o -name "*.env" | head -20
find . -name "*.pem" -o -name "*.key" -o -name "*.p12" -o -name "*.pfx" | head -20
grep -rn "DEBUG\s*=\s*[Tt]rue" --include="*.{py,env,yml,yaml,json,properties}"

# IaC security
grep -rn "0\.0\.0\.0/0\|::/0" --include="*.{tf,yaml,yml,json}"  # Overly permissive CIDR
grep -rn "privileged.*true\|runAsRoot.*true" --include="*.{yaml,yml}"  # Container privilege
grep -rn "latest" --include="Dockerfile"  # Unpinned base images
```

## Compliance Scoring

### Per-Dimension Score (0-10)

Each audit dimension receives a score based on the proportion of applicable controls that are compliant:

```
dimension_score = (controls_compliant + 0.5 * controls_partial) / controls_applicable * 10
```

Where controls that are "not applicable" are excluded from the denominator.

**Rating scale**:
| Score | Rating |
|---|---|
| 9.0 - 10.0 | Fully Compliant |
| 7.0 - 8.9 | Substantially Compliant |
| 5.0 - 6.9 | Partially Compliant |
| 3.0 - 4.9 | Non-Compliant |
| 0.0 - 2.9 | Significantly Non-Compliant |

### Overall Framework Compliance Score

Weighted average of dimension scores. Weights reflect the framework's emphasis areas:

| Dimension | Default Weight | Frameworks with Higher Weight |
|---|---|---|
| Access Control | 0.15 | SOX (0.20), HIPAA (0.20) |
| Encryption | 0.12 | GDPR (0.15), HIPAA (0.15), PCI DSS (0.18) |
| Audit Logging | 0.12 | SOX (0.18), PCI DSS (0.15), FedRAMP (0.15) |
| Change Management | 0.10 | SOX (0.15) |
| Vulnerability Mgmt | 0.10 | PCI DSS (0.15), NIST (0.12), CMMC (0.15) |
| Data Protection | 0.10 | GDPR (0.20), CCPA (0.20), HIPAA (0.15) |
| Incident Response | 0.08 | DORA (0.15), NIS2 (0.12) |
| Backup/Recovery | 0.06 | DORA (0.12), HIPAA (0.10) |
| Supply Chain | 0.06 | NIS2 (0.15), NIST (0.10), PCI DSS (0.08) |
| Config Security | 0.06 | FedRAMP (0.10), ISO 27001 (0.08) |
| API Security | 0.03 | PCI DSS (0.05) |
| Secure SDLC | 0.02 | SOC 2 (0.05), NIST (0.05) |

## Report Template

Each per-framework report follows this structure:

```markdown
# {Framework Name} Compliance Audit Report

**Audit Date**: {YYYY-MM-DD}
**Target System**: {project name and path}
**Framework**: {full framework name and version}
**Auditor**: Sentinel (AI-powered software system auditor)

> **Disclaimer**: This report was generated by an AI-powered audit agent. All findings,
> compliance determinations, and scores require review by a qualified human auditor
> before being used for regulatory compliance decisions.

## Executive Summary

**Overall Compliance Score**: {X.X} / 10.0 ({Rating})
**Findings**: {Critical: N} | {High: N} | {Medium: N} | {Low: N} | {Informational: N}

{2-3 sentence summary of overall compliance posture and top priorities}

## Compliance Dashboard

| Control Area | Controls Assessed | Compliant | Partial | Non-Compliant | N/A | Score |
|---|---|---|---|---|---|---|
| {area} | {N} | {N} | {N} | {N} | {N} | {X.X} |
| ... | | | | | | |
| **Overall** | **{N}** | **{N}** | **{N}** | **{N}** | **{N}** | **{X.X}** |

## Findings

### Critical Findings
{FINDING-NNN entries using the finding format}

### High Findings
{FINDING-NNN entries}

### Medium Findings
{FINDING-NNN entries}

### Low Findings
{FINDING-NNN entries}

### Informational
{FINDING-NNN entries}

## Cross-Framework Observations

{Findings that also apply to other frameworks the user selected, with cross-references}

## Supply Chain Analysis

- **Dependencies scanned**: {N}
- **Vulnerable dependencies**: {N} (Critical: {N}, High: {N}, Medium: {N})
- **License conflicts**: {N}
- **Unmaintained dependencies**: {N} (no updates in 2+ years)
- **SBOM generated**: {Yes/No}

## Remediation Plan

| Priority | Finding ID | Description | Effort | Recommended Deadline |
|---|---|---|---|---|
| {1} | FINDING-{NNN} | {description} | {S/M/L/XL} | {timeframe} |
| ... | | | | |

## Methodology Notes

- **Agent**: Sentinel (software-system-auditor)
- **Model**: {model_id}
- **Tools used**: {list of tools and versions}
- **Fallbacks activated**: {list or "none"}
- **Files analyzed**: {count} of {total} ({sampling note})
- **Sampling approach**: {full scan | SHA-256 deterministic 30%}
- **Security-sensitive paths scanned**: {list}
- **Dependency audit tools**: {tools used or "manual analysis -- tools not available"}
- **Limitations**: {any known gaps in analysis}
- **Analysis date**: {YYYY-MM-DD}
```

## Large Codebase Strategies

### Deterministic Sampling (500+ files)

```
1. List all source files (exclude build/, dist/, node_modules/, vendor/, .git/)
2. Compute SHA-256 hash of each file's relative path
3. Select files where hash mod 100 < 30 (30% sample)
4. Always include files matching security-sensitive paths:
   - auth/, authentication/, authorization/
   - crypto/, encryption/, security/
   - config/, configuration/, settings/
   - secrets/, keys/, certs/, certificates/
   - middleware/, interceptor/, filter/
5. Always include files exceeding 200 LOC
6. Record the complete file list in methodology notes
```

### Hotspot Prioritization

When time-constrained, prioritize:
1. Files in security-sensitive paths (highest priority)
2. Files with recent changes (git log --since="3 months ago")
3. Files with high complexity (LOC > 200)
4. Entry points (main files, route definitions, API handlers)
5. Configuration files (all, regardless of sampling)

### Tool Delegation

Prefer tool output over manual scanning:
- Dependency vulnerabilities: `npm audit`, `pip audit`, `cargo audit`, `mvn dependency-check:check`
- Secret scanning: `git log --diff-filter=A --name-only`, `grep -r` for known patterns
- IaC scanning: if Checkov/Terrascan available, use their output
- License scanning: if FOSSology/ScanCode available, use their output

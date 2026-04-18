---
name: framework-sox
description: Sarbanes-Oxley (SOX) audit mapping. Controls, evidence-to-control rules, scoring weights, report template, and false-positives for auditing software systems under SOX Section 404 ITGC scope.
framework_id: sox
framework_name: Sarbanes-Oxley Act
evidence_schema: "1"
---

# SOX (Sarbanes-Oxley Act)

**Applies to**: Publicly traded companies (US) and their subsidiaries. Subject: IT General Controls (ITGC) supporting financial reporting.
**Key sections**: 302 (CEO/CFO certification), 404 (management assessment of internal controls over financial reporting).
**Auditor reliance**: External auditor evaluates ITGC as part of ICFR audit. Deficiencies classified as control deficiency / significant deficiency / material weakness.

## Scope notes

SOX does not regulate software directly. It regulates the *financial reporting chain*. When auditing a software system for SOX:
- **In scope**: systems that produce, transform, or store financial-reporting data, and the IT systems that control access to them.
- **Out of scope**: marketing sites, internal dev tools unrelated to financial reporting, experimental/beta features not yet in ICFR scope.

Ask the user for scope clarification if the system's role in the financial reporting chain is unclear. Record the answer in `codebase.audit_scope.sox_scope_confirmation`.

## Control areas (ITGC)

Four ITGC categories apply:

### 1. Access to Programs and Data (APD)

| Control | Evidence-bundle path | Rule | Deficiency classification |
|---|---|---|---|
| APD-01 Unique user identification | `facts.access_control.authn_mechanisms[]` contains non-shared credential mechanism | pass/fail | Critical if absent |
| APD-02 Authentication strength | `facts.access_control.mfa_present == true` for privileged access | pass/fail | High if MFA absent |
| APD-03 Authorization (least privilege) | `facts.access_control.rbac_present == true` AND `facts.access_control.privileged_endpoints_without_auth == []` | pass/partial/fail | Critical if any endpoint unauthenticated |
| APD-04 Password policy | `facts.access_control.password_policy.min_length >= 12` AND `complexity_rules == true` | pass/partial/fail | Medium |
| APD-05 Session management | `facts.access_control.session_management.httpOnly AND .secure AND .sameSite != "none"` | pass/fail | Medium |
| APD-06 Audit of access changes | `facts.audit_logging.auth_events_logged == true` | pass/fail | High |

### 2. Change Management (CM)

| Control | Evidence-bundle path | Rule |
|---|---|---|
| CM-01 Version control | `facts.change_management.vcs != null` | pass/fail |
| CM-02 Peer review required | `facts.change_management.branch_protection.required_reviews >= 1` | pass/fail |
| CM-03 CI checks gate merges | `facts.change_management.branch_protection.required_checks[]` non-empty | pass/fail |
| CM-04 Production deployment gate | `facts.change_management.deployment_gates[]` contains a manual approval or equivalent | pass/fail |
| CM-05 Change audit trail | Git history present + PR metadata retained | pass |
| CM-06 Segregation of duties | Developers do not auto-approve own PRs (branch-protection policy check) | pass/fail |

### 3. Computer Operations (CO)

| Control | Evidence-bundle path | Rule |
|---|---|---|
| CO-01 Backup configuration | `facts.backup_recovery.backup_config_detected == true` | pass/fail |
| CO-02 Backup encryption | `facts.backup_recovery.backup_encryption == true` | pass/fail |
| CO-03 Recovery tested | `facts.backup_recovery.dr_tested_evidence != null` | pass/partial/fail |
| CO-04 Job scheduling / batch integrity | Search for scheduled-job definitions + failure handling | pass/partial/fail |
| CO-05 Incident handling process | `facts.incident_response.runbook_present == true` | pass/fail |

### 4. Data Integrity (DI)

| Control | Evidence-bundle path | Rule |
|---|---|---|
| DI-01 Log integrity protection | `facts.audit_logging.log_tamper_protection == true` | pass/fail |
| DI-02 Encrypted timestamps / checksums | Signed log shipping or append-only store evidence | pass/partial/fail |
| DI-03 Data tampering detection | Checksums/signatures on financial records | pass/partial/fail |

## Dimension weighting (SOX-specific)

Overrides the default weights in `audit-methodology.md`:

| Dimension | Weight |
|---|---|
| Access Control | 0.22 |
| Change Management | 0.22 |
| Audit Logging | 0.20 |
| Backup/Recovery | 0.10 |
| Incident Response | 0.08 |
| Configuration Security | 0.06 |
| Vulnerability Mgmt | 0.05 |
| Data Protection | 0.04 |
| Encryption | 0.03 |

Note the heavy weighting on access + change + logging — these are what SOX external auditors test most.

## Deficiency classification

Instead of Critical/High/Medium/Low, SOX findings map to:

| Bundle severity | SOX deficiency |
|---|---|
| Critical | Material Weakness (reasonable possibility of a material misstatement not being prevented or detected) |
| High | Significant Deficiency (less severe than material weakness but important enough to merit attention) |
| Medium | Control Deficiency (design or operating deficiency not rising to significant) |
| Low/Informational | Observation / recommendation |

Include both the bundle severity and the SOX deficiency classification in each finding.

## Report template additions

Beyond the base template, every SOX report includes:

```markdown
## Management Assertion Context

This audit supports management's assessment of ICFR under SOX Section 404. It is not itself an ICFR audit. Findings require review by finance, internal audit, and external auditors.

## ITGC Area Summary

| ITGC Area | Controls Assessed | Pass | Partial | Fail | Material Weaknesses | Significant Deficiencies |
|---|---|---|---|---|---|---|
| Access to Programs and Data | 6 | ... | ... | ... | ... | ... |
| Change Management | 6 | ... | ... | ... | ... | ... |
| Computer Operations | 5 | ... | ... | ... | ... | ... |
| Data Integrity | 3 | ... | ... | ... | ... | ... |

## Segregation of Duties Analysis

{Evidence that code authors are not also sole approvers for their own changes; branch-protection config; CODEOWNERS review distribution}
```

## Common false-positives

- **MFA missing on dev tooling** — not in ITGC scope for financial reporting. Flag as informational, do not count against score.
- **Password policy in non-financial systems** — same as above.
- **Lack of 2+ reviewer rule** — SOX does not mandate 2 reviewers; 1 independent reviewer is generally acceptable if not the commit author.
- **No tamper-proof logs on dev/staging** — in-scope only for production financial systems.

## Documentation retention

Every SOX audit finding's evidence, remediation plan, and verification must be retainable for at least 7 years. Note this in the methodology section.

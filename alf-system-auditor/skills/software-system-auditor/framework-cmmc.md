---
name: framework-cmmc
description: CMMC 2.0 audit mapping. Three certification levels for US DoD contractors handling FCI or CUI.
framework_id: cmmc
framework_name: Cybersecurity Maturity Model Certification 2.0
evidence_schema: "1"
---

# CMMC 2.0

**Applies to**: All entities doing business with the US Department of Defense that handle Federal Contract Information (FCI) or Controlled Unclassified Information (CUI).
**Implementation**: Final rule published October 2024; phased implementation November 2025 through 2028.
**Basis**: FAR 52.204-21 (Level 1), NIST SP 800-171 Rev 2 (Level 2), NIST SP 800-172 (Level 3).

## Three levels

| Level | Scope | Practices | Assessment |
|---|---|---|---|
| Level 1 — Foundational | Basic FCI protection | 17 practices (FAR 52.204-21) | Annual **self**-assessment |
| Level 2 — Advanced | General CUI protection | 110 practices (NIST SP 800-171 Rev 2) | **C3PAO** (certified third-party) or self-assessment depending on contract sensitivity |
| Level 3 — Expert | Enhanced CUI protection against APTs | Level 2 + 24 from SP 800-172 | **Government-led** (DIBCAC) |

Confirm target level with user. Record in `codebase.audit_scope.cmmc_level`.

## Scope determination

Ask:
- Does the system handle FCI (Federal Contract Information, not yet released to the public)?
- Does the system handle CUI (Controlled Unclassified Information — marked by contract or per 32 CFR 2002)?
- Target level (1, 2, or 3)?

If only FCI, Level 1 applies. If any CUI, Level 2 or 3 applies.

## Level 1 — 17 practices (FAR 52.204-21)

Covers 6 domains. Minimal software-audit coverage:

| Domain | Practice IDs | Evidence rule |
|---|---|---|
| Access Control (AC) | AC.L1-3.1.1, AC.L1-3.1.2, AC.L1-3.1.20, AC.L1-3.1.22 | Basic authentication + authorization + external-system limits |
| Identification and Authentication (IA) | IA.L1-3.5.1, IA.L1-3.5.2 | Unique user IDs + authentication |
| Media Protection (MP) | MP.L1-3.8.3 | Sanitize/destroy media containing FCI |
| Physical Protection (PE) | PE.L1-3.10.1, PE.L1-3.10.3, PE.L1-3.10.4, PE.L1-3.10.5 | Physical controls — out-of-scope for code |
| System and Communications Protection (SC) | SC.L1-3.13.1, SC.L1-3.13.5 | Boundary protection + public-facing separation |
| System and Information Integrity (SI) | SI.L1-3.14.1, SI.L1-3.14.2, SI.L1-3.14.4, SI.L1-3.14.5 | Flaw remediation + malicious code protection + scanning |

## Level 2 — 110 practices (NIST SP 800-171 Rev 2)

14 domains. Primary focus for most audits. Mapping:

### AC — Access Control (22 practices)
Reuses NIST 800-53 AC-family mappings from `framework-fedramp.md`. Evidence: `access_control.*`

### AT — Awareness and Training (3 practices)
Organizational — out-of-scope for code.

### AU — Audit and Accountability (9 practices)
| Practice | Rule |
|---|---|
| AU.L2-3.3.1 Audit records created | `audit_logging.auth_events_logged` |
| AU.L2-3.3.2 Audit records content | Who/what/when/where |
| AU.L2-3.3.3 Review/update audited events | Periodic review evidence |
| AU.L2-3.3.4 Alert on audit failure | Alerting evidence |
| AU.L2-3.3.5 Audit record retention | Retention configured |
| AU.L2-3.3.6 Audit report generation | Reporting capability |
| AU.L2-3.3.7 Time synchronization | NTP |
| AU.L2-3.3.8 Protect audit information | `log_tamper_protection == true` |
| AU.L2-3.3.9 Authorize audit-log management | Admin controls on logs |

### CM — Configuration Management (9 practices)
Reuses 800-53 CM-family mappings. Evidence: IaC + baselines + change control.

### IA — Identification and Authentication (11 practices)
| Practice | Rule |
|---|---|
| IA.L2-3.5.3 MFA for privileged access + remote access | `mfa_present == true` |
| IA.L2-3.5.4 Replay-resistant authentication | Nonce/timestamp-based tokens |
| IA.L2-3.5.5 No reuse of identifiers | Unique user IDs |
| IA.L2-3.5.7 Password complexity | `password_policy.complexity_rules == true` |
| IA.L2-3.5.10 Store and transmit only cryptographically-protected passwords | bcrypt/argon2/pbkdf2 evidence |

### IR — Incident Response (3 practices)
Evidence: `incident_response.*`

### MA — Maintenance (6 practices)
Organizational + physical.

### MP — Media Protection (9 practices)
`data_protection.right_to_erasure` (sanitization) + transport controls.

### PE — Physical Protection (6 practices)
Out-of-scope for code.

### PS — Personnel Security (2 practices)
Organizational.

### RA — Risk Assessment (3 practices)
`vulnerability_management.*` + scanning cadence.

### SC — System and Communications Protection (16 practices)
| Practice | Rule |
|---|---|
| SC.L2-3.13.1 Boundary protection | Firewall/SG |
| SC.L2-3.13.8 CUI encryption in transit | `in_transit.tls_enforced == true` with FIPS-validated |
| SC.L2-3.13.11 CUI encryption at rest | `at_rest.detected` with FIPS-validated |
| SC.L2-3.13.16 CUI in storage encrypted | same |

### SI — System and Information Integrity (7 practices)
Flaw remediation, malicious code, monitoring.

## Level 3 — Level 2 + 24 from SP 800-172

Enhanced controls for APT resistance:
- Advanced persistence-resistant access controls
- Threat hunting capabilities
- Advanced malware protection
- Network segmentation
- Cryptographic key management at elevated level

## FIPS requirements

Both Level 2 and Level 3 require FIPS 140-2/3 validated cryptographic modules for CUI. Same validation checks as FedRAMP. Use of non-FIPS-validated crypto for CUI = **Critical**.

## SPRS score

DoD's Supplier Performance Risk System (SPRS) score = 110 - sum of deficiency weights (each practice has a 1/3/5 point weight). Minimum score for Level 2 self-assessment: varies by contract; often 110 (all practices met) or 88+ with a POA&M.

The audit can produce an estimated SPRS score. Include in report with explicit "estimated" qualifier.

## Subcontractor flow-down

CMMC flows down: prime contractor's CMMC level applies to subcontractors handling the same CUI. Flag any third-party dependency that plausibly handles CUI as needing its own CMMC assessment.

## False Claims Act exposure

Misrepresenting CMMC status is actionable under the False Claims Act. Include in the disclaimer: "This audit output does not constitute a CMMC assessment. Self-assessment posture for attestation to DoD must be validated by a qualified assessor or submitted only when genuinely met."

## Dimension weighting (CMMC Level 2)

| Dimension | Weight |
|---|---|
| Access Control | 0.20 |
| Encryption | 0.15 |
| Audit Logging | 0.12 |
| Configuration Security | 0.12 |
| Vulnerability Mgmt | 0.10 |
| Incident Response | 0.08 |
| Change Management | 0.08 |
| Data Protection (CUI) | 0.08 |
| Supply Chain | 0.05 |
| Other | 0.02 |

## Report template additions

```markdown
## Scope Confirmation

- Data types handled: FCI only / CUI / both
- Target level: 1 / 2 / 3
- Assessment type: self / C3PAO / DIBCAC

## FIPS Cryptographic Validation

{Same table as FedRAMP}

## Practice Dashboard (Level 2 — 110 practices)

| Domain | Practices | Met | Partial | Not Met | Out-of-scope (policy/physical) |
|---|---|---|---|---|---|
| AC | 22 | ... | ... | ... | ... |
| AT | 3 | N/A | N/A | N/A | 3 |
| AU | 9 | ... | ... | ... | ... |
| ... | | | | | |

## Estimated SPRS Score

Score: {110 − sum of deficiency weights} (estimated; requires qualified assessor for binding score)

## POA&M Candidates

Practices that can be flagged in a Plan of Action and Milestones:
| Practice | Deficiency | Planned Remediation | Target Date |
|---|---|---|---|

## Subcontractor Flow-Down

Third-party dependencies plausibly handling CUI:
| Dependency | Reason | Requires own CMMC? |
|---|---|---|
```

## Common false-positives

- **Level 2 claim on FCI-only system** — Level 1 is sufficient; do not over-scope.
- **MFA missing on end-customer access** — required for DoD employee / contractor access to CUI; end-customer MFA may be softer.
- **FIPS modules used incorrectly** — using a FIPS-validated module in a non-FIPS mode is a nuanced finding; flag with detail.
- **"Documentation insufficient"** — CMMC requires specific documentation artifacts (SSP, POA&M). Their absence is a finding; flag as high.

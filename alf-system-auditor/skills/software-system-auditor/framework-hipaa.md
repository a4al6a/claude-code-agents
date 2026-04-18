---
name: framework-hipaa
description: HIPAA Security Rule audit mapping. Administrative, physical, and technical safeguards with evidence-to-control rules for systems handling ePHI.
framework_id: hipaa
framework_name: Health Insurance Portability and Accountability Act — Security Rule
evidence_schema: "1"
---

# HIPAA (Health Insurance Portability and Accountability Act)

**Applies to**: Covered Entities (providers, health plans, clearinghouses) and Business Associates handling electronic Protected Health Information (ePHI) in the US.
**Key regulation**: 45 CFR Part 164, Subpart C (Security Rule). Also: Privacy Rule (Subpart E), Breach Notification Rule (Subpart D).
**Documentation retention**: 6 years from date of creation or last effective date.
**Enforcement**: US Department of Health and Human Services (HHS) Office for Civil Rights.

## Scope notes

Only systems that create, receive, maintain, or transmit ePHI are in scope. If the system has no ePHI, the audit is out-of-scope — recommend `alf-security-assessor` instead. Confirm ePHI handling via the applicability questionnaire and `facts.data_protection.phi_handling_detected`.

Business Associate Agreement (BAA) should be in place between covered entities and business associates — record presence in `codebase.audit_scope.hipaa_baa_in_place`.

## Safeguards

HIPAA groups requirements into three safeguard types. Software audit focuses mostly on Technical and partially on Administrative.

### Technical Safeguards (§164.312) — primary software audit scope

| Control | Standard | Required/Addressable | Evidence-bundle rule |
|---|---|---|---|
| 164.312(a)(1) | Access Control | Required | `facts.access_control.rbac_present == true` AND no unauth privileged endpoints |
| 164.312(a)(2)(i) | Unique User Identification | Required | no shared accounts; `authn_mechanisms[]` shows per-user auth |
| 164.312(a)(2)(ii) | Emergency Access Procedure | Required | runbook documents break-glass access (`facts.incident_response.runbook_present`) |
| 164.312(a)(2)(iii) | Automatic Logoff | Addressable → **Required after 2025 update** | session timeout present (`facts.access_control.session_management.maxAge` reasonable) |
| 164.312(a)(2)(iv) | Encryption and Decryption | Addressable → **Required after 2025 update** | `facts.encryption.at_rest.detected == true` with AES-128+ |
| 164.312(b) | Audit Controls | Required | `facts.audit_logging.auth_events_logged == true` AND `data_access_logged == true` |
| 164.312(c)(1) | Integrity | Required | integrity controls on ePHI (checksums, signed records, or database constraints) |
| 164.312(c)(2) | Mechanism to Authenticate ePHI | Addressable | hashing/signatures for ePHI at rest |
| 164.312(d) | Person or Entity Authentication | Required | multi-factor for ePHI access (`facts.access_control.mfa_present`) |
| 164.312(e)(1) | Transmission Security | Required | `facts.encryption.in_transit.tls_enforced == true` + `min_version >= TLS 1.2` |
| 164.312(e)(2)(i) | Integrity Controls in Transit | Addressable | TLS provides this; ensure not using unauthenticated ciphers |
| 164.312(e)(2)(ii) | Encryption in Transit | Addressable → **Required after 2025 update** | same as 164.312(e)(1) |

### Administrative Safeguards (§164.308) — partial software audit scope

| Control | Rule |
|---|---|
| 164.308(a)(1)(ii)(A) Risk Analysis | Presence of threat-model docs; partial signal only |
| 164.308(a)(1)(ii)(B) Risk Management | Remediation tracking evidence (issue tracker integration) |
| 164.308(a)(3) Workforce Security | Deprovisioning workflow (partial — mostly org) |
| 164.308(a)(4) Information Access Management | `rbac_present == true` |
| 164.308(a)(5) Security Awareness and Training | Out-of-scope for code audit |
| 164.308(a)(6) Security Incident Procedures | `facts.incident_response.runbook_present == true` AND breach-notification procedure |
| 164.308(a)(7) Contingency Plan | `facts.backup_recovery.dr_tested_evidence != null` |
| 164.308(a)(8) Evaluation | Periodic technical evaluation — partial signal from scanning in CI |
| 164.308(b) Business Associate Contracts | Out-of-scope for code |

### Physical Safeguards (§164.310) — out-of-scope

Physical safeguards address facility access, workstation security, device/media controls. Not assessable from code. Note in report.

### Breach Notification Rule (§164.404–§164.414)

| Rule | Evidence |
|---|---|
| Notification within 60 days | `facts.incident_response.breach_notification_procedure == true` |
| Log of breaches | Logged incidents table / issue tracker integration |

## 2025 update — Addressable → Required

The HHS Notice of Proposed Rulemaking (published 2024, effective 2025) converts previously "addressable" specifications to **required**. Treat these as required in the audit:
- Encryption at rest
- Encryption in transit
- Automatic logoff
- Network segmentation for ePHI systems
- MFA for all ePHI access
- Annual technology asset inventories
- 72-hour system restoration capability
- Regular vulnerability scans and penetration tests

When confirming scope, ask whether the audit target is pre- or post-effective-date of the 2025 update. Record in `codebase.audit_scope.hipaa_2025_rule_applicable`.

## Dimension weighting (HIPAA-specific)

| Dimension | Weight |
|---|---|
| Access Control | 0.20 |
| Encryption | 0.18 |
| Audit Logging | 0.15 |
| Data Protection (ePHI-specific) | 0.15 |
| Incident Response | 0.10 |
| Backup/Recovery | 0.08 |
| Vulnerability Mgmt | 0.06 |
| Change Management | 0.05 |
| Other | 0.03 |

## Report template additions

```markdown
## ePHI Inventory

| Field | File:line | ePHI element type (per Safe Harbor 18 identifiers) | Encrypted at rest? | Logged on access? |
|---|---|---|---|---|
| patient_ssn | src/models/patient.py:18 | SSN (#7) | Yes | Yes |
| ... | | | | |

## Safeguard Summary

| Safeguard Type | Required | Addressable | Met | Partial | Not Met | Out-of-scope (policy-only) |
|---|---|---|---|---|---|---|
| Technical (§164.312) | ... | ... | ... | ... | ... | ... |
| Administrative (§164.308) | ... | ... | ... | ... | ... | ... |

## Breach Readiness

- Notification procedure documented: yes/no
- Data subject communication template: yes/no
- Covered-entity vs business-associate flow: {description}
- Risk Assessment of low-probability-of-compromise presumption: yes/no

## 18 HIPAA Identifiers — Detection

{For each of the 18 Safe Harbor identifiers, note whether the code handles them.}
```

## Common false-positives

- **Clinical test data in fixtures** — obviously-fake patient records are not ePHI. Use fixture markers to de-scope.
- **De-identified data** — once de-identified per Safe Harbor method or Expert Determination, not ePHI. If the code handles de-identification, note it as a strength.
- **Limited Data Set** — intermediate category; some identifiers stripped. Treat as ePHI for conservative audit.
- **"MFA not present for patient self-service"** — patient portals may use password + email-link (acceptable). MFA required for workforce access; self-service patient access has softer requirements under the rule. Ask which context applies.

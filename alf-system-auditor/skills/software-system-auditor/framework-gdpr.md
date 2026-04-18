---
name: framework-gdpr
description: GDPR audit mapping. Articles, evidence-to-control rules, scoring weights, report template, and false-positives for systems processing EU/EEA/UK personal data.
framework_id: gdpr
framework_name: General Data Protection Regulation (EU) 2016/679
evidence_schema: "1"
---

# GDPR (General Data Protection Regulation)

**Applies to**: Any organization processing personal data of individuals in the EU / EEA / UK, regardless of where the controller or processor is based (Art. 3 territorial scope).
**Penalties**: Up to €20M or 4% of global annual turnover, whichever is higher.
**Regulators**: Supervisory authorities (national DPAs) + European Data Protection Board for cross-border cases.

## Controller vs processor

Ask the user whether the system acts as a controller (determines purposes) or processor (acts on controller's instructions). Different articles apply:
- **Controller-heavy**: Arts. 6, 7, 12–22, 30(1), 33, 35
- **Processor-heavy**: Arts. 28, 30(2), 32, 33(2)

Record in `codebase.audit_scope.gdpr_role`. When unknown, map both.

## Articles mapped to evidence

### Principles (Art. 5)

| Article | Principle | Evidence-bundle path | Rule |
|---|---|---|---|
| 5(1)(a) | Lawfulness, fairness, transparency | Privacy notice presence (docs) | partial |
| 5(1)(b) | Purpose limitation | Data-model annotations or documentation of purpose per field | partial |
| 5(1)(c) | Data minimization | PII field inventory count vs. documented purposes | partial |
| 5(1)(d) | Accuracy | Data-correction endpoints (`facts.data_protection.data_portability` as proxy) | partial |
| 5(1)(e) | Storage limitation | `facts.data_protection.data_retention_policy.enforced_in_code == true` | pass/fail |
| 5(1)(f) | Integrity and confidentiality | `facts.encryption.at_rest.detected` AND `.in_transit.tls_enforced` AND `facts.audit_logging.pii_in_logs == []` | pass/fail |
| 5(2) | Accountability | Records of processing + audit logging present | partial |

### Lawfulness (Arts. 6, 7, 9)

| Article | Rule |
|---|---|
| 6 Lawful basis | Consent mechanism or documented legal basis per data category |
| 7 Consent conditions | `facts.data_protection.consent_mechanism.present == true` AND withdrawal endpoint evidence |
| 9 Special categories | If `facts.data_protection.phi_handling_detected == true` or sensitive-category PII fields present, require explicit consent / Art. 9(2) basis |

### Data subject rights (Arts. 12–22)

| Article | Right | Rule |
|---|---|---|
| 13, 14 | Information provided | Privacy policy present |
| 15 | Access | Data-export endpoint (`facts.data_protection.data_portability.implemented == true`) |
| 16 | Rectification | Update endpoints exist for personal data |
| 17 | Erasure ("right to be forgotten") | `facts.data_protection.right_to_erasure.implemented == true` |
| 18 | Restriction | Ability to mark records as restricted (hard to auto-detect — partial) |
| 20 | Portability | `facts.data_protection.data_portability.implemented == true` AND structured export (JSON/CSV) |
| 21 | Objection | Opt-out mechanism for processing based on legitimate interests |
| 22 | Automated decision-making | If ML/automated decisioning, evidence of human review / explanation path |

### Controller/Processor obligations (Arts. 24–32)

| Article | Rule |
|---|---|
| 25 Data protection by design and default | Minimized PII collection, encryption by default, access-by-default closed |
| 28 Processor agreements | `facts.third_party_risk.data_processing_agreements_detected == true` |
| 30 Records of processing activities | ROPA documentation presence |
| 32 Security of processing | `facts.encryption.*` + `facts.access_control.mfa_present` + `facts.audit_logging.auth_events_logged` |

### Breach notification (Arts. 33, 34)

| Article | Rule |
|---|---|
| 33 Notification to DPA within 72h | `facts.incident_response.breach_notification_procedure == true` |
| 34 Notification to data subjects | Subject-communication runbook evidence |

### DPIA (Art. 35)

| Article | Rule |
|---|---|
| 35 | DPIA documentation present when high-risk processing detected (ML, large-scale sensitive data, public monitoring) |

### International transfers (Chapter V, Arts. 44–49)

| Article | Rule |
|---|---|
| 44–46 | Evidence of SCCs, BCRs, or adequacy decision when data leaves EU/EEA. Infrastructure regions in IaC: flag if non-EU regions used for EU-origin data. |

## Dimension weighting (GDPR-specific)

| Dimension | Weight |
|---|---|
| Data Protection | 0.25 |
| Encryption | 0.18 |
| Access Control | 0.12 |
| Audit Logging | 0.10 |
| Incident Response | 0.10 |
| Data Retention / Erasure | 0.10 |
| Configuration Security | 0.05 |
| Supply Chain (processors) | 0.05 |
| Other | 0.05 |

## Report template additions

```markdown
## Role Determination

Controller | Processor | Both — {brief justification based on code and inputs}

## Personal Data Inventory

| Field | File:line | Category | Encrypted? | Subject to erasure? | Subject to portability? |
|---|---|---|---|---|---|
| email | src/models/user.py:12 | identifier | No | No | Yes |
| ... | | | | | |

## Lawful Basis Map

| Processing purpose | Lawful basis (Art. 6) | Evidence |
|---|---|---|
| Account management | Contract (6(1)(b)) | ... |
| Marketing emails | Consent (6(1)(a)) | opt-in table, consent record |

## International Transfer Analysis

{Cloud regions, data store locations, sub-processor locations}

## Data Subject Rights Readiness

| Right | Article | Implemented? | Evidence | Effort to implement |
|---|---|---|---|---|

## Breach Response Readiness

{72-hour notification procedure, DPO contact, notification template, subject-communication runbook}
```

## Common false-positives

- **"PII field detected in test fixture"** — fixture data with obvious fake values (`foo@example.com`, `123-45-6789`) is not a violation. Maintain an allowlist of test-data patterns.
- **"No consent mechanism"** — not a violation if the lawful basis is contract (Art. 6(1)(b)) or legitimate interest (Art. 6(1)(f)). Ask about lawful basis before penalizing.
- **"No right-to-erasure endpoint"** — acceptable if all PII has legal-obligation basis that prevents erasure. Document rather than fail.
- **"Logs contain `user_id`"** — not necessarily PII if it's an opaque ID. Distinguish from email/name/IP.
- **"No DPIA"** — only required for high-risk processing. Do not penalize small-scale low-risk systems.

## EU-UK notes

Since 2021, UK GDPR applies in the UK. Most articles are mirrored. Where a user says "UK only", note this and keep the same mapping. International transfers between UK ↔ EU still rely on the EU-UK adequacy decision as of the audit date — verify currency.

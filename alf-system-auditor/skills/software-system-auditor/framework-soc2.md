---
name: framework-soc2
description: SOC 2 audit mapping. Trust Service Criteria controls, evidence-to-control rules, scoring weights, report template, and false-positives for SaaS / service-organization attestations.
framework_id: soc2
framework_name: SOC 2 Trust Services Criteria
evidence_schema: "1"
---

# SOC 2 (AICPA Trust Services Criteria)

**Applies to**: Service organizations (SaaS, cloud, data processors) whose customers require a customer-trust attestation.
**Attestation types**: Type I (point-in-time design), Type II (operating effectiveness over 6–12 months).
**Auditor**: Licensed CPA firm. The AI-generated findings here are inputs to SOC 2 readiness, not a replacement for the attestation itself.

## Scope selection — which TSCs apply

SOC 2 has **five** Trust Services Criteria. "Security" (Common Criteria) is **required**; the other four are elective based on the service's scope. Ask the user which elective TSCs apply before mapping:

- **Security (CC)** — required, always audited
- **Availability (A)** — elect if customers rely on uptime commitments
- **Processing Integrity (PI)** — elect if the service processes data accurately (e.g. financial transactions, health records)
- **Confidentiality (C)** — elect if the service protects non-public business data
- **Privacy (P)** — elect if the service handles personal information at scale

Record elected TSCs in `codebase.audit_scope.soc2_tscs_elected`. Skip mapping for non-elected criteria.

## Common Criteria (CC) — always mapped

### CC1 — Control Environment
Mostly organizational policy; software audit cannot evaluate. Flag as out-of-scope for static analysis with a note.

### CC2 — Communication and Information
Same as CC1 — organizational. Out-of-scope.

### CC3 — Risk Assessment
| Control | Evidence-bundle path | Rule |
|---|---|---|
| CC3.2 Risk identification | Presence of threat-model or risk-register docs (`facts.incident_response.runbook_present`) | partial signal |

### CC4 — Monitoring Activities
| Control | Rule |
|---|---|
| CC4.1 Ongoing evaluation | `facts.vulnerability_management.scanning_in_ci == true` |

### CC5 — Control Activities
| Control | Rule |
|---|---|
| CC5.2 Change controls | `facts.change_management.branch_protection.required_reviews >= 1` AND `required_checks[]` non-empty |

### CC6 — Logical and Physical Access

| Control | Evidence-bundle path | Rule |
|---|---|---|
| CC6.1 Access software and infrastructure | `facts.access_control.rbac_present == true` | pass/fail |
| CC6.2 User registration / deprovisioning | Onboarding/offboarding evidence (often absent in code; partial-signal) | partial |
| CC6.3 Role-based access | `facts.access_control.rbac_present == true` AND no unauthenticated privileged endpoints | pass/fail |
| CC6.6 External threat protection | `facts.api_security.rate_limiting.present == true` AND WAF/CDN evidence | partial |
| CC6.7 Data transmission controls | `facts.encryption.in_transit.tls_enforced == true` AND `min_version >= "TLS 1.2"` | pass/fail |
| CC6.8 Unauthorized software protection | `facts.configuration_security.container_hygiene.unpinned_base_images == []` AND SBOM/dep scanning | partial |

### CC7 — System Operations

| Control | Rule |
|---|---|
| CC7.1 Vulnerability detection | `facts.vulnerability_management.dependency_audit_tools_present[]` non-empty |
| CC7.2 Anomaly detection | `facts.audit_logging.auth_events_logged == true` AND alerting evidence |
| CC7.3 Incident detection | `facts.incident_response.oncall_config_detected[]` non-empty |
| CC7.4 Incident response | `facts.incident_response.runbook_present == true` |
| CC7.5 Recovery | `facts.backup_recovery.rto_documented != null` |

### CC8 — Change Management

| Control | Rule |
|---|---|
| CC8.1 Change authorization, design, implementation | full branch-protection + CI gates + deployment approvals |

### CC9 — Risk Mitigation

Mostly organizational (vendor risk, BCP). Flag what's visible: third-party dependency hygiene.

| Control | Rule |
|---|---|
| CC9.2 Vendor and business-partner risk | `facts.supply_chain.unmaintained_dependencies == []` AND lockfile freshness |

## Availability (A) — if elected

| Control | Evidence path | Rule |
|---|---|---|
| A1.1 Capacity management | Evidence of autoscaling/capacity planning in IaC | partial |
| A1.2 Environmental protections | Cloud region / AZ redundancy in IaC | partial |
| A1.3 Recovery | `facts.backup_recovery.dr_tested_evidence != null` AND RTO/RPO documented |

## Processing Integrity (PI) — if elected

| Control | Rule |
|---|---|
| PI1.1 System processing — inputs | `facts.api_security.input_validation.coverage_estimate >= 0.9` |
| PI1.2 System processing — throughput | Evidence of idempotency keys, exactly-once semantics where stated |
| PI1.3 System processing — outputs | Checksums / reconciliation evidence |
| PI1.4 Data storage | Integrity controls (e.g., database constraints, audit tables) |
| PI1.5 Data logging | `facts.audit_logging.data_access_logged == true` |

## Confidentiality (C) — if elected

| Control | Rule |
|---|---|
| C1.1 Identification and protection of confidential info | `facts.encryption.at_rest.detected == true` AND classification tags |
| C1.2 Disposal | `facts.data_protection.right_to_erasure.implemented == true` for confidential data |

## Privacy (P) — if elected

Reuses GDPR/CCPA-style checks. If the user has also selected GDPR or CCPA, reuse those findings here.

| Control | Rule |
|---|---|
| P1.1 Notice | Privacy policy present (docs search) |
| P2.1 Choice and consent | `facts.data_protection.consent_mechanism.present == true` |
| P3.1 Collection | Data minimization evidence (hard to automate; partial signal from PII field inventory) |
| P4.1 Use and retention | `facts.data_protection.data_retention_policy.enforced_in_code == true` |
| P5.1 Access | `facts.data_protection.data_portability.implemented == true` |
| P6.1 Disclosure | DPA/SCC evidence (docs search) |
| P7.1 Quality | Data-correction endpoints evidence |
| P8.1 Monitoring and enforcement | Privacy incident procedures |

## Dimension weighting (SOC 2-specific)

| Dimension | Weight |
|---|---|
| Access Control | 0.18 |
| Audit Logging | 0.14 |
| Change Management | 0.12 |
| Vulnerability Mgmt | 0.11 |
| Encryption | 0.11 |
| Incident Response | 0.10 |
| Data Protection | 0.08 |
| Backup/Recovery | 0.06 |
| API Security | 0.05 |
| Secure SDLC | 0.05 |

## Type I vs Type II

This audit supports **Type I readiness** (design of controls). Type II requires observed operating effectiveness over 6–12 months, which static code analysis cannot confirm. State this explicitly in every SOC 2 report.

## Report template additions

```markdown
## TSC Coverage

Elected TSCs: {Security, Availability, ...}
Not-elected TSCs: {list with brief rationale}

## Readiness vs Attestation

This analysis supports **SOC 2 Type I readiness** assessment. It evaluates whether controls are designed and in place. It does NOT:
- Observe operating effectiveness over a period (Type II requirement)
- Substitute for a CPA firm's attestation engagement
- Evaluate organizational CC1/CC2 (control environment, communication) from code alone
```

## Common false-positives

- **Missing CC1/CC2 evidence** — expected from static code analysis. Do not report as a finding; surface as an informational note that these require policy/interview evidence.
- **No formal risk register in code** — expected. Look for ADRs and threat-model markdown files; if absent, flag medium.
- **Type II claims from code** — if user asks for a Type II readiness score, refuse and explain: operating effectiveness requires time-series evidence.

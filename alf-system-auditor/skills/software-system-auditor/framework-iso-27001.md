---
name: framework-iso-27001
description: ISO/IEC 27001:2022 audit mapping. Annex A controls and ISMS clauses with evidence-to-control rules for organizations pursuing ISMS certification.
framework_id: iso-27001
framework_name: ISO/IEC 27001:2022 Information Security Management Systems
evidence_schema: "1"
---

# ISO/IEC 27001:2022

**Applies to**: Organizations seeking or maintaining ISMS certification.
**Released**: October 2022. Organizations certified under ISO 27001:2013 must recertify against :2022 by **October 31, 2025**.
**Certification body**: Accredited conformity assessment body (not NIST/ANSI; varies by country).

ISO 27001 is an ISMS standard — a management system framework. The software audit supports the **Annex A controls** (which became Annex A of ISO 27002:2022) and surfaces evidence for clauses 6–10 of the main body.

## Structure

The standard has two parts:
1. **Clauses 4–10** — ISMS requirements (context, leadership, planning, support, operation, performance, improvement). Mostly organizational.
2. **Annex A** — 93 controls in 4 themes (Organizational, People, Physical, Technological). **Technological controls** (A.8.*) are the primary software-audit focus.

## Annex A Controls — Theme A.8 (Technological)

34 technological controls. Primary software-audit scope.

| Control | Name | Evidence rule |
|---|---|---|
| A.8.1 | User endpoint devices | Partial — endpoint policy |
| A.8.2 | Privileged access rights | `access_control.rbac_present` + least-privilege for admins |
| A.8.3 | Information access restriction | Privileged endpoints protected + data-access rules |
| A.8.4 | Access to source code | `change_management.branch_protection` + access control on repo |
| A.8.5 | Secure authentication | `mfa_present == true` for sensitive systems |
| A.8.6 | Capacity management | Autoscaling evidence in IaC |
| A.8.7 | Protection against malware | AV/endpoint agent evidence (partial) |
| A.8.8 | Management of technical vulnerabilities | `vulnerability_management.dependency_audit_tools_present[]` |
| A.8.9 | Configuration management | IaC present + baseline hardening |
| A.8.10 | Information deletion | `data_protection.right_to_erasure.implemented` |
| A.8.11 | Data masking | PII/PHI masking in logs + displays |
| A.8.12 | Data leakage prevention | DLP evidence (partial) |
| A.8.13 | Information backup | `backup_recovery.backup_config_detected` + encrypted |
| A.8.14 | Redundancy of information processing facilities | Multi-AZ/region IaC |
| A.8.15 | Logging | `audit_logging.auth_events_logged` + `data_access_logged` |
| A.8.16 | Monitoring activities | Alerting + anomaly detection |
| A.8.17 | Clock synchronization | NTP config evidence |
| A.8.18 | Use of privileged utility programs | Break-glass controls |
| A.8.19 | Installation of software on operational systems | Change control, CI/CD gates |
| A.8.20 | Network security | Firewall/SG rules, segmentation |
| A.8.21 | Security of network services | TLS, hardening |
| A.8.22 | Segregation of networks | VPC design, network policies |
| A.8.23 | Web filtering | Out-of-scope for most software audits |
| A.8.24 | Use of cryptography | `encryption.at_rest` + `.in_transit`, no weak crypto |
| A.8.25 | Secure development life cycle | SDLC: code review, testing, security gates in CI |
| A.8.26 | Application security requirements | Requirements docs, threat models |
| A.8.27 | Secure system architecture and engineering principles | ADRs, architecture docs |
| A.8.28 | Secure coding | Linting, secure-coding standards in CONTRIBUTING |
| A.8.29 | Security testing in development and acceptance | SAST/DAST in CI |
| A.8.30 | Outsourced development | Contractor access controls — org |
| A.8.31 | Separation of development, test and production environments | Env separation in IaC |
| A.8.32 | Change management | branch protection + deployment approvals |
| A.8.33 | Test information | No production data in test — PII in test-fixture checks |
| A.8.34 | Protection of information systems during audit testing | Out-of-scope |

## Annex A — Other themes (partial mapping)

- **A.5 Organizational (37 controls)**: mostly policies. Surface A.5.1 (policies), A.5.23 (information security in cloud services), A.5.31 (legal compliance), A.5.33 (protection of records).
- **A.6 People (8 controls)**: screening, NDAs, awareness. Out-of-scope for code.
- **A.7 Physical (14 controls)**: facility controls. Out-of-scope.

## Clauses 4–10 (ISMS main body)

Mostly organizational. Surface from code:
- **Clause 8 Operation** — change management, risk treatment implementation visible in code.
- **Clause 9 Performance evaluation** — monitoring, internal audit cadence.
- **Clause 10 Improvement** — corrective actions tracked in issues.

## New in 2022 (vs 2013)

Eleven **new controls** (flag explicitly if missing):
- A.5.7 Threat intelligence
- A.5.23 Information security for use of cloud services
- A.5.30 ICT readiness for business continuity
- A.7.4 Physical security monitoring (out-of-scope)
- A.8.9 Configuration management
- A.8.10 Information deletion
- A.8.11 Data masking
- A.8.12 Data leakage prevention
- A.8.16 Monitoring activities
- A.8.23 Web filtering
- A.8.28 Secure coding

## Statement of Applicability (SoA)

Every certified ISMS maintains an SoA — a document listing all Annex A controls, whether each is applied, and justification. The audit does not produce the SoA, but flags whether it exists: look for `SoA.*`, `Statement-of-Applicability*`, or similar documents. Record in report.

## Dimension weighting (ISO 27001)

| Dimension | Weight |
|---|---|
| Access Control | 0.14 |
| Encryption | 0.12 |
| Audit Logging | 0.10 |
| Secure SDLC | 0.10 |
| Configuration Security | 0.10 |
| Vulnerability Mgmt | 0.10 |
| Data Protection | 0.08 |
| Change Management | 0.08 |
| Backup/Recovery | 0.08 |
| Incident Response | 0.06 |
| Supply Chain | 0.04 |

## Report template additions

```markdown
## ISMS Context

- Target certification: yes (new) / yes (recertification) / pre-certification readiness
- Current stage: Stage 1 readiness / Stage 2 prep / Surveillance / Recertification

## Annex A Applicability Summary (SoA signal)

| Theme | Total | Applied (evidence) | Partial | Not Applied | N/A |
|---|---|---|---|---|---|
| A.5 Organizational | 37 | ... | ... | ... | ... |
| A.6 People | 8 | ... | ... | ... | ... |
| A.7 Physical | 14 | N/A | N/A | N/A | 14 |
| A.8 Technological | 34 | ... | ... | ... | ... |

## New 2022 Controls — Readiness

| Control | Name | Status |
|---|---|---|
| A.5.7 Threat intelligence | ... |
| A.8.9 Configuration management | ... |
| ... | ... |

## Nonconformity Classification

- Major nonconformity (certification-blocking): {list}
- Minor nonconformity: {list}
- Observation: {list}
```

## Common false-positives

- **Expecting all Annex A controls applied** — ISO 27001 allows justified exclusions. Missing a control is not automatically a nonconformity if the SoA excludes it with valid rationale. Ask for the SoA before failing controls.
- **Penalty for physical controls (A.7)** — out-of-scope for code.
- **Nonconformity vs observation** — only auditors classify. Use our severity as guidance with explicit note.

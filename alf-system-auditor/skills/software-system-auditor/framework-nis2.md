---
name: framework-nis2
description: NIS2 Directive audit mapping. Risk management measures under Article 21 for EU essential and important entities.
framework_id: nis2
framework_name: Network and Information Security Directive (EU) 2022/2555
evidence_schema: "1"
---

# NIS2 Directive

**Applies to**: 18 EU sectors (Annex I essential entities, Annex II important entities), plus medium-and-large entities within these sectors. National transposition was due by October 17, 2024.
**Penalties**: Essential entities up to €10M or 2% of global revenue; important entities up to €7M or 1.4%. Management bodies **personally liable** for non-compliance.
**Regulators**: Member state competent authorities + CSIRT network + EU CyCLONe.

## Scope

Confirm with user:
- Entity type: essential or important?
- Sector (from Annex I/II)?
- Transposition status in the member state?

Record in `codebase.audit_scope.nis2_entity_type` and `.nis2_sector`.

## Article 21 — Cybersecurity risk management measures

The core of NIS2 for software audit. Ten measures required:

| Measure | Evidence rule |
|---|---|
| 21(2)(a) Policies on risk analysis and information system security | Risk register, security policy |
| 21(2)(b) Incident handling | `incident_response.runbook_present == true` + incident tracking |
| 21(2)(c) Business continuity + backup + crisis management | `backup_recovery.*` + DR plan + crisis comms |
| 21(2)(d) Supply chain security | Vendor registry + `supply_chain.*` |
| 21(2)(e) Security in network and information systems acquisition, development, and maintenance (including vulnerability handling and disclosure) | SDLC + `vulnerability_management.*` + vuln-disclosure policy (security.txt) |
| 21(2)(f) Policies and procedures to assess effectiveness | Scanning + pen tests + audit cadence |
| 21(2)(g) Basic cyber hygiene + training | Awareness training — org |
| 21(2)(h) Policies on use of cryptography and, where appropriate, encryption | `encryption.at_rest.*` + `.in_transit.*` |
| 21(2)(i) Human resources security, access control policies, asset management | Access control + onboarding/offboarding + asset inventory |
| 21(2)(j) Use of MFA or continuous authentication, secured voice/video/text, secured emergency communications | `access_control.mfa_present == true` + secured comms |

## Article 21(3) — Supply chain focus

NIS2 explicitly emphasizes supply chain security. Essential requirements:
- **Supplier registry** with risk classification
- **Supplier selection criteria** based on cybersecurity
- Contractual flow-downs (cybersecurity, incident reporting, audit rights)
- Vulnerability management extended to suppliers (periodic supplier inspection)

Map to:
- `supply_chain.sbom` — SBOM for supplier visibility
- `supply_chain.unmaintained_dependencies[]` — unmaintained dependency risk
- `third_party_risk.vendor_registry_path` — vendor registry

## Article 23 — Reporting obligations

**Timeline**:
- Early warning: within 24 hours of becoming aware of significant incident
- Incident notification: within 72 hours (updated assessment)
- Final report: within 1 month

Evidence:
- Incident severity classification criteria defined
- Notification procedure to national CSIRT/competent authority
- Cross-border notification workflow (Art. 23(5))

## Article 24 — Use of European cybersecurity certification schemes

Optionally requires entities to use products/services certified under EU cybersecurity certification schemes (EUCC, EU5G, etc.). Surface: evidence of certified components in use (partial).

## Management responsibility

Article 20 makes management bodies approve and oversee cybersecurity risk management measures, and undergo training. Cannot be assessed from code alone. Note in report as out-of-scope, recommend board-level attestation.

## Dimension weighting (NIS2)

| Dimension | Weight |
|---|---|
| Supply Chain | 0.20 |
| Access Control + MFA | 0.15 |
| Incident Response | 0.15 |
| Vulnerability Mgmt | 0.12 |
| Backup/Recovery + Business Continuity | 0.10 |
| Audit Logging | 0.10 |
| Encryption | 0.08 |
| Secure SDLC | 0.05 |
| Other | 0.05 |

## Report template additions

```markdown
## Entity Classification

- Essential or important entity: {essential | important}
- Sector (Annex I or II): {...}
- Member state of establishment: {...}
- Main establishment (for multi-state): {...}

## Article 21 Measure Dashboard

| Measure | Implemented | Partial | Not Implemented | Evidence |
|---|---|---|---|---|
| 21(2)(a) Risk analysis policies | ... | ... | ... | ... |
| ... | | | | |

## Supply Chain Assessment (Art. 21(2)(d), 21(3))

- Supplier registry: yes/no
- Critical suppliers identified and assessed: yes/no
- Flow-down contractual clauses: yes/no
- Unmaintained dependencies posing supply-chain risk: {count and list}
- SBOM maintained: yes/no

## Reporting Readiness (Art. 23)

- 24h early-warning procedure: yes/no
- 72h notification procedure: yes/no
- 1-month final-report template: yes/no
- Cross-border CSIRT coordination: yes/no

## Management Body Responsibility (Art. 20)

Out-of-scope for code audit. Recommend board attestation + training records.
```

## Common false-positives

- **"No SBOM"** — essential/important entities should maintain SBOM for critical suppliers, but SBOM for every dependency is best-practice not strict requirement.
- **MFA on all user access** — required for workforce access to in-scope services. End-customer MFA may or may not be mandatory depending on entity type.
- **Penalty for not using EU certification schemes** — Art. 24 allows member states to require this, not strict in all states.

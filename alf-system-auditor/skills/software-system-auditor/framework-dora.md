---
name: framework-dora
description: DORA audit mapping. ICT risk management requirements for EU financial entities and their critical ICT third-party providers.
framework_id: dora
framework_name: Digital Operational Resilience Act (EU) 2022/2554
evidence_schema: "1"
---

# DORA (Digital Operational Resilience Act)

**Applies to**: 20 categories of EU financial entities (banks, insurers, investment firms, payment institutions, crypto-asset service providers, crowdfunding platforms, etc.) AND their critical ICT third-party service providers (CTPPs).
**Effective**: January 17, 2025 (no transition period).
**Penalties**: Up to 2% of total annual worldwide turnover; CTPPs up to 1% of daily worldwide turnover.
**Regulators**: Competent authorities under the ESAs (EBA, EIOPA, ESMA) + joint oversight framework for CTPPs.

## Scope

Confirm with user:
- Is the organization a regulated EU financial entity? If yes, which type?
- Or a critical ICT third-party provider?

Record in `codebase.audit_scope.dora_entity_type`.

## Key pillars

DORA has five pillars. Software audit contributes primarily to Pillars 1, 3, and 5.

### Pillar 1 — ICT risk management (Arts. 5–16)

| Article | Requirement | Evidence rule |
|---|---|---|
| Art. 5 | ICT risk management framework documented, reviewed at least annually | Risk register + annual review evidence |
| Art. 6 | ICT governance + responsibility of management body | Board-level governance docs |
| Art. 7 | ICT systems, protocols, tools | Architecture docs, ADRs |
| Art. 8 | Identification of ICT risks | Threat model, risk assessment |
| Art. 9 | Protection and prevention | Access control, encryption, network security, cryptographic controls — maps to most of the evidence bundle |
| Art. 9(4)(a) | Policies/procedures for ICT security | Security policy presence |
| Art. 9(4)(c) | Access control policies | `rbac_present` + `mfa_present` |
| Art. 9(4)(d) | Cryptographic controls | `encryption.at_rest` + `.in_transit` |
| Art. 9(4)(e) | ICT change management | `branch_protection` + deployment gates |
| Art. 10 | Detection (logging, incident detection) | `audit_logging.*` + alerting |
| Art. 11 | Response and recovery | `incident_response.runbook_present` + DR |
| Art. 12 | Backup policies and procedures | `backup_recovery.backup_config_detected` + encrypted + tested |
| Art. 13 | Learning and evolving | Postmortem templates + lessons learned |
| Art. 14 | Communication | Internal/external incident comms |

### Pillar 2 — ICT-related incident management, classification, reporting (Arts. 17–23)

Mostly organizational but surface:
- Incident classification criteria present
- Mandatory reporting procedure (4-hour initial notification for major incidents)
- Threat-led penetration testing results (TLPT) integration

### Pillar 3 — Digital operational resilience testing (Arts. 24–27)

| Article | Requirement | Evidence rule |
|---|---|---|
| Art. 24 | Testing programme | Evidence of regular testing (pen tests, scans) |
| Art. 25 | Standard tests | Vulnerability assessments, scenario-based testing, end-to-end testing |
| Art. 26 | Threat-Led Penetration Testing (TLPT) for significant institutions | TLPT evidence; every 3 years min |
| Art. 27 | Testers' requirements | Independent testers |

### Pillar 4 — ICT third-party risk management (Arts. 28–44)

| Article | Rule |
|---|---|
| Art. 28 | ICT TPR strategy + register | Vendor registry, third-party dependency inventory |
| Art. 29 | Pre-contractual due diligence | Vendor assessment docs |
| Art. 30 | Key contractual provisions | DPA/MSA with exit rights, termination triggers, audit rights, data location |
| Art. 31 | Critical ICT services | List of critical services + concentration risk |
| Art. 32–44 | CTPP oversight framework | Out-of-scope for non-CTPPs |

### Pillar 5 — Information sharing (Art. 45)

Voluntary threat intelligence sharing. Surface: threat-intel feed integration evidence.

## Contractual flow-down

For ICT third-party contracts, DORA requires specific contractual provisions:
- Clear and complete description of services
- Service location (data, processing)
- Service levels (availability, performance)
- Assistance to financial entity in incident management
- Cooperation with competent authorities
- Termination rights
- Exit strategy requirements

Audit: search for DPA/MSA templates and flag missing clauses (organizational signal, partial).

## Dimension weighting (DORA)

| Dimension | Weight |
|---|---|
| ICT Risk Management (governance + identify + protect) | 0.25 |
| Audit Logging | 0.12 |
| Incident Response | 0.15 |
| Backup/Recovery (operational resilience) | 0.12 |
| Supply Chain (ICT TPR) | 0.15 |
| Vulnerability Mgmt + Testing | 0.10 |
| Encryption | 0.06 |
| Access Control | 0.05 |

## Report template additions

```markdown
## Entity Classification

- Financial entity type: {bank, insurer, investment firm, payment institution, ... }
- Significant institution (TLPT applicable): yes/no
- Critical ICT third-party provider (CTPP designation): yes/no

## Pillar-by-Pillar Dashboard

| Pillar | Controls Assessed | Met | Partial | Not Met |
|---|---|---|---|---|
| 1 — ICT risk management | ... | ... | ... | ... |
| 2 — Incident management/reporting | ... | ... | ... | ... |
| 3 — Operational resilience testing | ... | ... | ... | ... |
| 4 — ICT third-party risk | ... | ... | ... | ... |
| 5 — Information sharing | ... | ... | ... | ... |

## ICT Third-Party Register

{List of critical ICT dependencies from the supply chain facts, enriched with concentration risk assessment}

## Testing Evidence

- Vulnerability scans: {frequency, tool}
- Pen tests: {frequency, last date}
- TLPT (if applicable): {last execution, methodology}

## Incident Reporting Readiness

- 4-hour initial notification procedure: yes/no
- Intermediate/final reports: templates + evidence

## Exit Strategy

{For critical ICT providers: contract exit clauses, data-return procedures, portability}
```

## Common false-positives

- **Missing TLPT** — only required for significant institutions. Confirm significance threshold before penalizing.
- **CTPP-only clauses on vendor list not present** — only required if vendor is designated as critical by the financial entity.
- **Penalty for missing ISO 27001** — DORA does not mandate ISO 27001 specifically, though it is often used as the control framework basis.

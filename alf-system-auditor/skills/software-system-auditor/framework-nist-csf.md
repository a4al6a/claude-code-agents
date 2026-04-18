---
name: framework-nist-csf
description: NIST Cybersecurity Framework 2.0 audit mapping across Govern, Identify, Protect, Detect, Respond, Recover functions with subcategory-to-evidence rules.
framework_id: nist-csf
framework_name: NIST Cybersecurity Framework 2.0
evidence_schema: "1"
---

# NIST CSF 2.0

**Applies to**: Recommended for any organization; referenced in US federal agency cybersecurity programs. Voluntary but widely adopted.
**Released**: February 26, 2024.
**Key change from 1.1**: Addition of **Govern (GV)** function (risk management strategy, supply chain, oversight).

NIST CSF is an *outcomes* framework — it describes what you should achieve, not how. Evidence mapping shows whether outcomes are addressed technically. Organizational outcomes (policy, risk appetite) are harder to assess from code alone and should be marked partial.

## Six functions

### GV — Govern

| Category | Outcome | Evidence rule |
|---|---|---|
| GV.OC | Organizational context | Mission/scope docs — partial |
| GV.RM | Risk management strategy | Risk register or threat-model docs |
| GV.RR | Roles, responsibilities, authorities | CODEOWNERS, RACI, ownership docs |
| GV.PO | Policy | Security policy, code of conduct present |
| GV.OV | Oversight | Review meeting cadence docs |
| GV.SC | Supply chain risk management | `facts.supply_chain.sbom.generated` + vendor registry + `dependency_signing` |

### ID — Identify

| Category | Rule |
|---|---|
| ID.AM Asset management | Asset/service inventory; IaC reflects all infra |
| ID.RA Risk assessment | Threat models, risk register |
| ID.IM Improvement | Lessons-learned / postmortem template (`facts.incident_response.post_mortem_template`) |

### PR — Protect

| Category | Rule |
|---|---|
| PR.AA Identity mgmt, authentication, access control | `access_control.rbac_present` + `mfa_present` + session mgmt |
| PR.AT Awareness and training | Out-of-scope for code audit |
| PR.DS Data security | `encryption.at_rest.detected` + `encryption.in_transit.tls_enforced` + data classification |
| PR.PS Platform security | Configuration hardening, `configuration_security.container_hygiene` |
| PR.IR Technology infrastructure resilience | Backup + DR + RTO/RPO |

### DE — Detect

| Category | Rule |
|---|---|
| DE.CM Continuous monitoring | `audit_logging.auth_events_logged` + SIEM/alerting evidence |
| DE.AE Adverse event analysis | Alert-routing evidence |

### RS — Respond

| Category | Rule |
|---|---|
| RS.MA Incident management | `incident_response.runbook_present` |
| RS.AN Analysis | Incident investigation procedure |
| RS.CO Communication | Stakeholder comms templates |
| RS.MI Mitigation | Containment runbooks |

### RC — Recover

| Category | Rule |
|---|---|
| RC.RP Recovery plan | DR plan, RTO/RPO documented |
| RC.CO Communication | Recovery comms templates |

## Implementation Tiers

NIST CSF defines four tiers describing the rigor of risk management practices:

| Tier | Name | Characteristics |
|---|---|---|
| 1 | Partial | Ad hoc, reactive |
| 2 | Risk Informed | Risk-aware but not enterprise-wide |
| 3 | Repeatable | Formalized, consistent |
| 4 | Adaptive | Continuously improved |

The audit assesses achieved tier per function. Static code analysis can mostly distinguish Tier 1 vs Tier 3+. Distinguishing 3 vs 4 typically requires interviews.

## Profile (Current vs Target)

Report a **Current Profile** (what the evidence shows) and invite the user to express a **Target Profile**. Gaps become the remediation plan.

## Dimension weighting (NIST CSF)

CSF is function-weighted rather than dimension-weighted. Use:

| Function | Weight |
|---|---|
| GV Govern | 0.20 |
| ID Identify | 0.15 |
| PR Protect | 0.25 |
| DE Detect | 0.15 |
| RS Respond | 0.15 |
| RC Recover | 0.10 |

Within each function, aggregate subcategory scores evenly.

## Optional deeper mapping — NIST SP 800-53 Rev 5

If the user wants control-level depth (federal/FedRAMP contexts), also map to 800-53 families: AC, AU, CM, IA, RA, SC, SI, SR. Use the control IDs from `cross-framework-mapping.md`.

## Report template additions

```markdown
## Current Profile

| Function | Tier Achieved | Key Gaps |
|---|---|---|
| Govern | 2 | Supply chain risk mgmt policy missing |
| Identify | 3 | ... |
| Protect | 3 | MFA coverage partial |
| Detect | 2 | No SIEM integration |
| Respond | 3 | ... |
| Recover | 2 | DR untested |

## Target Profile

{User-stated target tier per function, and key outcomes to reach it}

## Profile Gap Analysis

{Diff between Current and Target, prioritized}
```

## Common false-positives

- **Govern function "not evidenced"** — govern outcomes are mostly organizational; missing policy files in the repo doesn't mean no program exists. Ask the user.
- **Penalty for missing AT (awareness training)** — not assessable from code. Flag informational.
- **Tier 4 claim from code alone** — code rarely proves continuous improvement; always cap at Tier 3 unless there is time-series evidence (git history of iterative threat models, recurring audits).

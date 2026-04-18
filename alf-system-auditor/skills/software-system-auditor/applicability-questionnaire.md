---
name: applicability-questionnaire
description: Pre-scope questionnaire that asks the user data-driven questions about their system and recommends which regulatory frameworks apply, before the user confirms final scope
---

# Applicability Questionnaire

Runs in Phase 1 (SCOPE) before framework selection. Asks a short, structured set of questions about the system and produces a *recommended* framework set. The user then confirms, removes, or adds frameworks — they always have the final say.

## Why this exists

Users who own codebases often do not know that:
- Processing EU personal data → GDPR applies regardless of where the company is
- Handling any card data → PCI DSS applies (even if a processor handles the raw PAN)
- Processing PHI in the US → HIPAA applies
- Being a listed US company → SOX applies to the IT general controls under financial reporting scope
- Selling into EU critical infrastructure → NIS2 applies
- Serving US federal agencies → FedRAMP applies; often CMMC if DoD-related

This questionnaire encodes that knowledge so the agent recommends frameworks rather than asking the user to already know them.

## Questionnaire

Ask these via `AskUserQuestion`. All questions are multi-select or yes/no. Group them; do not ask all 12 at once — ask in the order below, stopping early if answers are unambiguous.

### Block A — Data types processed

1. **Does the system store, process, or transmit any of these data types?** (multi-select)
   - Payment card numbers (PAN, credit/debit card data)
   - Protected Health Information (PHI) — US healthcare data
   - EU / EEA / UK resident personal data (names, emails, behavioural data, IPs)
   - California resident personal data
   - US federal Controlled Unclassified Information (CUI) or Federal Contract Information (FCI)
   - Financial-reporting data for a publicly listed company
   - None of the above

### Block B — Organizational context

2. **Which describes your organization?** (multi-select)
   - US publicly listed company (or subsidiary of one)
   - EU-regulated financial entity (bank, insurer, payment institution, crypto-asset service provider, etc.)
   - Operator of essential services or digital infrastructure in the EU (per NIS2 Annexes)
   - US Department of Defense contractor or subcontractor
   - Vendor selling cloud services to US federal agencies
   - Service organization seeking customer trust attestations (SaaS, B2B data processor)
   - None of the above

### Block C — Geography / customer base

3. **Where are your end users or customers located?** (multi-select)
   - European Union / European Economic Area / United Kingdom
   - California (US)
   - United States (other than California)
   - Global (no geographical restriction)
   - Internal/corporate only

### Block D — Optional deepening (ask only if ISO 27001 or NIST are likely candidates)

4. **Are you seeking or maintaining any of these certifications/attestations?** (multi-select)
   - ISO 27001 certification
   - SOC 2 Type I / Type II
   - NIST CSF alignment for risk management
   - None / unsure

## Recommendation logic

Apply these rules in order. Accumulate a set; deduplicate at the end.

| Input answer | Add framework | Rationale |
|---|---|---|
| Block A: Payment card numbers | **PCI DSS 4.0** | Any PAN handling puts the system in CDE scope |
| Block A: PHI | **HIPAA** | Technical safeguards required for ePHI |
| Block A: EU/EEA/UK personal data | **GDPR** | Processing territorial scope (Art. 3) |
| Block A: California personal data | **CCPA/CPRA** | Consumer rights and audit requirements |
| Block A: CUI or FCI | **CMMC 2.0** (Level 1 or Level 2 depending on data type) | DoD acquisition requirement |
| Block A: Financial-reporting data | **SOX** | ITGC scope under Section 404 |
| Block B: US publicly listed | **SOX** | Section 404 ITGC scope |
| Block B: EU financial entity | **DORA** | Mandatory since Jan 17, 2025 |
| Block B: EU essential services | **NIS2** | Mandatory for in-scope sectors |
| Block B: DoD contractor | **CMMC 2.0** | Phased requirement from Nov 2025 |
| Block B: Federal cloud vendor | **FedRAMP** (Moderate/High based on data sensitivity) | Required for federal customers |
| Block B: Service organization seeking attestation | **SOC 2** | Customer trust attestation pattern |
| Block D: Seeking ISO 27001 | **ISO 27001:2022** | Certification target |
| Block D: Seeking SOC 2 | **SOC 2** (add if not already) | Attestation target |
| Block D: NIST CSF alignment | **NIST CSF 2.0** | Voluntary alignment |

If the accumulated set is empty, default recommendation is **OWASP-only** (direct the user to `alf-security-assessor` rather than `alf-system-auditor`, since there is no regulatory target).

## Confirmation prompt

After computing the recommendation, present it to the user like:

```
Based on your answers, these frameworks likely apply:
  ✓ GDPR — you process EU personal data
  ✓ SOC 2 — you are pursuing a customer-trust attestation
  ✓ ISO 27001 — you are seeking certification

These are likely NOT applicable (skipping):
  ✗ HIPAA, PCI DSS, SOX, FedRAMP, DORA, NIS2, CMMC, CCPA, NIST CSF

Confirm this scope? You can add/remove frameworks before the audit runs.
```

Use `AskUserQuestion` one more time to let the user confirm, add, or remove. Persist the final scope in the evidence bundle metadata under `codebase.audit_scope`.

## When a framework is ambiguous

Some frameworks have fuzzy thresholds (CCPA revenue thresholds, SOC 2 elective scope). Do not attempt to apply threshold logic — recommend the framework anyway with a note:

```
Note on CCPA/CPRA: applicability depends on revenue thresholds and data volume.
If you are a small business below the revenue threshold, you may remove this before confirming.
```

## Output contract

The questionnaire produces:

```json
{
  "recommended_frameworks": ["gdpr", "soc2", "iso-27001"],
  "user_confirmed_frameworks": ["gdpr", "soc2"],
  "questionnaire_answers": {
    "block_a": ["eu_personal_data"],
    "block_b": ["service_org_attestation"],
    "block_c": ["eu"],
    "block_d": ["soc2"]
  },
  "rationale_per_framework": {
    "gdpr": "Block A: EU/EEA/UK personal data",
    "soc2": "Block B: Service organization seeking attestation; Block D: Pursuing SOC 2"
  }
}
```

This JSON is embedded in the evidence bundle as `codebase.audit_scope` and surfaced in every framework report's methodology section so reviewers can audit the scope decision itself.

## Subagent mode

In subagent mode (Task tool invocation), skip `AskUserQuestion`. Instead, return:

```json
{
  "CLARIFICATION_NEEDED": true,
  "questions": [
    {"id": "block_a", "prompt": "What data types does the system handle?", "options": [...]},
    {"id": "block_b", "prompt": "What describes your organization?", "options": [...]}
  ]
}
```

Let the orchestrator collect answers and re-invoke with them bundled in the task prompt.

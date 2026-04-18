---
name: framework-ccpa
description: CCPA/CPRA audit mapping. California consumer privacy rights with evidence-to-control rules for systems processing California residents' personal information.
framework_id: ccpa
framework_name: California Consumer Privacy Act / California Privacy Rights Act
evidence_schema: "1"
---

# CCPA/CPRA

**Applies to**: For-profit businesses meeting any of: (a) >$25M annual revenue, (b) buys/sells/shares personal information of 100,000+ California consumers/households annually, or (c) derives 50%+ revenue from selling/sharing California consumers' personal information.
**Effective**: CCPA original effective Jan 1, 2020. CPRA amendments effective Jan 1, 2023. Annual audit requirement phased through 2028.
**Regulator**: California Privacy Protection Agency (CPPA) + California Attorney General.
**Documentation retention**: 5 years. Annual certification signed under penalty of perjury.

## Scope

Confirm applicability with user before mapping (thresholds (a)/(b)/(c) above). If none apply, the law does not apply. Record in `codebase.audit_scope.ccpa_threshold`.

## Consumer rights

| Right | Rule |
|---|---|
| Right to Know | Data-access endpoint (similar to GDPR Art. 15) — `facts.data_protection.data_portability.implemented` as proxy |
| Right to Delete | `facts.data_protection.right_to_erasure.implemented == true` |
| Right to Correct | Update endpoints for personal data |
| Right to Opt-Out (Sale/Share) | "Do Not Sell or Share" link evidence + opt-out flag in data model |
| Right to Limit Use of Sensitive PI | Opt-out for sensitive-PI processing (SSN, geolocation, biometric, genetic, precise location, health, sexual orientation, union membership) |
| Right to Non-Discrimination | No penalization for exercising rights — evidence of equal service |
| Right to Data Portability | Structured-format export |

## Categories of personal information (CPRA-specific)

CPRA introduces **Sensitive Personal Information (SPI)** with stronger protections:
- Government IDs (SSN, driver's license, passport)
- Account credentials
- Precise geolocation
- Racial/ethnic origin
- Religious/philosophical beliefs
- Union membership
- Contents of mail, email, text (not addressed to the business)
- Genetic data
- Biometric data for identification
- Health data
- Sex life / sexual orientation

Check whether the system handles any SPI category and flag each category with its location.

## Annual audit requirements (CPRA § 1798.185(a)(15)(A))

Required components per the audit regulation (finalized 2024, phased starting 2026):
- Thorough, evidence-based audit, not primarily management assertions
- Identify gaps that increase risk of unauthorized access
- Annual certification signed under penalty of perjury
- 5-year record retention
- Independence of the auditor

## Controls

### Access controls
- `access_control.rbac_present == true` for SPI
- `access_control.mfa_present == true` for SPI access

### Encryption
- SPI encrypted at rest
- `encryption.in_transit.tls_enforced == true`

### Data minimization
- Collection proportional to disclosed purpose
- PII field inventory cross-referenced with privacy notice

### Consumer request handling
Operational controls — verify technical enablers:
- Authentication method for consumer-rights requests (verifying the requester is the consumer)
- Timeline compliance (respond to access/delete requests within 45 days; extendable to 90)
- Two-step confirmation for deletion (optional but recommended)

### Vendor/service-provider controls
- DPA-like contracts with service providers
- Flow-through of consumer opt-outs to service providers

### Children's data
- If service targets or knowingly collects from consumers under 16, opt-in consent required for sale/share

## Leverage other frameworks

CPRA regulations allow audits conducted for other frameworks (notably NIST CSF 2.0) to satisfy CCPA audit requirements if they **fully** cover CCPA's specific scope. If the user has also selected NIST CSF, note cross-leverage in the report.

## Dimension weighting (CCPA)

| Dimension | Weight |
|---|---|
| Data Protection | 0.30 |
| Access Control | 0.15 |
| Encryption | 0.12 |
| Audit Logging | 0.10 |
| Consent/Opt-Out mechanisms | 0.12 |
| Vendor management | 0.10 |
| Incident Response | 0.06 |
| Other | 0.05 |

## Report template additions

```markdown
## Applicability Confirmation

- Revenue threshold (a) met: yes/no
- Volume threshold (b) met: yes/no
- Selling/sharing threshold (c) met: yes/no

## Personal Information / Sensitive PI Inventory

| Field | File:line | Category | SPI? | Encrypted | Sold/Shared |
|---|---|---|---|---|---|
| ... | | | | | |

## Consumer Rights Readiness

| Right | Implemented? | Response timeline evidence | Identity verification | Evidence |
|---|---|---|---|---|
| Know | ... | ... | ... | ... |
| Delete | ... | ... | ... | ... |
| Correct | ... | ... | ... | ... |
| Opt-Out of Sale/Share | ... | ... | ... | ... |
| Limit Use of SPI | ... | ... | ... | ... |

## Privacy Notice Conformance

{Privacy policy present? Lists categories of PI collected, sources, purposes, third parties, rights, contact}

## Service Provider / Contractor Flow-Through

{Contracts with service providers forbid sale/sharing and require opt-out honoring}
```

## Common false-positives

- **Small business** — if below all thresholds, CCPA does not apply. Confirm before auditing.
- **"Sale" interpretation** — "sale" is broadly defined to include disclosure for monetary or "other valuable consideration". Ad exchange cookies may count as sale. Flag conservatively, note ambiguity.
- **"Do Not Sell" link missing but nothing is sold/shared** — not a violation, but recommend implementing anyway for customer trust.
- **Children's data** — do not assume presence based on age fields alone; verify whether the business targets children.

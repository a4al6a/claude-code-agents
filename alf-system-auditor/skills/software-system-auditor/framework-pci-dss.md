---
name: framework-pci-dss
description: PCI DSS 4.0 audit mapping. Twelve requirements with evidence-to-control rules for systems handling payment card data (CDE scope).
framework_id: pci-dss
framework_name: PCI DSS 4.0 (Payment Card Industry Data Security Standard)
evidence_schema: "1"
---

# PCI DSS 4.0

**Applies to**: Any entity that stores, processes, or transmits cardholder data (CHD) or sensitive authentication data (SAD).
**Effective**: PCI DSS v4.0.1 fully in force since March 31, 2025. v3.2.1 retired.
**Compliance validation**: Merchant Level (1–4) or Service Provider (Level 1–2) determines whether a Qualified Security Assessor (QSA) audit (ROC) or Self-Assessment Questionnaire (SAQ) applies.

## Scope note — Cardholder Data Environment (CDE)

PCI DSS applies only to systems **in** the CDE and those **connected to** the CDE. Out-of-scope systems are truly out-of-scope if segmented. Ask:
1. Does the system store, process, or transmit PAN (Primary Account Number)?
2. Is the system network-connected to any system that does?
3. Does the system provide security services to the CDE (auth, log aggregation, etc.)?

Record in `codebase.audit_scope.pci_cde_scope = cde | connected | security-service | out-of-scope`.

If out-of-scope, refuse to run this mapping and recommend network-segmentation validation instead.

## SAD handling — special rule

**Sensitive Authentication Data** (full track, CVV/CVC, PIN, PIN block) **must never be stored after authorization**, even encrypted. Detection rule: search for any persistence of these fields in DB models or log output. Any match is **Critical**.

## The 12 Requirements

### Requirement 1 — Network security controls

| Sub-req | Rule |
|---|---|
| 1.2 Config standards for network devices | IaC firewall rules present |
| 1.3 Restrict inbound/outbound to CDE | `facts.configuration_security.overly_permissive_network_rules == []` for CDE subnets |
| 1.4 Install NSCs between trusted/untrusted networks | Segmentation controls present |

### Requirement 2 — Secure configurations

| Sub-req | Rule |
|---|---|
| 2.2 Config standards | IaC hardening baselines present |
| 2.3 No default credentials | `facts.configuration_security.default_credentials == []` |
| 2.2.7 Encrypt non-console admin access | TLS on admin interfaces |

### Requirement 3 — Protect stored account data

| Sub-req | Rule |
|---|---|
| 3.2 No SAD stored post-authorization | **Critical if any SAD field persisted** |
| 3.3 Masking when displayed | Masked PAN display in templates/API responses |
| 3.5 Render PAN unreadable | `facts.encryption.at_rest.detected == true` with AES-256 or tokenization |
| 3.5.1 Disk-level encryption no longer sufficient (4.0) | must have DB/column/field encryption |
| 3.6 Cryptographic key management | `facts.encryption.at_rest.key_management` != "hardcoded" |
| 3.7 Key lifecycle | Rotation evidence |

### Requirement 4 — Transmission encryption

| Sub-req | Rule |
|---|---|
| 4.1 Strong crypto on transmission over open/public networks | `tls_enforced == true` AND `min_version >= TLS 1.2` |
| 4.2 No insecure protocols (SSL, early TLS) | `encryption.in_transit.weak_ciphers_present == false` |

### Requirement 5 — Malware

| Sub-req | Rule |
|---|---|
| 5.2 Anti-malware deployed | Endpoint agent evidence (partial — org-level) |
| 5.3 Periodically evaluated for commonly exploited systems | |

### Requirement 6 — Secure development

| Sub-req | Rule |
|---|---|
| 6.2 Bespoke software developed securely | SDLC: code review, CI security scanning |
| 6.3.1 Vulnerabilities identified | `vulnerability_management.scanning_in_ci == true` |
| 6.3.2 Inventory of bespoke and custom software | SBOM present |
| 6.3.3 Patch critical/high in <30 days | Patch cadence evidence |
| 6.4.1 Public-facing web apps protected | WAF evidence OR 6.4.2 application-level |
| 6.4.3 Scripts on payment pages inventoried and integrity-verified (new in 4.0) | CSP + SRI on payment pages |
| 6.5 Secure coding practices for in-scope code | Input validation, output encoding, auth checks in code |

### Requirement 7 — Restrict access

| Sub-req | Rule |
|---|---|
| 7.2 Access control based on need-to-know | `rbac_present == true` with minimum-privilege roles |
| 7.3 Access control system for all system components | All CDE services behind central auth |

### Requirement 8 — Identify users

| Sub-req | Rule |
|---|---|
| 8.3 Strong auth for all users into CDE | **MFA required** — `mfa_present == true` for all CDE access (4.0 expanded from admin-only) |
| 8.2.2 Session timeout | `session_management.maxAge` reasonable |
| 8.3.6 Password complexity | `password_policy` min_length ≥ 12, complexity OR MFA |

### Requirement 9 — Physical access

Out-of-scope for code audit. Note in report.

### Requirement 10 — Logging and monitoring

| Sub-req | Rule |
|---|---|
| 10.2 Audit events | `auth_events_logged` AND `data_access_logged` AND changes to crypto keys |
| 10.3 Log protection | `log_tamper_protection == true` |
| 10.4 Automated log review (SIEM) | Log-forwarding / SIEM integration evidence |
| 10.5 Log retention | 12 months total, 3 months immediately available |
| 10.7 Failure of critical security control systems detected | Alerting evidence |

### Requirement 11 — Test security of systems

| Sub-req | Rule |
|---|---|
| 11.3 Vulnerability scans | internal (11.3.1) + external (11.3.2) quarterly — partial signal from `scanning_in_ci` |
| 11.4 Penetration testing | Annual + after significant change — evidence of pen test reports |
| 11.5 Intrusion detection | IDS/file-integrity evidence |
| 11.6.1 Change-and-tamper detection on payment pages (new in 4.0) | SRI + CSP reporting |

### Requirement 12 — Policy

Mostly org-level. Software audit surfaces: Incident Response Plan (12.10) — `facts.incident_response.runbook_present == true`.

## 4.0 "new" requirements to emphasize

- **6.4.3** — payment-page script inventory + integrity (SRI, CSP)
- **11.6.1** — change-and-tamper detection on payment pages
- **8.3** — MFA expanded from admin-only to **all access into CDE**
- **3.5.1** — disk-level encryption insufficient; column/field encryption required
- **12.5.2.1** — scope confirmation documentation

Flag any of these gaps as High if otherwise compliant on earlier requirements.

## Dimension weighting (PCI DSS-specific)

| Dimension | Weight |
|---|---|
| Encryption | 0.20 |
| Access Control | 0.15 |
| Audit Logging | 0.15 |
| Vulnerability Mgmt | 0.15 |
| Secure SDLC | 0.10 |
| Data Protection (PAN/SAD) | 0.10 |
| Configuration Security | 0.08 |
| Incident Response | 0.05 |
| Other | 0.02 |

## Report template additions

```markdown
## CDE Scope Confirmation

- In CDE: yes/no
- Connected to CDE: yes/no
- Security-service to CDE: yes/no
- Segmentation controls verified: yes/no

## PAN / SAD Handling

- PAN storage: {hashed | tokenized | encrypted | in-memory only | not stored}
- SAD storage post-authorization: **must be NONE**
- PAN display masking: {masked | unmasked | not displayed}

## Requirement-by-Requirement Dashboard

| Req | Title | Compliant | Partial | Non-Compliant | N/A |
|---|---|---|---|---|---|
| 1 | Network security | ... | ... | ... | ... |
| ... | ... | ... | ... | ... | ... |

## 4.0 New-Requirement Readiness

| Requirement | Status | Evidence |
|---|---|---|
| 6.4.3 Payment-page script inventory | ... | ... |
| 11.6.1 Change-and-tamper detection | ... | ... |
| 8.3 MFA for all CDE access | ... | ... |
```

## Common false-positives

- **Tokenized PAN** treated as raw PAN — tokens from an approved tokenization provider are **not** CHD. Distinguish by pattern (e.g., tokens are typically non-Luhn-compliant).
- **Test card numbers** — `4242...`, `4000...` are Stripe test cards, not real PAN. Exclude from findings.
- **Fields named `card_*` but not actually card data** — e.g., `card_id` (UUID), `card_last_four` (last-four display). Verify context before flagging.

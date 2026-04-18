---
name: framework-fedramp
description: FedRAMP audit mapping. NIST SP 800-53 Rev 5 controls at Low/Moderate/High baselines for cloud services sold to US federal agencies.
framework_id: fedramp
framework_name: Federal Risk and Authorization Management Program
evidence_schema: "1"
---

# FedRAMP

**Applies to**: Cloud Service Providers (CSPs) offering services to US federal agencies.
**Basis**: NIST SP 800-53 Rev 5 + FedRAMP-specific parameter overlays.
**Authorization paths**: Agency ATO (single agency) or Provisional ATO (P-ATO, via Joint Authorization Board / JAB).
**Baselines**: Low (125 controls), Moderate (323 controls), High (410 controls) depending on FIPS 199 impact level of the federal data.

**FedRAMP 20x (2025 modernization)**: transitioning from periodic documentation reviews to continuous-evidence automation with Key Security Indicators (KSIs). Static code audit contributes to KSI automation readiness.

## Scope

Ask the user:
1. Target baseline: Low / Moderate / High
2. Authorization path: Agency ATO or JAB P-ATO
3. FIPS 199 impact level of data: Low / Moderate / High (usually matches baseline)

Record in `codebase.audit_scope.fedramp_baseline` and `.fedramp_path`.

## Control families (selected for software audit)

FedRAMP uses NIST 800-53 Rev 5 control families. The following have the strongest software-audit signal.

### AC — Access Control

| Control | Baselines | Rule |
|---|---|---|
| AC-2 Account management | L/M/H | `rbac_present` + user provisioning/deprovisioning workflows |
| AC-3 Access enforcement | L/M/H | Policy-based access checks at endpoints |
| AC-4 Information flow enforcement | M/H | Network segmentation in IaC |
| AC-5 Separation of duties | M/H | CODEOWNERS, no self-approval |
| AC-6 Least privilege | L/M/H | RBAC roles scoped narrowly |
| AC-7 Unsuccessful logon attempts | L/M/H | Account lockout / rate limiting on login |
| AC-11 Device lock | M/H | Session timeout |
| AC-12 Session termination | L/M/H | `session_management.maxAge` reasonable |

### AU — Audit and Accountability

| Control | Baselines | Rule |
|---|---|---|
| AU-2 Event logging | L/M/H | `auth_events_logged == true` |
| AU-3 Content of audit records | L/M/H | Logs include who/what/when/where |
| AU-4 Audit log storage capacity | L/M/H | Retention configured |
| AU-6 Audit review, analysis, reporting | M/H | SIEM integration |
| AU-9 Protection of audit information | L/M/H | `log_tamper_protection == true` |
| AU-12 Audit record generation | L/M/H | Generation at all required components |

### CM — Configuration Management

| Control | Baselines | Rule |
|---|---|---|
| CM-2 Baseline configuration | L/M/H | IaC with versioned baselines |
| CM-3 Configuration change control | M/H | Branch protection + approval gates |
| CM-6 Configuration settings | L/M/H | Hardening baselines in IaC |
| CM-7 Least functionality | L/M/H | Only required ports/services enabled |
| CM-8 System component inventory | L/M/H | SBOM + asset inventory |

### IA — Identification and Authentication

| Control | Baselines | Rule |
|---|---|---|
| IA-2 Identification and authentication (organizational users) | L/M/H | MFA required for all users (Moderate+) |
| IA-2(1) MFA to privileged accounts | L/M/H | `mfa_present == true` for admin |
| IA-2(12) Acceptance of PIV credentials | M/H | PIV/CAC integration evidence |
| IA-5 Authenticator management | L/M/H | `password_policy` complexity, rotation |

### RA — Risk Assessment

| Control | Baselines | Rule |
|---|---|---|
| RA-5 Vulnerability monitoring and scanning | L/M/H | Monthly vuln scans (FedRAMP parameter) |
| RA-5(2) Update vulnerabilities | L/M/H | Automated feed |

### SC — System and Communications Protection

| Control | Baselines | Rule |
|---|---|---|
| SC-7 Boundary protection | L/M/H | Firewall/WAF/SG |
| SC-8 Transmission confidentiality and integrity | L/M/H | `in_transit.tls_enforced` |
| SC-12 Cryptographic key establishment and management | M/H | KMS / HSM evidence |
| SC-13 Cryptographic protection | L/M/H | **FIPS 140-2/3 validated modules** — parameter for FedRAMP |
| SC-28 Protection of information at rest | M/H | `at_rest.detected` with FIPS-validated algos |

### SI — System and Information Integrity

| Control | Baselines | Rule |
|---|---|---|
| SI-2 Flaw remediation | L/M/H | Patch cadence + CI scanning |
| SI-3 Malicious code protection | L/M/H | Endpoint AV evidence |
| SI-4 System monitoring | M/H | SIEM + IDS/IPS |
| SI-7 Software, firmware, and information integrity | M/H | Code signing, binary integrity |

### SR — Supply Chain Risk Management

| Control | Baselines | Rule |
|---|---|---|
| SR-2 SCRM plan | M/H | Vendor registry, DPA evidence |
| SR-3 Supply chain controls and processes | M/H | Dependency-signing, lockfile integrity |
| SR-4 Provenance | M/H | Signed artifacts, SBOM |
| SR-8 Notification agreements | M/H | BAA/DPA signal |
| SR-11 Component authenticity | M/H | Signature verification |

## FedRAMP-specific parameter overrides

Several 800-53 parameters have FedRAMP-specific required values. Examples:
- **Password length**: FedRAMP-High: ≥ 14 chars
- **Session timeout**: ≤ 15 minutes inactivity
- **Vulnerability scan frequency**: monthly
- **Log retention**: minimum 1 year online, 3 years total

Enforce these parameter values in scoring, not the baseline defaults.

## FIPS 140 — cryptographic modules

**Hard requirement**: cryptographic modules used by the system must be FIPS 140-2 or 140-3 validated. Check:
- TLS libraries used (BoringSSL FIPS, OpenSSL-FIPS, AWS-LC-FIPS, wolfCrypt-FIPS)
- KMS usage (AWS KMS is FIPS-validated; Azure Key Vault HSM-tier; GCP Cloud KMS HSM)
- JDK crypto providers (BouncyCastle-FIPS)

Any use of non-FIPS-validated crypto for in-scope data = **Critical**.

## FedRAMP 20x / KSIs

The modernization introduces Key Security Indicators — machine-readable evidence automated from cloud control planes. For forward-looking audits, map each finding to the nearest KSI candidate:
- KSI-001 All ingress traffic authenticated
- KSI-002 Admin sessions MFA-enforced
- KSI-003 All stored data encrypted with FIPS modules
- etc. (candidate list published by FedRAMP PMO; keep updated)

## Dimension weighting (FedRAMP Moderate baseline)

| Dimension | Weight |
|---|---|
| Access Control | 0.15 |
| Audit Logging | 0.12 |
| Encryption | 0.12 |
| Vulnerability Mgmt | 0.10 |
| Configuration Security | 0.10 |
| Change Management | 0.08 |
| Incident Response | 0.08 |
| Data Protection | 0.08 |
| Backup/Recovery | 0.07 |
| Supply Chain | 0.05 |
| Other | 0.05 |

For High baseline, increase Encryption to 0.15 and Access Control to 0.18.

## Report template additions

```markdown
## Baseline Context

- Target baseline: Low / Moderate / High
- Authorization path: Agency ATO / JAB P-ATO
- FIPS 199 data impact: Low / Moderate / High

## FIPS 140 Cryptographic Validation

| Usage | Algorithm | Module | FIPS-validated? | Evidence |
|---|---|---|---|---|
| TLS | TLS 1.2 with AES-256-GCM | AWS-LC-FIPS | Yes (cert #4565) | ... |
| KMS | AES-256 | AWS KMS | Yes | ... |

## Control-Family Dashboard

| Family | Applicable | Implemented | Partial | Not Met | N/A |
|---|---|---|---|---|---|
| AC | ... | ... | ... | ... | ... |
| AU | ... | ... | ... | ... | ... |
| ... | ... | ... | ... | ... | ... |

## Continuous Monitoring Readiness

- Monthly vulnerability scan evidence
- Log shipping to agency-accessible repository
- POA&M automation
- KSI mapping (FedRAMP 20x readiness)
```

## Common false-positives

- **Library uses AES, so it's compliant** — AES alone is not FIPS; the *module* must be validated.
- **Missing PIV/CAC** — only required for federal workforce users, not end-customer users in all cases.
- **Session timeout 30 min** — may be acceptable for Low, not for Moderate/High.

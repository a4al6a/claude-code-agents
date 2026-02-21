---
name: cross-framework-mapping
description: Control-to-framework mapping matrix showing which audit controls satisfy which regulatory requirements, enabling single-evidence multi-framework compliance
---

# Cross-Framework Compliance Mapping

This skill contains the mapping between audit controls and regulatory frameworks. A single audit check can satisfy requirements across multiple frameworks. Evidence is collected once and applied to each relevant framework's report.

## Control-to-Framework Matrix

Use this matrix to determine which frameworks are satisfied by each audit control. When generating per-framework reports, reference the specific control IDs from this mapping.

| Audit Control | SOX | SOC 2 | GDPR | HIPAA | PCI DSS | NIST | ISO 27001 | FedRAMP | DORA | NIS2 | CCPA | CMMC |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Access Control & MFA** | X | X | X | X | X | X | X | X | X | X | X | X |
| **Encryption at Rest** | | X | X | X | X | X | X | X | X | X | X | X |
| **Encryption in Transit** | | X | X | X | X | X | X | X | X | X | X | X |
| **Audit Logging** | X | X | X | X | X | X | X | X | X | X | | X |
| **Change Management** | X | X | | | X | X | X | X | X | X | | X |
| **Vulnerability Management** | | X | | | X | X | X | X | X | X | | X |
| **Incident Response** | | X | X | X | X | X | X | X | X | X | | X |
| **Data Backup & Recovery** | X | X | | X | | X | X | X | X | | | X |
| **Third-Party Risk Mgmt** | | X | X | X | X | X | X | X | X | X | X | X |
| **Security Awareness** | X | X | X | X | X | X | X | X | X | X | | X |
| **Data Classification** | | X | X | X | X | X | X | X | | X | X | X |
| **Penetration Testing** | | | | | X | X | X | X | X | X | | X |
| **SBOM/Dependency Mgmt** | | | | | X | X | | X | X | X | | X |
| **Privacy Impact Assessment** | | | X | | | | | | | | X | |
| **Secure SDLC** | | X | | | X | X | X | X | X | X | | X |

## Framework-Specific Control ID Mappings

When a finding maps to a framework, use these specific control identifiers in the report.

### Access Control & MFA

| Framework | Control ID | Control Name |
|---|---|---|
| SOX | ITGC-AC | IT General Controls -- Access Controls |
| SOC 2 | CC6.1, CC6.2, CC6.3 | Logical and Physical Access Controls |
| GDPR | Art. 32(1)(b) | Ability to ensure ongoing confidentiality |
| HIPAA | 164.312(a), 164.312(d) | Access Control, Authentication |
| PCI DSS | Req 7, Req 8 | Restrict Access, Identify Users |
| NIST | AC-1 through AC-25 | Access Control family |
| ISO 27001 | A.8.3, A.8.5 | Access rights management, Authentication |
| FedRAMP | AC (Moderate/High) | Access Control controls |
| DORA | Art. 9(4)(c) | ICT access control policies |
| NIS2 | Art. 21(2)(i) | Human resources security and access control |
| CCPA | 1899.122(a)(1) | Access control assessment |
| CMMC | AC.L1-3.1.1 through AC.L2-3.1.20 | Access Control practices |

### Encryption at Rest

| Framework | Control ID | Control Name |
|---|---|---|
| SOC 2 | CC6.1, C1.1 | Logical access, Confidentiality |
| GDPR | Art. 32(1)(a) | Pseudonymisation and encryption |
| HIPAA | 164.312(a)(2)(iv) | Encryption (access control) |
| PCI DSS | Req 3.5 | Protect stored account data |
| NIST | SC-28 | Protection of Information at Rest |
| ISO 27001 | A.8.24 | Use of cryptography |
| FedRAMP | SC-28 | Protection of Information at Rest |
| DORA | Art. 9(4)(d) | Cryptographic controls |
| NIS2 | Art. 21(2)(h) | Policies on cryptography and encryption |
| CCPA | 1899.122(a)(2) | Encryption assessment |
| CMMC | SC.L2-3.13.11 | CUI encryption at rest |

### Encryption in Transit

| Framework | Control ID | Control Name |
|---|---|---|
| SOC 2 | CC6.1, CC6.7 | System operations, Transmission controls |
| GDPR | Art. 32(1)(a) | Pseudonymisation and encryption |
| HIPAA | 164.312(e)(1) | Transmission Security |
| PCI DSS | Req 4 | Protect cardholder data in transit |
| NIST | SC-8 | Transmission Confidentiality and Integrity |
| ISO 27001 | A.8.24 | Use of cryptography |
| FedRAMP | SC-8 | Transmission Confidentiality and Integrity |
| DORA | Art. 9(4)(d) | Cryptographic controls |
| NIS2 | Art. 21(2)(h) | Policies on cryptography and encryption |
| CCPA | 1899.122(a)(2) | Encryption assessment |
| CMMC | SC.L2-3.13.8 | CUI encryption in transit |

### Audit Logging

| Framework | Control ID | Control Name |
|---|---|---|
| SOX | ITGC-LM | IT General Controls -- Log Management |
| SOC 2 | CC7.1, CC7.2 | Monitoring, Anomaly detection |
| GDPR | Art. 5(2), Art. 30 | Accountability, Records of processing |
| HIPAA | 164.312(b) | Audit Controls |
| PCI DSS | Req 10.2, 10.3, 10.4, 10.5 | Audit log implementation through retention |
| NIST | AU-1 through AU-16 | Audit and Accountability family |
| ISO 27001 | A.8.15 | Logging |
| FedRAMP | AU (Moderate/High) | Audit and Accountability |
| DORA | Art. 10 | Logging and incident detection |
| NIS2 | Art. 21(2)(b) | Incident handling |
| CMMC | AU.L2-3.3.1 through AU.L2-3.3.9 | Audit and Accountability practices |

### Change Management

| Framework | Control ID | Control Name |
|---|---|---|
| SOX | ITGC-CM | IT General Controls -- Change Management |
| SOC 2 | CC8.1 | Change Management |
| PCI DSS | Req 6.5 | Change management procedures |
| NIST | CM-1 through CM-11 | Configuration Management family |
| ISO 27001 | A.8.32 | Change management |
| FedRAMP | CM (Moderate/High) | Configuration Management |
| DORA | Art. 9(4)(e) | ICT change management |
| NIS2 | Art. 21(2)(e) | Security in acquisition, development and maintenance |
| CMMC | CM.L2-3.4.1 through CM.L2-3.4.9 | Configuration Management practices |

### Vulnerability Management

| Framework | Control ID | Control Name |
|---|---|---|
| SOC 2 | CC7.1 | Monitoring for vulnerabilities |
| PCI DSS | Req 6.3.3, 11.3 | Patching, Vulnerability scanning |
| NIST | RA-5, SI-2 | Vulnerability Monitoring, Flaw Remediation |
| ISO 27001 | A.8.8 | Management of technical vulnerabilities |
| FedRAMP | RA-5, SI-2 | Vulnerability Monitoring, Flaw Remediation |
| DORA | Art. 9(2) | ICT risk management |
| NIS2 | Art. 21(2)(e) | Vulnerability handling and disclosure |
| CMMC | RA.L2-3.11.2, SI.L2-3.14.1 | Risk Assessment, System Integrity |

### Incident Response

| Framework | Control ID | Control Name |
|---|---|---|
| SOC 2 | CC7.3, CC7.4, CC7.5 | Incident detection, response, recovery |
| GDPR | Art. 33, Art. 34 | Breach notification (72h), Communication to data subjects |
| HIPAA | 164.308(a)(6) | Security Incident Procedures |
| PCI DSS | Req 12.10 | Incident response plan |
| NIST | IR-1 through IR-10 | Incident Response family |
| ISO 27001 | A.5.24, A.5.25, A.5.26 | Incident management planning through learning |
| FedRAMP | IR (Moderate/High) | Incident Response |
| DORA | Art. 17, Art. 18, Art. 19 | ICT-related incident management |
| NIS2 | Art. 21(2)(b), Art. 23 | Incident handling, Notification |
| CMMC | IR.L2-3.6.1 through IR.L2-3.6.3 | Incident Response practices |

### Supply Chain / Dependency Management

| Framework | Control ID | Control Name |
|---|---|---|
| PCI DSS | Req 6.3.2 | Inventory of bespoke and custom software |
| NIST | ID.SC, SR-1 through SR-12 | Supply Chain Risk Management |
| FedRAMP | SR (Moderate/High) | Supply Chain Risk Management |
| DORA | Art. 28, Art. 29, Art. 30 | Third-party ICT risk management |
| NIS2 | Art. 21(2)(d), Art. 21(3) | Supply chain security |
| CMMC | SR.L2-3.16.1 through SR.L2-3.16.3 | Supply Chain Risk Management |

### Data Protection / Privacy

| Framework | Control ID | Control Name |
|---|---|---|
| SOC 2 | P1.1 through P8.1 | Privacy criteria (collection through disposal) |
| GDPR | Art. 5 through Art. 11 | Data processing principles |
| HIPAA | 164.502 through 164.514 | Uses and disclosures of PHI |
| PCI DSS | Req 3 | Protect stored account data |
| NIST | PT-1 through PT-8 | PII Processing and Transparency family |
| ISO 27001 | A.5.33, A.5.34 | Protection of records, Privacy |
| FedRAMP | PT (Moderate/High) | PII Processing and Transparency |
| CCPA | 1899.100 through 1899.102 | Consumer rights and business obligations |
| CMMC | MP.L2-3.8.1 through MP.L2-3.8.9 | Media Protection practices |

## Overlap Analysis

**Highest overlap controls** (satisfy 10+ frameworks simultaneously):
1. **Access Control & MFA** -- 12/12 frameworks
2. **Encryption at Rest** -- 11/12 frameworks
3. **Encryption in Transit** -- 11/12 frameworks
4. **Third-Party Risk Management** -- 11/12 frameworks
5. **Incident Response** -- 11/12 frameworks
6. **Audit Logging** -- 10/12 frameworks

**Interpretation**: Thoroughly auditing these six control areas covers the majority of requirements across all major frameworks. Prioritize these dimensions for deepest analysis.

## Using This Mapping

When generating a finding:

1. Identify which audit control the finding relates to (e.g., "Encryption at Rest")
2. Look up the control in the matrix above to see which selected frameworks it maps to
3. For each applicable framework, use the framework-specific control ID from the detailed tables
4. Include the finding in each relevant framework's report with the appropriate control ID and context
5. In the "Cross-Framework Observations" section of each report, note which other selected frameworks share this finding

This approach ensures evidence is collected once but compliance impact is assessed per framework.

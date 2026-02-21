---
name: regulatory-frameworks
description: Detailed control areas, evidence requirements, and audit checks per supported compliance framework
---

# Regulatory Frameworks Reference

This skill contains the control areas and evidence requirements for each supported compliance framework. Load this during Phase 1 (SCOPE) to determine which audit dimensions apply to the selected frameworks.

## SOX (Sarbanes-Oxley Act)

**Applies to**: Publicly traded companies (US)
**Key sections**: 302 (CEO/CFO certification), 404 (internal controls over financial reporting)

**Control areas for software audit**:
| Control Area | What to Check | Evidence |
|---|---|---|
| IT Security | Data breach prevention, incident remediation | Security policies, incident response plans, security scanning configs |
| Access Controls | Physical and electronic controls preventing unauthorized access to financial data | RBAC implementation, user provisioning code, access review logs |
| Change Management | Software update processes with full audit trail (who, what, when) | Git history, PR review requirements, CI/CD pipeline configs, deployment logs |
| Data Backup | Backup systems subject to same compliance as primary | Backup configuration, recovery test documentation |
| Data Tampering Safeguards | Systems tracking user access with detection of unauthorized access | Audit logging implementation, tamper detection, integrity checks |
| Timeline/Timestamping | Automatic remote storage with encrypted checksums | Log shipping config, timestamp verification, checksum implementation |

**Documentation retention**: All analyses, remediation plans, sanctions, and reviews must be documented.

## SOC 2 (Trust Service Criteria)

**Applies to**: Service organizations (SaaS, cloud, tech providers) handling customer data

**Five Trust Service Criteria**:

| TSC | Full Name | Key Audit Checks |
|---|---|---|
| CC (Security) | Common Criteria -- Required | MFA, firewalls, endpoint protection, incident response, access controls |
| A (Availability) | Availability | Uptime monitoring, incident response, failover, capacity planning |
| PI (Processing Integrity) | Processing Integrity | Input/output validation, data processing accuracy, error detection |
| C (Confidentiality) | Confidentiality | Encryption at rest/transit, access restrictions, data retention |
| P (Privacy) | Privacy | PII collection/use/retention/disclosure/disposal controls |

**Software-specific controls**:
- CC6.1: Logical access controls (RBAC, least privilege)
- CC6.6: Security measures against threats outside system boundaries
- CC7.1: Monitoring for anomalies and security events
- CC7.2: Incident detection and response procedures
- CC8.1: Change management with approval workflows
- Secure SDLC: peer code reviews, automated testing, CI/CD security checks

## GDPR (General Data Protection Regulation)

**Applies to**: All organizations processing EU residents' personal data
**Penalties**: Up to 4% of global annual revenue

**Control areas**:
| Article | Requirement | What to Check |
|---|---|---|
| Art. 5(1)(f) | Integrity and confidentiality | Encryption, access controls, security measures |
| Art. 6 | Lawful basis for processing | Consent mechanisms, legal basis documentation |
| Art. 7 | Conditions for consent | Consent management implementation, withdrawal mechanism |
| Art. 17 | Right to erasure | Data deletion implementation, cascade handling |
| Art. 20 | Data portability | Data export functionality |
| Art. 25 | Data protection by design and default | Privacy-by-design patterns, data minimization |
| Art. 30 | Records of processing activities | Processing activity documentation |
| Art. 32 | Security of processing | Encryption (AES-256 at rest, TLS 1.2+ in transit), MFA, access controls |
| Art. 33 | Breach notification (72h) | Breach notification procedures, incident response plan |
| Art. 35 | DPIA for high-risk processing | DPIA documentation |

**Evidence to collect**: Records of processing activities, DPIA documentation, data retention policies, consent management records, data breach response plans.

## HIPAA (Health Insurance Portability and Accountability Act)

**Applies to**: Entities handling electronic Protected Health Information (ePHI)
**Retention**: All documentation retained for 6 years

**Five technical safeguard standards (164.312)**:

| Standard | Requirement | What to Check |
|---|---|---|
| 164.312(a) | Access Control | Unique user IDs, emergency access, automatic logoff, encryption |
| 164.312(b) | Audit Controls | Hardware/software mechanisms recording ePHI access and activity |
| 164.312(c) | Integrity | Mechanisms confirming ePHI not altered/destroyed unauthorized |
| 164.312(d) | Authentication | Identity verification for ePHI access |
| 164.312(e) | Transmission Security | Encryption and integrity controls for data in transit |

**2025 proposed updates**: Encryption, MFA, and network segmentation mandatory (no longer "addressable"). Annual technology asset inventories. 72-hour system restoration capability. Regular vulnerability scans and penetration tests.

## PCI DSS 4.0

**Applies to**: All entities handling payment card data
**Effective**: March 31, 2025 (mandatory)

**Key requirements for software audit**:
| Requirement | What to Check |
|---|---|
| Req 3 | Protect stored account data (encryption at rest -- disk-level no longer qualifies) |
| Req 6.2 | Secure software development lifecycle |
| Req 6.3.3 | Patch critical/high vulnerabilities within defined timeframes |
| Req 6.4.3 | Client-side security (new in 4.0) |
| Req 8 | Strong authentication (MFA for all CDE access) |
| Req 10.2 | Audit log implementation |
| Req 10.3 | Audit log protection against modification |
| Req 10.4 | Automated audit log review (SIEM required) |
| Req 10.5 | 12-month log retention, 3 months immediately available |
| Req 11.3 | Quarterly vulnerability scanning |
| Req 11.4 | Penetration testing |
| Req 11.6.1 | Client-side change detection (new in 4.0) |
| Req 12.3.4 | Annual cryptographic review |

## NIST CSF 2.0

**Applies to**: Recommended for all organizations; mandatory for US federal agencies
**Six functions**: Govern (GV), Identify (ID), Protect (PR), Detect (DE), Respond (RS), Recover (RC)

**Key control families (NIST SP 800-53 Rev 5)**:
| Family | Code | Software Audit Focus |
|---|---|---|
| Access Control | AC | Authentication, authorization, least privilege |
| Audit and Accountability | AU | Audit events, content, storage, generation |
| Configuration Management | CM | Baseline configuration, change control |
| Identification and Authentication | IA | Identity management, authenticator management |
| Risk Assessment | RA | Vulnerability monitoring, scanning |
| System and Communications Protection | SC | Boundary protection, cryptographic protections |
| System and Information Integrity | SI | Flaw remediation, malicious code protection |
| Supply Chain Risk Management | ID.SC | Dependency management, SBOM, provenance |

## ISO 27001:2022

**Applies to**: Organizations seeking ISMS certification
**Recertification**: By October 31, 2025 (against 2022 version)

**Audit stages**: Stage 1 (documentation review), Stage 2 (evidential field review), Surveillance (annual), Recertification (every 3 years)
**Finding categories**: Minor non-conformity, major non-conformity, observation

**Required documentation**: ISMS scope, Statement of Applicability, Information Security Policy, Risk Assessment, Risk Treatment Plan.

**Key Annex A controls for software**:
- A.5: Organizational controls (policies, roles, asset management)
- A.6: People controls (screening, awareness, training)
- A.7: Physical controls (security perimeters, equipment)
- A.8: Technological controls (access rights, authentication, encryption, logging, secure development, vulnerability management, configuration management, data masking, DLP, monitoring, web filtering, secure coding)

## FedRAMP

**Applies to**: Cloud services used by US federal agencies
**Basis**: NIST SP 800-53 Rev 5 controls at moderate or high impact

**Key requirements**:
- System Security Plan (SSP) mapping to NIST controls
- FIPS 140-2 validated cryptographic modules
- Comprehensive vulnerability management
- Logging and monitoring systems
- Continuous monitoring with monthly vulnerability scans

**FedRAMP 20x (2025 modernization)**: Replaces periodic reviews with continuous monitoring. Evidence via logs, configuration files, and automated integrations. Key Security Indicators (KSIs) pilot.

## CCPA/CPRA

**Applies to**: Organizations processing California consumers' personal information meeting revenue/volume thresholds
**Effective**: January 1, 2026 (phased through 2028)

**Audit requirements**: Annual, thorough, evidence-based audit reports. Cannot rely primarily on management assertions. Must identify gaps increasing unauthorized access risk. Records retained for 5 years. Annual certification signed under penalty of perjury.

**Cross-framework leverage**: May leverage audits conducted for other frameworks (e.g., NIST CSF 2.0) if they meet all CCPA requirements.

## DORA (Digital Operational Resilience Act)

**Applies to**: 20 categories of EU financial entities and their ICT providers
**Effective**: January 17, 2025 (no transition period)
**Penalties**: Up to 2% of annual worldwide turnover

**Key ICT requirements**:
- ICT risk management framework (documented, reviewed annually)
- Internal ICT audit by auditors with sufficient ICT risk knowledge
- Third-party ICT provider oversight
- Digital operational resilience testing (vulnerability assessments, pen tests, scenario-based)
- Structured incident reporting

## NIS2 Directive

**Applies to**: 18 EU critical sectors
**Penalties**: Essential entities up to EUR 10M or 2% of global revenue. Executive personal liability.

**Key requirements**:
- Supply chain security policy with rules for all direct suppliers
- Contractual flow-downs for cybersecurity, incident reporting, audit rights
- Supplier register with regular risk management
- Vulnerability management including periodic supplier inspection
- Regular audits mapped to NIS2 obligations

**Article 21 emphasis**: Supply chain security including supplier selection criteria, evaluation of cybersecurity practices, and resilience analysis.

## CMMC 2.0

**Applies to**: All entities doing business with US DoD handling FCI or CUI
**Implementation**: Phased from November 2025 through 2028

**Three levels**:
| Level | Scope | Controls | Assessment |
|---|---|---|---|
| Level 1 | Basic FCI protection | 17 practices (FAR 52.204-21) | Annual self-assessment |
| Level 2 | General CUI protection | 110 practices (NIST SP 800-171 Rev 2) | Third-party (C3PAO) or self-assessment |
| Level 3 | Enhanced CUI against APTs | 110+ practices (NIST SP 800-172) | Government-led (DIBCAC) |

**Key audit areas**: Evidence retention for 6 years, subcontractor compliance flow-down, False Claims Act exposure for misrepresenting compliance status.

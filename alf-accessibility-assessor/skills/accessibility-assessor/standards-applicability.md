---
name: standards-applicability
description: Pre-scope questionnaire that asks the user which accessibility standards/regulations apply to their system, before assessment begins. Standards overlay on the universal WCAG 2.2 AA baseline.
---

# Standards Applicability Questionnaire

Runs in Phase 1 (SCOPE) before the assessment begins. WCAG 2.2 AA is the **universal baseline** — every assessment evaluates against it. This questionnaire determines which **regulatory overlays** apply on top, each adding specific delta requirements.

## Why this exists

Most users know they want an "accessibility assessment". Fewer know that:
- **ADA (US)** applies to public accommodations, state/local government, employers with 15+ employees
- **Section 508** applies to US federal agencies and contractors
- **EN 301 549** applies to public-sector procurement in the EU
- **EAA (European Accessibility Act)** applies to many private-sector products in the EU starting June 28, 2025
- **AODA (Ontario)** applies to organizations operating in Ontario with 50+ employees
- **California Unruh / AB 1757** applies to websites doing business in California

The questionnaire asks about organizational and geographic context, then recommends which regulatory overlays to apply. Users confirm/adjust.

## Questionnaire

Use `AskUserQuestion`. Multi-select where sensible.

### Block A — Geography / customer base

1. **Where are your users?** (multi-select)
   - United States (general public)
   - California specifically (triggers stricter interpretation via Unruh Act case law)
   - European Union / European Economic Area
   - United Kingdom
   - Canada (Ontario specifically)
   - Australia
   - Other / Global

### Block B — Organizational context

2. **Which applies to your organization?** (multi-select)
   - US Federal agency, contractor, or recipient of federal funds
   - US state or local government
   - US private-sector business open to the public (retail, services, education, healthcare, financial services, entertainment)
   - US employer with 15 or more employees
   - EU public-sector body (government, healthcare, education)
   - EU private-sector selling "products and services covered by EAA" (e-commerce, banking, transport, e-books, ATMs, etc.)
   - Ontario organization with 50+ employees, or any public-sector org
   - None of the above

### Block C — Product context

3. **What kind of product is this?** (multi-select)
   - Public-facing website or web app
   - Mobile app
   - Native desktop app
   - Internal/enterprise-only tool
   - Hardware with software component (kiosks, ATMs, ticketing)
   - Document-heavy (PDFs, forms)
   - Digital-only (no physical counterpart)

### Block D — Conformance target

4. **What conformance level?** (single-select)
   - Level A (minimum required)
   - **Level AA (default and recommended for almost all regulations)**
   - Level AAA (aspirational; few products meet this)

## Recommendation rules

Apply in order. Accumulate a set; deduplicate.

| Input | Add overlay | Rationale |
|---|---|---|
| Block B: US Federal / contractor | **Section 508** | Revised Section 508 (January 2018) aligns with WCAG 2.0 AA; federal requirement. |
| Block B: US state/local government | **ADA Title II** (aligned with WCAG 2.1 AA as of final DOJ rule April 2024) | DOJ final rule requires WCAG 2.1 AA compliance over a phased period. |
| Block B: US public accommodation | **ADA Title III** | Web accessibility under ADA case law (Gil v. Winn-Dixie, Robles v. Domino's, etc.). No specific technical standard, but WCAG 2.1 AA is the de-facto bar. |
| Block A: California (alone or US combined) | **California Unruh Act / AB 1757** | California courts interpret ADA + Unruh strictly; AB 1757 reinforces via WCAG 2.1 AA. |
| Block B: EU public sector | **EN 301 549** | EU procurement standard, aligned with WCAG 2.1 AA. |
| Block B: EU private sector + covered products | **European Accessibility Act (EAA)** | Mandatory June 28, 2025 for covered sectors. References EN 301 549. |
| Block A: UK | **UK Equality Act 2010 + PSBAR 2018** for public sector | Public-sector requirement WCAG 2.1 AA; private sector via Equality Act case law. |
| Block B: Ontario | **AODA (IASR)** | WCAG 2.0 AA (IASR deadline passed); many organizations targeting 2.1/2.2 now. |
| Block A: Australia | **DDA + WCAG 2.2 AA** guidance | Disability Discrimination Act; AGIMO/DTA guidance. |

If no overlays accumulate, assessment runs against WCAG 2.2 AA with no regulatory framing.

## Delta requirements per overlay (beyond WCAG 2.2 AA)

Each overlay adds specific considerations. Apply these in Phase 4 (CLASSIFY) when mapping findings:

### Section 508 deltas
- Functional Performance Criteria (FPC) — ensure all mandatory functionality is operable via each type of assistive technology
- Software requirements per subpart — including API support for AT (504.2)
- Closed products (kiosks) have specific requirements
- Electronic documents: each PDF/Word doc is individually in scope

### ADA deltas
- No single technical standard; courts generally accept WCAG 2.1 AA (2.2 likely as the update matures)
- Accessibility statement recommended
- Alternative means of access (phone, in-person) may be required
- Third-party content still attributable to the covered entity

### EN 301 549 deltas
- Includes ICT beyond websites: telecom relay, real-time text, multimedia
- Clause 5 (generic ICT), Clause 9 (web), Clause 10 (non-web docs), Clause 11 (software), Clause 12 (documentation/support)
- Chapter 4: functional performance statements

### EAA deltas
- Specific product categories: consumer terminal hardware, e-readers, e-commerce, banking, transport, broadcasting
- Member-state implementation varies — apply generically and cite the EAA; user must check local law
- Mandatory after June 28, 2025; existing services have transitional period until 2030

### AODA deltas
- WCAG 2.0 AA minimum, many aiming for 2.1
- Accessibility policy requirement + multi-year plan
- Training requirements for staff

### California Unruh / AB 1757 deltas
- WCAG 2.1 AA explicitly cited in AB 1757
- Private-sector coverage broader than federal ADA
- Courts have awarded statutory damages beyond injunctive relief

## Confirmation prompt

Present the recommendation:

```
Based on your answers, this assessment will evaluate against:

  • WCAG 2.2 Level AA (universal baseline)
  • Section 508 (because you're a US federal contractor)
  • ADA Title III (because you serve the US public)
  • California Unruh / AB 1757 (because many users are in California)

These are NOT being applied (skipping):
  EN 301 549, EAA, AODA, UK PSBAR, Australia DDA

Confirm? You can add or remove overlays.
```

## Output contract

Embed the scope decision in the assessment:

```json
{
  "conformance_target": "WCAG 2.2 AA",
  "regulatory_overlays": ["section-508", "ada-title-iii", "california-ab-1757"],
  "regulatory_overlays_not_applied": ["en-301-549", "eaa", "aoda"],
  "questionnaire_answers": {
    "geography": ["us", "california"],
    "organization": ["us_federal_contractor", "us_public_accommodation"],
    "product": ["public_web"]
  },
  "rationale_per_overlay": {
    "section-508": "US federal contractor",
    "ada-title-iii": "US public accommodation",
    "california-ab-1757": "Serves California users"
  }
}
```

Each finding in the final report receives a `regulatory_applicability` array listing which overlays the finding is material to. Section 508 has a stricter Functional Performance requirement → some findings material to Section 508 but not to base WCAG 2.2 AA compliance.

## When unsure

If the user does not know their jurisdictional coverage, recommend the conservative superset:
- **WCAG 2.2 AA + Section 508 + ADA Title III + EAA + EN 301 549**

This covers the majority of global obligations. Note in the report that conservative scope was applied.

## Subagent mode

Skip `AskUserQuestion`. Return:

```json
{
  "CLARIFICATION_NEEDED": true,
  "questions": [
    {"id": "geography", "prompt": "...", "options": [...]},
    {"id": "organization", "prompt": "...", "options": [...]},
    {"id": "product", "prompt": "...", "options": [...]},
    {"id": "conformance_level", "prompt": "Level?", "options": ["A", "AA", "AAA"]}
  ]
}
```

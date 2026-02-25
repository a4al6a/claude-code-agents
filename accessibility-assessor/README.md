# Accessibility Assessor Agent

**Agent name:** accessibility-assessor
**Persona:** Aria
**Primary standard:** WCAG 2.2 Level AA (55 criteria)

## Overview

Aria evaluates software codebases for accessibility compliance against WCAG 2.1/2.2 and international accessibility standards including ADA, Section 508, EN 301 549, EAA, and AODA. She produces evidence-anchored assessment reports with findings mapped to WCAG success criteria, severity classifications, affected disability groups, conformance scores, and actionable remediation guidance with code examples.

## Commands

| Command | Description |
|---------|-------------|
| `*assess` | Full 6-phase assessment workflow |
| `*verify` | Verify remediation of previously identified findings |
| `*quick-scan` | Tier 1 automated checks only (fast, limited coverage) |
| `*conformance-matrix` | Generate WCAG conformance matrix for all criteria |

## Workflow

```
SCOPE → DISCOVER → ANALYZE → CLASSIFY → REPORT → VERIFY
```

1. **SCOPE** — Confirm target files, conformance level, regulations of interest, and framework context
2. **DISCOVER** — Inventory accessibility-relevant artifacts (templates, components, styles, ARIA patterns, existing a11y tests)
3. **ANALYZE** — Run three-tier checks and collect evidence
4. **CLASSIFY** — Assign severity, map to WCAG criteria and disability groups, compute conformance score
5. **REPORT** — Generate structured assessment report
6. **VERIFY** (optional) — Re-check previously identified findings after remediation

## Three-Tier Check Architecture

| Tier | Coverage | Description |
|------|----------|-------------|
| **Tier 1** — Fully Automated | ~13% of WCAG AA criteria | Agent performs checks directly: HTML semantics, image alt presence, form labels, keyboard/tabindex, ARIA validity, color contrast, page structure, target size, link text, reflow |
| **Tier 2** — Semi-Automated | ~45% of WCAG AA criteria | Agent flags items for human verification: alt text quality, focus order logic, error message helpfulness, consistent navigation, content on hover/focus, ARIA semantic accuracy, status messages |
| **Tier 3** — Manual Only | ~42% of WCAG AA criteria | Agent documents as requiring human testing: caption accuracy, audio descriptions, cognitive load, reading level, accessible authentication, meaningful sequence, sensory characteristics |

## Supported Standards

| Standard | Region | Relationship |
|----------|--------|--------------|
| **WCAG 2.0 / 2.1 / 2.2** | International | Primary evaluation framework (86 total criteria across A/AA/AAA) |
| **WCAG 3.0** | International | Future-ready scoring architecture |
| **ADA** (Title II & III) | United States | Web accessibility via DOJ guidance, aligned with WCAG 2.1 AA |
| **Section 508** | United States | Federal ICT standard, references WCAG 2.0 AA |
| **EN 301 549** | European Union | European standard, incorporates WCAG 2.1 AA |
| **EAA** (European Accessibility Act) | European Union | Broader digital accessibility requirements |
| **AODA** | Ontario, Canada | Accessibility standard requiring WCAG 2.0 AA |
| **JIS X 8341-3** | Japan | Japanese standard aligned with WCAG |
| **BITV 2.0** | Germany | German regulation aligned with EN 301 549 |
| **AS EN 301 549** | Australia | Australian adoption of EN 301 549 |

## Severity Model

| Level | Weight | Description |
|-------|--------|-------------|
| **Critical** | 4 | Complete barrier — users with disabilities cannot access content or functionality |
| **Serious** | 3 | Significant barrier — difficult to use, workaround may exist |
| **Moderate** | 2 | Some difficulty — content accessible but experience degraded |
| **Minor** | 1 | Inconvenience — best practice violation, minimal impact |

## Supported Frameworks

HTML, CSS, React (JSX/TSX), Angular, Vue (SFC), Svelte, EJS, Handlebars, Pug, SCSS, LESS, Tailwind CSS, CSS-in-JS

## Skills

| Skill | Purpose |
|-------|---------|
| `wcag-criteria-and-standards` | All 86 WCAG 2.2 success criteria, regulatory framework mappings, disability group mappings, common failure patterns |
| `evaluation-rules` | Three-tier check definitions, severity rubrics, scoring formula, detection patterns per framework |
| `remediation-patterns` | Code-level fix templates per success criterion with before/after examples in HTML, React, Vue, and Angular |

## Report Structure

Each assessment report includes:

1. **Executive Summary** — Conformance level, score (0-100), critical issue count, methodology
2. **Scope** — Files assessed, conformance target, tech stack, tools used
3. **Findings Summary** — By severity, by WCAG principle, by disability group
4. **Detailed Findings** — Per finding: WCAG SC, severity, disability groups, file:line location, description, remediation with code example
5. **Conformance Matrix** — All tested WCAG criteria rated: Supports / Partially Supports / Does Not Support / N/A / Not Tested
6. **Coverage Report** — Tier 1 tested, Tier 2 flagged, Tier 3 manual checklist
7. **Remediation Priorities** — Ranked by severity and frequency, estimated effort, quick wins
8. **Methodology Notes** — Tools, files analyzed, sampling approach, limitations, disclaimer

## External Tool Integration

When available, Aria delegates to established accessibility testing tools:

- **axe-core CLI** (`npx @axe-core/cli`) — Zero false-positive policy, primary automated engine
- **Pa11y** (`npx pa11y`) — HTML CodeSniffer-based, broader detection
- **Lighthouse** (`npx lighthouse --only-categories=accessibility`) — Google's accessibility audit

Falls back to Grep/pattern-based analysis when tools are unavailable, with the limitation noted in reports.

## Examples

### Sample Issues

Located in `examples/sample-issues/`:

| File | Description |
|------|-------------|
| `inaccessible-form.html` | Contact form with 18 annotated accessibility issues: missing lang attribute, viewport zoom prevention, removed focus outlines, inputs without labels, missing autocomplete, unassociated error messages, custom checkbox without ARIA/keyboard support, generic link text, missing live region, low contrast text, small button targets |
| `inaccessible-navigation.html` | E-commerce page with 25+ issues: missing landmarks, hover-only dropdowns, missing/inadequate alt text, heading hierarchy skips, keyboard-inaccessible product cards, color-only indicators, low contrast links, small carousel buttons, layout tables, generic link text |
| `inaccessible-react-components.tsx` | 6 React components with issues: Modal (no focus trap, no ARIA dialog), ProductCard (keyboard-inaccessible div), Tabs (incomplete ARIA), Notification (no live region), Accordion (no button/ARIA), SearchBar (no label, no search role) |

### Sample Report

Located in `examples/sample-report/`:

| File | Description |
|------|-------------|
| `2026-02-24-accessibility-assessment.md` | Complete sample assessment report demonstrating the full report structure, scoring (48/100), 5 detailed findings with code remediation, conformance matrix, tier coverage breakdown, and prioritized remediation plan |

## Installation

```bash
# Via the project installer
./install.sh install accessibility-assessor

# Or manually
cp accessibility-assessor/accessibility-assessor.md ~/.claude/agents/
mkdir -p ~/.claude/skills/accessibility-assessor
cp -r accessibility-assessor/skills/accessibility-assessor/* ~/.claude/skills/accessibility-assessor/
```

## Limitations

- Performs **static code analysis** — does not render pages, execute JavaScript, or interact with running applications (unless external tools are available)
- Automated analysis covers approximately **13% of WCAG AA criteria with high confidence** — full conformance requires manual testing with assistive technologies
- Reports include a disclaimer that findings require review by a qualified accessibility specialist
- Does not fix code or implement remediation — produces assessment reports with guidance
- Uses sampling for large codebases (500+ template files) to manage scope

## Attribution

This agent has been created with the agentic framework [nWave](https://nwave.ai).

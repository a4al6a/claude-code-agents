---
name: evaluation-rules
description: Three-tier check definitions, severity classification rubrics, scoring formulas, conformance rating system, and detection patterns for automated accessibility checks
---

# Evaluation Rules

Load this skill during Phase 3 (ANALYZE) for check execution and Phase 4 (CLASSIFY) for severity and scoring.

## Severity Classification (axe-core impact model)

| Severity | Weight | Description | User Impact | Examples |
|----------|--------|-------------|-------------|---------|
| Critical | 4 | Content or functionality completely inaccessible | Users with disabilities cannot access content at all | No keyboard access, missing form labels causing screen reader failure, dangerous flashing |
| Serious | 3 | Significant barrier making content very difficult | Users can access with extreme difficulty | Missing alt on informative images, no focus indicator, contrast below 3:1 |
| Moderate | 2 | Unnecessary time or effort required | Users can access but with notable friction | Missing heading structure, redundant tab stops, contrast 3:1 to 4.5:1 |
| Minor | 1 | Best practice; accessible but improvable | Minimal direct impact | Redundant ARIA, suboptimal heading text, tabindex on natively focusable elements |

## Conformance Rating per Criterion

| Rating | Definition | When to Apply |
|--------|-----------|---------------|
| Supports | All instances pass | Zero failures detected for this criterion |
| Partially Supports | Some instances pass, some fail | Failures in <50% of instances |
| Does Not Support | Majority of instances fail | Failures in >=50% of instances |
| Not Applicable | Criterion does not apply | No relevant content exists (e.g., no media for 1.2.x) |
| Not Tested | Requires manual verification | Tier 3 criterion or tool unavailable |

## Scoring Formula

```
Accessibility Score = ((Total_Criteria - Weighted_Failures) / Total_Criteria) * 100

Weighted_Failures = sum(severity_weight * failure_count / total_instances) per criterion
```

Score interpretation:

| Score | Rating | Interpretation |
|-------|--------|----------------|
| 90-100 | Conformant | Meets target conformance level with minor issues |
| 75-89 | Substantially Conformant | Most criteria met, some gaps |
| 50-74 | Partial Conformance | Significant accessibility barriers remain |
| 25-49 | Non-Conformant | Major accessibility barriers throughout |
| 0-24 | Severely Non-Conformant | Fundamental accessibility absent |

## Tier 1: Automated Check Definitions

These checks produce reliable results from static code analysis.

### 1.1 HTML Semantics (SC 1.3.1)

**Heading hierarchy**
- Pattern: `<h[1-6]>` elements
- Rule: headings follow sequential order (no skipping from h1 to h3)
- Severity: Moderate
- Disability: Visual, Cognitive
- Detection: Parse heading elements, verify level sequence per section

**Landmark regions**
- Pattern: `<main>`, `<nav>`, `<header>`, `<footer>`, `<aside>`, `role="banner|navigation|main|contentinfo|complementary"`
- Rule: page has at least one `<main>` landmark; landmarks are not nested incorrectly
- Severity: Moderate
- Disability: Visual
- Detection: Grep for landmark elements and ARIA roles

**List structure**
- Pattern: `<ul>`, `<ol>`, `<dl>` with direct children `<li>`, `<dt>`, `<dd>`
- Rule: list items are wrapped in list containers; no orphaned `<li>` elements
- Severity: Moderate
- Disability: Visual

**Table markup**
- Pattern: `<table>` with `<th>`, `<caption>`, `scope`, `headers`
- Rule: data tables have header cells; layout tables do not use header markup
- Severity: Serious
- Disability: Visual

### 1.2 Image Accessibility (SC 1.1.1)

**Alt text presence**
- Pattern: `<img>`, `<input type="image">`, `[role="img"]`, `<svg>` used as images
- Rule: informative images have non-empty alt; decorative images have `alt=""` or `role="presentation"`
- Severity: Serious (informative without alt), Minor (decorative without explicit marking)
- Disability: Visual, Cognitive

**Image detection patterns (by framework)**:
- HTML: `<img`, `<input type="image"`, `<svg`
- React/JSX: `<img`, `<Image` (Next.js), `<Avatar`, custom image components
- Vue: `<img`, `<v-img`, `<nuxt-img`
- Angular: `<img`, `<mat-icon`, `[ngSrc]`

### 1.3 Form Accessibility (SC 1.3.1, 3.3.2)

**Label association**
- Pattern: `<input>`, `<select>`, `<textarea>` paired with `<label for="">`, `aria-label`, `aria-labelledby`
- Rule: every form control has an associated text label
- Severity: Critical (no label mechanism at all), Serious (label exists but not programmatically associated)
- Disability: Visual, Cognitive

**Input purpose (SC 1.3.5)**
- Pattern: `autocomplete` attribute on identity/payment fields
- Rule: fields collecting user info (name, email, phone, address, payment) have appropriate `autocomplete` values
- Severity: Moderate
- Disability: Cognitive, Motor

### 1.4 Keyboard Accessibility (SC 2.1.1, 2.1.2)

**Tabindex misuse**
- Pattern: `tabindex` values
- Rule: positive tabindex values are discouraged (disrupts natural tab order); tabindex="-1" acceptable for programmatic focus; tabindex="0" acceptable for custom interactive elements
- Severity: Moderate (positive tabindex), Informational (redundant tabindex="0" on natively focusable)
- Disability: Motor, Visual

**Keyboard trap indicators**
- Pattern: event handlers (onkeydown, onkeypress) without escape/close handlers on modals/dialogs
- Rule: all interactive components allow keyboard escape; modals have close mechanism
- Severity: Critical
- Disability: Motor, Visual

**Mouse-only interactions**
- Pattern: `onclick`, `onmouseover`, `onmousedown` without corresponding keyboard handlers (`onkeydown`, `onkeyup`, `onkeypress`)
- Rule: every mouse-triggered interaction has a keyboard equivalent
- Severity: Serious
- Disability: Motor, Visual
- Framework patterns:
  - React: `onClick` without `onKeyDown`/`onKeyUp` on non-button/link elements
  - Vue: `@click` without `@keydown` on non-button/link elements
  - Angular: `(click)` without `(keydown)` on non-button/link elements

### 1.5 ARIA Validation (SC 4.1.2)

**Valid ARIA roles**
- Rule: all `role=` values are valid WAI-ARIA roles
- Severity: Serious
- Detection: Match role values against valid role list

**Required ARIA properties**
- Rule: elements with ARIA roles have all required properties (e.g., `role="checkbox"` requires `aria-checked`)
- Severity: Serious

**ARIA state management**
- Rule: dynamic ARIA states (aria-expanded, aria-selected, aria-checked, aria-pressed) are toggled in response to user interaction
- Severity: Serious (static state that should be dynamic), Moderate (missing optional states)

### 1.6 Page Structure

**Language attribute (SC 3.1.1)**
- Pattern: `lang` attribute on `<html>` element
- Rule: html element has a valid lang attribute
- Severity: Serious
- Disability: Visual, Cognitive
- Detection: Check root element for lang attribute and valid BCP 47 value

**Page title (SC 2.4.2)**
- Pattern: `<title>` element in `<head>`
- Rule: every page has a non-empty, descriptive title
- Severity: Serious
- Disability: Visual, Cognitive

**Skip navigation (SC 2.4.1)**
- Pattern: skip link targeting `#main`, `#content`, or similar
- Rule: first focusable element is a skip link to main content
- Severity: Moderate
- Disability: Visual, Motor

### 1.7 Link Text (SC 2.4.4)

**Generic link text**
- Patterns to flag: "click here", "read more", "learn more", "here", "more", "link", "this"
- Rule: link text describes the destination or purpose, either alone or with surrounding context
- Severity: Moderate
- Disability: Visual, Cognitive

### 1.8 Viewport and Reflow (SC 1.4.10)

**Viewport restrictions**
- Pattern: `<meta name="viewport" content="...maximum-scale=1...">` or `user-scalable=no`
- Rule: viewport meta does not prevent zooming
- Severity: Critical
- Disability: Visual

### 1.9 Target Size (SC 2.5.8)

**Interactive element dimensions**
- Rule: clickable/tappable targets are at least 24x24 CSS pixels (or have sufficient spacing)
- Severity: Moderate
- Disability: Motor
- Detection: Check CSS for width/height/padding on interactive elements, min-height/min-width values

## Tier 2: Semi-Automated Check Definitions

Flag these for human verification. Provide the evidence found and the question to resolve.

| Check | SC | What Agent Detects | What Human Verifies |
|-------|----|--------------------|---------------------|
| Alt text quality | 1.1.1 | Presence, length, filename patterns | Accuracy and descriptiveness |
| Link purpose clarity | 2.4.4 | Generic text patterns | Context-based purpose |
| Error message quality | 3.3.1, 3.3.3 | Error display mechanism exists | Message helpfulness |
| Consistent navigation | 3.2.3 | Nav structure across templates | Functional consistency |
| Focus order logic | 2.4.3 | Tab sequence from tabindex/DOM | Logical meaning of order |
| Content on hover/focus | 1.4.13 | Hover/focus triggered content | Dismissibility and persistence |
| Focus visibility | 2.4.7, 2.4.11 | CSS outline/focus styles | Visibility against all backgrounds |
| ARIA role semantics | 4.1.2 | Valid syntax and required properties | Correct semantic meaning |
| Status messages | 4.1.3 | role="status", aria-live regions | Appropriate AT announcement |
| Dragging alternatives | 2.5.7 | Drag event handlers | Single-pointer alternative exists |
| Consistent help | 3.2.6 | Help/support link patterns | Consistent placement across pages |
| Redundant entry | 3.3.7 | Multi-step form patterns | Auto-population of previous values |
| Accessible auth | 3.3.8 | Auth form patterns | Cognitive function test alternatives |

## Tier 3: Manual-Only Checklist

Generate this list in the report for human testers.

| Check | SC | Level | Why Manual Required |
|-------|----|----|---------------------|
| Caption accuracy | 1.2.2 | A | Requires understanding spoken content |
| Live captions | 1.2.4 | AA | Real-time evaluation needed |
| Audio description quality | 1.2.5 | AA | Requires understanding visual content |
| Meaningful sequence | 1.3.2 | A | Requires understanding content meaning |
| Sensory characteristics | 1.3.3 | A | Requires understanding instruction context |
| Use of color (sole indicator) | 1.4.1 | A | Requires visual assessment of information coding |
| Timing adjustable | 2.2.1 | A | Requires interaction with live content |
| Pause, stop, hide | 2.2.2 | A | Requires interaction with animations/auto-updating |
| Three flashes | 2.3.1 | A | Requires measurement of flash frequency |
| Multiple ways | 2.4.5 | AA | Requires navigation architecture assessment |
| Headings and labels descriptive | 2.4.6 | AA | Requires semantic quality judgment |
| Focus not obscured | 2.4.11 | AA | Requires visual inspection with sticky elements |
| Error suggestion quality | 3.3.3 | AA | Requires contextual understanding |
| Error prevention (legal/financial) | 3.3.4 | AA | Requires workflow assessment |
| Screen reader testing | 4.1.2 | A | Requires NVDA/VoiceOver/JAWS testing |

## Detection Pattern Reference (Framework-Specific)

### React/JSX Patterns
```
// Image without alt
<img src={url} />                    // Missing alt entirely
<img src={url} alt="" />             // OK if decorative, flag if informative

// Form without label
<input type="text" />                // No label mechanism
<input type="text" aria-label="" />  // Empty aria-label

// Mouse-only interaction on div
<div onClick={handler}>              // Non-interactive element with click only
<div onClick={handler} role="button" tabIndex={0} onKeyDown={handler}>  // Correct

// Keyboard trap in modal
// Flag: modal with onClick close but no onKeyDown for Escape
```

### Vue Patterns
```
// Image without alt
<img :src="url" />                   // Missing alt
<v-img :src="url" />                 // Vuetify without alt

// Mouse-only
<div @click="handler">               // Flag: no keyboard handler
<div @click="handler" @keydown.enter="handler" tabindex="0" role="button">  // Correct
```

### Angular Patterns
```
// Image without alt
<img [src]="url" />                  // Missing alt
<img [src]="url" alt="">             // OK if decorative

// Mouse-only
<div (click)="handler()">            // Flag: no keyboard handler
<div (click)="handler()" (keydown.enter)="handler()" tabindex="0" role="button">  // Correct
```

### CSS Contrast Extraction Patterns
```
// Direct color values
color: #767676;
background-color: #FFFFFF;
background: #FFF;

// CSS custom properties (flag for manual check if resolved value unknown)
color: var(--text-secondary);
background: var(--bg-primary);

// Tailwind classes (map to values)
text-gray-500    // #6B7280
bg-white         // #FFFFFF
// Requires Tailwind config resolution for custom themes
```

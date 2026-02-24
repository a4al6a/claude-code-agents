---
name: remediation-patterns
description: Code-level remediation templates for common accessibility issues, organized by WCAG success criterion with framework-specific examples (HTML, React, Vue, Angular)
---

# Remediation Patterns

Load this skill during Phase 5 (REPORT) when generating remediation guidance. Every finding in the report should reference the appropriate pattern from this skill, adapted to the project's specific tech stack.

## Remediation Template Structure

For each finding, produce:
1. **What**: one-sentence description of the issue
2. **Why**: which users are affected and how
3. **Fix**: concrete code change (before/after)
4. **Effort**: Low (minutes), Medium (hours), High (days)

## SC 1.1.1 Non-text Content

### Missing alt text on informative image

**Why**: Screen reader users hear nothing or the filename. Cognitive disability users miss context.

HTML:
```html
<!-- Before -->
<img src="product-shoe.jpg">

<!-- After -->
<img src="product-shoe.jpg" alt="Red running shoe, Nike Air Max, side view">
```

React:
```jsx
// Before
<img src={product.imageUrl} />

// After
<img src={product.imageUrl} alt={`${product.name} - ${product.color}, ${product.style}`} />
```

**Effort**: Low per image, Medium for bulk remediation

### Decorative image not marked

```html
<!-- Before -->
<img src="decorative-divider.svg">

<!-- After -->
<img src="decorative-divider.svg" alt="" role="presentation">
```

## SC 1.3.1 Info and Relationships

### Heading hierarchy skip

```html
<!-- Before: skips from h1 to h3 -->
<h1>Products</h1>
<h3>Running Shoes</h3>

<!-- After -->
<h1>Products</h1>
<h2>Running Shoes</h2>
```

### Missing landmark regions

```html
<!-- Before -->
<div class="header">...</div>
<div class="sidebar">...</div>
<div class="content">...</div>

<!-- After -->
<header>...</header>
<aside>...</aside>
<main>...</main>
```

### Data table without headers

```html
<!-- Before -->
<table>
  <tr><td>Name</td><td>Price</td></tr>
  <tr><td>Widget</td><td>$9.99</td></tr>
</table>

<!-- After -->
<table>
  <caption>Product pricing</caption>
  <thead>
    <tr><th scope="col">Name</th><th scope="col">Price</th></tr>
  </thead>
  <tbody>
    <tr><td>Widget</td><td>$9.99</td></tr>
  </tbody>
</table>
```

## SC 1.4.3 Contrast (Minimum)

### Low contrast text

```css
/* Before: contrast ratio 3.8:1 */
.secondary-text {
  color: #767676;
  background-color: #FFFFFF;
}

/* After: contrast ratio 7.0:1 */
.secondary-text {
  color: #595959;
  background-color: #FFFFFF;
}
```

Provide specific color suggestions that meet the target ratio while staying close to the original design intent.

Common passing alternatives for light backgrounds (#FFFFFF):
- Minimum AA (4.5:1): #767676 is 4.48:1 (fails), use #757575 or darker
- Comfortable AA (5:1+): #6B6B6B (5.0:1)
- AAA (7:1): #595959 (7.0:1)

## SC 2.1.1 Keyboard / SC 2.1.2 No Keyboard Trap

### Non-interactive element with click handler

React:
```jsx
// Before: div with click but no keyboard access
<div className="card" onClick={handleSelect}>
  {content}
</div>

// After: button semantics with keyboard support
<button className="card" onClick={handleSelect}>
  {content}
</button>

// Or if div must be used:
<div
  className="card"
  onClick={handleSelect}
  onKeyDown={(e) => { if (e.key === 'Enter' || e.key === ' ') handleSelect(); }}
  role="button"
  tabIndex={0}
>
  {content}
</div>
```

Vue:
```vue
<!-- Before -->
<div class="card" @click="handleSelect">{{ content }}</div>

<!-- After -->
<button class="card" @click="handleSelect">{{ content }}</button>

<!-- Or with div -->
<div
  class="card"
  @click="handleSelect"
  @keydown.enter="handleSelect"
  @keydown.space.prevent="handleSelect"
  role="button"
  tabindex="0"
>{{ content }}</div>
```

### Modal keyboard trap

React:
```jsx
// Before: modal with no focus management
function Modal({ isOpen, onClose, children }) {
  if (!isOpen) return null;
  return <div className="modal-overlay" onClick={onClose}>
    <div className="modal-content">{children}</div>
  </div>;
}

// After: focus trap, Escape key, focus restoration
function Modal({ isOpen, onClose, children }) {
  const modalRef = useRef(null);
  const previousFocus = useRef(null);

  useEffect(() => {
    if (isOpen) {
      previousFocus.current = document.activeElement;
      modalRef.current?.focus();
    }
    return () => { previousFocus.current?.focus(); };
  }, [isOpen]);

  const handleKeyDown = (e) => {
    if (e.key === 'Escape') onClose();
    if (e.key === 'Tab') trapFocus(e, modalRef.current);
  };

  if (!isOpen) return null;
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div
        ref={modalRef}
        className="modal-content"
        role="dialog"
        aria-modal="true"
        aria-label="Dialog title"
        tabIndex={-1}
        onKeyDown={handleKeyDown}
        onClick={(e) => e.stopPropagation()}
      >
        <button onClick={onClose} aria-label="Close dialog">X</button>
        {children}
      </div>
    </div>
  );
}
```

**Effort**: Medium

## SC 2.4.1 Bypass Blocks

### Missing skip navigation

```html
<!-- Add as first child of body -->
<body>
  <a href="#main-content" class="skip-link">Skip to main content</a>
  <header>...</header>
  <nav>...</nav>
  <main id="main-content">...</main>
</body>
```

```css
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  padding: 8px 16px;
  background: #000;
  color: #fff;
  z-index: 100;
}
.skip-link:focus {
  top: 0;
}
```

**Effort**: Low

## SC 2.4.7 Focus Visible

### Missing or suppressed focus indicator

```css
/* Before: focus outline removed */
*:focus { outline: none; }

/* After: visible focus indicator */
*:focus-visible {
  outline: 2px solid #005fcc;
  outline-offset: 2px;
}
```

**Effort**: Low

## SC 2.5.8 Target Size (Minimum)

### Interactive element too small

```css
/* Before: 16x16 icon button */
.icon-button {
  width: 16px;
  height: 16px;
}

/* After: meets 24x24 minimum */
.icon-button {
  min-width: 24px;
  min-height: 24px;
  /* Or keep visual size but add padding */
  padding: 4px;
}
```

## SC 3.1.1 Language of Page

### Missing lang attribute

```html
<!-- Before -->
<html>

<!-- After -->
<html lang="en">
```

React (Next.js):
```jsx
// In _document.js or layout.tsx
<html lang="en">
```

**Effort**: Low

## SC 3.3.2 Labels or Instructions

### Form input without label

```html
<!-- Before -->
<input type="email" placeholder="Enter email">

<!-- After: visible label -->
<label for="email-input">Email address</label>
<input type="email" id="email-input" placeholder="e.g. user@example.com">

<!-- Alternative: aria-label for icon-only fields -->
<input type="search" aria-label="Search products">
```

React:
```jsx
// Before
<input type="email" placeholder="Enter email" />

// After
<label htmlFor="email-input">Email address</label>
<input type="email" id="email-input" placeholder="e.g. user@example.com" />
```

**Effort**: Low per field

## SC 3.3.1 Error Identification

### Error without programmatic association

```html
<!-- Before: error visible but not associated -->
<input type="email" id="email">
<span class="error">Please enter a valid email</span>

<!-- After: error linked to input -->
<input type="email" id="email" aria-invalid="true" aria-describedby="email-error">
<span id="email-error" class="error" role="alert">Please enter a valid email address</span>
```

## SC 4.1.2 Name, Role, Value

### Custom widget missing ARIA

```html
<!-- Before: custom dropdown with no semantics -->
<div class="dropdown" onclick="toggle()">
  <div class="selected">Option 1</div>
  <div class="options">
    <div onclick="select(1)">Option 1</div>
    <div onclick="select(2)">Option 2</div>
  </div>
</div>

<!-- After: proper ARIA combobox pattern -->
<div class="dropdown">
  <button
    role="combobox"
    aria-expanded="false"
    aria-haspopup="listbox"
    aria-controls="options-list"
    aria-label="Select option"
  >
    Option 1
  </button>
  <ul id="options-list" role="listbox" hidden>
    <li role="option" aria-selected="true">Option 1</li>
    <li role="option" aria-selected="false">Option 2</li>
  </ul>
</div>
```

**Effort**: High (custom widgets require significant refactoring)

## SC 4.1.3 Status Messages

### Dynamic message not announced

```jsx
// Before: message appears visually but screen reader misses it
<div className="success-message">{message}</div>

// After: live region announces to screen readers
<div className="success-message" role="status" aria-live="polite">
  {message}
</div>

// For errors (more urgent)
<div className="error-message" role="alert" aria-live="assertive">
  {errorMessage}
</div>
```

## SC 1.4.10 Reflow

### Viewport zoom prevention

```html
<!-- Before: prevents user zoom -->
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

<!-- After: allows zoom -->
<meta name="viewport" content="width=device-width, initial-scale=1">
```

## SC 1.3.5 Identify Input Purpose

### Missing autocomplete on identity fields

```html
<!-- Before -->
<input type="text" name="fname">
<input type="email" name="email">
<input type="tel" name="phone">

<!-- After -->
<input type="text" name="fname" autocomplete="given-name">
<input type="email" name="email" autocomplete="email">
<input type="tel" name="phone" autocomplete="tel">
```

Common autocomplete values: `name`, `given-name`, `family-name`, `email`, `tel`, `street-address`, `postal-code`, `country`, `cc-number`, `cc-exp`, `cc-csc`, `username`, `new-password`, `current-password`.

## Effort Estimation Guide

| Issue Category | Typical Effort | Quick Win? |
|---------------|---------------|------------|
| Missing lang attribute | Low (minutes) | Yes |
| Skip navigation | Low (30 min) | Yes |
| Form label association | Low per field | Yes |
| Alt text addition | Low per image | Yes |
| Focus indicator restoration | Low (30 min) | Yes |
| Autocomplete attributes | Low per form | Yes |
| Heading hierarchy fixes | Low (1-2 hours) | Yes |
| Color contrast adjustments | Medium (design review) | Depends |
| Keyboard handler addition | Medium per component | No |
| Modal focus management | Medium (2-4 hours) | No |
| Custom widget ARIA | High (1-3 days per widget) | No |
| Full landmark restructure | High (multi-day) | No |

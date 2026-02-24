/**
 * Example: Inaccessible React Components
 * This file demonstrates common accessibility failures in React component code.
 * An accessibility-assessor agent would detect the issues annotated below.
 */

import React, { useState } from 'react';

// ISSUE: Modal with no focus trap, no Escape key handler, no focus restoration
// (SC 2.1.2 No Keyboard Trap, Critical | SC 2.4.7 Focus Visible, Serious)
// Affects: Motor, Visual
function Modal({ isOpen, onClose, children }: {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
}) {
  if (!isOpen) return null;

  return (
    // ISSUE: No role="dialog", no aria-modal, no aria-label
    // (SC 4.1.2 Name Role Value, Serious)
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* ISSUE: Close button has no accessible name (SC 4.1.2, Serious) */}
        <button onClick={onClose} className="close-btn">X</button>
        {children}
      </div>
    </div>
  );
}

// ISSUE: Product card uses div with onClick but no keyboard handler
// (SC 2.1.1 Keyboard, Critical)
// Affects: Motor, Visual
function ProductCard({ product }: { product: { id: number; name: string; price: number; imageUrl: string } }) {
  return (
    // ISSUE: Non-interactive element with click handler, no role, no tabIndex, no keyboard handler
    <div className="product-card" onClick={() => window.location.href = `/product/${product.id}`}>
      {/* ISSUE: Empty alt on informative product image (SC 1.1.1, Serious) */}
      <img src={product.imageUrl} alt="" />
      {/* ISSUE: Heading hierarchy likely wrong - h4 without preceding h3 */}
      <h4>{product.name}</h4>
      <p>${product.price.toFixed(2)}</p>
      {/* ISSUE: Generic link text "View" with no context (SC 2.4.4, Moderate) */}
      <a href={`/product/${product.id}`}>View</a>
    </div>
  );
}

// ISSUE: Tabs component with incomplete ARIA and keyboard navigation
function Tabs({ tabs }: { tabs: { label: string; content: string }[] }) {
  const [activeTab, setActiveTab] = useState(0);

  return (
    <div>
      {/* ISSUE: Tab list has no role="tablist" (SC 4.1.2, Serious) */}
      <div className="tab-list">
        {tabs.map((tab, index) => (
          // ISSUE: Tabs use divs with onClick, missing:
          // - role="tab"
          // - aria-selected
          // - aria-controls
          // - keyboard navigation (Arrow keys)
          // (SC 4.1.2, Serious | SC 2.1.1, Serious)
          <div
            key={index}
            className={`tab ${index === activeTab ? 'active' : ''}`}
            onClick={() => setActiveTab(index)}
          >
            {tab.label}
          </div>
        ))}
      </div>
      {/* ISSUE: Tab panel has no role="tabpanel", no aria-labelledby (SC 4.1.2, Serious) */}
      <div className="tab-panel">
        {tabs[activeTab].content}
      </div>
    </div>
  );
}

// ISSUE: Notification component with no live region
// (SC 4.1.3 Status Messages, Serious)
// Affects: Visual (screen reader users)
function Notification({ message, type }: { message: string; type: 'success' | 'error' }) {
  // ISSUE: Dynamic message not announced to assistive technologies
  // Missing role="status" or role="alert" and aria-live
  return (
    <div className={`notification notification-${type}`}>
      {/* ISSUE: Icon conveys meaning through color alone (SC 1.4.1, Serious) */}
      <span className={type === 'error' ? 'text-red' : 'text-green'}>
        {type === 'error' ? '!' : '✓'}
      </span>
      {message}
    </div>
  );
}

// ISSUE: Accordion with incomplete ARIA
function Accordion({ items }: { items: { title: string; content: string }[] }) {
  const [openIndex, setOpenIndex] = useState<number | null>(null);

  return (
    <div>
      {items.map((item, index) => (
        <div key={index}>
          {/* ISSUE: Accordion header is a div, not a button or heading
              Missing: role="button", aria-expanded, aria-controls
              (SC 4.1.2, Serious | SC 2.1.1, Serious) */}
          <div
            className="accordion-header"
            onClick={() => setOpenIndex(openIndex === index ? null : index)}
          >
            {item.title}
          </div>
          {openIndex === index && (
            // ISSUE: No role="region", no aria-labelledby (SC 4.1.2, Moderate)
            <div className="accordion-content">
              {item.content}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}

// ISSUE: Search form with accessibility failures
function SearchBar() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<string[]>([]);

  return (
    <div>
      {/* ISSUE: Input with no visible label, placeholder-only (SC 3.3.2, Critical) */}
      {/* ISSUE: Missing role="search" on container or form (SC 1.3.1, Moderate) */}
      {/* ISSUE: Missing autocomplete="off" or appropriate value (SC 1.3.5, Minor) */}
      <input
        type="text"
        placeholder="Search..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      {/* ISSUE: Search results container has no aria-live for dynamic updates (SC 4.1.3, Serious) */}
      <div className="search-results">
        {results.map((r, i) => <div key={i}>{r}</div>)}
      </div>
    </div>
  );
}

export { Modal, ProductCard, Tabs, Notification, Accordion, SearchBar };

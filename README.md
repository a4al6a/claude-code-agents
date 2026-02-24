# Claude Code Agents

A collection of specialized agents for software development workflows, designed to work together in pipelines for comprehensive problem-solving and code quality improvement.

## Agents Overview

### Problem Analysis & Planning Agents

#### problem-analyst
Analyzes and understands complex problems thoroughly before implementation begins. Creates comprehensive problem domain understanding and identifies core requirements.

**Use when:** You have complex requirements that need deep analysis before development starts.

#### user-story-writer  
Breaks down problem statements into granular, implementable user stories using techniques like Elephant Carpaccio. Creates small, independent, valuable, and testable stories.

**Use when:** You need to decompose features into actionable user stories with clear acceptance criteria.

#### atdd-developer
Implements user stories using Acceptance Test Driven Development (ATDD) methodology. Follows the Red-Green-Refactor cycle systematically. During the REFACTOR phase, delegates to the **clean-coder** agent for code quality improvements.

**Use when:** You have user stories with acceptance criteria that need systematic implementation using TDD/BDD practices.

### Code Quality Agents

#### clean-coder
Refactors code to improve readability, maintainability, and structure following clean code principles. Applies SOLID, GRASP, and other design principles to transform working code into clean, well-structured code. Based on Robert C. Martin's *"Clean Code"* principles.

**Use when:** You have working code that needs to be cleaned up, or during the REFACTOR phase of TDD to improve code quality while keeping tests green.

#### code-smell-detector
Analyzes code for potential quality issues, design problems, and maintainability concerns. Identifies patterns like God Objects, violation of SOLID principles, and other code smells.

**Use when:** You want to identify problematic patterns in existing code before refactoring.

#### refactoring-expert
Generates comprehensive refactoring recommendations based on detected code smells. Provides specific techniques and step-by-step guidance for improving code quality. Includes knowledge of both Martin Fowler's *"Refactoring: Improving the Design of Existing Code"* and Joshua Kerievsky's *"Refactoring to Patterns"*.

**Use when:** You have identified code smells and need expert guidance on refactoring approaches.

#### legacy-code-expert
Specializes in safely modifying legacy code that lacks tests. Based on Michael Feathers' *"Working Effectively with Legacy Code"*, it applies techniques including seam identification, dependency-breaking, and characterization testing strategies.

**Use when:** You need to modify code that has no tests, break dependencies to enable testability, or introduce tests into existing codebases.

### Compliance & Auditing Agents

#### software-system-auditor
Audits software systems against regulatory compliance frameworks, producing one separate audit report per selected regulation. Supports 12 frameworks: SOX, SOC 2, GDPR, HIPAA, PCI DSS 4.0, NIST CSF 2.0, ISO 27001:2022, FedRAMP, CCPA/CPRA, DORA, NIS2, and CMMC 2.0. Presents frameworks as a multi-select list, then runs a 7-phase audit workflow (SCOPE > DISCOVER > COLLECT > ANALYZE > SYNTHESIZE > REPORT > VERIFY) with cross-framework compliance mapping so a single finding is traced to every applicable regulation's specific control IDs. Includes 3 skill files covering regulatory frameworks, audit methodology, and a 15-control x 12-framework mapping matrix.

**Use when:** You need to assess a codebase's compliance posture against one or more regulatory frameworks, generate audit evidence, or produce framework-specific remediation guidance.

#### accessibility-assessor
Evaluates software system accessibility compliance against WCAG 2.1/2.2, ADA, Section 508, EN 301 549, EAA, AODA, and other international standards. Analyzes codebases (HTML, CSS, JS, React, Angular, Vue) using a three-tier check architecture: Tier 1 (fully automated, 9 check categories), Tier 2 (semi-automated, flagged for human verification), and Tier 3 (manual-only checklist). Produces structured assessment reports with severity classification (Critical/Serious/Moderate/Minor), WCAG success criteria mapping, disability group impact, conformance scoring, and remediation guidance with framework-specific code examples. Runs a 6-phase workflow (SCOPE > DISCOVER > ANALYZE > CLASSIFY > REPORT > VERIFY) and delegates to external tools (axe-core, Pa11y, Lighthouse) when available. Includes 3 skill files covering WCAG criteria and standards, evaluation rules, and remediation patterns.

**Use when:** You need to assess a codebase's accessibility compliance, identify WCAG violations, generate accessibility audit reports, or verify remediation of previously identified accessibility issues.

### Codebase Assessment Agents

#### cognitive-load-analyzer
Calculates a Cognitive Load Index (CLI) score (0-1000) for a codebase, measuring how much mental effort it demands from developers. Analyzes 8 dimensions: Structural Complexity, Nesting Depth, Volume/Size, Naming Quality, Coupling, Cohesion, Duplication, and Navigability. Uses sigmoid normalization, P90-weighted aggregation, and a Python calculation library for deterministic, reproducible results.

**Use when:** You want to assess how complex a codebase is to understand, identify cognitive hotspots, prioritize refactoring, or prepare for developer onboarding.

#### test-design-reviewer
Evaluates test code quality using Dave Farley's 8 Properties of Good Tests, producing a Farley Index score (0-10) with per-property breakdown, signal evidence, worst offenders, and prioritized recommendations. Uses a two-phase methodology: static signal detection (deterministic) blended 60/40 with LLM semantic assessment. Supports Java, Python, JavaScript/TypeScript, Go, and C#.

**Use when:** You want a quantitative assessment of test suite quality, need to identify test smells across a codebase, or want evidence-anchored recommendations for improving test design.

#### system-walkthrough
Analyzes codebases and produces narrative-driven, slide-based presentations (Marp Markdown) covering design, architecture, code, testing, and infrastructure. Runs a 6-layer analysis pipeline (static structure, behavioral/git mining, architecture recovery, decision recovery, test quality, infrastructure) and synthesizes findings into a 7-section slide deck that tells the system's story — explaining not just what the code does, but why it was built that way. Also validates AI-generated code quality with a Health Score (0-10). Based on program comprehension research (Von Mayrhauser, Pennington, Sillito) and documentation frameworks (Arc42, C4, Diataxis).

**Use when:** You need to onboard new developers, prepare a system walkthrough presentation, audit AI-generated code quality, or document a system's architecture and design decisions after the fact.

## Agent Pipelines

### Pipeline 1: Feature Development (Problem → Stories → Implementation)

```
Problem Statement → problem-analyst → user-story-writer → atdd-developer (→ clean-coder)
```

**Workflow:**
1. **problem-analyst**: Analyzes the problem domain, identifies core requirements and constraints
2. **user-story-writer**: Applies Elephant Carpaccio technique to create small, implementable user stories
3. **atdd-developer**: Implements each story using Red-Green-Refactor ATDD methodology
   - RED: Write failing acceptance tests
   - GREEN: Implement minimal code to pass tests
   - REFACTOR: Delegates to **clean-coder** for code quality improvements

**Best for:** New feature development, complex requirements, systematic implementation

### Pipeline 2: Code Quality Improvement (Detection → Analysis → Refactoring)

```
Legacy Code → code-smell-detector → refactoring-expert → Implementation
```

**Workflow:**
1. **code-smell-detector**: Identifies code smells, design issues, and quality problems
2. **refactoring-expert**: Analyzes findings and creates comprehensive refactoring plan
3. Manual implementation following the refactoring recommendations

**Best for:** Legacy code improvement, technical debt reduction, code quality enhancement

### Pipeline 3: Safe Legacy Code Modification (Analysis → Dependency Breaking → Testing)

```
Untested Code → legacy-code-expert → Characterization Tests → Safe Modification
```

**Workflow:**
1. **legacy-code-expert**: Analyzes code for seams, identifies dependency-breaking techniques
2. Apply recommended techniques to create testability
3. Write characterization tests to document existing behavior
4. Make changes safely with test coverage

**Best for:** Modifying code without tests, introducing testability, breaking problematic dependencies

### Pipeline 4: Hybrid Development (Analysis + Quality)

```
Problem Statement → problem-analyst → user-story-writer → atdd-developer → code-smell-detector → refactoring-expert
```

**Workflow:**
1. Full feature development pipeline (1-3)
2. Quality review of implemented code (4-5)
3. Iterative improvement

**Best for:** High-quality feature development with built-in quality assurance

### Pipeline 5: Test Quality Assessment

```
Test Suite → test-design-reviewer → Improvement Plan → Refactored Tests
```

**Workflow:**
1. **test-design-reviewer**: Analyzes test files against Farley's eight properties
2. Review the Farley Score and detailed property breakdown
3. Apply recommendations to improve test quality

**Best for:** Auditing existing test suites, improving flaky tests, ensuring TDD compliance

### Pipeline 6: System Understanding (Walkthrough → Deep Assessment)

```
Codebase → system-walkthrough → Slide Deck + Analysis Data
                                        ↓
                               cognitive-load-analyzer → CLI Score
                               code-smell-detector → Smell Report
                               test-design-reviewer → Farley Score
```

**Workflow:**
1. **system-walkthrough**: Analyzes the full codebase (structure, git history, architecture, decisions, tests, infrastructure) and produces a narrative slide deck
2. Use the walkthrough findings to target deeper analysis:
   - **cognitive-load-analyzer**: Quantify cognitive complexity of flagged hotspots
   - **code-smell-detector**: Deep-dive into modules identified as problematic
   - **test-design-reviewer**: Assess test quality for suites flagged as weak

**Best for:** Developer onboarding, AI-generated code auditing, system documentation, architecture review

### Pipeline 7: Compliance Auditing (Selection → Audit → Per-Framework Reports)

```
Codebase → software-system-auditor → Framework Selection (multi-select)
                                            ↓
                                   SCOPE → DISCOVER → COLLECT → ANALYZE → SYNTHESIZE → REPORT
                                            ↓
                                   One report per selected regulation
```

**Workflow:**
1. **software-system-auditor**: Presents all 12 supported compliance frameworks for multi-select
2. Runs 7-phase audit: scopes the system, discovers architecture, collects evidence, analyzes findings, synthesizes cross-framework mappings, generates reports
3. Produces one separate audit report per selected regulation with framework-specific control IDs, compliance scores, and remediation guidance
4. Cross-framework mapping ensures a single finding (e.g., missing MFA) appears in every relevant report mapped to that framework's requirements

**Best for:** Regulatory compliance assessment, audit preparation, security posture evaluation, multi-framework gap analysis

### Pipeline 8: Accessibility Compliance Assessment (Scan → Assessment → Remediation Verification)

```
Codebase → accessibility-assessor (*assess) → Assessment Report
                                                      ↓
                                             Fix Issues → accessibility-assessor (*verify) → Verification Report
```

**Workflow:**
1. **accessibility-assessor** (`*assess`): Scopes the codebase, discovers accessibility-relevant artifacts, runs three-tier checks (automated + semi-automated flags + manual checklist), classifies findings by severity and WCAG criteria, produces assessment report with conformance score
2. Development team remediates findings using the code-level fix examples in the report
3. **accessibility-assessor** (`*verify`): Re-checks previous findings, classifies as remediated/partially remediated/unchanged/regressed, produces updated score

**Best for:** WCAG compliance assessment, ADA/Section 508 audit preparation, accessibility gap analysis, pre-launch accessibility review, remediation tracking

## Examples

### Example 1: Tax Calculator Service
Located in `examples/tax-calculator/` - demonstrates the complete feature development pipeline from problem analysis through ATDD implementation.

### Example 2: Legacy Code Refactoring
Located in `examples/legacy-refactoring/` - shows how to identify code smells and create systematic refactoring plans for a UserManager class with multiple responsibilities.

### Example 3: Refactoring to Patterns
Located in `examples/refactoring-to-patterns/` - demonstrates applying Joshua Kerievsky's pattern-directed refactoring techniques to an OrderProcessingSystem, showing how to evolve code toward design patterns through safe, incremental steps.

### Example 4: Test Quality Assessment - Low Score
Located in `examples/test-quality-low-score/` - demonstrates an expression parser with poorly designed tests that violate Farley's testing principles, resulting in a low Farley Score. Shows common anti-patterns to avoid.

### Example 5: Test Quality Assessment - High Score
Located in `examples/test-quality-high-score/` - demonstrates the same expression parser with well-designed tests following TDD principles, achieving a high Farley Score. Shows best practices for test design.

### Example 6: System Walkthrough - Express API
Located in `system-walkthrough/examples/express-api-walkthrough/` - demonstrates a full system walkthrough of a small Express.js payment API, showing the 7-section slide structure, Mermaid diagrams, inferred design decisions, and hotspot analysis.

### Example 7: System Walkthrough - AI-Generated Code Audit
Located in `system-walkthrough/examples/ai-generated-fastapi/` - demonstrates the AI code validation dimension on a FastAPI application generated by Claude Code, showing health score computation, god class detection, test effectiveness analysis, and audit flags woven into the walkthrough narrative.

### Example 8: Accessibility Assessment - Sample Issues & Report
Located in `accessibility-assessor/examples/` - demonstrates the accessibility assessor agent with sample inaccessible HTML pages, React components with common accessibility violations, and a complete assessment report showing scoring, WCAG conformance matrix, and remediation guidance.

## Installation

### Quick Install (All Agents)

```bash
# Clone the repository
git clone https://github.com/yourusername/claude-code-agents.git
cd claude-code-agents

# Install all agents
./install.sh
```

### Installation Options

```bash
# List available agents
./install.sh list

# Check installation status
./install.sh status

# Install all agents (with overwrite prompt for existing)
./install.sh install

# Install all agents (force overwrite without prompts)
./install.sh install -f

# Install a specific agent
./install.sh install code-smell-detector

# Uninstall a specific agent
./install.sh uninstall code-smell-detector

# Uninstall all agents
./install.sh uninstall
```

### Supported Platforms

The installer works on:
- **macOS** (zsh/bash)
- **Linux** (bash)
- **Windows** (Git Bash, WSL)

### Manual Installation

If you prefer manual installation, copy the agent `.md` files to `~/.claude/agents/`:

```bash
mkdir -p ~/.claude/agents
cp code-smell-detector/code-smell-detector.md ~/.claude/agents/
# ... repeat for other agents
```

Some agents (e.g., cognitive-load-analyzer, system-walkthrough, accessibility-assessor) include skills that must be copied to `~/.claude/skills/`:

```bash
mkdir -p ~/.claude/skills
cp -r cognitive-load-analyzer/skills/cognitive-load-analyzer ~/.claude/skills/
cp -r system-walkthrough/skills/system-walkthrough ~/.claude/skills/
cp -r accessibility-assessor/skills/accessibility-assessor ~/.claude/skills/
```

After installation, restart Claude Code or start a new session to use the agents.

## Getting Started

1. Choose the appropriate pipeline based on your needs
2. Start with the first agent in the pipeline
3. Use each agent's output as input for the next agent
4. Follow the systematic approach for consistent, high-quality results

Each example folder contains detailed documentation showing how to use these agents effectively in practice.
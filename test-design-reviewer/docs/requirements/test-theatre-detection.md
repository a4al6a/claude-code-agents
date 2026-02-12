# Requirements: Test Theatre Detection for test-design-reviewer

## Problem Statement

The test-design-reviewer agent currently evaluates test quality using Dave Farley's 8 Properties of Good Tests. It detects structural anti-patterns (sleep, reflection, shared state, trivial assertions) but has a significant blind spot: **tests that look structurally correct but don't actually verify production behaviour** -- commonly known as "test theatre."

A test that sets up a mock, programs its return value, and then asserts that the mock returns that value will score well on every structural dimension (clean naming, single assertion, no I/O, no shared state) while providing zero value. The agent cannot reliably detect this today.

### Evidence

- The research document (`test-design-reviewer-research.md:595-602`) explicitly acknowledges this gap under "Static Analysis Cannot Detect Semantic Quality"
- The mock anti-patterns research (`mock-anti-patterns-research.md`) confirms that **no mainstream static analysis tool** (SonarQube, PMD, ESLint, tsDetect, testsmells.org) has rules for any of the four mock anti-patterns
- The gap spans all five language ecosystems the agent supports (Java, Python, JS/TS, Go, C#)

### Scope

Four anti-patterns to detect, ordered by severity:

| Anti-Pattern | Severity | Description |
|---|---|---|
| **Mock Tautology** | Critical | Assert that a mock returns its programmed value, with no production code in between |
| **No Production Code Exercised** | Critical | Test where every object is a mock -- no real class is instantiated or called |
| **Over-Specified Mock Interactions** | High | verify() with exact counts, argument matchers, call ordering -- tests HOW not WHAT |
| **Testing Internal Details** | High | Asserting on intermediate state, internal call ordering, white-box mock expectations |

### Mocking Frameworks in Scope

| Language | Frameworks |
|---|---|
| Java | Mockito |
| Python | unittest.mock, pytest-mock |
| JavaScript/TypeScript | Jest mocks, Sinon |
| Go | testify/mock, gomock |
| C# | Moq, NSubstitute |

---

## User Stories

### US-1: Detect Mock Tautology (Static)

**As a** developer reviewing test quality,
**I want** the agent to detect tests that configure a mock's return value and then assert on that same mock's return value,
**so that** I know which tests are testing the mocking framework instead of my production code.

#### Acceptance Criteria

```gherkin
Feature: Mock tautology static detection

  Scenario: Java Mockito mock tautology
    Given a test file containing:
      """
      when(service.findById(1)).thenReturn(user);
      User result = service.findById(1);
      assertEquals(user, result);
      """
    And "service" is created via mock(UserService.class)
    When the agent collects signals for this test method
    Then a "mock-tautology" negative signal is recorded for property N (Necessary)
    And a "mock-tautology" negative signal is recorded for property M (Maintainable)
    And the signal includes the file:line reference

  Scenario: Python unittest.mock tautology
    Given a test file containing:
      """
      service.find_by_id.return_value = expected_user
      result = service.find_by_id(1)
      self.assertEqual(expected_user, result)
      """
    And "service" is created via Mock()
    When the agent collects signals for this test method
    Then a "mock-tautology" negative signal is recorded

  Scenario: Jest mock tautology
    Given a test file containing:
      """
      const service = { findById: jest.fn().mockReturnValue(user) };
      const result = service.findById(1);
      expect(result).toEqual(user);
      """
    When the agent collects signals for this test method
    Then a "mock-tautology" negative signal is recorded

  Scenario: Not a tautology -- production code in between
    Given a test file containing:
      """
      when(repository.findById(1)).thenReturn(user);
      UserService service = new UserService(repository);
      User result = service.getUser(1);
      assertEquals(user, result);
      """
    When the agent collects signals for this test method
    Then no "mock-tautology" signal is recorded
    Because a real class (UserService) is instantiated and invoked between setup and assertion
```

---

### US-2: Detect No Production Code Exercised (Static)

**As a** developer reviewing test quality,
**I want** the agent to detect tests where every object is a mock and no real class is instantiated,
**so that** I know which tests are exercising only mock framework machinery.

#### Acceptance Criteria

```gherkin
Feature: No production code exercised detection

  Scenario: All-mock test with verify-only assertions
    Given a test method that:
      - Creates 3 mock objects via mock()
      - Calls methods only on those mock objects
      - Uses verify() as its only assertions
      - Does NOT instantiate any non-mock class with "new"
    When the agent collects signals for this test method
    Then a "no-production-code" negative signal is recorded for property N (Necessary)
    And the signal severity is "High"

  Scenario: Test with production code present
    Given a test method that:
      - Creates 2 mock objects (repository, gateway)
      - Creates 1 real object: "new OrderService(repository, gateway)"
      - Calls a method on the real OrderService
      - Asserts on the return value
    When the agent collects signals for this test method
    Then no "no-production-code" signal is recorded

  Scenario: Test with static method call on production class
    Given a test method that:
      - Creates 1 mock object
      - Calls a static method on a production class
    When the agent collects signals for this test method
    Then no "no-production-code" signal is recorded
    Because a production class is exercised via static method
```

---

### US-3: Detect Over-Specified Mock Interactions (Static)

**As a** developer reviewing test quality,
**I want** the agent to detect tests that over-specify mock interactions through exact call counts, call ordering, and verifyNoMoreInteractions,
**so that** I know which tests will break on any refactoring even when behaviour is preserved.

#### Acceptance Criteria

```gherkin
Feature: Over-specified mock interactions detection

  Scenario: Exact call count verification
    Given a test containing "verify(service, times(1)).process(any())"
    When the agent collects signals for this test method
    Then an "over-specified-interaction" negative signal is recorded for property M (Maintainable)
    And the signal severity is "Medium"

  Scenario: Call ordering verification
    Given a test containing "InOrder inOrder = inOrder(a, b, c)"
    When the agent collects signals for this test method
    Then an "over-specified-interaction" negative signal is recorded
    And the signal severity is "High"

  Scenario: No more interactions
    Given a test containing "verifyNoMoreInteractions(service)"
    When the agent collects signals for this test method
    Then an "over-specified-interaction" negative signal is recorded
    And the signal severity is "High"

  Scenario: Simple verify without count (acceptable)
    Given a test containing "verify(service).process(any())"
    And no times(), InOrder, or verifyNoMoreInteractions
    When the agent collects signals for this test method
    Then no "over-specified-interaction" signal is recorded
```

---

### US-4: Detect Testing Internal Details (Static)

**As a** developer reviewing test quality,
**I want** the agent to detect tests that verify internal implementation details through ArgumentCaptor deep inspection, verify(never()) mirroring branches, type checking assertions, and high verify-to-assert ratios,
**so that** I know which tests are coupled to implementation rather than behaviour.

#### Acceptance Criteria

```gherkin
Feature: Testing internal details detection

  Scenario: ArgumentCaptor with deep property inspection
    Given a test containing:
      """
      ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
      verify(repo).save(captor.capture());
      assertEquals("PROCESSING", captor.getValue().getInternalStatus());
      """
    When the agent collects signals for this test method
    Then an "internal-detail-testing" negative signal is recorded for property M (Maintainable)

  Scenario: verify(never()) mirroring control flow branches
    Given a test that uses both:
      - verify(discounts).getPremiumDiscount(user)
      - verify(discounts, never()).getStandardDiscount(any())
    When the agent collects signals for this test method
    Then an "internal-detail-testing" negative signal is recorded

  Scenario: Type hierarchy assertion
    Given a test containing "Assert.IsAssignableFrom<IProcessor>(processor)"
    When the agent collects signals for this test method
    Then an "internal-detail-testing" negative signal is recorded

  Scenario: High verify-to-assert ratio
    Given a test with 5 verify() calls and 0 assertEquals() calls
    When the agent collects signals for this test method
    Then an "internal-detail-testing" negative signal is recorded
    And the signal note indicates "interaction-heavy test (5:0 verify-to-assert ratio)"
```

---

### US-5: LLM Rubric Guidance for Test Theatre

**As a** developer reviewing test quality,
**I want** the LLM assessment phase to have explicit guidance on recognising test theatre patterns,
**so that** the 40% LLM scoring component actively looks for semantic test theatre beyond what static signals catch.

#### Acceptance Criteria

```gherkin
Feature: LLM rubric test theatre guidance

  Scenario: LLM rubric for Necessary (N) includes test theatre
    Given the farley-properties-and-scoring skill
    When the LLM evaluates a test file for the Necessary property
    Then the rubric explicitly instructs the LLM to check for:
      - Tests that only verify mock return values (mock tautology)
      - Tests with no production class under test
      - Tests that would pass even if the production code were deleted

  Scenario: LLM rubric for Maintainable (M) includes interaction coupling
    Given the farley-properties-and-scoring skill
    When the LLM evaluates a test file for the Maintainable property
    Then the rubric explicitly instructs the LLM to check for:
      - Over-specified mock interactions (exact counts, ordering)
      - Tests asserting on implementation details rather than observable behaviour
      - Tests that would break on a behaviour-preserving refactoring

  Scenario: LLM rubric for First/TDD (T) includes mock-heavy indicator
    Given the farley-properties-and-scoring skill
    When the LLM evaluates a test file for the First (TDD) property
    Then the rubric explicitly notes that mock-heavy tests with no production code suggest test-after, not TDD
```

---

### US-6: Update Signal Detection Patterns Skill

**As a** maintainer of the test-design-reviewer agent,
**I want** the signal-detection-patterns skill updated with mock-specific anti-pattern signals for all supported languages,
**so that** Phase 2 (Signal Collection) has concrete patterns to scan for.

#### Acceptance Criteria

- New section "Mock Anti-Patterns" added to signal-detection-patterns.md
- Contains signal tables for all four anti-patterns
- Contains language-specific regex/detection patterns for all 9 mocking frameworks
- Each signal is mapped to the Farley properties it affects (N, M, or both)
- Each signal has a severity level (Critical/High/Medium/Low)
- Signal overlap with existing signals is documented

---

### US-7: Update Agent Definition

**As a** maintainer of the test-design-reviewer agent,
**I want** the test-design-reviewer.md agent definition updated to reference test theatre detection in its workflow,
**so that** the agent knows to scan for mock anti-patterns during Phase 2 and to include test theatre findings in the report.

#### Acceptance Criteria

- Phase 2 (Signal Collection) lists mock anti-pattern signals alongside existing signals
- Phase 3 (Scoring) notes that mock anti-patterns affect N and M property scores
- Phase 4 (Reporting) includes mock anti-patterns in the signal summary table
- No changes to the Farley Index formula or property weights
- No new Farley property is introduced

---

## Files to Update

| File | Change Type | Stories |
|---|---|---|
| `skills/test-design-reviewer/signal-detection-patterns.md` | Add mock anti-pattern signals and language-specific patterns | US-1, US-2, US-3, US-4, US-6 |
| `skills/test-design-reviewer/farley-properties-and-scoring.md` | Add LLM rubric guidance for test theatre | US-5 |
| `test-design-reviewer.md` | Reference mock anti-patterns in workflow phases | US-7 |
| `docs/test-design-reviewer-research.md` | Add section on mock anti-patterns with evidence base | Reference |

## Out of Scope

- No new Farley property (no 9th property, no formula change)
- No Python CLI calculator changes (signals are collected by the agent, not a separate tool)
- No new examples (existing high/low examples remain; new examples can be added in a follow-up)
- No AST-based detection (regex/structural patterns only; AST is a future enhancement)

## Risks

| Risk | Mitigation |
|---|---|
| False positives on mock tautology (e.g., integration-style tests where mock setup is intentional) | Use severity levels; LLM assessment as second opinion; document known false-positive scenarios |
| False positives on verify(times(1)) when idempotency is a business requirement | Note in rubric that exact-count verification MAY be legitimate for idempotency; flag as Medium severity, not Critical |
| Regex patterns miss aliased variables or multi-line expressions | Accept MEDIUM confidence for static detection; rely on LLM phase (40%) for semantic catch |
| Over-specified detection inflates Maintainable (M) penalty | Signals affect M and N scores proportionally via existing sigmoid normalization; no special-case needed |

## Dependencies

- Research: `docs/mock-anti-patterns-research.md` (complete)
- Existing agent: all current skills and agent definition files

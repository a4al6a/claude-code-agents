# Test Design Review

## Farley Index: 2.47 / 10.0 (Critical)

This test suite is a comprehensive showcase of mock anti-patterns (AP1-AP4). Of the 12 test methods, only 4 instantiate the production class `OrderService`. The majority of tests verify mock framework behavior rather than production behavior. The test suite would continue to pass if the production code were deleted, making these tests actively harmful -- they provide false confidence.

### Property Breakdown

| Property | Static | LLM | Blended | Weight | Weighted | Key Evidence |
|---|---|---|---|---|---|---|
| Understandable (U) | 0.1 | 2.0 | 0.87 | 1.50x | 1.30 | 12/12 methods use cryptic `test_` naming; zero `@Nested`, zero `@DisplayName`; AP4 signals obscure intent |
| Maintainable (M) | 0.2 | 1.5 | 0.73 | 1.50x | 1.10 | 11/12 methods have mock anti-pattern signals (AP1-AP4); verify with times(), InOrder, verifyNoMoreInteractions, ArgumentCaptor |
| Repeatable (R) | 3.5 | 7.0 | 4.92 | 1.25x | 6.15 | Shared static mocks via `@BeforeAll` (line 24-28) create cross-test contamination risk; no Thread.sleep or I/O |
| Atomic (A) | 1.5 | 3.0 | 2.09 | 1.00x | 2.09 | `private static` mocks (lines 19-21) shared across all tests; `@BeforeAll` without `@BeforeEach` reset; AP3 over-specified interactions couple tests |
| Necessary (N) | 0.1 | 1.0 | 0.46 | 1.00x | 0.46 | 5 mock tautologies (AP1); 7 tests with no production code (AP2); 1 `assertTrue(true)` (line 261); 3 `assertNotNull(mock())` (lines 249-251, 262-264) |
| Granular (G) | 3.5 | 3.5 | 3.50 | 1.00x | 3.50 | `test_everything` has 10 assertions (line 224-252); `test_captorInspection` has 4 assertions; `test_verifyNever` has 4 verify + 1 assert |
| Fast (F) | 8.7 | 8.5 | 8.59 | 0.75x | 6.44 | No Thread.sleep, no I/O, no network; pure mock operations execute in milliseconds |
| First/TDD (T) | 1.0 | 1.5 | 1.21 | 1.00x | 1.21 | 7/12 tests exercise zero production code; tests mirror mock framework API, not behavior; no evidence of test-first design |

### Signal Summary

| Signal | Count | Locations | Affects | Severity |
|---|---|---|---|---|
| AP1: Mock tautology | 5 | lines 38-42, 51-55, 230-231, 233-234, 236-237 | N, M | Critical |
| AP2: No production code exercised | 7 | tests 1,2,3,4,10,11,12 (lines 37-43, 50-56, 64-75, 83-87, 209-216, 224-252, 260-265) | N, M, T | Critical |
| AP3: Over-specified interactions (times()) | 3 | lines 103-105, 243-246, and test_exactCallCounts | M, A | High |
| AP3: Over-specified interactions (InOrder) | 1 | lines 122-125 (test_callOrdering) | M, A | High |
| AP3: Over-specified interactions (verifyNoMoreInteractions) | 1 | line 150 (test_noOtherInteractions) | M, A | High |
| AP4: ArgumentCaptor deep inspection | 1 | lines 169-176 (test_captorInspection) | M, U | High |
| AP4: verify(never()) | 1 | line 199 (test_verifyNever) | M | High |
| AP4: High verify-to-assert ratio | 3 | test_placeOrder_allMocks (3:0), test_notificationSent (1:0), test_verifyNever (4:1) | M | Medium |
| Shared static mutable state | 1 | lines 19-21, 24-28 (@BeforeAll with static mocks) | A, R | High |
| Cryptic naming (test_ prefix only) | 12 | all 12 test methods | U | Medium |
| Trivial assertions (assertTrue(true)) | 1 | line 261 | N | Medium |
| Trivial assertions (assertNotNull(mock())) | 4 | lines 249-251, 262-264 | N | Medium |
| Mega-test (>5 assertions) | 1 | test_everything: 10 assertions (lines 224-252) | G | High |
| No @Nested or @DisplayName | -- | entire file | U | Low |
| No @ParameterizedTest | -- | entire file | N | Low |
| No @BeforeEach reset | -- | entire file | A | Medium |

### Tautology Theatre Analysis

Tests whose outcome is predetermined by their own setup, independent of production code. The defining test: "Would this test still pass if all production code were deleted?" If yes, it is tautology theatre.

#### Mock Tautologies

Configures a mock return value, then asserts that the mock returns it, with no production code in between. Logically equivalent to `x = 5; assert x == 5`.

| # | Test Method | Line | Mock Setup | Assertion |
|---|---|---|---|---|
| 1 | test_chargePayment | 38 | `when(gateway.charge(49.99)).thenReturn(true)` | `assertTrue(result)` on `gateway.charge(49.99)` |
| 2 | test_reserveInventory | 51 | `when(inventory.reserve("SKU-1", 2)).thenReturn(true)` | `assertTrue(result)` on `inventory.reserve("SKU-1", 2)` |
| 3 | test_everything | 230 | `when(gateway.charge(49.99)).thenReturn(true)` | `assertTrue(chargeResult)` on `gateway.charge(49.99)` |
| 4 | test_everything | 233 | `when(inventory.reserve("SKU-1", 2)).thenReturn(true)` | `assertTrue(reserveResult)` on `inventory.reserve("SKU-1", 2)` |
| 5 | test_everything | 236 | `when(gateway.refund(49.99)).thenReturn(true)` | `assertTrue(refundResult)` on `gateway.refund(49.99)` |

#### Mock-Only Tests

Every object in the test is a mock; no real class is instantiated or invoked. The test exercises only mock framework machinery.

| # | Test Method | Line | Evidence |
|---|---|---|---|
| 1 | test_chargePayment | 37 | Calls `gateway.charge()` directly on mock; no `OrderService` instantiated |
| 2 | test_reserveInventory | 50 | Calls `inventory.reserve()` directly on mock; no `OrderService` instantiated |
| 3 | test_placeOrder_allMocks | 64 | Configures and calls 3 mocks directly; verifies all 3 calls; no real class |
| 4 | test_notificationSent | 83 | Calls `notifier.sendConfirmation()` directly on mock; verifies it was called |
| 5 | test_paymentFailed | 209 | Configures `gateway.charge()` to return false; asserts false; no `OrderService` |
| 6 | test_everything | 224 | All operations on mocks; 10 assertions; no real class instantiated |
| 7 | test_framework | 260 | All assertions on framework primitives and mock constructors |

#### Trivial Tautologies

Assertions that are always true regardless of any code: `assertTrue(true)`, `assertEquals(1, 1)`, `assertNotNull(new Object())`.

| # | Test Method | Line | Assertion |
|---|---|---|---|
| 1 | test_everything | 249 | `assertNotNull(mock(PaymentGateway.class))` |
| 2 | test_everything | 250 | `assertNotNull(mock(InventoryService.class))` |
| 3 | test_everything | 251 | `assertNotNull(mock(NotificationService.class))` |
| 4 | test_framework | 261 | `assertTrue(true)` |

#### Framework Tests

Tests that verify language or framework behavior, not application code.

| # | Test Method | Line | Assertion | What It Actually Tests |
|---|---|---|---|---|
| 1 | test_framework | 262 | `assertNotNull(mock(PaymentGateway.class))` | Mockito's `mock()` returns non-null |
| 2 | test_framework | 263 | `assertNotNull(mock(InventoryService.class))` | Mockito's `mock()` returns non-null |
| 3 | test_framework | 264 | `assertNotNull(mock(NotificationService.class))` | Mockito's `mock()` returns non-null |

#### Tautology Theatre Summary

**19** tautology theatre instances across **8** of **12** test methods: 5 mock tautologies, 7 mock-only tests, 4 trivial tautologies, 3 framework tests. These tests provide zero verification of production behaviour and create false confidence in test coverage.

### Top 5 Worst Offenders

1. **OrderServiceTest.java:224 `test_everything()`** -- Farley ~0.5/10 -- Mega-test combining AP1 (3 mock tautologies at lines 230-237), AP2 (no OrderService instantiated), AP3 (4 verify with times(1) at lines 243-246), and 4 trivial assertNotNull(mock) at lines 249-251. 10 assertions verifying nothing about production behavior. This single test has every anti-pattern.

2. **OrderServiceTest.java:37 `test_chargePayment()`** -- Farley ~0.8/10 -- Pure AP1 mock tautology. Configures `gateway.charge(49.99)` to return true (line 38), calls the mock directly (line 40), asserts true (line 42). Zero production code. Logically equivalent to `x = true; assertTrue(x)`.

3. **OrderServiceTest.java:83 `test_notificationSent()`** -- Farley ~0.8/10 -- AP2: calls `notifier.sendConfirmation()` directly (line 84), then verifies it was called (line 86). No OrderService, no production code. The test verifies that Mockito's verify() works.

4. **OrderServiceTest.java:260 `test_framework()`** -- Farley ~0.9/10 -- `assertTrue(true)` (line 261) tests the JUnit framework. `assertNotNull(mock(...))` (lines 262-264) tests Mockito's mock() method. Zero production relevance. This is tautology theatre in its purest form.

5. **OrderServiceTest.java:64 `test_placeOrder_allMocks()`** -- Farley ~1.0/10 -- AP2: configures mocks (lines 65-66), calls mocks directly (lines 68-70) without instantiating OrderService, then verifies all three calls happened (lines 72-74). The test choreographs mock calls and verifies the choreography. 3 verifies, 0 asserts.

### Recommendations

1. **[Critical, targets N 0.46 and M 0.73 -- highest-weight properties] Delete or rewrite tests 1, 2, 3, 4, 10, 11, and 12.** These 7 tests exercise zero production code. They test Mockito, not OrderService. Every test must instantiate `new OrderService(gateway, inventory, notifier)` and call `service.placeOrder(...)`, then assert on the returned `OrderResult`. If you deleted the production code entirely, these 7 tests would still pass -- that is the definition of a worthless test.

2. **[Critical, targets M 0.73] Replace verify(times(1)), InOrder, and verifyNoMoreInteractions with outcome-based assertions.** Tests 5, 6, and 7 instantiate OrderService (good) but then over-specify mock interactions. Instead of verifying exact call counts and ordering, assert on the `OrderResult` returned by `placeOrder()` -- check `result.isSuccess()`, `result.getMessage()`, etc. Use simple `verify(notifier).sendConfirmation(orderId, email)` without `times(1)` only when verifying a side effect is genuinely necessary.

3. **[High, targets A 2.09] Replace @BeforeAll static mocks with @BeforeEach instance-level mocks.** Lines 19-21 declare mocks as `private static` and lines 24-28 initialize them once in `@BeforeAll`. This means all 12 tests share the same mock instances with accumulated interaction history. Mockito does not automatically reset mocks between tests unless `@ExtendWith(MockitoExtension.class)` with `@Mock` instance fields is used. Switch to `@BeforeEach` with instance fields, or better yet, use `@ExtendWith(MockitoExtension.class)` with `@Mock` annotations.

4. **[High, targets U 0.87] Adopt behavior-driven naming: `shouldReturnFailureWhenPaymentDeclined`, `shouldReleaseInventoryWhenPaymentFails`, etc.** All 12 methods use `test_` prefix naming (`test_chargePayment`, `test_everything`) which communicates nothing about expected behavior. Use `@Nested` classes to group tests by scenario (e.g., `@Nested class WhenPaymentSucceeds`, `@Nested class WhenInventoryUnavailable`).

5. **[Medium, targets G 3.50] Split test_everything into individual single-assertion tests.** A test with 10 assertions covering multiple behaviors violates granularity. Each scenario (successful charge, failed charge, inventory reservation) should be its own test method asserting one outcome.

### Per-Method Signal Detail

| Method | Line | AP1 | AP2 | AP3 | AP4 | Shared State | Cryptic Name | Trivial Assert | Assertions | Production Code? |
|---|---|---|---|---|---|---|---|---|---|---|
| test_chargePayment | 37 | Yes | Yes | -- | -- | Yes (static mock) | Yes | -- | 1 | No |
| test_reserveInventory | 50 | Yes | Yes | -- | -- | Yes (static mock) | Yes | -- | 1 | No |
| test_placeOrder_allMocks | 64 | -- | Yes | -- | -- | Yes (static mock) | Yes | -- | 0 (3 verify) | No |
| test_notificationSent | 83 | -- | Yes | -- | -- | Yes (static mock) | Yes | -- | 0 (1 verify) | No |
| test_exactCallCounts | 96 | -- | -- | times(1) x3 | -- | Yes (static mock) | Yes | -- | 0 (3 verify) | Yes |
| test_callOrdering | 115 | -- | -- | InOrder | -- | Yes (static mock) | Yes | -- | 0 (3 verify) | Yes |
| test_noOtherInteractions | 135 | -- | -- | verifyNoMore | -- | -- | Yes | -- | 0 (3 verify + vnmi) | Yes |
| test_captorInspection | 160 | -- | -- | -- | Captor x2 | Yes (static mock) | Yes | -- | 4 | Yes |
| test_verifyNever | 185 | -- | -- | -- | never() | -- | Yes | -- | 1 + 4 verify | Yes (partial) |
| test_paymentFailed | 209 | Yes | Yes | -- | -- | Yes (static mock) | Yes | -- | 1 + 1 verify | No |
| test_everything | 224 | Yes x3 | Yes | times(1) x4 | -- | -- | Yes | assertNotNull x3 | 10 | No |
| test_framework | 260 | -- | Yes | -- | -- | -- | Yes | assertTrue(true), assertNotNull x3 | 4 | No |

### Methodology Notes
- Static/LLM blend: 60/40
- LLM model: claude-opus-4-6
- Files analyzed: 1 (no sampling needed -- single test file)
- Test methods analyzed: 12
- Language: Java
- Framework: JUnit 5 with Mockito
- Mocking framework: Mockito
- T (First/TDD) note: Static evidence for TDD is necessarily indirect. The LLM assessment carries particular weight for this property. The absence of production code in 7/12 tests is strong negative evidence -- tests with no SUT could never have been written test-first.

### Dimensions Not Measured
Predictive, Inspiring, Composable, Writable (from Beck's Test Desiderata -- require runtime or team context)

### Reference
Based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/

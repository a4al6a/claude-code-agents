# Mock Anti-Patterns: Comprehensive Research

## Research Metadata

| Field | Value |
|---|---|
| Researcher | Nova (nw-researcher) |
| Date | 2026-02-12 |
| Scope | Four mock-specific test anti-patterns: definitions, code examples, static detection, taxonomy mapping |
| Languages Covered | Java (Mockito), Python (unittest.mock, pytest-mock), JavaScript/TypeScript (Jest, Sinon), Go (testify/mock, gomock), C# (Moq, NSubstitute) |
| Source Count | 22 sources consulted, 18 cited |
| Confidence Distribution | HIGH: 55%, MEDIUM: 35%, LOW: 10% |

---

## 1. Mock Tautology / Self-Verifying Mock

### 1.1 Definition

A **mock tautology** (also called a **self-verifying mock**) is a test that configures a mock's return value and then asserts that the mock returns that exact value, with no production code exercised between the setup and assertion. The test is circular: it verifies the behavior of the mocking framework itself, not any production logic.

The test always passes because it is logically equivalent to `x = 5; assert x == 5`. It proves nothing about the correctness of the system under test.

**Confidence: HIGH** -- Documented across 5+ independent sources with consistent definition.

### 1.2 Taxonomy Mapping

| Taxonomy | Name | Reference |
|---|---|---|
| James Carr (2006) | "The Mockery" (partial overlap) | [TDD Anti-Patterns, James Carr](https://web.archive.org/web/20160605001457/http://blog.james-carr.org:80/2006/11/03/tdd-anti-patterns/) |
| Yegor Bugayenko (2018) | "Mockery" | [Unit Testing Anti-Patterns, Full List](https://www.yegor256.com/2018/12/11/unit-testing-anti-patterns.html) |
| Meszaros (2007) | Not directly cataloged; closest is "Redundant Assertion" (testing what is trivially true) | [xUnit Test Patterns](http://xunitpatterns.com/) |
| testsmells.org | "Redundant Assertion" (assertion where expected == actual by construction) | [testsmells.org catalog](https://testsmells.org/pages/testsmells.html) |
| J.B. Rainsberger (2013) | "Tautological TDD" | [The Curious Case of Tautological TDD](https://blog.thecodewhisperer.com/permalink/the-curious-case-of-tautological-tdd) |
| Peter Williams (2005) | "Proving the Code is Written Like the Code is Written" | [Test Anti-Pattern](https://barelyenough.org/blog/2005/10/test-anti-pattern-proving-the-code-is-written-like-the-code-is-written/) |
| Kent Beck (2019) | Violated desideratum: "Predictive" (test passes but predicts nothing about production correctness) | [Test Desiderata](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3) |
| Mockito Wiki | "Avoid coding a tautology" | [How to write good tests](https://github.com/mockito/mockito/wiki/How-to-write-good-tests) |

**Note**: testsmells.org does NOT have a dedicated "mock tautology" smell. The closest match is "Redundant Assertion" (defined as "an assertion method that contains a numeric literal as an argument" with identical expected and actual parameters). The mock-specific variant is a gap in that catalog.

### 1.3 Code Examples

#### Java (Mockito)

```java
// ANTI-PATTERN: Mock tautology
@Test
void shouldFindUser() {
    UserService service = mock(UserService.class);
    User expectedUser = new User("Alice");

    when(service.findById(1)).thenReturn(expectedUser);

    // This ONLY tests Mockito's when/thenReturn machinery
    User result = service.findById(1);
    assertEquals(expectedUser, result);  // Always passes -- tautology
}

// CORRECTED: Test production code through the mock
@Test
void shouldFindUser() {
    UserRepository repository = mock(UserRepository.class);
    User expectedUser = new User("Alice");
    when(repository.findById(1)).thenReturn(expectedUser);

    // Production class is instantiated and exercised
    UserService service = new UserService(repository);
    User result = service.getUser(1);

    assertEquals(expectedUser, result);  // Tests UserService logic
}
```

#### Python (unittest.mock)

```python
# ANTI-PATTERN: Mock tautology
def test_find_user(self):
    service = Mock()
    expected_user = User("Alice")

    service.find_by_id.return_value = expected_user

    # Only tests Mock framework's return_value machinery
    result = service.find_by_id(1)
    self.assertEqual(expected_user, result)  # Always passes

# CORRECTED: Test production code
def test_find_user(self):
    repository = Mock(spec=UserRepository)
    expected_user = User("Alice")
    repository.find_by_id.return_value = expected_user

    service = UserService(repository)  # Production class
    result = service.get_user(1)

    self.assertEqual(expected_user, result)
```

#### Python (pytest-mock)

```python
# ANTI-PATTERN: Mock tautology
def test_find_user(mocker):
    service = mocker.Mock()
    expected_user = User("Alice")
    service.find_by_id.return_value = expected_user

    result = service.find_by_id(1)
    assert result == expected_user  # Tautology

# CORRECTED
def test_find_user(mocker):
    repository = mocker.Mock(spec=UserRepository)
    repository.find_by_id.return_value = User("Alice")

    service = UserService(repository)
    result = service.get_user(1)
    assert result.name == "Alice"
```

#### JavaScript (Jest)

```javascript
// ANTI-PATTERN: Mock tautology
test('should find user', () => {
  const service = {
    findById: jest.fn().mockReturnValue({ name: 'Alice' }),
  };

  // Only tests jest.fn() mockReturnValue machinery
  const result = service.findById(1);
  expect(result).toEqual({ name: 'Alice' });  // Always passes
});

// CORRECTED
test('should find user', () => {
  const repository = {
    findById: jest.fn().mockReturnValue({ name: 'Alice' }),
  };

  const service = new UserService(repository);  // Production class
  const result = service.getUser(1);
  expect(result).toEqual({ name: 'Alice' });
});
```

#### JavaScript (Sinon)

```javascript
// ANTI-PATTERN: Mock tautology
test('should find user', () => {
  const service = { findById: sinon.stub().returns({ name: 'Alice' }) };

  const result = service.findById(1);
  assert.deepEqual(result, { name: 'Alice' });  // Tautology
});
```

#### Go (testify/mock)

```go
// ANTI-PATTERN: Mock tautology
func TestFindUser(t *testing.T) {
    service := new(MockUserService)
    expectedUser := &User{Name: "Alice"}

    service.On("FindByID", 1).Return(expectedUser, nil)

    // Only tests testify/mock's On/Return machinery
    result, err := service.FindByID(1)
    assert.NoError(t, err)
    assert.Equal(t, expectedUser, result)  // Always passes
}
```

#### Go (gomock)

```go
// ANTI-PATTERN: Mock tautology
func TestFindUser(t *testing.T) {
    ctrl := gomock.NewController(t)
    service := NewMockUserService(ctrl)
    expectedUser := &User{Name: "Alice"}

    service.EXPECT().FindByID(1).Return(expectedUser, nil)

    result, err := service.FindByID(1)  // Tests gomock machinery
    assert.NoError(t, err)
    assert.Equal(t, expectedUser, result)
}
```

#### C# (Moq)

```csharp
// ANTI-PATTERN: Mock tautology
[Fact]
public void ShouldFindUser()
{
    var service = new Mock<IUserService>();
    var expectedUser = new User("Alice");

    service.Setup(s => s.FindById(1)).Returns(expectedUser);

    // Only tests Moq's Setup/Returns machinery
    var result = service.Object.FindById(1);
    Assert.Equal(expectedUser, result);  // Always passes
}
```

#### C# (NSubstitute)

```csharp
// ANTI-PATTERN: Mock tautology
[Fact]
public void ShouldFindUser()
{
    var service = Substitute.For<IUserService>();
    var expectedUser = new User("Alice");

    service.FindById(1).Returns(expectedUser);

    var result = service.FindById(1);  // Tests NSubstitute machinery
    Assert.Equal(expectedUser, result);  // Always passes
}
```

### 1.4 Static Detection Patterns

The core detection heuristic: **the same mock object that has its return value configured is also the object whose return value is asserted, with no production class instantiation in between.**

#### Structural Pattern (Framework-Agnostic)

```
1. Identify mock setup: a line configuring a mock's return value for method M
2. Identify assertion: a line asserting on the return value of method M
3. Check: is the object in step 1 the SAME object as in step 2?
4. Check: is there NO instantiation of a non-mock class between steps 1 and 2?
If (3) AND (4): flag as mock tautology.
```

#### Java (Mockito) Regex Patterns

```regex
# Detect when/thenReturn on a mock, then assertEquals on same mock's method
# Step 1: Find when(...).thenReturn(...)
SETUP:  when\((\w+)\.(\w+)\(.*?\)\)\.thenReturn\((\w+)\)

# Step 2: Find assertion on same variable and method
ASSERT: assert(Equals|Same|That)\(.*\b\3\b.*,\s*\1\.\2\(

# Simplified single-line: mock return then assert same value
# Captures: when(VAR.METHOD(...)).thenReturn(VALUE); ... assertEquals(VALUE, VAR.METHOD(...))
COMBINED: when\((\w+)\.(\w+)\([^)]*\)\)\.thenReturn\((\w+)\)[\s\S]{0,500}assert\w*\(\s*\3\s*,\s*\1\.\2\(
```

#### Python (unittest.mock / pytest-mock)

```regex
# Step 1: mock.method.return_value = value
SETUP:  (\w+)\.(\w+)\.return_value\s*=\s*(\w+)

# Step 2: assert mock.method(...) == value  OR  assertEqual(value, mock.method(...))
ASSERT_PYTEST: assert\s+\1\.\2\([^)]*\)\s*==\s*\3
ASSERT_UNITTEST: assertEqual\(\s*\3\s*,\s*\1\.\2\(

# Also detect: patch return_value then assert
PATCH_SETUP: (mocker\.)?patch\([^)]+\)\.return_value\s*=\s*(\w+)
```

#### JavaScript (Jest)

```regex
# Step 1: obj.method = jest.fn().mockReturnValue(value)
#     OR: jest.fn().mockReturnValue(value) assigned
SETUP:  (\w+)\.(\w+)\s*[:=]\s*jest\.fn\(\)\.mockReturnValue\(([^)]+)\)

# Alternative: mockResolvedValue for async
SETUP_ASYNC: (\w+)\.(\w+)\s*[:=]\s*jest\.fn\(\)\.mockResolvedValue\(([^)]+)\)

# Step 2: expect(obj.method(...)).toEqual(value)
ASSERT: expect\(\s*\1\.\2\([^)]*\)\s*\)\.to(Equal|Be|StrictEqual)\(\s*\3\s*\)
```

#### JavaScript (Sinon)

```regex
# Step 1: sinon.stub().returns(value)
SETUP:  (\w+)\.(\w+)\s*=\s*sinon\.stub\(\)\.returns\(([^)]+)\)

# Step 2: assert on same
ASSERT: (assert\.(deepEqual|equal|strictEqual))\(\s*\1\.\2\([^)]*\)\s*,\s*\3\s*\)
```

#### Go (testify/mock)

```regex
# Step 1: mock.On("Method", args).Return(value)
SETUP:  (\w+)\.On\("(\w+)"[^)]*\)\.Return\((\w+)

# Step 2: assert.Equal(t, value, mock.Method(...))
ASSERT: assert\.(Equal|Same)\(\s*t\s*,\s*\3\s*,\s*\1\.\2\(
```

#### Go (gomock)

```regex
# Step 1: mock.EXPECT().Method(args).Return(value)
SETUP:  (\w+)\.EXPECT\(\)\.(\w+)\([^)]*\)\.Return\((\w+)

# Step 2: assert on return
ASSERT: assert\.(Equal|Same)\(\s*t\s*,\s*\3.*\1\.\2\(
```

#### C# (Moq)

```regex
# Step 1: mock.Setup(x => x.Method(args)).Returns(value)
SETUP:  (\w+)\.Setup\([^)]*\.(\w+)\([^)]*\)\)\.Returns\((\w+)\)

# Step 2: Assert.Equal(value, mock.Object.Method(...))
ASSERT: Assert\.\w+\(\s*\3\s*,\s*\1\.Object\.\2\(
```

#### C# (NSubstitute)

```regex
# Step 1: sub.Method(args).Returns(value)
SETUP:  (\w+)\.(\w+)\([^)]*\)\.Returns\((\w+)\)

# Step 2: Assert.Equal(value, sub.Method(...))
ASSERT: Assert\.\w+\(\s*\3\s*,\s*\1\.\2\(
```

### 1.5 Higher-Fidelity Detection (AST-Level)

Regex patterns have inherent limitations (multi-line matching, variable aliasing, complex expressions). For higher fidelity:

1. **Parse the test method body into an AST**
2. **Build a data-flow graph**: track which variables are mock objects vs. production objects
3. **For each assertion**: trace the asserted value's origin. If it flows directly from a mock setup with no intervening production code call, flag it
4. **Key heuristic**: count production class instantiations (`new RealClass(...)` or equivalent) in the test body. If zero, and assertions exist on mock return values, flag as tautology

### 1.6 Confidence and Knowledge Gaps

**Confidence: HIGH** for the definition and code examples. All major sources agree on what constitutes this anti-pattern.

**Confidence: MEDIUM** for regex detection. The patterns above catch the most common forms but miss:
- Variable aliasing (`var x = mock.method(); ... assertEquals(expected, x)`)
- Builder/fluent patterns that span multiple lines
- Indirect mock tautologies where a helper method wraps the mock call

**Knowledge Gap**: No existing static analysis tool (SonarQube, PMD, ESLint, tsDetect) specifically flags "mock tautology" as a named rule. This is a gap in all major linter ecosystems as of 2026-02-12.

---

## 2. No Production Code Exercised

### 2.1 Definition

A test where **no real (non-mock) class or function is instantiated or invoked**. Every object in the test is a mock, stub, or fake. The test exercises only the mock framework's internal machinery, verifying that mocks return what they were told to return, or that mocks recorded calls as expected.

This is a superset of mock tautology: all mock tautologies have no production code exercised, but "no production code exercised" also includes tests that use `verify()` on mocks without any real object in the call chain.

Also known as: "Testing the mocking framework," "The Mockery" (James Carr), "Mock-heavy test."

**Confidence: HIGH** -- Documented across 6+ independent sources.

### 2.2 Taxonomy Mapping

| Taxonomy | Name | Reference |
|---|---|---|
| James Carr (2006) | "The Mockery" | [TDD Anti-Patterns](https://web.archive.org/web/20160605001457/http://blog.james-carr.org:80/2006/11/03/tdd-anti-patterns/) |
| Yegor Bugayenko (2018) | "Mockery" | [Unit Testing Anti-Patterns](https://www.yegor256.com/2018/12/11/unit-testing-anti-patterns.html) |
| Codurance (2021) | "The Mockery" | [TDD Anti-Patterns Chapter 2](https://www.codurance.com/publications/tdd-anti-patterns-chapter-2) |
| Marabesi (2021) | "The Mockery" | [TDD Anti-Patterns Episode 2](https://marabesi.com/tdd/tdd-anti-patterns-ep-2.html) |
| Mockito Wiki | "Don't mock everything" | [How to write good tests](https://github.com/mockito/mockito/wiki/How-to-write-good-tests) |
| Meszaros (2007) | Not directly cataloged; related to "Overspecified Software" symptom | [xUnit Test Patterns](http://xunitpatterns.com/) |
| testsmells.org | Not cataloged | Gap in catalog |
| Kent Beck (2019) | Violated: "Predictive" (if everything is mocked, passing tests predict nothing about production) | [Test Desiderata](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3) |
| Khorikov (2020) | "Mocking should only be used for unmanaged out-of-process dependencies" | [Unit Testing Principles, Practices, and Patterns](https://www.manning.com/books/unit-testing) |
| Learn Go with Tests | "If your test is reliant on setting up various interactions with mocks, your code needs work" | [Anti-Patterns](https://quii.gitbook.io/learn-go-with-tests/meta/anti-patterns) |

### 2.3 Code Examples

#### Java (Mockito)

```java
// ANTI-PATTERN: No production code exercised
@Test
void shouldProcessPayment() {
    PaymentGateway gateway = mock(PaymentGateway.class);
    UserRepository userRepo = mock(UserRepository.class);
    NotificationService notifier = mock(NotificationService.class);

    when(userRepo.findById(1)).thenReturn(new User("Alice"));
    when(gateway.charge(any())).thenReturn(true);

    // NO real class instantiated. Who is the SUT?
    userRepo.findById(1);
    gateway.charge(new PaymentDetails());
    notifier.send("Payment processed");

    verify(gateway).charge(any());
    verify(notifier).send("Payment processed");
}

// CORRECTED: Production code exercised
@Test
void shouldProcessPayment() {
    PaymentGateway gateway = mock(PaymentGateway.class);
    UserRepository userRepo = mock(UserRepository.class);
    NotificationService notifier = mock(NotificationService.class);

    when(userRepo.findById(1)).thenReturn(new User("Alice"));
    when(gateway.charge(any())).thenReturn(true);

    // PaymentService is the SUT -- real production code
    PaymentService service = new PaymentService(userRepo, gateway, notifier);
    boolean result = service.processPayment(1, new PaymentDetails());

    assertTrue(result);
    verify(notifier).send(contains("Payment processed"));
}
```

#### Python (unittest.mock)

```python
# ANTI-PATTERN: No production code exercised
def test_process_payment(self):
    gateway = Mock(spec=PaymentGateway)
    user_repo = Mock(spec=UserRepository)
    notifier = Mock(spec=NotificationService)

    user_repo.find_by_id.return_value = User("Alice")
    gateway.charge.return_value = True

    # No real class. Just calling mocks directly.
    user_repo.find_by_id(1)
    gateway.charge(PaymentDetails())
    notifier.send("Payment processed")

    gateway.charge.assert_called_once()
    notifier.send.assert_called_with("Payment processed")

# CORRECTED
def test_process_payment(self):
    gateway = Mock(spec=PaymentGateway)
    user_repo = Mock(spec=UserRepository)
    notifier = Mock(spec=NotificationService)

    user_repo.find_by_id.return_value = User("Alice")
    gateway.charge.return_value = True

    service = PaymentService(user_repo, gateway, notifier)  # Real SUT
    result = service.process_payment(1, PaymentDetails())

    self.assertTrue(result)
    notifier.send.assert_called_with("Payment processed for Alice")
```

#### JavaScript (Jest)

```javascript
// ANTI-PATTERN: No production code exercised
test('should process payment', () => {
  const gateway = { charge: jest.fn().mockReturnValue(true) };
  const userRepo = { findById: jest.fn().mockReturnValue({ name: 'Alice' }) };
  const notifier = { send: jest.fn() };

  // No real class. Just calling mock functions.
  userRepo.findById(1);
  gateway.charge({ amount: 100 });
  notifier.send('Payment processed');

  expect(gateway.charge).toHaveBeenCalled();
  expect(notifier.send).toHaveBeenCalledWith('Payment processed');
});

// CORRECTED
test('should process payment', () => {
  const gateway = { charge: jest.fn().mockReturnValue(true) };
  const userRepo = { findById: jest.fn().mockReturnValue({ name: 'Alice' }) };
  const notifier = { send: jest.fn() };

  const service = new PaymentService(userRepo, gateway, notifier);  // Real SUT
  const result = service.processPayment(1, { amount: 100 });

  expect(result).toBe(true);
  expect(notifier.send).toHaveBeenCalledWith('Payment processed for Alice');
});
```

#### Go (testify/mock)

```go
// ANTI-PATTERN: No production code exercised
func TestProcessPayment(t *testing.T) {
    gateway := new(MockPaymentGateway)
    userRepo := new(MockUserRepository)
    notifier := new(MockNotificationService)

    userRepo.On("FindByID", 1).Return(&User{Name: "Alice"}, nil)
    gateway.On("Charge", mock.Anything).Return(true, nil)
    notifier.On("Send", "Payment processed").Return(nil)

    // Calling mocks directly -- no real SUT
    userRepo.FindByID(1)
    gateway.Charge(&PaymentDetails{})
    notifier.Send("Payment processed")

    gateway.AssertCalled(t, "Charge", mock.Anything)
    notifier.AssertCalled(t, "Send", "Payment processed")
}
```

#### C# (Moq)

```csharp
// ANTI-PATTERN: No production code exercised
[Fact]
public void ShouldProcessPayment()
{
    var gateway = new Mock<IPaymentGateway>();
    var userRepo = new Mock<IUserRepository>();
    var notifier = new Mock<INotificationService>();

    userRepo.Setup(r => r.FindById(1)).Returns(new User("Alice"));
    gateway.Setup(g => g.Charge(It.IsAny<PaymentDetails>())).Returns(true);

    // No real class instantiated
    userRepo.Object.FindById(1);
    gateway.Object.Charge(new PaymentDetails());
    notifier.Object.Send("Payment processed");

    gateway.Verify(g => g.Charge(It.IsAny<PaymentDetails>()), Times.Once());
    notifier.Verify(n => n.Send("Payment processed"), Times.Once());
}
```

### 2.4 Static Detection Patterns

The core detection heuristic: **a test method contains mock/stub creation and mock assertions but no instantiation of a non-mock, non-test class.**

#### Structural Pattern (Framework-Agnostic)

```
1. Parse test method body
2. Identify all object instantiations
3. Classify each as: mock/stub/fake OR production class
4. If ALL instantiations are mocks AND the test contains assertions or verifications:
   Flag as "no production code exercised"
```

#### Java (Mockito) -- Detection

```regex
# Detect test method with mock() but no "new RealClass("
# Step 1: Contains mock creation
HAS_MOCK: (mock|spy)\(\w+\.class\)|@Mock\s|@Spy\s|Mockito\.(mock|spy)\(

# Step 2: Contains assertion or verify
HAS_ASSERT: (assert|verify|then)\w*\(

# Step 3: Does NOT contain production class instantiation
# (Heuristic: no "new " followed by a non-mock class name)
NO_REAL_NEW: ^(?!.*\bnew\s+(?!Mock|Spy|InOrder)\w+\s*\().*$

# Combined heuristic (per test method):
# IF HAS_MOCK AND HAS_ASSERT AND NO_REAL_NEW THEN flag
```

**Mockito Strict Stubbing**: Mockito's `STRICT_STUBS` mode (default in Mockito 3+) detects **unnecessary stubbings** (stubs configured but never exercised by production code). This partially overlaps with this anti-pattern by catching stubs that exist only for the mock framework to replay. Enable via `@MockitoSettings(strictness = Strictness.STRICT_STUBS)`.

Source: [Mockito Strict Stubbing](https://www.baeldung.com/mockito-unnecessary-stubbing-exception), [Mockito Blog](http://blog.mockito.org/2017/01/clean-tests-produce-clean-code-strict.html)

#### Python (unittest.mock / pytest-mock) -- Detection

```regex
# Step 1: Contains Mock() or patch()
HAS_MOCK: Mock\(|MagicMock\(|patch\(|mocker\.(Mock|patch|MagicMock)

# Step 2: Contains assertion
HAS_ASSERT: (assert_called|assert_has_calls|assert\s|assertEqual|assertTrue)

# Step 3: No real class instantiation
# Heuristic: no "ClassName(" that is not Mock/MagicMock/patch
# This requires knowing production class names -- harder with regex alone

# Simpler heuristic: count ratio of Mock()/patch() to total object creations
# If ratio >= 1.0 (all objects are mocks), flag
```

#### JavaScript (Jest) -- Detection

```regex
# Step 1: Contains jest.fn() or jest.mock()
HAS_MOCK: jest\.fn\(|jest\.mock\(|jest\.spyOn\(

# Step 2: Contains expect()
HAS_ASSERT: expect\(

# Step 3: No "new RealClass(" -- heuristic
NO_REAL_NEW: ^(?!.*\bnew\s+(?!Mock)\w+\s*\().*$

# eslint-plugin-jest provides:
# - "jest/expect-expect": ensures tests have assertions (partial)
# - No built-in rule for "no production code exercised"
```

#### Go -- Detection

```regex
# Step 1: Contains mock creation
HAS_MOCK: new\(Mock\w+\)|gomock\.NewController|NewMock\w+\(

# Step 2: Contains assertion
HAS_ASSERT: assert\.\w+\(|AssertCalled|AssertExpectations

# Step 3: No production struct instantiation
# Heuristic: no "RealType{" or "NewRealType(" patterns
# Requires knowledge of which types are mocks vs production
```

#### C# (Moq / NSubstitute) -- Detection

```regex
# Moq: Step 1
HAS_MOCK_MOQ: new\s+Mock<|Mock\.Of<

# NSubstitute: Step 1
HAS_MOCK_NSUB: Substitute\.For<

# Step 2: Contains verification
HAS_VERIFY_MOQ: \.Verify\(|Assert\.\w+\(
HAS_VERIFY_NSUB: \.Received\(|Assert\.\w+\(

# Step 3: No real class instantiation
# Heuristic: no "new RealClass(" where RealClass is not Mock<>
NO_REAL_NEW: ^(?!.*\bnew\s+(?!Mock)\w+\s*\().*$
```

### 2.5 Higher-Fidelity Detection

The most reliable detection requires **type resolution**:

1. Build a set of all types instantiated in the test method
2. Classify each as mock/stub/fake (by framework API) or production type
3. If the set of production types is empty, flag

**Mock-to-real ratio metric**: `mock_count / (mock_count + real_count)`. A ratio of 1.0 strongly signals this anti-pattern. A ratio above 0.8 is a warning.

### 2.6 Confidence and Knowledge Gaps

**Confidence: HIGH** for definition. All sources agree.

**Confidence: MEDIUM** for regex detection. The "no new RealClass" heuristic produces false positives when:
- Production code is accessed via static methods (`UserService.process(...)`)
- Production code is accessed via function calls (Python, JS functional style)
- The SUT is injected in `setUp` / `beforeEach` and not visible in the test method body

**Knowledge Gap**: No linter in any ecosystem has a dedicated rule for "all objects in test are mocks." Mockito's strict stubbing catches a subset (unused stubs) but not the full pattern.

---

## 3. Over-Specified Mock Interactions

### 3.1 Definition

A test that uses **verify()** (or equivalent) with **exact call counts**, **strict argument matchers**, and/or **call ordering constraints** on mock objects, making it brittle to any implementation change that preserves behavior but changes the call sequence, count, or argument details.

The test describes **how** the software does something rather than **what** it achieves. Any refactoring that changes the internal call structure breaks the test, even if external behavior is preserved.

Also known as: "Interaction over-specification," "Overspecified Software" (Meszaros), "Behavior Sensitivity" (Meszaros), "Mockery" (when combined with over-mocking).

**Confidence: HIGH** -- This is one of the most widely documented mock anti-patterns, with primary source material from Meszaros (2007).

### 3.2 Taxonomy Mapping

| Taxonomy | Name | Reference |
|---|---|---|
| Meszaros (2007) | "Overspecified Software" (symptom), "Fragile Test" (smell), "Behavior Sensitivity" (cause) | [xUnit Test Patterns - Fragile Test](http://xunitpatterns.com/Fragile%20Test.html) |
| Meszaros (2007) | Mock Objects can lead to "tests that describe how the software should do something, not what it should achieve" | [xUnit Test Patterns - Behavior Verification](http://xunitpatterns.com/Behavior%20Verification.html) |
| Ian Cooper (2007) | "Mocks and the Dangers of Overspecified Software" | [CodeBetter Blog](http://codebetter.com/iancooper/2007/12/19/mocks-and-the-dangers-of-overspecified-software/) |
| Kent Beck (2019) | Violated: "Structure-insensitive" ("tests should be coupled to the behavior of code and decoupled from the structure of code") | [Test Desiderata](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3) |
| Khorikov (2020) | "The relationship between mocks and test fragility" -- overuse of mocks couples tests to implementation | [Unit Testing PPP](https://www.manning.com/books/unit-testing) |
| Spock Framework | "Strict mocking can lead to over-specification, resulting in brittle tests" | [Interaction Based Testing](https://spockframework.org/spock/docs/2.4-M4/interaction_based_testing.html) |
| testsmells.org | Not directly cataloged (gap) | -- |
| Dave Farley | Violates "Maintainable" property: "tests must act as a defence of our system, breaking when we want them to" -- over-specified tests break when we DON'T want them to | [Properties of Good Tests](https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/) |

### 3.3 Code Examples

#### Java (Mockito) -- Over-Specified

```java
// ANTI-PATTERN: Over-specified mock interactions
@Test
void shouldProcessOrder() {
    OrderRepository repo = mock(OrderRepository.class);
    PaymentService payments = mock(PaymentService.class);
    NotificationService notifier = mock(NotificationService.class);
    AuditLogger audit = mock(AuditLogger.class);

    when(payments.charge(any())).thenReturn(true);

    OrderService service = new OrderService(repo, payments, notifier, audit);
    service.processOrder(new Order(1, 100.0));

    // Over-specified: exact call count
    verify(repo, times(1)).save(any(Order.class));

    // Over-specified: exact argument matching on internal details
    verify(payments, times(1)).charge(argThat(p ->
        p.getAmount() == 100.0 &&
        p.getCurrency().equals("USD") &&
        p.getOrderId() == 1
    ));

    // Over-specified: call ordering
    InOrder inOrder = inOrder(payments, repo, notifier, audit);
    inOrder.verify(payments).charge(any());
    inOrder.verify(repo).save(any());
    inOrder.verify(notifier).send(any());
    inOrder.verify(audit).log(any());

    // Over-specified: no other interactions allowed
    verifyNoMoreInteractions(repo, payments, notifier, audit);
}

// CORRECTED: Verify only essential behavior
@Test
void shouldProcessOrder() {
    OrderRepository repo = mock(OrderRepository.class);
    PaymentService payments = mock(PaymentService.class);
    NotificationService notifier = mock(NotificationService.class);
    AuditLogger audit = mock(AuditLogger.class);

    when(payments.charge(any())).thenReturn(true);

    OrderService service = new OrderService(repo, payments, notifier, audit);
    service.processOrder(new Order(1, 100.0));

    // Verify essential outcomes only
    verify(payments).charge(any());         // Was payment attempted?
    verify(repo).save(any(Order.class));    // Was order persisted?
    verify(notifier).send(contains("Order 1"));  // Was user notified?
    // audit.log is an internal detail -- don't verify
}
```

#### Python (unittest.mock)

```python
# ANTI-PATTERN: Over-specified
def test_process_order(self):
    repo = Mock(spec=OrderRepository)
    payments = Mock(spec=PaymentService)
    notifier = Mock(spec=NotificationService)

    payments.charge.return_value = True

    service = OrderService(repo, payments, notifier)
    service.process_order(Order(1, 100.0))

    # Over-specified: exact call count
    payments.charge.assert_called_once_with(
        PaymentDetails(amount=100.0, currency="USD", order_id=1)
    )

    # Over-specified: exact call ordering
    expected_calls = [
        call.charge(ANY),
        call.save(ANY),
        call.send(ANY),
    ]
    # ... manual ordering verification

    # Over-specified: assert_has_calls with any_order=False
    repo.assert_has_calls([call.save(ANY)], any_order=False)

# CORRECTED
def test_process_order(self):
    repo = Mock(spec=OrderRepository)
    payments = Mock(spec=PaymentService)
    notifier = Mock(spec=NotificationService)

    payments.charge.return_value = True

    service = OrderService(repo, payments, notifier)
    service.process_order(Order(1, 100.0))

    payments.charge.assert_called()  # Was payment attempted?
    repo.save.assert_called()        # Was order persisted?
```

#### JavaScript (Jest)

```javascript
// ANTI-PATTERN: Over-specified
test('should process order', () => {
  const repo = { save: jest.fn() };
  const payments = { charge: jest.fn().mockReturnValue(true) };
  const notifier = { send: jest.fn() };

  const service = new OrderService(repo, payments, notifier);
  service.processOrder({ id: 1, amount: 100 });

  // Over-specified: exact call count
  expect(payments.charge).toHaveBeenCalledTimes(1);
  expect(repo.save).toHaveBeenCalledTimes(1);
  expect(notifier.send).toHaveBeenCalledTimes(1);

  // Over-specified: exact argument structure
  expect(payments.charge).toHaveBeenCalledWith({
    amount: 100,
    currency: 'USD',
    orderId: 1,
    timestamp: expect.any(Number),
  });

  // Over-specified: call order
  const chargeOrder = payments.charge.mock.invocationCallOrder[0];
  const saveOrder = repo.save.mock.invocationCallOrder[0];
  expect(chargeOrder).toBeLessThan(saveOrder);
});
```

#### JavaScript (Sinon)

```javascript
// ANTI-PATTERN: Over-specified
test('should process order', () => {
  const payments = { charge: sinon.stub().returns(true) };
  const repo = { save: sinon.spy() };
  const notifier = { send: sinon.spy() };

  const service = new OrderService(repo, payments, notifier);
  service.processOrder({ id: 1, amount: 100 });

  // Over-specified: exact count
  sinon.assert.calledOnce(payments.charge);
  sinon.assert.calledOnce(repo.save);

  // Over-specified: call order
  sinon.assert.callOrder(payments.charge, repo.save, notifier.send);

  // Over-specified: exact args
  sinon.assert.calledWithExactly(payments.charge, {
    amount: 100, currency: 'USD', orderId: 1
  });
});
```

#### Go (testify/mock)

```go
// ANTI-PATTERN: Over-specified
func TestProcessOrder(t *testing.T) {
    repo := new(MockOrderRepository)
    payments := new(MockPaymentService)
    notifier := new(MockNotificationService)

    payments.On("Charge", mock.MatchedBy(func(p PaymentDetails) bool {
        return p.Amount == 100.0 &&
               p.Currency == "USD" &&
               p.OrderID == 1
    })).Return(true, nil).Once()  // .Once() = exact count

    repo.On("Save", mock.Anything).Return(nil).Once()
    notifier.On("Send", mock.Anything).Return(nil).Once()

    service := NewOrderService(repo, payments, notifier)
    service.ProcessOrder(Order{ID: 1, Amount: 100.0})

    // Over-specified: all expectations must match exactly
    repo.AssertExpectations(t)
    payments.AssertExpectations(t)
    notifier.AssertExpectations(t)

    // Over-specified: no other calls allowed
    repo.AssertNumberOfCalls(t, "Save", 1)
}
```

#### Go (gomock)

```go
// ANTI-PATTERN: Over-specified
func TestProcessOrder(t *testing.T) {
    ctrl := gomock.NewController(t)
    repo := NewMockOrderRepository(ctrl)
    payments := NewMockPaymentService(ctrl)

    // gomock enforces ordering by default within gomock.InOrder
    gomock.InOrder(
        payments.EXPECT().Charge(gomock.Any()).Return(true, nil).Times(1),
        repo.EXPECT().Save(gomock.Any()).Return(nil).Times(1),
    )

    service := NewOrderService(repo, payments)
    service.ProcessOrder(Order{ID: 1, Amount: 100.0})
}
```

#### C# (Moq)

```csharp
// ANTI-PATTERN: Over-specified
[Fact]
public void ShouldProcessOrder()
{
    var repo = new Mock<IOrderRepository>();
    var payments = new Mock<IPaymentService>();
    var notifier = new Mock<INotificationService>();

    payments.Setup(p => p.Charge(It.IsAny<PaymentDetails>())).Returns(true);

    var service = new OrderService(repo.Object, payments.Object, notifier.Object);
    service.ProcessOrder(new Order(1, 100.0m));

    // Over-specified: exact count
    payments.Verify(p => p.Charge(It.IsAny<PaymentDetails>()), Times.Exactly(1));
    repo.Verify(r => r.Save(It.IsAny<Order>()), Times.Exactly(1));
    notifier.Verify(n => n.Send(It.IsAny<string>()), Times.Exactly(1));

    // Over-specified: no other calls
    repo.VerifyNoOtherCalls();
    payments.VerifyNoOtherCalls();
    notifier.VerifyNoOtherCalls();
}
```

#### C# (NSubstitute)

```csharp
// ANTI-PATTERN: Over-specified
[Fact]
public void ShouldProcessOrder()
{
    var repo = Substitute.For<IOrderRepository>();
    var payments = Substitute.For<IPaymentService>();
    var notifier = Substitute.For<INotificationService>();

    payments.Charge(Arg.Any<PaymentDetails>()).Returns(true);

    var service = new OrderService(repo, payments, notifier);
    service.ProcessOrder(new Order(1, 100.0m));

    // Over-specified: exact count
    payments.Received(1).Charge(Arg.Any<PaymentDetails>());
    repo.Received(1).Save(Arg.Any<Order>());
    notifier.Received(1).Send(Arg.Any<string>());
}
```

### 3.4 Static Detection Patterns

The core detection heuristic: **verify/assert calls with exact count arguments, ordering constraints, or exhaustive argument matchers.**

#### Severity Levels

| Signal | Severity | Rationale |
|---|---|---|
| `verify(mock, times(N))` where N > 0 | Medium | Exact count couples to implementation |
| `InOrder` / call ordering verification | High | Ordering is almost always an implementation detail |
| `verifyNoMoreInteractions` / `VerifyNoOtherCalls` | High | Prevents any future internal changes |
| Exact argument matching on >3 fields | Medium | Over-constrains the interaction contract |
| `times(1)` specifically | Low | Often just explicit "called once" which may be intentional |

#### Java (Mockito) Regex Patterns

```regex
# Exact count verification (HIGH signal when N > 1)
EXACT_COUNT:     verify\(\s*\w+\s*,\s*times\(\s*\d+\s*\)\s*\)

# atLeast/atMost (MEDIUM signal -- still specifying counts)
RANGE_COUNT:     verify\(\s*\w+\s*,\s*(atLeast|atMost)\(\s*\d+\s*\)\s*\)

# Call ordering
ORDERING:        InOrder\s+\w+\s*=\s*inOrder\(|inOrder\.verify\(

# No more interactions (HIGH signal)
NO_MORE:         verifyNoMoreInteractions\(|verifyNoInteractions\(|verifyZeroInteractions\(

# Complex argument matcher (MEDIUM signal)
COMPLEX_ARGMATCH: argThat\(\s*\w+\s*->\s*\w+\.get\w+\(\).*&&.*&&
```

#### Python (unittest.mock / pytest-mock) Regex Patterns

```regex
# Exact count assertion
EXACT_COUNT:     assert_called_once_with\(|assert_called_once\(\)

# Call count property check
COUNT_CHECK:     \.call_count\s*==\s*\d+

# Ordered calls (any_order=False is default, so presence of assert_has_calls is a signal)
ORDERING:        assert_has_calls\([^)]*,\s*any_order\s*=\s*False

# No other calls
NO_MORE:         assert_not_called\(\)|assert_called_once\(\)
```

#### JavaScript (Jest) Regex Patterns

```regex
# Exact call count
EXACT_COUNT:     toHaveBeenCalledTimes\(\s*\d+\s*\)

# Call ordering (manual invocationCallOrder check)
ORDERING:        \.mock\.invocationCallOrder

# Exact arguments with complex matchers
EXACT_ARGS:      toHaveBeenCalledWith\(\s*\{[^}]*,[^}]*,[^}]*\}

# Exhaustive: toHaveBeenLastCalledWith + toHaveBeenNthCalledWith
NTH_CALL:        toHaveBeenNthCalledWith\(|toHaveBeenLastCalledWith\(
```

#### JavaScript (Sinon) Regex Patterns

```regex
# Exact count
EXACT_COUNT:     calledOnce|calledTwice|calledThrice|callCount\s*===?\s*\d+

# Call ordering
ORDERING:        sinon\.assert\.callOrder\(

# Exact arguments
EXACT_ARGS:      calledWithExactly\(

# Sinon also has: alwaysCalledWith, neverCalledWith
ALWAYS_NEVER:    alwaysCalledWith|neverCalledWith
```

#### Go (testify/mock) Regex Patterns

```regex
# Exact count: .Once(), .Times(N)
EXACT_COUNT:     \.(Once|Times)\(\s*\d*\s*\)

# Number of calls assertion
CALL_COUNT:      AssertNumberOfCalls\(\s*t\s*,

# All expectations (forces all On().Return() to match exactly)
ALL_EXPECT:      AssertExpectations\(

# Not called
NOT_CALLED:      AssertNotCalled\(
```

#### Go (gomock) Regex Patterns

```regex
# Exact count
EXACT_COUNT:     \.Times\(\s*\d+\s*\)

# Min/Max count
RANGE_COUNT:     \.(MinTimes|MaxTimes)\(\s*\d+\s*\)

# Call ordering
ORDERING:        gomock\.InOrder\(

# Any times (POSITIVE -- less specified, not an anti-pattern)
ANY_TIMES:       \.AnyTimes\(\)
```

#### C# (Moq) Regex Patterns

```regex
# Exact count
EXACT_COUNT:     Times\.(Once|Exactly|AtMostOnce|AtLeastOnce|AtMost|AtLeast)\(

# Specifically exact count
STRICT_COUNT:    Times\.Exactly\(\s*\d+\s*\)

# No other calls
NO_MORE:         VerifyNoOtherCalls\(\)

# Strict mock (fails on unexpected calls)
STRICT_MOCK:     MockBehavior\.Strict
```

#### C# (NSubstitute) Regex Patterns

```regex
# Exact count (Received with numeric argument)
EXACT_COUNT:     \.Received\(\s*\d+\s*\)\.

# Did not receive
NOT_CALLED:      \.DidNotReceive\(\)\.

# Received with any args
ANY_ARGS:        \.ReceivedWithAnyArgs\(
```

### 3.5 Composite Severity Scoring

For a single test method, count the over-specification signals:

| Count | Severity | Interpretation |
|---|---|---|
| 0 | None | No over-specification detected |
| 1 (times(1) or assert_called_once) | Low | Likely intentional; borderline |
| 2-3 (mixed exact counts) | Medium | Moderate coupling to implementation |
| 4+ OR any ordering constraint OR noMoreInteractions | High | Strongly coupled to implementation; brittle |

### 3.6 Confidence and Knowledge Gaps

**Confidence: HIGH** for definition and taxonomy. Meszaros's "Overspecified Software" and "Fragile Test" are the canonical references, supported by Ian Cooper, Kent Beck, and Vladimir Khorikov.

**Confidence: HIGH** for regex detection. The patterns are syntactically distinctive and framework-specific.

**Knowledge Gap**: Distinguishing **legitimate** interaction verification from over-specification. In some cases, verifying that a payment gateway was called exactly once IS the essential behavior (idempotency requirement). Context determines whether the verification is essential or over-specified. Static analysis alone cannot make this distinction.

**Knowledge Gap**: testsmells.org does not catalog interaction over-specification. This is a significant gap given its prevalence and the extensive documentation from Meszaros.

---

## 4. Testing Internal Details (Beyond Reflection)

### 4.1 Definition

Tests that are coupled to **implementation details** through mechanisms subtler than direct reflection or private member access. This includes:

1. **Asserting on intermediate state**: Verifying internal data structures, intermediate computation results, or transient object states that are not part of the public API
2. **Verifying internal method call ordering**: Using mock verification to assert that internal helper methods were called in a specific sequence
3. **White-box mock expectations**: Setting up mock expectations that mirror the exact internal control flow, such that any refactoring (even behavior-preserving) breaks the test
4. **Structural inspection**: Verifying that the SUT implements certain interfaces, has certain internal components, or follows a specific class hierarchy
5. **Testing internal event sequences**: Asserting on the order of internal state transitions rather than the final observable outcome

This is distinct from simple reflection/private access (which is already covered by the Maintainable property signals). This anti-pattern is about tests that use only public APIs and standard mock frameworks but are still coupled to implementation.

**Confidence: HIGH** -- Documented across 5+ independent sources.

### 4.2 Taxonomy Mapping

| Taxonomy | Name | Reference |
|---|---|---|
| Meszaros (2007) | "Fragile Test" (specifically "Behavior Sensitivity" and "Interface Sensitivity" causes) | [xUnit Test Patterns](http://xunitpatterns.com/Fragile%20Test.html) |
| Kent Beck (2019) | Violated: "Structure-insensitive" ("tests should not change their result if the structure of the code changes") | [Test Desiderata](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3) |
| Khorikov (2020) | "Observable behavior vs. implementation details" -- "coupling a test to implementation details will reduce the tests' resistance to refactoring" | [Unit Testing PPP](https://www.manning.com/books/unit-testing) |
| Enterprise Craftsmanship (Khorikov blog) | "Structural Inspection" | [Structural Inspection](https://enterprisecraftsmanship.com/posts/structural-inspection) |
| James Carr (2006) | "The Inspector" -- "violates encapsulation by verifying internal state" | [TDD Anti-Patterns](https://web.archive.org/web/20160605001457/http://blog.james-carr.org:80/2006/11/03/tdd-anti-patterns/) |
| Marabesi (2021) | "The Inspector" -- "breaks encapsulation just for the sake of testing" | [TDD Anti-Patterns Episode 2](https://marabesi.com/tdd/tdd-anti-patterns-ep-2.html) |
| Codepipes (2018) | "Testing internal implementation" -- "if you find yourself continuously fixing existing tests as you add new features, it means your tests are tightly coupled to internal implementation" | [Software Testing Anti-Patterns](https://blog.codepipes.com/testing/software-testing-antipatterns.html) |
| Dave Farley | Violates "Maintainable" -- tests that break on implementation change without behavior change fail this property | [Properties of Good Tests](https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/) |
| testsmells.org | Not directly cataloged (gap) | -- |

### 4.3 Sub-Categories with Code Examples

#### 4.3.1 Asserting on Intermediate State

Tests that verify internal state snapshots rather than final observable output.

**Java (Mockito)**
```java
// ANTI-PATTERN: Asserting on intermediate internal state
@Test
void shouldProcessOrder() {
    OrderRepository repo = mock(OrderRepository.class);
    PaymentService payments = mock(PaymentService.class);

    when(payments.charge(any())).thenReturn(true);

    OrderService service = new OrderService(repo, payments);
    service.processOrder(new Order(1, 100.0));

    // Captures the Order object passed to repo.save()
    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    verify(repo).save(captor.capture());

    Order savedOrder = captor.getValue();
    // Over-inspecting internal state of the saved order:
    assertEquals("PROCESSING", savedOrder.getInternalStatus());  // Internal enum
    assertEquals(3, savedOrder.getRetryCount());                  // Internal counter
    assertNotNull(savedOrder.getInternalCorrelationId());         // Internal ID
    assertTrue(savedOrder.getAuditTrail().size() > 0);            // Internal audit
}

// CORRECTED: Assert on observable behavior
@Test
void shouldProcessOrder() {
    OrderRepository repo = mock(OrderRepository.class);
    PaymentService payments = mock(PaymentService.class);
    when(payments.charge(any())).thenReturn(true);

    OrderService service = new OrderService(repo, payments);
    OrderResult result = service.processOrder(new Order(1, 100.0));

    assertEquals(OrderResult.SUCCESS, result);   // Observable output
    verify(repo).save(any(Order.class));          // Side effect occurred
    verify(payments).charge(any());               // Payment was attempted
}
```

**Python (unittest.mock)**
```python
# ANTI-PATTERN: Inspecting captured arguments for internal details
def test_process_order(self):
    repo = Mock(spec=OrderRepository)
    payments = Mock(spec=PaymentService)
    payments.charge.return_value = True

    service = OrderService(repo, payments)
    service.process_order(Order(1, 100.0))

    # Inspecting the internal state of the saved order
    saved_order = repo.save.call_args[0][0]
    self.assertEqual("PROCESSING", saved_order._internal_status)
    self.assertEqual(3, saved_order._retry_count)
    self.assertIsNotNone(saved_order._correlation_id)
```

**JavaScript (Jest)**
```javascript
// ANTI-PATTERN: Inspecting mock call arguments for internal details
test('should process order', () => {
  const repo = { save: jest.fn() };
  const payments = { charge: jest.fn().mockReturnValue(true) };

  const service = new OrderService(repo, payments);
  service.processOrder({ id: 1, amount: 100 });

  // Over-inspecting internal state
  const savedOrder = repo.save.mock.calls[0][0];
  expect(savedOrder._internalStatus).toBe('PROCESSING');
  expect(savedOrder._retryCount).toBe(3);
  expect(savedOrder._correlationId).toBeDefined();
});
```

#### 4.3.2 Verifying Internal Method Call Ordering

**Java (Mockito)**
```java
// ANTI-PATTERN: Verifying internal helper call sequence
@Test
void shouldBuildReport() {
    DataFetcher fetcher = mock(DataFetcher.class);
    Formatter formatter = mock(Formatter.class);
    Validator validator = mock(Validator.class);
    Cache cache = mock(Cache.class);

    ReportBuilder builder = new ReportBuilder(fetcher, formatter, validator, cache);
    builder.build("Q1-2024");

    // Verifying exact internal call sequence
    InOrder inOrder = inOrder(cache, fetcher, validator, formatter, cache);
    inOrder.verify(cache).lookup("Q1-2024");           // Step 1: check cache
    inOrder.verify(fetcher).fetchData("Q1-2024");      // Step 2: fetch data
    inOrder.verify(validator).validate(any());          // Step 3: validate
    inOrder.verify(formatter).format(any());            // Step 4: format
    inOrder.verify(cache).store(eq("Q1-2024"), any()); // Step 5: cache result
}

// CORRECTED: Test the output, not the steps
@Test
void shouldBuildReport() {
    DataFetcher fetcher = mock(DataFetcher.class);
    Formatter formatter = mock(Formatter.class);
    Validator validator = mock(Validator.class);
    Cache cache = mock(Cache.class);

    when(fetcher.fetchData("Q1-2024")).thenReturn(rawData);
    when(formatter.format(any())).thenReturn(formattedReport);
    when(cache.lookup("Q1-2024")).thenReturn(null);  // Cache miss

    ReportBuilder builder = new ReportBuilder(fetcher, formatter, validator, cache);
    Report result = builder.build("Q1-2024");

    assertEquals(formattedReport, result);  // Observable output
}
```

**Go (testify/mock)**
```go
// ANTI-PATTERN: Verifying internal call sequence
func TestBuildReport(t *testing.T) {
    fetcher := new(MockDataFetcher)
    formatter := new(MockFormatter)
    cache := new(MockCache)

    cache.On("Lookup", "Q1-2024").Return(nil, false)
    fetcher.On("FetchData", "Q1-2024").Return(rawData, nil)
    formatter.On("Format", mock.Anything).Return(formattedReport, nil)
    cache.On("Store", "Q1-2024", mock.Anything).Return(nil)

    builder := NewReportBuilder(fetcher, formatter, cache)
    builder.Build("Q1-2024")

    // Verifying exact call sequence -- implementation detail
    cache.AssertCalled(t, "Lookup", "Q1-2024")
    fetcher.AssertCalled(t, "FetchData", "Q1-2024")
    formatter.AssertCalled(t, "Format", mock.Anything)
    cache.AssertCalled(t, "Store", "Q1-2024", mock.Anything)
}
```

#### 4.3.3 White-Box Mock Expectations Mirroring Control Flow

**Java (Mockito)**
```java
// ANTI-PATTERN: Mock expectations mirror the if/else branches exactly
@Test
void shouldHandlePremiumUser() {
    UserRepository repo = mock(UserRepository.class);
    DiscountService discounts = mock(DiscountService.class);
    BillingService billing = mock(BillingService.class);

    User premiumUser = new User("Alice", UserTier.PREMIUM);
    when(repo.findById(1)).thenReturn(premiumUser);
    when(discounts.getPremiumDiscount(premiumUser)).thenReturn(0.20);

    PricingService service = new PricingService(repo, discounts, billing);
    service.calculatePrice(1, 100.0);

    // This mirrors the PREMIUM branch of an if/else in production code
    verify(discounts).getPremiumDiscount(premiumUser);
    verify(discounts, never()).getStandardDiscount(any());
    verify(billing).applyDiscount(eq(premiumUser), eq(0.20));
}

// CORRECTED: Assert on the computed price (the observable behavior)
@Test
void premiumUserGets20PercentDiscount() {
    UserRepository repo = mock(UserRepository.class);
    DiscountService discounts = mock(DiscountService.class);
    BillingService billing = mock(BillingService.class);

    when(repo.findById(1)).thenReturn(new User("Alice", UserTier.PREMIUM));
    when(discounts.getPremiumDiscount(any())).thenReturn(0.20);

    PricingService service = new PricingService(repo, discounts, billing);
    double price = service.calculatePrice(1, 100.0);

    assertEquals(80.0, price, 0.01);  // Observable behavior
}
```

**Python (pytest-mock)**
```python
# ANTI-PATTERN: Expectations mirror internal branching
def test_handle_premium_user(mocker):
    repo = mocker.Mock(spec=UserRepository)
    discounts = mocker.Mock(spec=DiscountService)

    premium_user = User("Alice", tier="PREMIUM")
    repo.find_by_id.return_value = premium_user
    discounts.get_premium_discount.return_value = 0.20

    service = PricingService(repo, discounts)
    service.calculate_price(1, 100.0)

    # Mirrors the if tier == PREMIUM branch
    discounts.get_premium_discount.assert_called_once_with(premium_user)
    discounts.get_standard_discount.assert_not_called()

# CORRECTED
def test_premium_user_gets_20_percent_discount(mocker):
    repo = mocker.Mock(spec=UserRepository)
    discounts = mocker.Mock(spec=DiscountService)

    repo.find_by_id.return_value = User("Alice", tier="PREMIUM")
    discounts.get_premium_discount.return_value = 0.20

    service = PricingService(repo, discounts)
    price = service.calculate_price(1, 100.0)

    assert price == 80.0  # Observable output
```

#### 4.3.4 Structural Inspection

**C# (xUnit)**
```csharp
// ANTI-PATTERN: Structural inspection
[Fact]
public void MessageProcessor_implements_IProcessor()
{
    var processor = new MessageProcessor();
    Assert.IsAssignableFrom<IProcessor>(processor);
}

[Fact]
public void MessageProcessor_uses_correct_sub_processors()
{
    var processor = new MessageProcessor();
    IReadOnlyList<IProcessor> processors = processor.SubProcessors;

    Assert.Equal(3, processors.Count);
    Assert.IsAssignableFrom<HeaderProcessor>(processors[0]);
    Assert.IsAssignableFrom<BodyProcessor>(processors[1]);
    Assert.IsAssignableFrom<MetadataProcessor>(processors[2]);
}

// CORRECTED: Test the observable behavior
[Fact]
public void MessageProcessor_processes_complete_message()
{
    var processor = new MessageProcessor();
    var result = processor.Process(new Message("header", "body", "metadata"));

    Assert.Equal(expectedOutput, result);
}
```

Source: [Enterprise Craftsmanship - Structural Inspection](https://enterprisecraftsmanship.com/posts/structural-inspection)

### 4.4 Static Detection Patterns

This is the hardest of the four anti-patterns to detect statically because the coupling is semantic rather than syntactic. However, several structural signals indicate its presence.

#### Signal 1: ArgumentCaptor / call_args Inspection (High Signal)

When a test captures arguments passed to a mock and then asserts on the captured object's internal fields, it is likely inspecting implementation details.

```regex
# Java (Mockito)
ARG_CAPTOR:      ArgumentCaptor<\w+>\s+\w+\s*=\s*ArgumentCaptor\.forClass
CAPTOR_ASSERT:   captor\.getValue\(\)\.(get|is|has)\w+\(

# Python
CALL_ARGS:       \.call_args(\[|\.)
CALL_ARGS_DEEP:  \.call_args\[0\]\[\d+\]\.\w+

# JavaScript (Jest)
MOCK_CALLS:      \.mock\.calls\[\d+\]\[\d+\]\.\w+

# Go (testify)
# testify doesn't have argument captors; instead check for
# MatchedBy with deeply inspecting lambda
MATCHED_BY:      mock\.MatchedBy\(func\(\w+\s+\w+\)\s*bool\s*\{[^}]*\.[A-Z]
```

#### Signal 2: verify(never()) / assert_not_called (Medium Signal)

Asserting that an internal branch was NOT taken (by verifying a method was NOT called) often mirrors internal control flow.

```regex
# Java (Mockito)
VERIFY_NEVER:    verify\(\s*\w+\s*,\s*never\(\)\s*\)

# Python
NOT_CALLED:      assert_not_called\(\)

# JavaScript (Jest)
NOT_CALLED_JEST: not\.toHaveBeenCalled\(\)

# Sinon
NOT_CALLED_SIN:  notCalled

# C# (Moq)
NOT_CALLED_MOQ:  Times\.Never

# C# (NSubstitute)
NOT_CALLED_NSUB: \.DidNotReceive\(\)

# Go (testify)
NOT_CALLED_GO:   AssertNotCalled\(
```

**Important nuance**: `verify(never())` is not ALWAYS an anti-pattern. Verifying that a destructive operation (e.g., `deleteAll()`) was NOT called can be a legitimate safety assertion. The signal is stronger when paired with a corresponding `verify()` on the alternative branch (indicating the test is tracing both branches of an if/else).

#### Signal 3: InOrder / Call Sequencing (High Signal)

Call ordering verification almost always tests implementation details.

(Same patterns as Section 3.4 -- ORDERING patterns.)

#### Signal 4: IsAssignableFrom / instanceof in Assertions (High Signal)

Testing type hierarchy is pure structural inspection.

```regex
# Java
TYPE_CHECK:      assert(True|That).*instanceof|isAssignableFrom\(

# C#
TYPE_CHECK_CS:   Assert\.IsAssignableFrom<|Assert\.IsType<|Assert\.IsInstanceOf<

# Python
TYPE_CHECK_PY:   assertIsInstance\(|assert\s+isinstance\(

# JavaScript
TYPE_CHECK_JS:   expect\(.*\)\.toBeInstanceOf\(

# Go
TYPE_CHECK_GO:   \.\(\*?\w+\)\s*$  # type assertion in test context
```

#### Signal 5: Accessing Internal/Private Properties via Mock Captures (Medium Signal)

When captured arguments are inspected via underscored or clearly-internal property names.

```regex
# Python (underscore prefix = private convention)
PRIVATE_ACCESS:  \._\w+

# JavaScript (underscore prefix)
PRIVATE_ACCESS_JS: \._\w+

# Java (internal package imports)
INTERNAL_IMPORT: import\s+\w+\.internal\.|import\s+\w+\.impl\.

# Go (unexported fields -- lowercase)
# Harder to detect with regex; requires knowing which fields are unexported
```

#### Signal 6: High verify-to-assert Ratio (Composite Signal)

If a test has many `verify()` calls but few `assertEquals()` / `assertTrue()` calls, it likely tests interactions (implementation) rather than outcomes (behavior).

```
verify_count = count of verify/Received/AssertCalled calls
assert_count = count of assertEquals/assertTrue/expect().toBe calls on return values

IF verify_count > assert_count AND verify_count >= 3:
    Flag as "interaction-heavy test" (potential implementation coupling)
```

### 4.5 Composite Detection Strategy

For each test method, compute the following signals:

| Signal | Weight | Detection Method |
|---|---|---|
| ArgumentCaptor / call_args deep inspection | 3 | Regex: captor + subsequent getter chain |
| InOrder / call ordering verification | 3 | Regex: InOrder, callOrder, assert_has_calls(any_order=False) |
| verify(never()) paired with verify() on alternative | 2 | Regex: never() within same test that has other verify() |
| Type checking assertions (instanceof, IsAssignableFrom) | 2 | Regex: type-check patterns |
| Internal property access on captured args | 2 | Regex: underscore prefix, .internal. imports |
| High verify-to-assert ratio (>3:1) | 1 | Counting |
| Mock setup count exceeds 5 per test | 1 | Counting |

**Threshold**: sum >= 4 flags the test as "testing internal details."

### 4.6 Confidence and Knowledge Gaps

**Confidence: HIGH** for definition. Khorikov, Meszaros, Beck, and Farley all describe this anti-pattern from different but converging perspectives.

**Confidence: MEDIUM** for static detection. The signals above are structural proxies for a semantic problem. False positives occur when:
- ArgumentCaptor is used to verify essential contract details (e.g., the payment amount passed to the gateway)
- `verify(never())` guards against genuinely dangerous operations
- Type assertions verify a public API contract (e.g., factory method return types)

**Confidence: LOW** for distinguishing "essential interaction verification" from "implementation coupling." This fundamentally requires understanding the domain context: is "the payment was charged exactly once" a business requirement (idempotency) or an implementation detail? Static analysis cannot answer this.

**Knowledge Gap**: No existing test smell catalog (testsmells.org, tsDetect, Spadini 2022) covers the subtle forms of implementation coupling described in section 4.3. The catalogs focus on syntactic smells (assertion count, sleep, reflection) rather than semantic coupling. This is arguably the most important gap in test smell research.

---

## 5. Cross-Cutting Detection Summary

### 5.1 Framework-Specific API Surface

| Framework | Mock Setup | Return Config | Verify/Assert Interaction | Exact Count | Ordering |
|---|---|---|---|---|---|
| **Mockito** | `mock(Class)`, `@Mock` | `when().thenReturn()` | `verify()` | `times(N)` | `InOrder` |
| **unittest.mock** | `Mock()`, `MagicMock()`, `patch()` | `.return_value =` | `assert_called*` | `assert_called_once*`, `.call_count` | `assert_has_calls(any_order=False)` |
| **pytest-mock** | `mocker.Mock()`, `mocker.patch()` | `.return_value =` | same as unittest.mock | same as unittest.mock | same |
| **Jest** | `jest.fn()`, `jest.mock()`, `jest.spyOn()` | `.mockReturnValue()` | `expect().toHaveBeenCalled*` | `toHaveBeenCalledTimes(N)` | `.mock.invocationCallOrder` |
| **Sinon** | `sinon.stub()`, `sinon.spy()`, `sinon.mock()` | `.returns()` | `sinon.assert.called*` | `calledOnce`, `callCount` | `sinon.assert.callOrder()` |
| **testify/mock** | `mock.Mock` embedded struct | `.On().Return()` | `AssertCalled`, `AssertExpectations` | `.Once()`, `.Times(N)` | not built-in |
| **gomock** | `NewMock*(ctrl)` | `.EXPECT().Return()` | implicit (controller) | `.Times(N)` | `gomock.InOrder()` |
| **Moq** | `new Mock<T>()` | `.Setup().Returns()` | `.Verify()` | `Times.Exactly(N)` | `MockSequence` |
| **NSubstitute** | `Substitute.For<T>()` | `.Returns()` | `.Received()` | `.Received(N)` | not built-in |

### 5.2 Anti-Pattern to Signal Matrix

| Signal | AP1: Mock Tautology | AP2: No Prod Code | AP3: Over-Specified | AP4: Internal Details |
|---|---|---|---|---|
| Mock setup + assert same mock return | PRIMARY | Contributing | -- | -- |
| Zero production class instantiation | Contributing | PRIMARY | -- | -- |
| `verify(times(N))` exact count | -- | -- | PRIMARY | Contributing |
| `InOrder` / call ordering | -- | -- | PRIMARY | PRIMARY |
| `verifyNoMoreInteractions` | -- | -- | PRIMARY | Contributing |
| ArgumentCaptor + deep inspection | -- | -- | Contributing | PRIMARY |
| `verify(never())` | -- | -- | Contributing | PRIMARY |
| instanceof/type assertions | -- | -- | -- | PRIMARY |
| High mock-to-real ratio | Contributing | PRIMARY | Contributing | -- |
| High verify-to-assert ratio | -- | Contributing | PRIMARY | PRIMARY |
| Internal property access (underscore) | -- | -- | -- | PRIMARY |

### 5.3 Existing Tool Support

| Tool | AP1 | AP2 | AP3 | AP4 | Notes |
|---|---|---|---|---|---|
| Mockito Strict Stubs | Partial | Partial | No | No | Catches unused stubs, not tautologies |
| eslint-plugin-jest | No | No | No | No | Has `expect-expect` (ensures assertions exist) but no mock-specific rules |
| SonarQube | No | No | No | No | No mock-specific test smell rules |
| PMD | No | No | No | No | No mock-specific rules |
| tsDetect (Peruma 2020) | No | No | No | No | Focuses on structural smells, not mock patterns |
| testsmells.org detector | No | No | No | No | Catalog does not include mock anti-patterns |

**Key finding**: As of 2026-02-12, **no mainstream static analysis tool or linter has dedicated rules for any of these four mock anti-patterns**. This represents a significant tooling gap.

---

## 6. Relationship to Farley Properties

| Anti-Pattern | Primary Farley Property Violated | Secondary Properties |
|---|---|---|
| Mock Tautology | **N (Necessary)** -- test adds zero value; also **M (Maintainable)** -- creates false confidence | G (Granular) -- assertion is meaningless |
| No Production Code Exercised | **N (Necessary)** -- test proves nothing about production code | M (Maintainable), T (First) -- would never arise from TDD |
| Over-Specified Interactions | **M (Maintainable)** -- test breaks on every refactoring | A (Atomic) -- complex mock setup often requires shared state |
| Testing Internal Details | **M (Maintainable)** -- test couples to implementation, not behavior | U (Understandable) -- tests describe HOW not WHAT |

---

## 7. Sources

### Primary References (Definition and Theory)

1. Gerard Meszaros, *xUnit Test Patterns: Refactoring Test Code* (Addison-Wesley, 2007). Defines "Overspecified Software," "Fragile Test," "Behavior Sensitivity." [Amazon](https://www.amazon.com/xUnit-Test-Patterns-Refactoring-Code/dp/0131495054)
2. Kent Beck, "Test Desiderata" (Medium, 2019). Defines "Structure-insensitive" property. [Medium](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3)
3. Vladimir Khorikov, *Unit Testing: Principles, Practices, and Patterns* (Manning, 2020). "Observable behavior vs. implementation details," mock fragility. [Manning](https://www.manning.com/books/unit-testing)
4. Dave Farley, "TDD & The Properties of Good Tests" (LinkedIn, 2024). Maintainable property. [LinkedIn](https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/)
5. James Carr, "TDD Anti-Patterns" (2006). Defines "The Mockery" and "The Inspector." [Archive](https://web.archive.org/web/20160605001457/http://blog.james-carr.org:80/2006/11/03/tdd-anti-patterns/)

### Secondary References (Code Examples and Detection)

6. J.B. Rainsberger, "The Curious Case of Tautological TDD" (2013). [Blog](https://blog.thecodewhisperer.com/permalink/the-curious-case-of-tautological-tdd)
7. Peter Williams, "Test Anti-Pattern: Proving the Code is Written Like the Code is Written" (2005). [Blog](https://barelyenough.org/blog/2005/10/test-anti-pattern-proving-the-code-is-written-like-the-code-is-written/)
8. Yegor Bugayenko, "Unit Testing Anti-Patterns, Full List" (2018). [Blog](https://www.yegor256.com/2018/12/11/unit-testing-anti-patterns.html)
9. Marabesi, "TDD Anti-Patterns Episode 2" (2021). Code examples in Kotlin, PHP, JavaScript. [Blog](https://marabesi.com/tdd/tdd-anti-patterns-ep-2.html)
10. Codurance, "TDD Anti-Patterns Chapter 2" (2021). [Blog](https://www.codurance.com/publications/tdd-anti-patterns-chapter-2)
11. Khorikov, "Structural Inspection" (Enterprise Craftsmanship blog). C# examples of structural inspection. [Blog](https://enterprisecraftsmanship.com/posts/structural-inspection)
12. Ian Cooper, "Mocks and the Dangers of Overspecified Software" (2007). Links Meszaros to practical mock overuse. [Blog](http://codebetter.com/iancooper/2007/12/19/mocks-and-the-dangers-of-overspecified-software/)

### Framework Documentation

13. Mockito Wiki, "How to write good tests." Tautology avoidance, "don't mock everything." [GitHub](https://github.com/mockito/mockito/wiki/How-to-write-good-tests)
14. Mockito Strict Stubbing documentation. [Baeldung](https://www.baeldung.com/mockito-unnecessary-stubbing-exception), [Mockito Blog](http://blog.mockito.org/2017/01/clean-tests-produce-clean-code-strict.html)
15. Spock Framework, "Interaction Based Testing." Over-specification warnings. [Spock Docs](https://spockframework.org/spock/docs/2.4-M4/interaction_based_testing.html)
16. NSubstitute, "Checking received calls." Exact count verification syntax. [NSubstitute Docs](https://nsubstitute.github.io/help/received-calls/)

### Test Smell Catalogs

17. testsmells.org, "Test Smell Types." 20 cataloged smells (none mock-specific). [Catalog](https://testsmells.org/pages/testsmells.html)
18. Peruma et al., "tsDetect: An Open Source Test Smells Detection Tool" (FSE 2020). AST-based detection for 19 smells. [Paper](https://testsmells.org/assets/publications/FSE2020_TechnicalPaper.pdf)

### Additional Context

19. "Learn Go with Tests -- Anti-Patterns." Mock overuse in Go context. [GitBook](https://quii.gitbook.io/learn-go-with-tests/meta/anti-patterns)
20. obra/superpowers, "Testing Anti-Patterns." Mock presence testing, mock-first thinking. [GitHub](https://github.com/obra/superpowers/blob/main/skills/test-driven-development/testing-anti-patterns.md)
21. Codepipes, "Software Testing Anti-Patterns." Testing internal implementation. [Blog](https://blog.codepipes.com/testing/software-testing-antipatterns.html)
22. Guerrer, "Unit Testing Anti-Patterns, Part 4." Tautological tests, implementation coupling. [Blog](https://ajguerrer.github.io/blog/unit-testing/4-antipatterns/)

---

## 8. Knowledge Gaps

### 8.1 No Dedicated Tooling Exists [HIGH PRIORITY]

**Searched**: SonarQube rules, PMD rules, ESLint rules (eslint-plugin-jest), tsDetect smell catalog, testsmells.org detector, Checkstyle rules, Pylint/flake8 plugins.

**Finding**: None of these tools have rules specifically targeting the four mock anti-patterns documented above. Mockito's strict stubbing is the closest, but it only catches unused stubs (a subset of anti-pattern 2).

**Implication**: Custom detection rules are required. The regex patterns in this document provide a starting point.

### 8.2 Boundary Between Essential and Over-Specified Verification [MEDIUM PRIORITY]

**Searched**: Academic literature on mock verification necessity, practitioner guidance on when `verify(times(N))` is appropriate.

**Finding**: No formal heuristic exists. Practitioner consensus is that verification is essential only for:
- Idempotency requirements (payment must be charged exactly once)
- Side-effect-only methods with no return value (the ONLY way to test is verification)
- Safety assertions (destructive operation must NOT be called)

Everything else is likely over-specification. But this requires domain knowledge.

### 8.3 testsmells.org Does Not Cover Mock Anti-Patterns [MEDIUM PRIORITY]

**Searched**: testsmells.org catalog (20 smells), tsDetect (19 smells).

**Finding**: Neither catalog includes any mock-specific smell. The closest smells are "Redundant Assertion" (tangential to mock tautology) and "Eager Test" (tangential to over-specification). This is a significant gap given that mock anti-patterns are among the most frequently cited test quality issues in practitioner literature.

### 8.4 Multi-Line Regex Limitations [LOW PRIORITY]

The regex patterns in this document assume signals can be detected within a bounded window (typically the test method body). Multi-line patterns with variable gaps are inherently fragile. AST-based detection would be significantly more reliable but requires language-specific parsers.

### 8.5 Language-Specific Gaps

- **Go**: testify/mock lacks argument captors, making anti-pattern 4 detection harder in Go. gomock's stricter expectation model makes anti-pattern 3 more common but also more syntactically detectable.
- **Python**: Dynamic typing means type resolution for "is this a mock or a real object?" is harder without runtime analysis.
- **JavaScript/TypeScript**: Object literal mocks (`{ method: jest.fn() }`) are harder to distinguish from real objects than `mock(Class.class)` in Java.

---

## 9. Confidence Summary

| Section | Confidence | Basis |
|---|---|---|
| Anti-pattern definitions (1.1, 2.1, 3.1, 4.1) | HIGH | 5+ independent sources each |
| Taxonomy mappings (1.2, 2.2, 3.2, 4.2) | HIGH | Direct catalog references verified |
| Code examples (1.3, 2.3, 3.3, 4.3) | HIGH | Based on framework documentation and practitioner sources |
| Regex detection patterns (1.4, 2.4, 3.4, 4.4) | MEDIUM | Patterns are syntactically sound but not empirically validated against large codebases |
| testsmells.org gap finding (8.3) | HIGH | Catalog explicitly reviewed; smells enumerated |
| Tooling gap finding (8.1) | HIGH | Multiple tool ecosystems checked |
| Essential vs. over-specified boundary (8.2) | LOW | No formal heuristic exists; domain-dependent |

---
name: refactoring-simplifying-conditionals
description: Mechanics for the 8 Simplifying Conditional Expressions refactorings (Decompose Conditional, Consolidate Conditional Expression, Consolidate Duplicate Conditional Fragments, Remove Control Flag, Replace Nested Conditional with Guard Clauses, Replace Conditional with Polymorphism, Introduce Null Object, Introduce Assertion). Load when a recommendation in this category needs step-by-step mechanics.
---

# Simplifying Conditional Expressions — Mechanics

## 4.1 Decompose Conditional
**Problem**: Complex conditional (if-then/else or switch)
**Solution**: Break complicated parts into separate methods
**When to Use**: Long conditional code, nested conditions
**Mechanics**:
1. Extract condition into separate method with descriptive name
2. Extract then part into separate method
3. Extract else part into separate method if it exists
4. Replace conditional statement with method calls
**Benefits**: Improved readability, descriptive method names
**Risk Level**: Low

## 4.2 Consolidate Conditional Expression
**Problem**: Multiple conditionals leading to same result
**Solution**: Combine conditionals into single expression
**When to Use**: Multiple alternating operators, identical actions
**Mechanics**:
1. Check that none of conditionals have side effects
2. Consolidate conditionals using logical operators (and/or)
3. Extract consolidated condition into method with descriptive name
4. Test after each consolidation step
**Benefits**: Eliminates duplicate control flow, improves readability
**Risk Level**: Low

## 4.3 Consolidate Duplicate Conditional Fragments
**Problem**: Identical code in all conditional branches
**Solution**: Move duplicated code outside conditional
**When to Use**: Code evolution created duplication
**Mechanics**:
1. Identify code that executes same way regardless of condition
2. If code at beginning, move before conditional
3. If code at end, move after conditional
4. If code in middle, look to move statements before or after
5. Test after moving each statement
**Eliminates**: Duplicate Code
**Risk Level**: Low

## 4.4 Remove Control Flag
**Problem**: Boolean variable acting as control flag
**Solution**: Replace with break, continue, return operators
**When to Use**: Outdated control flag patterns
**Mechanics**:
1. Find value of control flag that gets you out of logic
2. Replace assignments of that value with break or continue
3. Replace any remaining use of control flag with extracted condition
4. Test after each replacement
**Benefits**: Simplifies structure, explicit control flow
**Risk Level**: Low

## 4.5 Replace Nested Conditional with Guard Clauses
**Problem**: Nested conditionals making execution flow difficult
**Solution**: Isolate special checks into separate clauses before main checks
**When to Use**: Complex nested logic, obscured normal flow
**Mechanics**:
1. Select outermost condition that is guard clause
2. Replace with guard clause that returns if condition true
3. Continue with remaining conditionals until all guards extracted
4. Consolidate guard conditions if they result in same action
**Benefits**: Linear, readable format, clear edge case handling
**Risk Level**: Low

## 4.6 Replace Conditional with Polymorphism
**Problem**: Conditional performs various actions depending on object type
**Solution**: Create subclasses, move branches to polymorphic methods
**When to Use**: Conditionals varying by class/interface/field values
**Mechanics**:
1. Use Extract Method on conditional expression and each leg
2. Use Move Method to move conditional to appropriate class
3. Decide whether to keep superclass method abstract or provide default
4. Replace conditional calls with polymorphic method calls
5. Remove conditional logic once all subclasses have implementations
**Benefits**: Tell-Don't-Ask principle, supports Open/Closed
**Risk Level**: High

## 4.7 Introduce Null Object
**Problem**: Many null checks because methods return null
**Solution**: Create Null Object class with default behavior
**When to Use**: Repetitive null checks, simplify conditional logic
**Mechanics**:
1. Create null subclass of source class
2. Create isNull method in source class returning false
3. Override isNull in null class to return true
4. Find places that compare variable with null and replace
5. Find places that call methods on result and override in null class
**Benefits**: Eliminates null checks, improves readability
**Risk Level**: Medium

## 4.8 Introduce Assertion
**Problem**: Code portion requires certain conditions to be true
**Solution**: Replace assumptions with specific assertion checks
**When to Use**: Make implicit assumptions explicit, provide live documentation
**Mechanics**:
1. Identify condition you believe is always true
2. Add assertion that tests condition
3. Copy assertion to all places where condition should hold
4. Don't overuse assertions; focus on meaningful invariants
**Benefits**: Stop execution before fatal consequences, highlight assumptions
**Risk Level**: Low

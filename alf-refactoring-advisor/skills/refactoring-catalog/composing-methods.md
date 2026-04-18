---
name: refactoring-composing-methods
description: Mechanics for the 9 Composing Methods refactorings (Extract Method, Inline Method, Extract Variable, Inline Temp, Replace Temp with Query, Split Temporary Variable, Remove Assignments to Parameters, Replace Method with Method Object, Substitute Algorithm). Load when a recommendation in this category needs step-by-step mechanics.
---

# Composing Methods — Mechanics

Load this skill when a recommendation involves any of the 9 techniques below and the consumer needs step-by-step mechanics, risk, or smell-elimination notes.

## 1.1 Extract Method
**Problem**: Code fragment that can be grouped together
**Solution**: Move code to separate method, replace with method call
**When to Use**: Long methods, duplicate code, improve readability
**Mechanics**:
1. Create new method with descriptive name based on method's purpose
2. Copy relevant code fragment to new method
3. Scan for local variables used in fragment
4. Handle read-only variables as parameters
5. Handle modified variables as return values or by reference
6. Replace original code with method call
7. Test thoroughly after each change
**Eliminates**: Duplicate Code, Long Method, Feature Envy
**Risk Level**: Medium

## 1.2 Inline Method
**Problem**: Method body more obvious than method itself
**Solution**: Replace method calls with method content, delete method
**When to Use**: Unnecessary method delegation, overly simple methods
**Mechanics**:
1. Verify method is not redefined in subclasses
2. Find all calls to method
3. Replace each call with method body
4. Test after each replacement
5. Delete method definition
**Eliminates**: Speculative Generality
**Risk Level**: Low

## 1.3 Extract Variable
**Problem**: Expression that's hard to understand
**Solution**: Place complex expression parts into self-explanatory variables
**When to Use**: Complex expressions, preparing for Extract Method
**Mechanics**:
1. Insert line before expression
2. Declare new variable with descriptive name
3. Assign part of complex expression to variable
4. Replace part of expression with variable
5. Test to ensure behavior unchanged
**Benefits**: Improved readability, clear variable names replace comments
**Risk Level**: Low

## 1.4 Inline Temp
**Problem**: Temporary variable assigned simple expression result
**Solution**: Replace variable references with original expression
**When to Use**: Part of Replace Temp with Query, preparing Extract Method
**Mechanics**:
1. Find all usage of temporary variable
2. Replace each usage with right-hand side of assignment
3. Test after each replacement
4. Delete declaration and assignment of temporary
**Caution**: Consider performance impact for expensive operations
**Risk Level**: Low

## 1.5 Replace Temp with Query
**Problem**: Expression result placed in local variable for later use
**Solution**: Move expression to separate method, replace variable with method calls
**When to Use**: Same expression in multiple methods, preparing Extract Method
**Mechanics**:
1. Ensure variable assigned to only once
2. Extract right-hand side of assignment into method
3. Replace all references to temp with method call
4. Test after each replacement
5. Delete temporary variable
**Eliminates**: Long Method, Duplicate Code
**Risk Level**: Medium

## 1.6 Split Temporary Variable
**Problem**: Local variable used for multiple different intermediate values
**Solution**: Use separate variables for different values
**When to Use**: Variable reuse for unrelated purposes, preparing method extraction
**Mechanics**:
1. At first assignment to variable, change name
2. Change all references to variable up to second assignment
3. Test compilation and execution
4. Repeat for other assignments with different names
**Benefits**: Each variable has single responsibility
**Risk Level**: Low

## 1.7 Remove Assignments to Parameters
**Problem**: Value assigned to parameter inside method body
**Solution**: Create local variable, replace parameter modifications
**When to Use**: Prevent side effects, improve code clarity
**Mechanics**:
1. Create local variable and assign parameter value
2. Replace all references to parameter after assignment with new variable
3. Replace assignment to parameter with assignment to local variable
**Benefits**: Each element responsible for one thing, facilitates method extraction
**Risk Level**: Low

## 1.8 Replace Method with Method Object
**Problem**: Long method with intertwined local variables preventing Extract Method
**Solution**: Transform method into separate class with variables as fields
**When to Use**: Complex methods that can't be easily extracted
**Mechanics**:
1. Create new class named after method
2. Give class final field for original object and fields for each temporary
3. Give class constructor that takes original object and method parameters
4. Give class method named "compute"
5. Copy original method body into compute method
6. Replace original method with creation and call of new object
**Eliminates**: Long Method
**Drawback**: Increases program complexity
**Risk Level**: High

## 1.9 Substitute Algorithm
**Problem**: Replace existing algorithm with new, more efficient implementation
**Solution**: Replace method body with new algorithm
**When to Use**: Algorithm too complex, simpler implementation available
**Mechanics**:
1. Prepare alternative algorithm and ensure it works
2. Run new algorithm against existing tests
3. If behavior identical, replace old algorithm
4. Test to ensure new algorithm works correctly
**Eliminates**: Duplicate Code, Long Method
**Risk Level**: Medium

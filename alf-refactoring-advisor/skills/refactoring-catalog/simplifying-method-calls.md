---
name: refactoring-simplifying-method-calls
description: Mechanics for the 14 Simplifying Method Calls refactorings (Rename Method, Add/Remove Parameter, Separate Query from Modifier, Parameterize Method, Replace Parameter with Explicit Methods/Method Call, Preserve Whole Object, Introduce Parameter Object, Remove Setting Method, Hide Method, Replace Constructor with Factory Method, Replace Error Code with Exception, Replace Exception with Test). Load when a recommendation in this category needs step-by-step mechanics.
---

# Simplifying Method Calls — Mechanics

## 5.1 Rename Method
**Problem**: Method name doesn't explain what method does
**Solution**: Rename method to better reflect functionality
**When to Use**: Poor initial naming, functionality evolved
**Mechanics**:
1. Create new method with better name
2. Copy body of old method to new method
3. Find all references to old method and change to call new method
4. Delete old method after all references changed
5. Test after changing each group of references
**Eliminates**: Alternative Classes with Different Interfaces, Comments
**Risk Level**: Low

## 5.2 Add Parameter
**Problem**: Method doesn't have enough data to perform actions
**Solution**: Create new parameter to pass necessary data
**When to Use**: Need additional information, occasional/changing data
**Mechanics**:
1. Check whether method is polymorphic (could be overridden)
2. Create new method with additional parameter
3. Copy body of original method to new method
4. Find all callers and change them to call new method
5. Delete old method after all callers changed
**Drawback**: Risk of Long Parameter List
**Risk Level**: Low

## 5.3 Remove Parameter
**Problem**: Parameter isn't used in method body
**Solution**: Remove unused parameter
**When to Use**: Unnecessary parameters add complexity
**Mechanics**:
1. Check that parameter isn't used in method body
2. Check whether method is polymorphic
3. Create new method without parameter, copying method body
4. Find all callers and change to call new method
5. Delete old method after all callers changed
**Eliminates**: Speculative Generality
**Risk Level**: Low

## 5.4 Separate Query from Modifier
**Problem**: Method returns value and changes object state
**Solution**: Split into query method and modifier method
**When to Use**: Implement CQRS, increase predictability
**Mechanics**:
1. Create query method that returns value without side effects
2. Modify original method to call query and return its result
3. Find every call to modifier and replace with separate calls
4. Make original method return void after all callers changed
**Benefits**: Call queries without state changes, increased predictability
**Risk Level**: Medium

## 5.5 Parameterize Method
**Problem**: Multiple methods perform similar actions with different internal values
**Solution**: Combine methods by introducing parameter
**When to Use**: Eliminate duplicate code, make code more flexible
**Mechanics**:
1. Create parameterized method taking parameter for variation
2. Extract common algorithm to parameterized method
3. Replace literal values with parameter references
4. Change each original method to call parameterized method
5. Test after changing each method
**Eliminates**: Duplicate Code
**Risk Level**: Medium

## 5.6 Replace Parameter with Explicit Methods
**Problem**: Method contains multiple code paths based on parameter value
**Solution**: Create separate methods for each parameter-dependent variant
**When to Use**: Improve readability, simplify method structure
**Mechanics**:
1. Create explicit method for each value of parameter
2. For each explicit method, copy conditional logic for that parameter
3. Find callers and replace with calls to appropriate explicit method
4. Delete original method when no longer called
**Benefits**: Easier to understand purpose, clearer intent
**Risk Level**: Medium

## 5.7 Preserve Whole Object
**Problem**: Get several values from object and pass as parameters
**Solution**: Pass entire object instead of individual values
**When to Use**: Centralize data extraction, reduce parameter management
**Mechanics**:
1. Add parameter for whole object to method
2. Remove individual parameters obtained from object
3. Change method body to get values from new parameter
4. Find callers and change to pass whole object instead of values
**Benefits**: Improved readability, increased flexibility
**Risk Level**: Medium

## 5.8 Replace Parameter with Method Call
**Problem**: Calling query method and passing results as parameters
**Solution**: Place query call inside method body
**When to Use**: Long parameter lists, simplify method calls
**Mechanics**:
1. Check that parameter value doesn't change during method execution
2. Check whether evaluation of parameter has side effects
3. Replace parameter references with direct method calls
4. Use Remove Parameter to remove parameter entirely
5. Test after each change
**Benefits**: Eliminates unneeded parameters, simplifies calls
**Risk Level**: Low

## 5.9 Introduce Parameter Object
**Problem**: Methods contain repeating group of parameters
**Solution**: Replace parameter groups with single parameter object
**When to Use**: Eliminate duplication, consolidate related parameters
**Mechanics**:
1. Create immutable class for parameter group
2. Use Add Parameter to add parameter object to method
3. For each parameter in group, remove parameter and update references
4. Look for behavior that should move into parameter object
5. Consider making parameter object into value object
**Eliminates**: Long Parameter List, Data Clumps, Primitive Obsession
**Risk Level**: Medium

## 5.10 Remove Setting Method
**Problem**: Field value should be set only when created
**Solution**: Remove methods that set field after initial creation
**When to Use**: Prevent changes after initialization, enforce immutability
**Mechanics**:
1. Check that setting method is called only in constructor or method called by constructor
2. Make setting method callable only during construction
3. Move calls to setting method into constructor or constructor-called method
4. Delete setting method
**Benefits**: Increases predictability, reduces unexpected changes
**Risk Level**: Low

## 5.11 Hide Method
**Problem**: Method isn't used by other classes or only in class hierarchy
**Solution**: Make method private or protected
**When to Use**: Restrict visibility, simplify public interface
**Mechanics**:
1. Check regularly whether method can be made more private
2. Make each method as private as possible
3. Check after each change that method still accessible where needed
4. Test after each visibility change
**Eliminates**: Data Class
**Risk Level**: Low

## 5.12 Replace Constructor with Factory Method
**Problem**: Complex constructor doing more than setting parameter values
**Solution**: Create static factory method to replace constructor calls
**When to Use**: Working with type codes, advanced creation strategies
**Mechanics**:
1. Create factory method that calls existing constructor
2. Replace all calls to constructor with calls to factory method
3. Make constructor private if all external calls replaced
4. Test after replacing each group of constructor calls
**Benefits**: Can return subclass objects, descriptive names, return existing objects
**Risk Level**: Medium

## 5.13 Replace Error Code with Exception
**Problem**: Method returns special error code to indicate problem
**Solution**: Replace error codes with throwing exceptions
**When to Use**: Eliminate conditional checks, modern error handling
**Mechanics**:
1. Find all callers that check return value for error codes
2. Change method to throw exception instead of returning error code
3. Change all callers to expect exception in try-catch blocks
4. Test after changing each caller
**Benefits**: Eliminates conditional checks, more succinct error handling
**Risk Level**: Medium

## 5.14 Replace Exception with Test
**Problem**: Throw exception where simple test would work
**Solution**: Replace exception handling with conditional test
**When to Use**: Exceptions for routine conditions, improve readability
**Mechanics**:
1. Add conditional test that replicates condition causing exception
2. Move code from catch block to conditional
3. Add else clause with original method call
4. Remove try-catch block after ensuring conditional handles all cases
**Benefits**: Improved readability, explicit edge case handling
**Risk Level**: Low

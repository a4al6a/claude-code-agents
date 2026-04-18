---
name: refactoring-dealing-with-generalization
description: Mechanics for the 12 Dealing with Generalization refactorings (Pull Up/Push Down Field/Method/Constructor Body, Extract Subclass/Superclass/Interface, Collapse Hierarchy, Form Template Method, Replace Inheritance with Delegation and vice versa). Load when a recommendation in this category needs step-by-step mechanics.
---

# Dealing with Generalization — Mechanics

## 6.1 Pull Up Field
**Problem**: Two classes have same field
**Solution**: Remove field from subclasses, move to superclass
**When to Use**: Subclasses developed separately, eliminate duplication
**Mechanics**:
1. Inspect all uses of candidate fields to ensure same purpose
2. If fields have different names, rename to same name
3. Create field in superclass
4. Remove fields from subclasses
5. Test after each field removal
**Eliminates**: Duplicate Code
**Risk Level**: Low

## 6.2 Pull Up Method
**Problem**: Subclasses have methods performing similar work
**Solution**: Make methods identical, move to superclass
**When to Use**: Subclasses developed independently, eliminate duplication
**Mechanics**:
1. Investigate methods to ensure they do same thing
2. If signatures different, change to match desired superclass signature
3. Copy method to superclass
4. Remove methods from subclasses one at a time
5. Test after each removal
**Eliminates**: Duplicate Code
**Risk Level**: Medium

## 6.3 Pull Up Constructor Body
**Problem**: Subclasses have constructors with mostly identical code
**Solution**: Create superclass constructor, move common code
**When to Use**: Constructors can't be inherited, common initialization
**Mechanics**:
1. Create constructor in superclass
2. Move common code from beginning of subclass constructors
3. Call superclass constructor from subclass constructors
4. Move common code from end if applicable
**Benefits**: Eliminates duplication, improves organization
**Risk Level**: Medium

## 6.4 Push Down Field
**Problem**: Field used only in few subclasses
**Solution**: Move field to specific subclasses where used
**When to Use**: Planned usage didn't materialize, improve coherency
**Mechanics**:
1. Declare field in all subclasses that need it
2. Remove field from superclass
3. Test compilation and execution
4. Remove field from subclasses that don't need it
**Eliminates**: Refused Bequest
**Risk Level**: Low

## 6.5 Push Down Method
**Problem**: Behavior in superclass used by only one subclass
**Solution**: Move method from superclass to relevant subclass
**When to Use**: Method intended universal but used in one subclass
**Mechanics**:
1. Declare method in all subclasses where relevant
2. Copy method body from superclass to subclasses
3. Remove method from superclass
4. Test compilation and execution
5. Remove method from subclasses where not needed
**Eliminates**: Refused Bequest
**Risk Level**: Medium

## 6.6 Extract Subclass
**Problem**: Class has features used only in certain cases
**Solution**: Create subclass to handle specific use cases
**When to Use**: Main class has rare use case methods/fields
**Mechanics**:
1. Create subclass of source class
2. Provide constructors for subclass
3. Find all calls to constructors of source class
4. Replace with subclass constructor where appropriate
5. Use Push Down Method and Push Down Field to move features
**Eliminates**: Large Class
**Risk Level**: Medium

## 6.7 Extract Superclass
**Problem**: Two classes with common fields and methods
**Solution**: Create shared superclass, move identical functionality
**When to Use**: Code duplication in similar classes
**Mechanics**:
1. Create abstract superclass
2. Make original classes subclasses of superclass
3. Use Pull Up Field, Pull Up Method, Pull Up Constructor Body
4. Examine methods left in subclasses for further extraction opportunities
5. Remove original classes if they become empty
**Benefits**: Eliminates duplication, centralizes common functionality
**Risk Level**: Medium

## 6.8 Extract Interface
**Problem**: Multiple clients using same part of class interface
**Solution**: Move common interface portion to dedicated interface
**When to Use**: Indicate special roles, prepare for server type flexibility
**Mechanics**:
1. Create empty interface
2. Declare all common operations in interface
3. Make relevant classes implement interface
4. Adjust client type declarations to use interface
**Benefits**: Isolates common interfaces, explicit role definition
**Risk Level**: Low

## 6.9 Collapse Hierarchy
**Problem**: Subclass practically same as superclass
**Solution**: Merge subclass and superclass into single class
**When to Use**: Classes became nearly identical, reduce complexity
**Mechanics**:
1. Choose which class to remove (usually subclass)
2. Use Pull Up Field and Pull Up Method to move features to remaining class
3. Adjust references to removed class to point to remaining class
4. Remove empty class
5. Test after each feature move
**Benefits**: Reduces complexity, fewer classes, easier navigation
**Risk Level**: Medium

## 6.10 Form Template Method
**Problem**: Subclasses implement algorithms with similar steps in same order
**Solution**: Move algorithm structure to superclass, leave different implementations in subclasses
**When to Use**: Prevent code duplication, enable parallel development
**Mechanics**:
1. Decompose methods to identify varying and invariant parts
2. Use Extract Method to extract invariant parts with same signature
3. Use Pull Up Method to pull identical methods to superclass
4. For varying methods, declare abstract methods in superclass
5. Replace original algorithm with template method calling extracted methods
**Eliminates**: Duplicate Code
**Risk Level**: High

## 6.11 Replace Inheritance with Delegation
**Problem**: Subclass uses only portion of superclass methods
**Solution**: Create field containing superclass object, delegate methods
**When to Use**: Violates Liskov substitution, prevent unintended calls
**Mechanics**:
1. Create field in subclass that refers to instance of superclass
2. Change methods to delegate to superclass field
3. Remove inheritance link
4. Provide methods to access delegate if clients need them
**Benefits**: Eliminates unnecessary methods, enables Strategy pattern
**Risk Level**: High

## 6.12 Replace Delegation with Inheritance
**Problem**: Class contains many simple methods delegating to another class
**Solution**: Transform class into subclass of delegate class
**When to Use**: Delegation becomes complex, delegating to all public methods
**Mechanics**:
1. Make delegating class subclass of delegate
2. Remove delegate field and delegating methods
3. Replace all delegate field references with calls to superclass
4. Test after removing each group of delegating methods
**Benefits**: Reduces code length, eliminates redundant methods
**Risk Level**: Medium

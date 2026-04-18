---
name: refactoring-moving-features
description: Mechanics for the 8 Moving Features Between Objects refactorings (Move Method, Move Field, Extract Class, Inline Class, Hide Delegate, Remove Middle Man, Introduce Foreign Method, Introduce Local Extension). Load when a recommendation in this category needs step-by-step mechanics.
---

# Moving Features Between Objects — Mechanics

## 2.1 Move Method
**Problem**: Method used more in another class than its own
**Solution**: Create new method in recipient class, move code
**When to Use**: Improve class coherence, reduce dependencies
**Mechanics**:
1. Examine all features used by source method in source class
2. Check for other methods on source class called by candidate method
3. Check whether method is polymorphic in subclasses
4. Declare method in target class with similar or exact signature
5. Copy code from source to target, adjust for new environment
6. Determine how to reference correct target object from source
7. Turn source method into delegating method or remove entirely
**Eliminates**: Feature Envy, Shotgun Surgery
**Risk Level**: Medium

## 2.2 Move Field
**Problem**: Field used more in another class than its own
**Solution**: Create field in new class, redirect all references
**When to Use**: Put fields where methods that use them are located
**Mechanics**:
1. If field is public, use Encapsulate Field
2. Create field in target class with getting and setting methods
3. Determine how to reference target object from source
4. Replace all references to source field with target field
5. Delete field in source class
**Eliminates**: Shotgun Surgery
**Risk Level**: Medium

## 2.3 Extract Class
**Problem**: One class doing work of two
**Solution**: Create new class, move relevant fields and methods
**When to Use**: Classes accumulate responsibilities, maintain Single Responsibility
**Mechanics**:
1. Create new class to represent split-off responsibility
2. Create link from old class to new class
3. Use Move Field on each field you want to move
4. Use Move Method to move methods over one at a time
5. Review and reduce interfaces of both classes
6. Decide whether to expose new class publicly or keep as helper
**Benefits**: Maintains SRP, improves clarity, makes classes more reliable
**Risk Level**: Medium

## 2.4 Inline Class
**Problem**: Class does almost nothing, isn't responsible for anything
**Solution**: Move all features to another class, eliminate original
**When to Use**: After features transplanted elsewhere, eliminating needless classes
**Mechanics**:
1. Choose absorbing class (create new one if no obvious candidate)
2. Use Move Field and Move Method to move features
3. Replace all references to inlined class with absorbing class
4. Delete inlined class
**Eliminates**: Lazy Class, Shotgun Surgery, Speculative Generality
**Risk Level**: Medium

## 2.5 Hide Delegate
**Problem**: Client gets object B from A, then calls method on B
**Solution**: Create new method in A that delegates to B
**When to Use**: Reduce call chain complexity, minimize client knowledge
**Mechanics**:
1. For each method on delegate, create simple delegating method on server
2. Update client code to call server methods instead of delegate
3. Test after each method delegation
4. Remove delegate accessor or make it private
**Eliminates**: Message Chains, Inappropriate Intimacy
**Risk Level**: Low

## 2.6 Remove Middle Man
**Problem**: Class has too many methods that simply delegate
**Solution**: Delete delegating methods, force direct calls
**When to Use**: Server class creates unnecessary complexity
**Mechanics**:
1. Create accessor for delegate object
2. Replace each client call to delegating method with call to delegate
3. Delete delegating method after each replacement and test
4. Consider partial removal if only some methods are middle men
**Eliminates**: Middle Man
**Risk Level**: Low

## 2.7 Introduce Foreign Method
**Problem**: Utility class lacks needed method you can't add
**Solution**: Create method in client class with utility object parameter
**When to Use**: Can't modify third-party library, temporary workaround
**Mechanics**:
1. Create method in client class taking server class instance as parameter
2. Add server object as first parameter to new method
3. Extract code that uses server object into this method
4. Comment method as "foreign method, should be on server class"
**Benefits**: Reduces repetition, provides field access
**Risk Level**: Low

## 2.8 Introduce Local Extension
**Problem**: Utility class lacks multiple needed methods
**Solution**: Create extension class (subclass or wrapper) with new methods
**When to Use**: Cannot modify original, need comprehensive functionality
**Approaches**: Subclass extension or wrapper extension
**Mechanics**:
1. Create extension class as subclass or wrapper of original
2. Add constructors that match all constructors of original
3. Add new features to extension class
4. Replace original class usage with extension class
5. Move any foreign methods into extension class
**Risk Level**: Medium

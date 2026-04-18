---
name: refactoring-organizing-data
description: Mechanics for the 15 Organizing Data refactorings (Self Encapsulate Field, Replace Data Value with Object, Change Value/Reference, Replace Array with Object, Duplicate Observed Data, association direction changes, Encapsulate Field/Collection, Replace Magic Number, Replace Type Code variants, Replace Subclass with Fields). Load when a recommendation in this category needs step-by-step mechanics.
---

# Organizing Data — Mechanics

## 3.1 Self Encapsulate Field
**Problem**: Direct access to private fields inside class
**Solution**: Create getter/setter methods, use only these for access
**When to Use**: Want operations during field access, enable lazy initialization
**Mechanics**:
1. Create getting and setting methods for field
2. Find all references to field and replace with getting/setting methods
3. Make field private
4. Test after each group of replacements
**Benefits**: Flexible indirect access, complex operations during access
**Risk Level**: Low

## 3.2 Replace Data Value with Object
**Problem**: Data field grown beyond simple primitive value
**Solution**: Create new class, place field and behavior in class
**When to Use**: Primitive field became complex, duplicate code across classes
**Mechanics**:
1. Create class for value and add field of original type
2. Add getting method for field and constructor taking original type
3. Change type of field in source class to new class
4. Change getting method in source class to relay to new class
5. Change setting method to create new instance of new class
6. Consider using Change Value to Reference if needed
**Benefits**: Improves relatedness, consolidates data and behaviors
**Risk Level**: Medium

## 3.3 Change Value to Reference
**Problem**: Many identical instances need to be replaced with single object
**Solution**: Convert identical objects to single reference object
**When to Use**: Simple value needs changeable data consistently tracked
**Mechanics**:
1. Use Replace Constructor with Factory Method on value class
2. Determine what object is responsible for providing access to new object
3. Modify factory method to return reference object
4. Test after changing each client to use factory
**Benefits**: Object contains current information, changes accessible program-wide
**Risk Level**: Medium

## 3.4 Change Reference to Value
**Problem**: Reference object too small/infrequently changed to justify lifecycle management
**Solution**: Transform reference object into value object
**When to Use**: References require inconvenient management, want immutability
**Mechanics**:
1. Check that candidate is unchangeable or can be made unchangeable
2. Create equals method and hash method if needed
3. Consider providing public constructor instead of factory method
4. Test creation and comparison of new value objects
**Benefits**: Promotes immutability, consistent query results
**Risk Level**: Low

## 3.5 Replace Array with Object
**Problem**: Array contains various types of data
**Solution**: Replace array with object having separate fields
**When to Use**: Arrays used like "post office boxes", different data types
**Mechanics**:
1. Create class to represent information in array
2. Change array field to object field and create accessing methods
3. Change each access to array to use new object methods
4. When all array accesses replaced, delete array field
**Eliminates**: Primitive Obsession
**Risk Level**: Medium

## 3.6 Duplicate Observed Data
**Problem**: Domain data stored in GUI classes
**Solution**: Separate data into domain classes, ensure synchronization
**When to Use**: Enable multiple interface views, avoid tight coupling
**Mechanics**:
1. Hide direct access to GUI component data with methods
2. Create domain class with interface matching GUI accessor methods
3. Implement Observer pattern between GUI and domain classes
4. Use self-encapsulation to ensure data synchronization
**Benefits**: Splits responsibilities, follows SRP, enables parallel development
**Risk Level**: High

## 3.7 Change Unidirectional Association to Bidirectional
**Problem**: Two classes need to use each other's features but have only one-way link
**Solution**: Add back-pointer to class that doesn't have it
**When to Use**: Need reverse lookup, optimize frequent queries
**Mechanics**:
1. Add field for reverse pointer and modify methods to update both
2. Determine which class will control association
3. Create helper method in non-controlling class to set association
4. Modify existing modifier methods to use controlling methods
**Risk Level**: Medium

## 3.8 Change Bidirectional Association to Unidirectional
**Problem**: Bidirectional association where one direction isn't needed
**Solution**: Remove unnecessary direction of association
**When to Use**: Reduce complexity, remove unneeded dependencies
**Mechanics**:
1. Examine all readers of field to be removed
2. Provide alternative means for clients to get needed object
3. Remove all updates to field and field itself
4. Test after each change to reading code
**Risk Level**: Medium

## 3.9 Encapsulate Field
**Problem**: Public field exists
**Solution**: Make field private, create access methods
**When to Use**: Support encapsulation principle, separate data from behaviors
**Mechanics**:
1. Create getting and setting methods for field
2. Find all clients that reference field and change to use access methods
3. Make field private after all clients changed
**Benefits**: Easier maintenance, allows complex operations on access
**Risk Level**: Low

## 3.10 Encapsulate Collection
**Problem**: Class contains collection with simple getter/setter
**Solution**: Make getter read-only, create add/delete methods
**When to Use**: Prevent direct collection manipulation, gain control
**Mechanics**:
1. Create add and remove methods for collection
2. Initialize field to empty collection in constructor
3. Find callers of setting method and modify to use add/remove
4. Find users of getting method and change to use specific methods
5. Make getter return read-only view or copy of collection
**Eliminates**: Data Class
**Risk Level**: Medium

## 3.11 Replace Magic Number with Symbolic Constant
**Problem**: Code uses number with specific meaning
**Solution**: Replace numeric value with named constant
**When to Use**: Magic numbers make code harder to understand
**Mechanics**:
1. Declare constant with appropriate name and value
2. Find all occurrences of magic number
3. Check that magic number indeed has same meaning as constant
4. Replace magic number with constant
**Benefits**: Live documentation, easier value changes
**Risk Level**: Low

## 3.12 Replace Type Code with Class
**Problem**: Field containing type code not used in operator conditions
**Solution**: Create new class to replace primitive type code
**When to Use**: Common in database interactions, lacks type verification
**Mechanics**:
1. Create class for type code with field containing original code
2. Create static variables with instances for each type code
3. Create static factory method that returns appropriate instance
4. Change type of original field to new class
5. Change accessors to use new class
**Eliminates**: Primitive Obsession
**Risk Level**: Medium

## 3.13 Replace Type Code with Subclasses
**Problem**: Coded type directly affects program behavior
**Solution**: Create subclasses for each value, extract behaviors
**When to Use**: Type code uses primitive values, control flow code
**Mechanics**:
1. Self-encapsulate type code field if not already done
2. Make constructor of superclass private
3. Create factory method for superclass using switch on type code
4. Create subclass for each type code value
5. Override factory method in each subclass to return correct instance
**Eliminates**: Primitive Obsession
**Risk Level**: High

## 3.14 Replace Type Code with State/Strategy
**Problem**: Coded type affects behavior but can't use subclasses
**Solution**: Replace type code with state object
**When to Use**: Cannot use subclasses, need flexible behavior variation
**Mechanics**:
1. Create state class to represent type code
2. Create subclass of state class for each type code
3. Create factory in superstate to return appropriate state subclass
4. Change type code field to state field
5. Delegate type code dependent methods to state object
**Benefits**: Change object state during lifetime, follows Open/Closed
**Risk Level**: High

## 3.15 Replace Subclass with Fields
**Problem**: Subclasses differing only in constant-returning methods
**Solution**: Replace with fields in parent class, eliminate subclasses
**When to Use**: Simplify architecture, remove unnecessary subclasses
**Mechanics**:
1. Use Replace Constructor with Factory Method on subclasses
2. Add fields to superclass for variant information
3. Change subclass methods to return superclass field
4. Use Pull Up Method to pull identical methods to superclass
5. Remove subclasses one at a time and test
**Risk Level**: Medium

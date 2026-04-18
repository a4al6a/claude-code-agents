---
name: smell-catalog-taxonomy
description: Comprehensive definitions of 50+ code smells organized by category (Structural/Size, Data/Primitive, Method/Behavior, Naming, OO Design, Control Flow, Coupling, Organization, Implementation, Testing, Architecture). Load when writing smell descriptions in the detection report.
---

# Code Smell Taxonomy

Cite each smell using the canonical name and category below. Categories map to the high-level groupings (Bloaters, Couplers, Change Preventers, etc.) in the report.

## Structural & Size Smells
- **Large Class (God Object)**: Class with too many responsibilities and methods. Often SRP violation.
- **Long Method**: Method containing too many lines, hard to read and reason about.
- **Long Parameter List**: Methods with three or more input parameters.
- **Lazy Element**: Element that doesn't contribute enough value to justify its complexity.
- **Middle Man**: Class primarily exists to delegate methods to other classes.
- **Dead Code**: Code that is never executed (commented-out, unreachable). YAGNI violation.

## Data & Primitive Smells
- **Primitive Obsession**: Using primitives where objects would be clearer (string for date vs. Date).
- **Data Clump**: Multiple related variables repeatedly passed around instead of an object.
- **Magic Number**: Unexplained numeric literals lacking meaningful context.
- **Global Data**: Globally available data; hard to test/reason about, breaks encapsulation.
- **Mutable Data**: Data structures that can be modified, leading to side effects.
- **Temporary Field**: Variable created but only used in specific situations.
- **Status Variable**: Variables used to control program flow instead of proper control structures.

## Method & Behavior Smells
- **Feature Envy**: Method uses more features of another class than its own.
- **Side Effects**: Methods that modify state in unexpected ways.
- **Hidden Dependencies**: Methods that silently resolve dependencies without explicit declaration.
- **Duplicated Code**: Identical or very similar code blocks repeated.
- **Shotgun Surgery**: Need to modify multiple classes for a single change. SRP violation.
- **Divergent Change**: Single class has multiple unrelated responsibilities. SRP violation.

## Naming & Communication Smells
- **Uncommunicative Name**: Names that don't clearly express their purpose.
- **Inconsistent Names**: Names lack standardization across project.
- **Fallacious Method Name**: Method name doesn't accurately describe what the method does.
- **Type Embedded in Name**: Type information redundantly included in variable names.
- **Binary Operator in Name**: Using "and", "or" in names inappropriately.
- **Fallacious Comment**: Comments that are misleading or out of sync with code.
- **Boolean Blindness**: Function operating on booleans loses contextual meaning. (Replace `Bool` with a named sum type like `Keep = Drop | Take`.)

## Object-Oriented Design Smells
- **Refused Bequest**: Subclass uses only a subset of parent methods. LSP violation.
- **Base Class Depends on Subclass**: Parent classes that know about their children.
- **Inappropriate Static**: Static methods or variables used inappropriately.
- **Alternative Classes with Different Interfaces**: Similar functionality, different interfaces. DRY violation.
- **Incomplete Library Class**: Library classes that don't provide all needed functionality.

## Control Flow & Logic Smells
- **Conditional Complexity**: Overly complex conditional statements.
- **Complicated Boolean Expression**: Boolean logic that is difficult to understand.
- **Complicated Regex Expression**: Regexes that are hard to read and maintain.
- **Callback Hell**: Deeply nested callback functions.
- **Null Check**: Excessive null checking throughout the code.
- **Flag Argument**: Boolean parameters that control method behavior.

## Coupling & Dependency Smells
- **Message Chain**: Object must traverse multiple intermediates to retrieve data. Law of Demeter violation.
- **Insider Trading**: Classes access internal details of other classes inappropriately.
- **Tramp Data**: Data passed through multiple methods without being used.
- **Parallel Inheritance Hierarchies**: Creating a subclass forces creating subclasses in related hierarchies.

## Code Organization & Structure Smells
- **Speculative Generality**: Unnecessary complexity anticipating future needs. YAGNI violation.
- **Dubious Abstraction**: Abstractions that don't add value.
- **Indecent Exposure**: Making internal implementation details public.
- **Oddball Solution**: Solving the same problem differently in different places.

## Implementation & Style Smells
- **Clever Code**: Unnecessarily complex implementations.
- **Imperative Loops**: Traditional loops where functional approaches would be clearer.
- **Inconsistent Style**: Different coding styles used inconsistently.
- **Obscured Intent**: Code where the purpose is not clear from reading it.

## Testing & Maintenance Smells
- **Required Setup or Teardown Code**: Tests that require complex setup or cleanup.
- **Afraid to Fail**: Unnecessary status checks and complex error handling; fail-fast violation.
- **Special Case**: Code handling special cases instead of generalizing.

## Design Pattern & Architecture Smells
- **Combinatorial Explosion**: Exponential growth in the number of classes or methods.
- **Fate Over Action**: Passive objects having things done to them rather than being active.
- **What Comment**: Comments explaining what the code does rather than why.

## Severity hierarchy (use when ranking findings)

- **High Severity (Architectural Impact)**: Global Data, Shotgun Surgery, Base Class depends on Subclass, Feature Envy, Large Class, Combinatorial Explosion.
- **Moderate Severity (Design Issues)**: Clever Code, Duplicated Code, Magic Number, Primitive Obsession, Long Method, Callback Hell, Message Chain.
- **Low-Medium Severity (Readability/Maintenance)**: Fallacious Comment, Fallacious Method Name, Boolean Blindness, Dead Code, Speculative Generality.

## Historical sources to cite where relevant
- Martin Fowler (1999/2018): *Refactoring: Improving the Design of Existing Code*
- William C. Wake (2004): *Refactoring Workbook*
- Robert C. Martin (2008): *Clean Code*
- Marcel Jerzyk (2022): *Code Smells: A Comprehensive Online Catalog and Taxonomy*

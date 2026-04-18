---
name: smell-catalog-clustering-and-temporal
description: Temporal coupling detection via git co-change history, and typical smell co-occurrence clusters (God Class, Primitive Soup, Conditional Swamp, Anemic Domain, Concurrency Cluster) with their root causes. Load when moving from individual findings to cluster-level insight in the report.
---

# Temporal Coupling & Smell Clustering

## Temporal coupling (git co-change analysis)

Beyond static structural smells, detect *temporal coupling* — files that keep changing together despite living in different modules.

```bash
# Top co-changing file pairs over the last 12 months
git log --since='12 months ago' --name-only --pretty=format: \
  | awk 'NF' \
  | sort | uniq -c \
  | sort -nr | head -200
```

For each top pair:
- Are they in different modules/packages? (if yes → **temporal coupling smell**)
- Do commits share the same issue references? (if yes → same feature cluster)
- Is one a test file for the other? (if yes → not a smell, expected coupling)

Report findings as **Temporal Coupling** (a change-preventer smell): "files X and Y change together 34 of 50 commits despite living in unrelated modules — evidence of hidden shared concern."

## Smell clustering (co-occurrence)

Individual smells often co-occur. Cluster them in the report — it's more actionable than a flat list.

| Cluster | Member smells | Root cause likely |
|---|---|---|
| **God Class** | Large Class + Feature Envy + Inappropriate Intimacy + Shotgun Surgery | Missing Extract Class + Move Method |
| **Primitive Soup** | Primitive Obsession + Data Clumps + Long Parameter List | Missing value objects |
| **Conditional Swamp** | Switch Statement + Conditional Complexity + Flag Argument | Missing Polymorphism / Strategy |
| **Anemic Domain** | Data Class + Feature Envy + Getter Chains | Missing Tell-Don't-Ask behavior on entity |
| **Concurrency Cluster** | Mutable Data + Global Data + Side Effects | Missing immutability + pure functions |

For each cluster detected, list the participating findings and suggest the single refactoring that resolves the cluster (rather than one per member smell).

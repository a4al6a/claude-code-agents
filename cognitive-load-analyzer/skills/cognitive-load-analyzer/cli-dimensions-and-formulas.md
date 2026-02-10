---
name: cli-dimensions-and-formulas
description: Dimension definitions, sigmoid parameters, weights, and aggregation formulas for the Cognitive Load Index (CLI)
---

# CLI Dimensions and Formulas

> **Implementation note**: All formulas below are implemented in `lib/` (`core.py`, `dimensions.py`, `aggregation.py`, `sampling.py`) and exposed via `lib/cli_calculator.py`. The Python scripts are authoritative for calculation -- invoke them via Bash rather than computing manually. This document remains the specification reference.

## Sigmoid Normalization Function

All dimensions use sigmoid normalization to map raw metrics to [0, 1):

```
sigmoid(x, midpoint, steepness) = 1 / (1 + e^(-steepness * (x - midpoint)))
```

Properties: asymptotic lower bound (approaches 0), asymptotic upper bound (approaches 1), calibrated midpoint where score transitions from acceptable to problematic.

## Dimension Weights

| # | Dimension | Weight | Primary Load Type |
|---|---|---|---|
| D1 | Structural Complexity | 0.20 | Intrinsic (amplified) |
| D2 | Nesting Depth | 0.15 | Extraneous |
| D3 | Volume and Size | 0.12 | Extraneous |
| D4 | Naming Quality | 0.15 | Extraneous |
| D5 | Coupling | 0.12 | Intrinsic + Extraneous |
| D6 | Cohesion | 0.10 | Extraneous |
| D7 | Duplication | 0.08 | Extraneous |
| D8 | Navigability | 0.08 | Extraneous |

Sum of weights = 1.00

## D1: Structural Complexity (20%)

Measures decision-path complexity density across the codebase.

**Raw metrics**: Cyclomatic or Cognitive Complexity per function (CogC_f or CC_f), total function count (F).

**Formula**:
```
D1_raw = 0.4 * mean(score_f for all f) + 0.6 * P90(score_f for all f)
D1 = sigmoid(D1_raw, midpoint=15, steepness=0.15)
```

**Sigmoid calibration**:
- D1 < 0.2: Mean CogC < 5, P90 < 10 (excellent)
- D1 0.2-0.5: Mean CogC 5-15, P90 10-25 (acceptable)
- D1 0.5-0.8: Mean CogC 15-30, P90 25-50 (concerning)
- D1 > 0.8: Mean CogC > 30, P90 > 50 (severe)

## D2: Nesting Depth (15%)

Measures how deeply control structures are nested, compounding cognitive effort.

**Raw metrics**: Maximum nesting depth per function (maxNest_f).

**Formula**:
```
D2_raw = 0.3 * mean(maxNest_f for all f) + 0.7 * P90(maxNest_f for all f)
D2 = sigmoid(D2_raw, midpoint=4, steepness=0.5)
```

**Sigmoid calibration**: Midpoint of 4 reflects consensus that nesting beyond 3 is problematic, beyond 5 is severe.

## D3: Volume and Size (12%)

Measures whether functions, files, and classes exceed working memory capacity.

**Raw metrics**: Lines per function (LOC_f), lines per file (LOC_file), parameters per function (params_f), methods per class (methods_c).

**Formula**:
```
size_func   = sigmoid(P90(LOC_f),     midpoint=30,  steepness=0.05)
size_file   = sigmoid(P90(LOC_file),  midpoint=300, steepness=0.005)
size_params = sigmoid(mean(params_f), midpoint=4,   steepness=0.5)
size_class  = sigmoid(P90(methods_c), midpoint=15,  steepness=0.1)

D3 = 0.35 * size_func + 0.25 * size_file + 0.20 * size_params + 0.20 * size_class
```

**Midpoint rationale**: Function 30 LOC (Clean Code intersection with research), file 300 LOC (~10-15 functions), params 4 (working memory limit), methods 15 (SRP threshold).

## D4: Naming Quality (15%)

Measures how well identifiers communicate intent.

**Raw metrics**: Average identifier length, abbreviation density, single-char variable count per 100 LOC, naming convention consistency ratio.

**Formula (with LLM assessment)**:
```
naming_short       = proportion of identifiers shorter than 3 chars (excluding i, j, k, x, y)
naming_abbrev      = abbreviation_density (already 0-1)
naming_single_char = sigmoid(single_char_count_per_100_LOC, midpoint=2, steepness=0.5)
naming_consistency = 1.0 - consistency_ratio (0 = consistent, 1 = fully mixed)

D4_static = 0.30 * naming_short + 0.25 * naming_abbrev + 0.25 * naming_single_char + 0.20 * naming_consistency

# LLM semantic assessment (40% of final D4 when available):
D4 = 0.60 * D4_static + 0.40 * llm_naming_score
```

**LLM-agnostic fallback** (when LLM assessment is impractical):
```
D4_fallback = 0.35 * naming_short + 0.30 * naming_abbrev + 0.15 * naming_single_char + 0.10 * naming_consistency + 0.10 * (1.0 - dictionary_coverage)
```

**LLM Reproducibility Protocol**:
- Temperature: 0
- Sample: 20 identifiers per file, deterministic selection via SHA-256 of file path
- Seed: `int(hashlib.sha256(file_path.encode()).hexdigest()[:8], 16)`
- Exclusions: loop variables (i, j, k, x, y, n, _), language keywords, stdlib names
- Score each identifier 0.0 (excellent) to 1.0 (poor) using rubric:
  - 0.0-0.2: Clear, self-documenting
  - 0.2-0.4: Acceptable, minor improvements possible
  - 0.4-0.6: Ambiguous, requires context
  - 0.6-0.8: Poor, abbreviations or unclear meaning
  - 0.8-1.0: Cryptic or misleading
- Aggregate: mean per file, then LOC-weighted mean across files

## D5: Coupling (12%)

Measures how tightly modules depend on each other.

**Raw metrics**: Efferent coupling per module (Ce), import count per file, instability metric.

**Formula**:
```
coupling_efferent  = sigmoid(mean(Ce), midpoint=8, steepness=0.2)
coupling_imports   = sigmoid(mean(imports_per_file), midpoint=10, steepness=0.15)
instability_risk   = mean(Ce / (Ca + Ce + epsilon)) * mean(Ce)

D5 = 0.40 * coupling_efferent + 0.35 * coupling_imports + 0.25 * sigmoid(instability_risk, midpoint=5, steepness=0.2)
```

## D6: Cohesion (10%)

Measures whether classes/modules have a single clear responsibility.

**Raw metrics**: LCOM per class (0 = fully cohesive, 1 = no cohesion).

**Formula (class-based languages)**:
```
D6 = sigmoid(mean(LCOM_per_class), midpoint=0.5, steepness=4)
```

**Formula (functional/module-based languages)**:
```
module_cohesion = 1.0 - (avg_exports_used_together / total_exports)
D6 = sigmoid(module_cohesion, midpoint=0.4, steepness=4)
```

## D7: Duplication (8%)

Measures code clone density.

**Raw metrics**: Duplication percentage (duplicated lines / total lines).

**Formula**:
```
D7 = sigmoid(duplication_pct * 100, midpoint=5, steepness=0.3)
```

Midpoint at 5% duplication (SonarQube quality gate is 3%; 5% is realistic midpoint).

## D8: Navigability (8%)

Measures how easily a developer can find relevant code.

**Raw metrics**: Maximum directory depth, files per directory (P90), coefficient of variation of file sizes.

**Formula**:
```
nav_depth    = sigmoid(max_directory_depth, midpoint=5, steepness=0.4)
nav_density  = sigmoid(P90(files_per_directory), midpoint=15, steepness=0.1)
nav_variance = sigmoid(cv_file_sizes, midpoint=1.5, steepness=0.8)

D8 = 0.35 * nav_depth + 0.35 * nav_density + 0.30 * nav_variance
```

## Aggregation

```
CLI_raw = W1*D1 + W2*D2 + W3*D3 + W4*D4 + W5*D5 + W6*D6 + W7*D7 + W8*D8
```

## Interaction Multiplier

Compounding pairs -- when both dimensions in a pair exceed 0.6, add penalty:

```
interaction_penalty = 0
if D1 > 0.6 and D2 > 0.6: interaction_penalty += 0.05   # complexity + nesting
if D4 > 0.6 and D3 > 0.6: interaction_penalty += 0.05   # poor naming + large functions
if D5 > 0.6 and D6 > 0.6: interaction_penalty += 0.05   # high coupling + low cohesion

CLI = min(999, round((CLI_raw + interaction_penalty) * 1000))
```

Maximum interaction penalty: +150 points (all three pairs triggered).

## Large Codebase Sampling

For codebases exceeding 100K LOC:
```
selected = [f for f in sorted(files) if int(hashlib.sha256(f.encode()).hexdigest()[:8], 16) % 100 < 30]
```
Additionally always include all files exceeding 200 LOC. This deterministic method ensures identical file selection across runs.

## Polyglot Codebases

Analyze each language subset independently, then aggregate weighted by LOC proportion:
```
CLI_polyglot = sum(LOC_lang / LOC_total * CLI_lang) for each language
```
Report both aggregate and per-language breakdowns.

## Dimension-Specific Actions (for Recommendations)

| Dimension | High Score Indicates | Recommended Actions |
|---|---|---|
| D1 | Complex control flow | Extract methods; replace conditionals with polymorphism; simplify boolean expressions |
| D2 | Deep nesting | Apply guard clauses (early returns); extract nested blocks into named functions |
| D3 | Oversized units | Split large functions (< 30 LOC target); split large files (< 300 LOC); reduce params with parameter objects |
| D4 | Poor identifier quality | Rename to describe intent; eliminate abbreviations; standardize convention |
| D5 | Tight dependencies | Apply dependency inversion; use interfaces; reduce import counts |
| D6 | Mixed responsibilities | Apply SRP; split classes by responsibility; group related functions |
| D7 | Duplicated code | Extract shared logic; apply DRY; use parameterization |
| D8 | Poor organization | Flatten deep directories; group related files; consistent file naming |

General principle: when dimensions conflict, prioritize higher-weighted dimensions (D1, D4 over D7, D8).

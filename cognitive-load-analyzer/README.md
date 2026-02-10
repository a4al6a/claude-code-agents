# Cognitive Load Analyzer Agent

An AI agent that calculates a Cognitive Load Index (CLI) score (0-1000) for any codebase, measuring how much mental effort it demands from developers.

## Purpose

The cognitive-load-analyzer measures 8 dimensions of cognitive load using static analysis tools and LLM-based naming assessment, producing a scored report with per-dimension breakdown and actionable improvement recommendations.

## When to Use

Use this agent when:
- Assessing the cognitive complexity or maintainability of a codebase
- Measuring how hard code is to understand for new or existing developers
- Getting a quantitative cognitive load score to track over time
- Identifying cognitive hotspots that cause the most developer friction
- Prioritizing refactoring efforts based on comprehension difficulty
- Planning onboarding for new team members

## What It Measures

The CLI score is composed of 8 weighted dimensions:

| # | Dimension | Weight | What It Measures |
|---|---|---|---|
| D1 | Structural Complexity | 20% | Decision-path complexity (cyclomatic/cognitive complexity) |
| D2 | Nesting Depth | 15% | How deeply control structures are nested |
| D3 | Volume and Size | 12% | Function length, file size, parameter counts |
| D4 | Naming Quality | 15% | Identifier clarity, abbreviations, consistency |
| D5 | Coupling | 12% | Inter-module dependencies |
| D6 | Cohesion | 10% | Single responsibility adherence |
| D7 | Duplication | 8% | Code clone density |
| D8 | Navigability | 8% | File organization, directory structure |

## Rating Scale

| CLI Score | Rating |
|---|---|
| 0-100 | Excellent |
| 101-250 | Good |
| 251-400 | Moderate |
| 401-600 | Concerning |
| 601-800 | Poor |
| 801-999 | Severe |

## Skills

This agent includes a Python library (`skills/cognitive-load-analyzer/lib/`) that performs all calculations:
- **core.py** - Sigmoid normalization, P90, mean, coefficient of variation
- **dimensions.py** - D1-D8 dimension normalization functions
- **aggregation.py** - Weighted sum, interaction penalties, rating
- **sampling.py** - Deterministic file/identifier selection for large codebases
- **cli_calculator.py** - CLI entry point invoked by the agent via Bash

Supporting skill documents:
- **cli-dimensions-and-formulas.md** - Sigmoid parameters, weights, and formulas
- **cli-tool-commands.md** - Language-specific tool commands and fallbacks

## Tests

Unit and integration tests are in `tests/unit/`:

```bash
cd cognitive-load-analyzer
python -m pytest tests/unit/ -v
```

## Research

The `docs/` folder contains `cognitive-load-index-research.md`, a comprehensive research document covering:
- Literature review of existing metrics (McCabe, Halstead, Maintainability Index, Cognitive Complexity)
- Cognitive Load Theory applied to code (Sweller, Team Topologies)
- Neuroscience-based metric validation
- 52 sources consulted, 42 cited

## Agent Definition

- `cognitive-load-analyzer.md` - The full agent definition

## Attribution

This agent has been created with the agentic framework [nWave](https://nwave.ai).

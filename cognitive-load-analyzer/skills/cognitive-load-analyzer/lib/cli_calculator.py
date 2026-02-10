#!/usr/bin/env python3
"""CLI entry point for Cognitive Load Index calculations.

All output is JSON to stdout. Errors go to stderr.
Invoked by the alf-cognitive-load-analyzer agent via Bash.

Usage:
    python cli_calculator.py normalize-d1 '{"complexity_scores": [5, 10, 15]}'
    python cli_calculator.py aggregate '{"D1": 0.45, "D2": 0.32, ...}'
    python cli_calculator.py sample-files '{"file_paths": [...], "file_locs": {...}}'
"""

import json
import sys
import warnings


# Suppress all warnings to prevent stdout contamination
warnings.filterwarnings("ignore")

# Import library modules using relative path manipulation
from pathlib import Path  # noqa: E402


_lib_dir = str(Path(__file__).resolve().parent)
if _lib_dir not in sys.path:
    sys.path.insert(0, _lib_dir)

from aggregation import aggregate_polyglot, compute_cli_score, get_rating  # noqa: E402
from dimensions import (  # noqa: E402
    normalize_d1,
    normalize_d2,
    normalize_d3,
    normalize_d4_fallback,
    normalize_d4_static,
    normalize_d4_with_llm,
    normalize_d5,
    normalize_d6_class,
    normalize_d6_module,
    normalize_d7,
    normalize_d8,
)
from sampling import select_files, select_identifiers_for_file  # noqa: E402


def _ok(result):
    return {"ok": True, "result": result}


def _err(message):
    return {"ok": False, "error": message}


def cmd_normalize_d1(data):
    return _ok(normalize_d1(data["complexity_scores"]))


def cmd_normalize_d2(data):
    return _ok(normalize_d2(data["nesting_depths"]))


def cmd_normalize_d3(data):
    return _ok(
        normalize_d3(
            data["func_locs"],
            data["file_locs"],
            data["param_counts"],
            data["methods_per_class"],
        )
    )


def cmd_normalize_d4_static(data):
    return _ok(
        normalize_d4_static(
            data["short_name_proportion"],
            data["abbreviation_density"],
            data["single_char_per_100loc"],
            data["consistency_ratio"],
        )
    )


def cmd_normalize_d4_llm(data):
    return _ok(normalize_d4_with_llm(data["d4_static"], data["llm_score"]))


def cmd_normalize_d4_fallback(data):
    return _ok(
        normalize_d4_fallback(
            data["short_name_proportion"],
            data["abbreviation_density"],
            data["single_char_per_100loc"],
            data["consistency_ratio"],
            data["dictionary_coverage"],
        )
    )


def cmd_normalize_d5(data):
    return _ok(
        normalize_d5(
            data["efferent_couplings"],
            data["imports_per_file"],
            data["afferent_couplings"],
        )
    )


def cmd_normalize_d6_class(data):
    return _ok(normalize_d6_class(data["lcom_values"]))


def cmd_normalize_d6_module(data):
    return _ok(
        normalize_d6_module(
            data["avg_exports_used_together"],
            data["total_exports"],
        )
    )


def cmd_normalize_d7(data):
    return _ok(normalize_d7(data["duplication_pct"]))


def cmd_normalize_d8(data):
    return _ok(
        normalize_d8(
            data["max_directory_depth"],
            data["files_per_directory"],
            data["file_sizes"],
        )
    )


def cmd_aggregate(data):
    result = compute_cli_score(data)
    return _ok(
        {
            "cli_score": result.cli_score,
            "rating": result.rating,
            "cli_raw": result.cli_raw,
            "interaction_penalty": result.interaction_penalty,
            "weighted_components": result.weighted_components,
        }
    )


def cmd_aggregate_polyglot(data):
    return _ok(aggregate_polyglot(data["language_scores"]))


def cmd_sample_files(data):
    selected = select_files(
        data["file_paths"],
        sample_pct=data.get("sample_pct", 30),
        min_loc=data.get("min_loc", 200),
        file_locs=data.get("file_locs"),
    )
    return _ok({"selected_files": selected, "count": len(selected)})


def cmd_sample_identifiers(data):
    selected = select_identifiers_for_file(
        data["file_path"],
        data["identifiers"],
        count=data.get("count", 20),
    )
    return _ok({"selected_identifiers": selected, "count": len(selected)})


def cmd_rating(data):
    return _ok({"rating": get_rating(data["score"])})


COMMANDS = {
    "normalize-d1": cmd_normalize_d1,
    "normalize-d2": cmd_normalize_d2,
    "normalize-d3": cmd_normalize_d3,
    "normalize-d4-static": cmd_normalize_d4_static,
    "normalize-d4-llm": cmd_normalize_d4_llm,
    "normalize-d4-fallback": cmd_normalize_d4_fallback,
    "normalize-d5": cmd_normalize_d5,
    "normalize-d6-class": cmd_normalize_d6_class,
    "normalize-d6-module": cmd_normalize_d6_module,
    "normalize-d7": cmd_normalize_d7,
    "normalize-d8": cmd_normalize_d8,
    "aggregate": cmd_aggregate,
    "aggregate-polyglot": cmd_aggregate_polyglot,
    "sample-files": cmd_sample_files,
    "sample-identifiers": cmd_sample_identifiers,
    "rating": cmd_rating,
}


def main():
    if len(sys.argv) < 3:
        print(
            json.dumps(_err(f"Usage: {sys.argv[0]} <command> '<json_data>'")),
            file=sys.stdout,
        )
        sys.exit(1)

    command = sys.argv[1]
    json_str = sys.argv[2]

    if command not in COMMANDS:
        print(
            json.dumps(_err(f"Unknown command: {command}. Available: {', '.join(sorted(COMMANDS))}")),
            file=sys.stdout,
        )
        sys.exit(1)

    try:
        data = json.loads(json_str)
    except json.JSONDecodeError as e:
        print(json.dumps(_err(f"Invalid JSON: {e}")), file=sys.stdout)
        sys.exit(1)

    try:
        result = COMMANDS[command](data)
        print(json.dumps(result), file=sys.stdout)
    except KeyError as e:
        print(json.dumps(_err(f"Missing required field: {e}")), file=sys.stdout)
        sys.exit(1)
    except Exception as e:
        print(json.dumps(_err(f"Calculation error: {e}")), file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()

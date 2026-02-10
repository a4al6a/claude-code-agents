"""Integration tests for cli_calculator.py -- subprocess invocation with JSON I/O."""

import json
import subprocess
import sys
from pathlib import Path

import pytest


CLI_SCRIPT = str(
    Path(__file__).resolve().parents[2] / "skills" / "cognitive-load-analyzer" / "lib" / "cli_calculator.py"
)


def run_cli(command: str, data: dict) -> dict:
    """Helper: invoke cli_calculator.py and parse JSON output."""
    result = subprocess.run(
        [sys.executable, CLI_SCRIPT, command, json.dumps(data)],
        capture_output=True,
        text=True,
    )
    assert result.returncode == 0, f"CLI failed: {result.stderr}\nstdout: {result.stdout}"
    return json.loads(result.stdout)


class TestCliNormalizeDimensions:
    def test_normalize_d1(self):
        out = run_cli("normalize-d1", {"complexity_scores": [5, 10, 15, 20, 25]})
        assert out["ok"] is True
        assert 0 < out["result"]["d1"] < 1

    def test_normalize_d2(self):
        out = run_cli("normalize-d2", {"nesting_depths": [2, 3, 4, 5]})
        assert out["ok"] is True
        assert "d2" in out["result"]

    def test_normalize_d3(self):
        out = run_cli(
            "normalize-d3",
            {
                "func_locs": [20, 30, 40],
                "file_locs": [200, 300],
                "param_counts": [2, 3, 4],
                "methods_per_class": [5, 10],
            },
        )
        assert out["ok"] is True
        assert "d3" in out["result"]

    def test_normalize_d4_static(self):
        out = run_cli(
            "normalize-d4-static",
            {
                "short_name_proportion": 0.1,
                "abbreviation_density": 0.15,
                "single_char_per_100loc": 1.5,
                "consistency_ratio": 0.1,
            },
        )
        assert out["ok"] is True
        assert "d4_static" in out["result"]

    def test_normalize_d4_llm(self):
        out = run_cli("normalize-d4-llm", {"d4_static": 0.3, "llm_score": 0.4})
        assert out["ok"] is True
        assert out["result"]["d4"] == pytest.approx(0.60 * 0.3 + 0.40 * 0.4)

    def test_normalize_d4_fallback(self):
        out = run_cli(
            "normalize-d4-fallback",
            {
                "short_name_proportion": 0.1,
                "abbreviation_density": 0.1,
                "single_char_per_100loc": 1.0,
                "consistency_ratio": 0.1,
                "dictionary_coverage": 0.9,
            },
        )
        assert out["ok"] is True
        assert "d4_fallback" in out["result"]

    def test_normalize_d5(self):
        out = run_cli(
            "normalize-d5",
            {
                "efferent_couplings": [3, 5, 7],
                "imports_per_file": [4, 6, 8],
                "afferent_couplings": [5, 6, 7],
            },
        )
        assert out["ok"] is True
        assert "d5" in out["result"]

    def test_normalize_d6_class(self):
        out = run_cli("normalize-d6-class", {"lcom_values": [0.3, 0.4, 0.5]})
        assert out["ok"] is True
        assert "d6" in out["result"]

    def test_normalize_d6_module(self):
        out = run_cli(
            "normalize-d6-module",
            {"avg_exports_used_together": 8, "total_exports": 10},
        )
        assert out["ok"] is True
        assert "d6" in out["result"]

    def test_normalize_d7(self):
        out = run_cli("normalize-d7", {"duplication_pct": 0.05})
        assert out["ok"] is True
        assert out["result"]["d7"] == pytest.approx(0.5)

    def test_normalize_d8(self):
        out = run_cli(
            "normalize-d8",
            {
                "max_directory_depth": 4,
                "files_per_directory": [5, 10, 15],
                "file_sizes": [100, 200, 300],
            },
        )
        assert out["ok"] is True
        assert "d8" in out["result"]


class TestCliAggregate:
    def test_aggregate(self):
        scores = {f"D{i}": 0.4 for i in range(1, 9)}
        out = run_cli("aggregate", scores)
        assert out["ok"] is True
        assert out["result"]["cli_score"] == 400
        assert out["result"]["rating"] == "Moderate"

    def test_aggregate_with_penalty(self):
        scores = {f"D{i}": 0.7 for i in range(1, 9)}
        out = run_cli("aggregate", scores)
        assert out["ok"] is True
        assert out["result"]["interaction_penalty"] == pytest.approx(0.15)

    def test_aggregate_polyglot(self):
        out = run_cli(
            "aggregate-polyglot",
            {
                "language_scores": {
                    "python": {"cli_score": 300, "loc": 10000},
                    "go": {"cli_score": 200, "loc": 10000},
                }
            },
        )
        assert out["ok"] is True
        assert out["result"]["cli_score"] == 250


class TestCliSampling:
    def test_sample_files(self):
        paths = [f"src/file_{i}.py" for i in range(100)]
        out = run_cli("sample-files", {"file_paths": paths})
        assert out["ok"] is True
        assert out["result"]["count"] > 0

    def test_sample_identifiers(self):
        out = run_cli(
            "sample-identifiers",
            {
                "file_path": "test.py",
                "identifiers": [f"var_{i}" for i in range(50)],
                "count": 20,
            },
        )
        assert out["ok"] is True
        assert out["result"]["count"] == 20


class TestCliRating:
    def test_rating(self):
        out = run_cli("rating", {"score": 534})
        assert out["ok"] is True
        assert out["result"]["rating"] == "Concerning"


class TestCliErrors:
    def test_missing_args(self):
        result = subprocess.run(
            [sys.executable, CLI_SCRIPT],
            capture_output=True,
            text=True,
        )
        assert result.returncode != 0

    def test_unknown_command(self):
        result = subprocess.run(
            [sys.executable, CLI_SCRIPT, "unknown-cmd", "{}"],
            capture_output=True,
            text=True,
        )
        assert result.returncode != 0

    def test_invalid_json(self):
        result = subprocess.run(
            [sys.executable, CLI_SCRIPT, "normalize-d1", "not-json"],
            capture_output=True,
            text=True,
        )
        assert result.returncode != 0

    def test_missing_field(self):
        result = subprocess.run(
            [sys.executable, CLI_SCRIPT, "normalize-d1", "{}"],
            capture_output=True,
            text=True,
        )
        assert result.returncode != 0

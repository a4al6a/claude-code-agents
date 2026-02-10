"""Tests for dimensions.py D1-D8 normalization functions."""

import pytest
from dimensions import (
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


class TestNormalizeD1:
    def test_empty_returns_zeros(self):
        result = normalize_d1([])
        assert result["d1"] == 0.0
        assert result["raw"] == 0.0

    def test_low_complexity_below_0_3(self):
        # Low complexity: mean~3, P90~5 → raw=0.4*3+0.6*5=4.2
        result = normalize_d1([1, 2, 3, 4, 5])
        assert result["d1"] < 0.3

    def test_midpoint_at_15(self):
        # All scores of 15 → raw=15 → sigmoid=0.5
        result = normalize_d1([15] * 10)
        assert result["d1"] == pytest.approx(0.5)

    def test_high_complexity_above_0_7(self):
        result = normalize_d1([30, 35, 40, 45, 50])
        assert result["d1"] > 0.7

    def test_result_contains_all_keys(self):
        result = normalize_d1([10, 20])
        assert set(result.keys()) == {"d1", "raw", "mean", "p90"}

    def test_single_value(self):
        result = normalize_d1([15])
        assert result["mean"] == 15.0
        assert result["p90"] == 15.0


class TestNormalizeD2:
    def test_empty_returns_zeros(self):
        result = normalize_d2([])
        assert result["d2"] == 0.0

    def test_shallow_nesting_below_0_3(self):
        result = normalize_d2([1, 1, 2, 1, 2])
        assert result["d2"] < 0.3

    def test_midpoint_at_4(self):
        result = normalize_d2([4] * 10)
        assert result["d2"] == pytest.approx(0.5)

    def test_deep_nesting_above_0_7(self):
        result = normalize_d2([5, 6, 7, 8])
        assert result["d2"] > 0.7


class TestNormalizeD3:
    def test_all_empty_returns_zero(self):
        result = normalize_d3([], [], [], [])
        assert result["d3"] == 0.0

    def test_small_functions_low_score(self):
        result = normalize_d3(
            func_locs=[10, 15, 20],
            file_locs=[100, 150],
            param_counts=[2, 3, 2],
            methods_per_class=[5, 8],
        )
        assert result["d3"] < 0.4

    def test_large_everything_high_score(self):
        result = normalize_d3(
            func_locs=[100, 200, 300],
            file_locs=[1000, 2000],
            param_counts=[8, 10, 12],
            methods_per_class=[30, 40],
        )
        assert result["d3"] > 0.6

    def test_sub_components_present(self):
        result = normalize_d3([20], [200], [3], [10])
        assert all(k in result for k in ["d3", "size_func", "size_file", "size_params", "size_class"])

    def test_partial_empty_inputs(self):
        # Only func_locs provided, rest empty
        result = normalize_d3([50, 60], [], [], [])
        assert result["size_func"] > 0
        assert result["size_file"] == 0.0
        assert result["size_params"] == 0.0
        assert result["size_class"] == 0.0


class TestNormalizeD4Static:
    def test_perfect_naming(self):
        result = normalize_d4_static(
            short_name_proportion=0.0,
            abbreviation_density=0.0,
            single_char_per_100loc=0.0,
            consistency_ratio=0.0,  # fully consistent → naming_consistency=1.0
        )
        # 0.30*0 + 0.25*0 + 0.25*sigmoid(0, 2, 0.5) + 0.20*1.0
        # sigmoid(0, 2, 0.5) = 1/(1+e^(0.5*2)) = 1/(1+e^1) ≈ 0.269
        assert result["d4_static"] < 0.3

    def test_terrible_naming(self):
        result = normalize_d4_static(
            short_name_proportion=0.8,
            abbreviation_density=0.7,
            single_char_per_100loc=10.0,
            consistency_ratio=0.8,  # very inconsistent → naming_consistency=0.2
        )
        assert result["d4_static"] > 0.5

    def test_all_keys_present(self):
        result = normalize_d4_static(0.1, 0.1, 1.0, 0.1)
        assert set(result.keys()) == {"d4_static", "naming_short", "naming_abbrev", "naming_single_char", "naming_consistency"}


class TestNormalizeD4WithLlm:
    def test_blending(self):
        result = normalize_d4_with_llm(0.3, 0.5)
        assert result["d4"] == pytest.approx(0.60 * 0.3 + 0.40 * 0.5)

    def test_perfect_scores(self):
        result = normalize_d4_with_llm(0.0, 0.0)
        assert result["d4"] == pytest.approx(0.0)

    def test_worst_scores(self):
        result = normalize_d4_with_llm(1.0, 1.0)
        assert result["d4"] == pytest.approx(1.0)


class TestNormalizeD4Fallback:
    def test_with_dictionary_coverage(self):
        result = normalize_d4_fallback(
            short_name_proportion=0.1,
            abbreviation_density=0.1,
            single_char_per_100loc=1.0,
            consistency_ratio=0.1,
            dictionary_coverage=0.9,
        )
        assert result["d4_fallback"] < 0.3
        assert "dict_penalty" in result

    def test_weights_sum_to_1(self):
        assert pytest.approx(1.0) == 0.35 + 0.30 + 0.15 + 0.10 + 0.10


class TestNormalizeD5:
    def test_low_coupling(self):
        result = normalize_d5(
            efferent_couplings=[2, 3, 4],
            imports_per_file=[3, 4, 5],
            afferent_couplings=[5, 6, 7],
        )
        assert result["d5"] < 0.4

    def test_high_coupling(self):
        result = normalize_d5(
            efferent_couplings=[20, 25, 30],
            imports_per_file=[20, 25, 30],
            afferent_couplings=[1, 1, 1],
        )
        assert result["d5"] > 0.6

    def test_empty_inputs(self):
        result = normalize_d5([], [], [])
        assert result["d5"] < 0.5  # sigmoid(0, 8, 0.2) contributes

    def test_mismatched_lengths_uses_minimum(self):
        result = normalize_d5([5, 10], [8], [3, 7, 9])
        assert "instability_risk" in result


class TestNormalizeD6Class:
    def test_empty_returns_zero(self):
        result = normalize_d6_class([])
        assert result["d6"] == 0.0

    def test_fully_cohesive(self):
        result = normalize_d6_class([0.0, 0.0, 0.1])
        assert result["d6"] < 0.3

    def test_midpoint_at_0_5(self):
        result = normalize_d6_class([0.5])
        assert result["d6"] == pytest.approx(0.5)

    def test_no_cohesion(self):
        result = normalize_d6_class([0.9, 0.95, 1.0])
        assert result["d6"] > 0.7


class TestNormalizeD6Module:
    def test_zero_exports(self):
        result = normalize_d6_module(0, 0)
        assert result["d6"] == 0.0

    def test_high_cohesion(self):
        result = normalize_d6_module(8, 10)  # cohesion=0.2
        assert result["d6"] < 0.4

    def test_low_cohesion(self):
        result = normalize_d6_module(2, 10)  # cohesion=0.8
        assert result["d6"] > 0.7


class TestNormalizeD7:
    def test_zero_duplication(self):
        result = normalize_d7(0.0)
        assert result["d7"] < 0.2

    def test_midpoint_at_5_percent(self):
        result = normalize_d7(0.05)  # 5% → sigmoid midpoint
        assert result["d7"] == pytest.approx(0.5)

    def test_high_duplication(self):
        result = normalize_d7(0.20)  # 20%
        assert result["d7"] > 0.9

    def test_input_is_fraction_not_percentage(self):
        # 0.05 means 5%, not 0.05%
        result = normalize_d7(0.05)
        assert result["duplication_pct_100"] == pytest.approx(5.0)


class TestNormalizeD8:
    def test_flat_project(self):
        result = normalize_d8(
            max_directory_depth=2,
            files_per_directory=[5, 8, 3],
            file_sizes=[100, 150, 200],
        )
        assert result["d8"] < 0.4

    def test_deep_dense_project(self):
        result = normalize_d8(
            max_directory_depth=10,
            files_per_directory=[30, 40, 50],
            file_sizes=[50, 500, 5000],
        )
        assert result["d8"] > 0.6

    def test_empty_lists(self):
        result = normalize_d8(
            max_directory_depth=3,
            files_per_directory=[],
            file_sizes=[],
        )
        assert result["nav_density"] == 0.0
        assert result["nav_variance"] < 0.5  # sigmoid(0, 1.5, 0.8) < 0.5

    def test_all_components_present(self):
        result = normalize_d8(3, [10], [100])
        assert set(result.keys()) == {"d8", "nav_depth", "nav_density", "nav_variance"}

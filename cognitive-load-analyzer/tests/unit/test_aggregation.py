"""Tests for aggregation.py -- weighted sum, penalties, ratings."""

import pytest
from aggregation import (
    WEIGHTS,
    aggregate_polyglot,
    compute_cli_score,
    compute_interaction_penalty,
    compute_weighted_sum,
    get_rating,
)


class TestComputeWeightedSum:
    def test_weights_sum_to_1(self):
        assert sum(WEIGHTS.values()) == pytest.approx(1.0)

    def test_all_zeros(self):
        scores = {f"D{i}": 0.0 for i in range(1, 9)}
        assert compute_weighted_sum(scores) == pytest.approx(0.0)

    def test_all_ones(self):
        scores = {f"D{i}": 1.0 for i in range(1, 9)}
        assert compute_weighted_sum(scores) == pytest.approx(1.0)

    def test_only_d1(self):
        scores = {f"D{i}": 0.0 for i in range(1, 9)}
        scores["D1"] = 0.5
        assert compute_weighted_sum(scores) == pytest.approx(0.5 * 0.20)

    def test_missing_dimensions_treated_as_zero(self):
        assert compute_weighted_sum({"D1": 0.5}) == pytest.approx(0.5 * 0.20)


class TestComputeInteractionPenalty:
    def test_no_penalty_below_threshold(self):
        scores = {f"D{i}": 0.5 for i in range(1, 9)}
        assert compute_interaction_penalty(scores) == 0.0

    def test_single_pair_penalty(self):
        scores = {f"D{i}": 0.5 for i in range(1, 9)}
        scores["D1"] = 0.7
        scores["D2"] = 0.7
        assert compute_interaction_penalty(scores) == pytest.approx(0.05)

    def test_all_three_pairs_penalty(self):
        scores = {f"D{i}": 0.7 for i in range(1, 9)}
        assert compute_interaction_penalty(scores) == pytest.approx(0.15)

    def test_one_below_threshold_no_penalty(self):
        scores = {f"D{i}": 0.5 for i in range(1, 9)}
        scores["D1"] = 0.7
        scores["D2"] = 0.59  # just below
        assert compute_interaction_penalty(scores) == 0.0

    def test_boundary_at_0_6_no_penalty(self):
        scores = {f"D{i}": 0.6 for i in range(1, 9)}
        assert compute_interaction_penalty(scores) == 0.0  # > 0.6, not >= 0.6


class TestComputeCliScore:
    def test_all_zeros_score_0(self):
        scores = {f"D{i}": 0.0 for i in range(1, 9)}
        result = compute_cli_score(scores)
        assert result.cli_score == 0
        assert result.rating == "Excellent"

    def test_all_ones_with_penalty(self):
        scores = {f"D{i}": 1.0 for i in range(1, 9)}
        result = compute_cli_score(scores)
        # raw=1.0, penalty=0.15, total=1.15 → min(999, 1150) = 999
        assert result.cli_score == 999
        assert result.rating == "Severe"

    def test_moderate_scores(self):
        scores = {f"D{i}": 0.35 for i in range(1, 9)}
        result = compute_cli_score(scores)
        # raw=0.35, no penalty → 350
        assert result.cli_score == 350
        assert result.rating == "Moderate"

    def test_result_has_all_fields(self):
        scores = {f"D{i}": 0.3 for i in range(1, 9)}
        result = compute_cli_score(scores)
        assert hasattr(result, "cli_score")
        assert hasattr(result, "rating")
        assert hasattr(result, "cli_raw")
        assert hasattr(result, "interaction_penalty")
        assert hasattr(result, "weighted_components")

    def test_capped_at_999(self):
        scores = {f"D{i}": 1.0 for i in range(1, 9)}
        result = compute_cli_score(scores)
        assert result.cli_score <= 999


class TestGetRating:
    def test_excellent(self):
        assert get_rating(0) == "Excellent"
        assert get_rating(100) == "Excellent"

    def test_good(self):
        assert get_rating(101) == "Good"
        assert get_rating(250) == "Good"

    def test_moderate(self):
        assert get_rating(251) == "Moderate"
        assert get_rating(400) == "Moderate"

    def test_concerning(self):
        assert get_rating(401) == "Concerning"
        assert get_rating(600) == "Concerning"

    def test_poor(self):
        assert get_rating(601) == "Poor"
        assert get_rating(800) == "Poor"

    def test_severe(self):
        assert get_rating(801) == "Severe"
        assert get_rating(999) == "Severe"


class TestAggregatePolyglot:
    def test_single_language(self):
        result = aggregate_polyglot({"python": {"cli_score": 300, "loc": 10000}})
        assert result["cli_score"] == 300

    def test_two_languages_weighted(self):
        result = aggregate_polyglot(
            {
                "python": {"cli_score": 342, "loc": 12000},
                "typescript": {"cli_score": 427, "loc": 15000},
            }
        )
        expected = round(12000 / 27000 * 342 + 15000 / 27000 * 427)
        assert result["cli_score"] == expected

    def test_empty_loc(self):
        result = aggregate_polyglot({"python": {"cli_score": 300, "loc": 0}})
        assert result["cli_score"] == 0

    def test_breakdown_present(self):
        result = aggregate_polyglot(
            {
                "python": {"cli_score": 300, "loc": 5000},
                "go": {"cli_score": 200, "loc": 5000},
            }
        )
        assert "python" in result["breakdown"]
        assert "go" in result["breakdown"]
        assert result["breakdown"]["python"]["weight"] == pytest.approx(0.5)

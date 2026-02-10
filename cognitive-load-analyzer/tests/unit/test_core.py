"""Tests for core.py math primitives."""


import pytest
from core import coefficient_of_variation, mean, p90, sigmoid


class TestSigmoid:
    def test_midpoint_returns_0_5(self):
        assert sigmoid(15, midpoint=15, steepness=0.15) == pytest.approx(0.5)

    def test_high_value_approaches_1(self):
        result = sigmoid(100, midpoint=15, steepness=0.15)
        assert result > 0.99

    def test_low_value_approaches_0(self):
        result = sigmoid(-50, midpoint=15, steepness=0.15)
        assert result < 0.01

    def test_overflow_guard_large_positive_exponent(self):
        # z = -steepness * (x - midpoint) > 500 → should return 0.0
        result = sigmoid(-10000, midpoint=0, steepness=1)
        assert result == 0.0

    def test_overflow_guard_large_negative_exponent(self):
        # z = -steepness * (x - midpoint) < -500 → should return 1.0
        result = sigmoid(10000, midpoint=0, steepness=1)
        assert result == 1.0

    def test_steepness_affects_slope(self):
        # Higher steepness = sharper transition
        gentle = sigmoid(16, midpoint=15, steepness=0.1)
        steep = sigmoid(16, midpoint=15, steepness=1.0)
        assert steep > gentle

    def test_zero_steepness_returns_0_5(self):
        assert sigmoid(100, midpoint=15, steepness=0) == pytest.approx(0.5)

    def test_known_d1_calibration(self):
        # D1 with midpoint=15, steepness=0.15, raw=15 → 0.5
        assert sigmoid(15, 15, 0.15) == pytest.approx(0.5)
        # raw=25 should be notably above 0.5
        result = sigmoid(25, 15, 0.15)
        assert 0.7 < result < 0.9


class TestP90:
    def test_empty_returns_0(self):
        assert p90([]) == 0.0

    def test_single_value(self):
        assert p90([42.0]) == 42.0

    def test_two_values(self):
        # P90 of [10, 20]: idx = 0.9 * (2-1) = 0.9
        # lo=0, hi=1, frac=0.9 → 10 + 0.9 * 10 = 19.0
        assert p90([10.0, 20.0]) == pytest.approx(19.0)

    def test_ten_values(self):
        values = list(range(1, 11))  # 1..10
        # idx = 0.9 * 9 = 8.1 → lo=8, hi=9, frac=0.1
        # sorted[8]=9, sorted[9]=10 → 9 + 0.1 * 1 = 9.1
        assert p90(values) == pytest.approx(9.1)

    def test_unsorted_input(self):
        assert p90([5, 1, 3, 2, 4]) == p90([1, 2, 3, 4, 5])

    def test_all_same_values(self):
        assert p90([7.0, 7.0, 7.0, 7.0]) == pytest.approx(7.0)

    def test_hundred_values(self):
        values = list(range(1, 101))
        # idx = 0.9 * 99 = 89.1
        # sorted[89]=90, sorted[90]=91 → 90 + 0.1 * 1 = 90.1
        assert p90(values) == pytest.approx(90.1)


class TestMean:
    def test_empty_returns_0(self):
        assert mean([]) == 0.0

    def test_single_value(self):
        assert mean([5.0]) == 5.0

    def test_multiple_values(self):
        assert mean([1.0, 2.0, 3.0, 4.0, 5.0]) == pytest.approx(3.0)

    def test_negative_values(self):
        assert mean([-5.0, 5.0]) == pytest.approx(0.0)


class TestCoefficientOfVariation:
    def test_empty_returns_0(self):
        assert coefficient_of_variation([]) == 0.0

    def test_all_same_returns_0(self):
        assert coefficient_of_variation([5.0, 5.0, 5.0]) == pytest.approx(0.0)

    def test_all_zeros_returns_0(self):
        assert coefficient_of_variation([0.0, 0.0]) == 0.0

    def test_known_values(self):
        # [2, 4, 4, 4, 5, 5, 7, 9] → mean=5, population std=2.0, CV=0.4
        values = [2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0]
        assert coefficient_of_variation(values) == pytest.approx(0.4, abs=0.01)

    def test_single_value_returns_0(self):
        # Single value: std=0, mean=nonzero → CV=0
        assert coefficient_of_variation([10.0]) == pytest.approx(0.0)

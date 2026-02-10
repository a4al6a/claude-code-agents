"""Pure math primitives for Cognitive Load Index calculation.

No external dependencies -- uses only Python stdlib math.
"""

import math


def sigmoid(x: float, midpoint: float, steepness: float) -> float:
    """Sigmoid normalization: 1 / (1 + e^(-steepness * (x - midpoint))).

    Overflow-safe: clamps the exponent to avoid math.exp overflow.
    Returns a value in (0, 1).
    """
    z = -steepness * (x - midpoint)
    if z > 500:
        return 0.0
    if z < -500:
        return 1.0
    return 1.0 / (1.0 + math.exp(z))


def p90(values: list[float]) -> float:
    """90th percentile via linear interpolation.

    Matches Python's statistics approach: sorts, computes the index
    for the 90th percentile, and interpolates between adjacent values.
    Returns 0.0 for empty input.
    """
    if not values:
        return 0.0
    sorted_vals = sorted(values)
    n = len(sorted_vals)
    if n == 1:
        return sorted_vals[0]
    # Index for P90 using linear interpolation (exclusive method)
    idx = 0.9 * (n - 1)
    lo = math.floor(idx)
    hi = math.ceil(idx)
    frac = idx - lo
    return sorted_vals[lo] + frac * (sorted_vals[hi] - sorted_vals[lo])


def mean(values: list[float]) -> float:
    """Arithmetic mean. Returns 0.0 for empty input."""
    if not values:
        return 0.0
    return sum(values) / len(values)


def coefficient_of_variation(values: list[float]) -> float:
    """Coefficient of variation (std_dev / mean). Returns 0.0 if mean is 0."""
    if not values:
        return 0.0
    m = mean(values)
    if m == 0:
        return 0.0
    variance = sum((v - m) ** 2 for v in values) / len(values)
    return math.sqrt(variance) / m

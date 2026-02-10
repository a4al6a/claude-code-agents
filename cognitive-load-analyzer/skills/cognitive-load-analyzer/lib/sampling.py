"""Deterministic file and identifier selection for large codebase sampling.

Uses SHA-256 hashing to ensure identical selection across runs.
"""

import hashlib


def sha256_seed(file_path: str) -> int:
    """Compute a deterministic integer seed from a file path.

    Args:
        file_path: The file path string to hash.

    Returns:
        Integer derived from first 8 hex chars of SHA-256 digest.
    """
    return int(hashlib.sha256(file_path.encode()).hexdigest()[:8], 16)


def select_files(
    paths: list[str],
    sample_pct: int = 30,
    min_loc: int = 200,
    file_locs: dict[str, int] | None = None,
) -> list[str]:
    """Select a deterministic subset of files for analysis.

    Uses SHA-256 hash modulo 100 to select ~sample_pct% of files.
    Additionally includes all files exceeding min_loc lines.

    Args:
        paths: List of file paths to sample from.
        sample_pct: Target percentage of files to select (default 30).
        min_loc: Always include files exceeding this LOC threshold (default 200).
        file_locs: Optional dict mapping file path to LOC count.

    Returns:
        Sorted list of selected file paths.
    """
    selected = set()
    sorted_paths = sorted(paths)
    for path in sorted_paths:
        if sha256_seed(path) % 100 < sample_pct:
            selected.add(path)
    if file_locs:
        for path, loc in file_locs.items():
            if loc > min_loc:
                selected.add(path)
    return sorted(selected)


def select_identifiers_for_file(
    file_path: str,
    identifiers: list[str],
    count: int = 20,
) -> list[str]:
    """Deterministically select identifiers from a file for D4 assessment.

    Uses SHA-256 of file path as seed for consistent selection.

    Args:
        file_path: Path used as seed for deterministic selection.
        identifiers: List of identifiers to sample from.
        count: Number of identifiers to select (default 20).

    Returns:
        List of selected identifiers (up to count).
    """
    if len(identifiers) <= count:
        return list(identifiers)
    seed = sha256_seed(file_path)
    # Deterministic shuffle using hash-based sorting
    decorated = [(hashlib.sha256(f"{seed}:{ident}".encode()).hexdigest(), ident) for ident in identifiers]
    decorated.sort()
    return [ident for _, ident in decorated[:count]]

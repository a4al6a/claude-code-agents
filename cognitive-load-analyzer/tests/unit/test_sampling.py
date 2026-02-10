"""Tests for sampling.py -- deterministic file and identifier selection."""


from sampling import select_files, select_identifiers_for_file, sha256_seed


class TestSha256Seed:
    def test_deterministic(self):
        assert sha256_seed("src/main.py") == sha256_seed("src/main.py")

    def test_different_paths_different_seeds(self):
        assert sha256_seed("src/a.py") != sha256_seed("src/b.py")

    def test_returns_integer(self):
        result = sha256_seed("test.py")
        assert isinstance(result, int)

    def test_within_32bit_range(self):
        result = sha256_seed("anything.py")
        assert 0 <= result < 2**32


class TestSelectFiles:
    def test_deterministic_selection(self):
        paths = [f"src/file_{i}.py" for i in range(100)]
        run1 = select_files(paths)
        run2 = select_files(paths)
        assert run1 == run2

    def test_approximately_30_percent(self):
        paths = [f"src/file_{i}.py" for i in range(1000)]
        selected = select_files(paths)
        # Allow wide margin for hash distribution
        assert 200 < len(selected) < 400

    def test_includes_large_files(self):
        paths = ["small.py", "large.py", "medium.py"]
        file_locs = {"small.py": 50, "large.py": 500, "medium.py": 150}
        selected = select_files(paths, file_locs=file_locs)
        assert "large.py" in selected

    def test_result_is_sorted(self):
        paths = [f"z_{i}.py" for i in range(50)] + [f"a_{i}.py" for i in range(50)]
        selected = select_files(paths)
        assert selected == sorted(selected)

    def test_empty_input(self):
        assert select_files([]) == []

    def test_custom_sample_pct(self):
        paths = [f"file_{i}.py" for i in range(1000)]
        selected_50 = select_files(paths, sample_pct=50)
        selected_10 = select_files(paths, sample_pct=10)
        assert len(selected_50) > len(selected_10)

    def test_min_loc_threshold(self):
        paths = ["a.py", "b.py"]
        file_locs = {"a.py": 200, "b.py": 201}
        selected = select_files(paths, min_loc=200, file_locs=file_locs, sample_pct=0)
        # Only b.py exceeds 200 (> not >=)
        assert "b.py" in selected
        assert "a.py" not in selected


class TestSelectIdentifiersForFile:
    def test_deterministic(self):
        ids = [f"var_{i}" for i in range(100)]
        run1 = select_identifiers_for_file("test.py", ids, count=20)
        run2 = select_identifiers_for_file("test.py", ids, count=20)
        assert run1 == run2

    def test_returns_requested_count(self):
        ids = [f"var_{i}" for i in range(100)]
        result = select_identifiers_for_file("test.py", ids, count=20)
        assert len(result) == 20

    def test_fewer_than_count_returns_all(self):
        ids = ["a", "b", "c"]
        result = select_identifiers_for_file("test.py", ids, count=20)
        assert len(result) == 3

    def test_different_files_different_selection(self):
        ids = [f"var_{i}" for i in range(100)]
        sel_a = select_identifiers_for_file("a.py", ids, count=20)
        sel_b = select_identifiers_for_file("b.py", ids, count=20)
        assert sel_a != sel_b

    def test_empty_identifiers(self):
        result = select_identifiers_for_file("test.py", [], count=20)
        assert result == []

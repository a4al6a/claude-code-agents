"""Test configuration for cognitive_load_analyzer tests.

Adds the lib directory to sys.path at import time so test modules
can import core, dimensions, aggregation, sampling directly.
"""

import sys
from pathlib import Path


_lib_dir = str(Path(__file__).resolve().parents[2] / "skills" / "cognitive-load-analyzer" / "lib")
if _lib_dir not in sys.path:
    sys.path.insert(0, _lib_dir)

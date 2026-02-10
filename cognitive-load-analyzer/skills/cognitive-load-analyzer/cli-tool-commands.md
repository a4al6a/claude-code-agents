---
name: cli-tool-commands
description: Language-specific tool commands, fallback heuristics, and tool detection for CLI dimension measurement
---

# CLI Tool Commands

## Tool Detection

Run these checks at the start of analysis to determine available tools:

```bash
# Primary multi-language tool
command -v lizard && echo "AVAILABLE: lizard" || echo "MISSING: lizard"

# Python-specific
command -v radon && echo "AVAILABLE: radon" || echo "MISSING: radon"

# Duplication detection
command -v jscpd && echo "AVAILABLE: jscpd" || echo "MISSING: jscpd"

# Go-specific
command -v gocyclo && echo "AVAILABLE: gocyclo" || echo "MISSING: gocyclo"

# JavaScript/TypeScript linting
command -v npx && echo "AVAILABLE: npx (eslint)" || echo "MISSING: npx"
```

Recommend installation for missing tools:
- lizard: `pip install lizard`
- radon: `pip install radon`
- jscpd: `npm install -g jscpd`
- gocyclo: `go install github.com/fzipp/gocyclo/cmd/gocyclo@latest`

## Tool Selection by Language

| Language | D1/D2 Tool | D3 Tool | D7 Tool | Fallback |
|---|---|---|---|---|
| Python | radon, lizard | lizard, wc | jscpd, pylint | grep, awk |
| JavaScript/TypeScript | lizard, eslint | lizard, wc | jscpd | grep, awk |
| Go | gocyclo, lizard | lizard, wc | jscpd | grep, awk |
| Java | lizard | lizard, wc | jscpd | grep, awk |
| Rust | lizard | lizard, wc | jscpd | grep, awk |
| Any (fallback) | grep keywords | wc, find | sort+uniq | grep, awk, find |

Lizard is the recommended primary tool: supports 30+ languages, outputs JSON, measures both cyclomatic complexity and nesting depth.

## D1: Structural Complexity Commands

**Python (radon)**:
```bash
radon cc -s -a -j <directory>
# JSON output with per-function complexity and summary averages
```

**Multi-language (lizard)**:
```bash
lizard --json <directory>
# JSON output: NLOC, CCN (cyclomatic), token count, params, nesting per function
```

**Go (gocyclo)**:
```bash
gocyclo -avg .
# Output: complexity package function file:line
gocyclo -top 10 .
gocyclo -over 15 .
```

**JavaScript/TypeScript (eslint)**:
```bash
npx eslint --rule '{"complexity": ["warn", 0]}' --format json src/
# Reports cyclomatic complexity per function in JSON
```

**Fallback (any language)**:
```bash
# Count decision keywords per file as proxy for complexity
grep -rn "if \|elif \|else \|for \|while \|switch \|case \|catch \|&&\|||" --include="*.{py,js,ts,go,java,rs}" <directory> | wc -l
# Divide by function count for approximate per-function complexity
```

## D2: Nesting Depth Commands

**Lizard (primary)**:
```bash
lizard --json <directory>
# JSON output includes max nesting depth per function
```

**Fallback (indentation-based)**:
```bash
# Measure max indentation depth per file (assuming 4-space indent)
awk '{match($0, /^[[:space:]]*/); depth=RLENGTH/4; if(depth>max) max=depth} END{print max}' <file>

# For tab-indented files
awk '{match($0, /^\t*/); depth=RLENGTH; if(depth>max) max=depth} END{print max}' <file>
```

## D3: Volume and Size Commands

**Function and file length**:
```bash
# File lengths (all source files)
find <directory> -name "*.py" -o -name "*.js" -o -name "*.ts" -o -name "*.go" -o -name "*.java" -o -name "*.rs" | xargs wc -l | sort -rn

# Function length via lizard
lizard --json <directory>
# JSON includes NLOC (net lines of code) per function
```

**Parameter count**:
```bash
# Via lizard JSON output (param count per function)
lizard --json <directory>

# Via radon (Python)
radon cc -s -j <directory>
# JSON includes parameter count
```

**Methods per class**:
```bash
# Python
grep -c "def " <file>

# JavaScript/TypeScript (approximate)
grep -cE '^\s*(public|private|protected)?\s*(static\s+)?(async\s+)?[a-z]\w*\s*\(' <file>

# Go (methods per receiver type)
grep -E 'func\s+\(\w+\s+\*?\w+\)' *.go | awk -F'[( )]' '{print $3}' | sort | uniq -c | sort -rn
```

## D4: Naming Quality Commands

**Extract identifiers**:
```bash
# Python AST-based extraction
python3 -c "
import ast, sys
tree = ast.parse(open(sys.argv[1]).read())
names = [node.id for node in ast.walk(tree) if isinstance(node, ast.Name)]
print('\n'.join(names))
" <file>

# General: extract identifiers via grep
grep -oE '[a-zA-Z_][a-zA-Z0-9_]{2,}' <file> | sort -u
```

**Identifier length distribution**:
```bash
grep -oE '[a-zA-Z_][a-zA-Z0-9_]*' <file> | awk '{print length, $0}' | sort -rn
```

**Naming convention mixing detection**:
```bash
# camelCase count
grep -oE '[a-z]+[A-Z][a-zA-Z]*' <file> | wc -l

# snake_case count
grep -oE '[a-z]+_[a-z]+' <file> | wc -l
```

**Single-character variables (excluding idiomatic)**:
```bash
grep -oE '\b[a-z]\b' <file> | grep -vE '^[ijkxyn_]$' | wc -l
```

## D5: Coupling Commands

**Import counts**:
```bash
# Python
grep -c "^import \|^from " <file>

# JavaScript/TypeScript
grep -c "^import \|require(" <file>

# Go
grep -c "\"" <file_imports_section>

# Aggregate: mean imports per file
find <directory> -name "*.py" -exec grep -c "^import \|^from " {} + | awk -F: '{sum+=$2; n++} END{print sum/n}'
```

**Cross-module dependency count**:
```bash
# Unique imported modules (Python)
grep -rh "^import \|^from " --include="*.py" <directory> | awk '{print $2}' | sort -u | wc -l

# Unique imported modules (JS/TS)
grep -rhE "^import .* from ['\"]" --include="*.ts" --include="*.js" <directory> | grep -oE "from ['\"][^'\"]+['\"]" | sort -u | wc -l
```

## D6: Cohesion Commands

**Python class analysis (LCOM approximation)**:
```bash
python3 -c "
import ast, sys
tree = ast.parse(open(sys.argv[1]).read())
for node in ast.walk(tree):
    if isinstance(node, ast.ClassDef):
        methods = [n for n in node.body if isinstance(n, (ast.FunctionDef, ast.AsyncFunctionDef))]
        # Count self.attr accesses per method
        fields = set()
        method_fields = {}
        for m in methods:
            mf = set()
            for child in ast.walk(m):
                if isinstance(child, ast.Attribute) and isinstance(getattr(child, 'value', None), ast.Name) and child.value.id == 'self':
                    mf.add(child.attr)
                    fields.add(child.attr)
            method_fields[m.name] = mf
        if fields and methods:
            # LCOM = 1 - (avg fields per method / total fields)
            avg_access = sum(len(mf) for mf in method_fields.values()) / len(methods)
            lcom = 1.0 - (avg_access / max(len(fields), 1))
            print(f'{node.name}: LCOM={lcom:.2f} methods={len(methods)} fields={len(fields)}')
" <file>
```

**Simpler heuristic (any language)**:
```bash
# Count methods and properties per class as proxy
# High method count with few shared fields suggests low cohesion
```

## D7: Duplication Commands

**jscpd (primary, multi-language)**:
```bash
jscpd --reporters json --output /tmp/jscpd-report <directory>
# JSON report includes: percentage of duplicated lines, clone list
```

**Fallback (line-level)**:
```bash
# Find duplicated non-blank lines
find <directory> -name "*.py" -exec cat {} + | grep -v '^\s*$' | sort | uniq -d | wc -l
# Compare against total lines for approximate duplication percentage
```

## D8: Navigability Commands

**Directory depth**:
```bash
find <directory> -type d | awk -F/ '{print NF}' | sort -rn | head -1
```

**Files per directory**:
```bash
find <directory> -type f \( -name "*.py" -o -name "*.js" -o -name "*.ts" -o -name "*.go" -o -name "*.java" -o -name "*.rs" \) | xargs -I{} dirname {} | sort | uniq -c | sort -rn
```

**File size coefficient of variation**:
```bash
find <directory> -type f \( -name "*.py" -o -name "*.js" -o -name "*.ts" -o -name "*.go" -o -name "*.java" -o -name "*.rs" \) -exec wc -l {} + | awk '{print $1}' | head -n -1 | awk '{sum+=$1; sumsq+=$1*$1; n++} END{mean=sum/n; var=sumsq/n-mean*mean; if(mean>0) print sqrt(var)/mean; else print 0}'
```

## General Fallback Strategy

When a dimension's primary tool is unavailable, apply these ordered fallbacks:

1. **Try lizard first** -- it covers D1, D2, D3 for 30+ languages
2. **Try language-specific tool** -- radon (Python), gocyclo (Go), eslint (JS/TS)
3. **Use grep/awk/find heuristics** -- always available, less accurate
4. **Record fallback usage** -- every dimension must note which measurement method was used

Accuracy degradation estimate when using heuristic fallbacks:
- D1/D2: ~20% less accurate (keyword counting misses nuance of control flow graphs)
- D3: ~5% less accurate (LOC counting is straightforward)
- D4: ~25% less accurate without LLM assessment
- D5: ~10% less accurate (import counting is reliable)
- D6: ~30% less accurate (LCOM requires AST analysis)
- D7: ~40% less accurate (line-level dedup misses structural clones)
- D8: ~5% less accurate (find/awk is already the primary method)

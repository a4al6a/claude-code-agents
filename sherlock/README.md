# sherlock

A CI-native code reviewer. Reviews only the diff of a pull/merge request, emits a structured JSON verdict, and is designed to be dropped into a pipeline as (a) a pass/fail blocker via exit code and (b) a source of inline review comments on the PR/MR.

## What it does

- Reads the unified diff between `BASE_SHA` and `HEAD_SHA` plus minimal surrounding context.
- Applies a configurable severity model (`blocker` / `major` / `minor` / `nit`) across correctness, security, concurrency, performance, testing, design, readability, and docs.
- Writes exactly one artifact: `code-review-verdict.json`.
- The CI wrapper turns that verdict into a review decision and an inline comment stream.

## Files in this directory

- `sherlock.md` — agent definition (installs into `~/.claude/agents/`).
- `examples/run-review.sh` — platform-agnostic CI entry point. Runs Claude Code headless, reads the verdict, dispatches to the platform poster, exits non-zero on fail (unless `ADVISORY=1`).
- `examples/post-github.sh` — posts a single GitHub PR review with inline comments via the Reviews API. Uses `REQUEST_CHANGES` on fail so branch protection can gate on it.
- `examples/post-gitlab.sh` — posts a summary note plus one position-anchored MR discussion per finding.
- `examples/github-actions.yml` — drop-in GitHub Actions workflow.
- `examples/gitlab-ci.yml` — drop-in GitLab CI job.

## Installation

```bash
mkdir -p ~/.claude/agents
cp sherlock/sherlock.md ~/.claude/agents/
```

Then invoke with `claude --agent sherlock`.

## Rollout

Start with `ADVISORY=1` for a couple of weeks. The agent posts comments but never fails the pipeline. Use that window to tune `.sherlock.yml` (threshold, protected paths, muted categories) against the real noise/signal on your PRs. Flip `ADVISORY=0` once the signal is trustworthy.

---

## `.sherlock.yml` configuration reference

Place `.sherlock.yml` at the repo root. It is optional — the agent ships with sensible defaults and will fall back gracefully if the file is missing, malformed, or contains unknown keys (those conditions are recorded in the `notes` field of the verdict, never fail the build directly).

### Schema

| Key | Type | Default | Meaning |
| --- | --- | --- | --- |
| `major_threshold` | int ≥ 0 | `3` | Pipeline fails if the count of `major` findings **exceeds** this number. Blockers always fail regardless of threshold. Set to `0` to fail on any major. |
| `protected_paths` | list of glob strings | `[]` | Paths deserving extra scrutiny. Any change touching these requires an explicit human override label (`sherlock-approved`) on the PR/MR to pass. Globs follow `.gitignore` syntax. |
| `override_label` | string | `sherlock-approved` | Label/tag on the PR/MR that clears the `protected_paths` gate. |
| `mute_categories` | list of category strings | `[]` | Categories the agent must not raise findings in. Valid values: `correctness`, `security`, `performance`, `concurrency`, `testing`, `design`, `readability`, `docs`. Use sparingly — muting `security` is almost always wrong. |
| `mute_paths` | list of glob strings | `[]` | Paths the agent skips entirely (e.g. generated code, vendored dirs). Still counted in `stats.files_reviewed` as `skipped`. |
| `language_hints` | map | `{}` | Per-language tuning. See below. |
| `rules` | map | `{}` | Per-rule overrides. See below. |
| `max_findings` | int ≥ 1 | `50` | Cap on findings to post. If exceeded, the highest-severity are kept and the rest collapsed into `notes`. Protects against review spam on huge PRs. |
| `context_lines` | int ≥ 0 | `30` | Lines of surrounding context the agent reads around each hunk. Raise for wide-reaching changes, lower for speed. |

### `language_hints`

Keyed by language. Known keys per language:

```yaml
language_hints:
  python:
    max_cyclomatic: 10         # flag functions over this complexity
    max_function_lines: 60
    max_file_lines: 500
  typescript:
    max_cyclomatic: 12
    max_function_lines: 80
    disallow_any: true          # treat `any` as a major finding on new code
  go:
    max_cyclomatic: 15
    require_error_wrap: true    # flag unwrapped errors returned from new funcs
```

Missing languages simply use defaults. Unknown keys under a known language are ignored with a note.

### `rules`

Per-rule overrides. Rule IDs are stable strings (e.g. `CR-SEC-001`, `CR-PERF-004`) emitted in each finding. Three operations:

```yaml
rules:
  CR-READ-012:               # disable a specific rule
    enabled: false
  CR-SEC-001:                # force severity for a rule
    severity: blocker
  CR-TEST-007:
    paths:                   # scope rule to specific paths
      - "src/api/**"
```

`severity` values: `blocker`, `major`, `minor`, `nit`. Setting `enabled: false` on a blocker rule is logged in the verdict `notes` for auditability.

### Full example

```yaml
# .sherlock.yml — at repo root
major_threshold: 2

protected_paths:
  - "infra/**"
  - "src/auth/**"
  - "migrations/**"
  - ".github/workflows/**"

override_label: sherlock-approved

mute_categories:
  - readability   # legacy codebase; don't nitpick style on the diff
mute_paths:
  - "src/generated/**"
  - "vendor/**"
  - "**/*.pb.go"

max_findings: 40
context_lines: 40

language_hints:
  typescript:
    max_cyclomatic: 12
    disallow_any: true
  python:
    max_cyclomatic: 10
    max_function_lines: 60

rules:
  CR-READ-012:                # we intentionally allow long import lists
    enabled: false
  CR-SEC-001:                 # treat any SQL string concat as a blocker
    severity: blocker
  CR-TEST-007:                # require tests only on API layer
    paths:
      - "src/api/**"
      - "src/handlers/**"
```

### Behaviour when `.sherlock.yml` is absent

Agent runs with all defaults above, no protected paths, no muted categories, and all built-in rules at their default severity. This is the right starting point for most repos; only add configuration in response to real false-positive or false-negative patterns observed during the advisory window.

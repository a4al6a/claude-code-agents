#!/usr/bin/env bash
# sherlock CI wrapper.
# Invokes Claude Code headless, captures the JSON verdict, posts inline
# comments on the PR/MR, and sets the process exit code for the pipeline gate.
#
# Required env:
#   ANTHROPIC_API_KEY        - for claude CLI
#   BASE_SHA, HEAD_SHA       - commit range to review
#   PR_PLATFORM              - "github" | "gitlab"
#   GITHUB_TOKEN             - if PR_PLATFORM=github
#   GITHUB_REPOSITORY, GITHUB_PR_NUMBER - if PR_PLATFORM=github
#   GITLAB_TOKEN             - if PR_PLATFORM=gitlab
#   CI_PROJECT_ID, CI_MERGE_REQUEST_IID, CI_API_V4_URL - if PR_PLATFORM=gitlab
#
# Optional env:
#   ADVISORY=1               - never fail the pipeline, post comments only
#   VERDICT_PATH             - default ./code-review-verdict.json

set -euo pipefail

VERDICT_PATH="${VERDICT_PATH:-./code-review-verdict.json}"
DIFF_FILE="$(mktemp -t sherlock-diff.XXXXXX)"
CHANGED_FILES="$(mktemp -t sherlock-changed.XXXXXX)"
trap 'rm -f "$DIFF_FILE" "$CHANGED_FILES"' EXIT

git diff --unified=3 "$BASE_SHA..$HEAD_SHA" > "$DIFF_FILE"
git diff --name-only "$BASE_SHA..$HEAD_SHA" > "$CHANGED_FILES"

if ! [ -s "$CHANGED_FILES" ]; then
  echo "No changes between $BASE_SHA..$HEAD_SHA — skipping review."
  exit 0
fi

# Invoke Claude Code in headless mode. We pass the agent's name via the system
# prompt and instruct it to review the diff using the bound env vars. The agent
# is expected to write code-review-verdict.json to the working directory.
claude \
  --print \
  --output-format json \
  --permission-mode acceptEdits \
  --allowed-tools "Read,Glob,Grep,Bash,Write" \
  --agent sherlock \
  <<EOF
BASE_SHA=$BASE_SHA
HEAD_SHA=$HEAD_SHA
DIFF_FILE=$DIFF_FILE
CHANGED_FILES=$CHANGED_FILES
CONFIG=.sherlock.yml

Review the diff per the sherlock contract. Write the verdict to
$VERDICT_PATH. Output nothing else.
EOF

if ! [ -s "$VERDICT_PATH" ]; then
  echo "ERROR: reviewer did not produce $VERDICT_PATH" >&2
  exit 2
fi

STATUS="$(jq -r '.status' "$VERDICT_PATH")"
SUMMARY="$(jq -r '.summary' "$VERDICT_PATH")"
echo "Reviewer verdict: $STATUS"
echo "Summary: $SUMMARY"

case "$PR_PLATFORM" in
  github) "$(dirname "$0")/post-github.sh" "$VERDICT_PATH" ;;
  gitlab) "$(dirname "$0")/post-gitlab.sh" "$VERDICT_PATH" ;;
  *) echo "Unknown PR_PLATFORM=$PR_PLATFORM — skipping comment posting." ;;
esac

if [ "${ADVISORY:-0}" = "1" ]; then
  echo "ADVISORY mode — not gating on verdict."
  exit 0
fi

[ "$STATUS" = "pass" ] || exit 1

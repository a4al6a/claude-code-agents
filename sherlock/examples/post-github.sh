#!/usr/bin/env bash
# Post sherlock findings as inline PR review comments on GitHub.
# Uses a single pending review so comments post atomically with an approval/
# request-changes verdict.
set -euo pipefail

VERDICT="$1"
: "${GITHUB_TOKEN:?}" "${GITHUB_REPOSITORY:?}" "${GITHUB_PR_NUMBER:?}" "${HEAD_SHA:?}"

STATUS="$(jq -r '.status' "$VERDICT")"
SUMMARY="$(jq -r '.summary' "$VERDICT")"
EVENT="COMMENT"
[ "$STATUS" = "fail" ] && EVENT="REQUEST_CHANGES"

BODY="$(jq -n --arg body "**sherlock**: $SUMMARY" \
                --arg event "$EVENT" \
                --arg sha "$HEAD_SHA" \
                --argjson comments "$(jq '[.findings[] | {
                   path: .file,
                   line: .line,
                   side: "RIGHT",
                   body: ("**[" + .severity + "/" + .category + "] " + .title + "**\n\n" + .message +
                          (if .suggestion then "\n\n```suggestion\n" + .suggestion + "\n```" else "" end) +
                          "\n\n_rule: " + .rule_id + "_")
                }]' "$VERDICT")" \
                '{commit_id:$sha, body:$body, event:$event, comments:$comments}')"

curl -sSf \
  -X POST \
  -H "Authorization: Bearer $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  "https://api.github.com/repos/$GITHUB_REPOSITORY/pulls/$GITHUB_PR_NUMBER/reviews" \
  -d "$BODY" > /dev/null

echo "Posted GitHub review ($EVENT) with $(jq '.findings | length' "$VERDICT") comments."

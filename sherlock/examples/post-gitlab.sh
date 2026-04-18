#!/usr/bin/env bash
# Post sherlock findings as inline MR discussions on GitLab.
# Each finding becomes one position-anchored discussion thread on HEAD_SHA.
set -euo pipefail

VERDICT="$1"
: "${GITLAB_TOKEN:?}" "${CI_API_V4_URL:?}" "${CI_PROJECT_ID:?}" "${CI_MERGE_REQUEST_IID:?}"
: "${BASE_SHA:?}" "${HEAD_SHA:?}"

# Post a summary note first.
SUMMARY="$(jq -r '.summary' "$VERDICT")"
STATUS="$(jq -r '.status' "$VERDICT")"
curl -sSf \
  -H "PRIVATE-TOKEN: $GITLAB_TOKEN" \
  --data-urlencode "body=**sherlock [$STATUS]**: $SUMMARY" \
  "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests/$CI_MERGE_REQUEST_IID/notes" \
  > /dev/null

# Post one position-anchored discussion per finding.
jq -c '.findings[]' "$VERDICT" | while IFS= read -r f; do
  FILE=$(jq -r '.file' <<<"$f")
  LINE=$(jq -r '.line' <<<"$f")
  SEV=$(jq -r '.severity' <<<"$f")
  CAT=$(jq -r '.category' <<<"$f")
  TITLE=$(jq -r '.title' <<<"$f")
  MSG=$(jq -r '.message' <<<"$f")
  SUG=$(jq -r '.suggestion // ""' <<<"$f")
  RULE=$(jq -r '.rule_id' <<<"$f")

  BODY="**[$SEV/$CAT] $TITLE**

$MSG"
  [ -n "$SUG" ] && BODY="$BODY

\`\`\`suggestion
$SUG
\`\`\`"
  BODY="$BODY

_rule: $RULE_"

  curl -sSf \
    -H "PRIVATE-TOKEN: $GITLAB_TOKEN" \
    --data-urlencode "body=$BODY" \
    --data-urlencode "position[position_type]=text" \
    --data-urlencode "position[base_sha]=$BASE_SHA" \
    --data-urlencode "position[head_sha]=$HEAD_SHA" \
    --data-urlencode "position[start_sha]=$BASE_SHA" \
    --data-urlencode "position[new_path]=$FILE" \
    --data-urlencode "position[old_path]=$FILE" \
    --data-urlencode "position[new_line]=$LINE" \
    "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests/$CI_MERGE_REQUEST_IID/discussions" \
    > /dev/null
done

echo "Posted $(jq '.findings | length' "$VERDICT") GitLab discussions."

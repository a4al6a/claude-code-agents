#!/usr/bin/env bash

# Claude Code Agents Uninstaller
# Removes agents installed by install.sh from ~/.claude/agents/ and ~/.claude/skills/
# Compatible with: macOS, Linux, Windows (Git Bash, WSL)

set -e

# Colors for output (disabled on Windows CMD)
if [[ -t 1 ]] && [[ -z "$NO_COLOR" ]]; then
    RED='\033[0;31m'
    GREEN='\033[0;32m'
    YELLOW='\033[1;33m'
    BLUE='\033[0;34m'
    NC='\033[0m'
else
    RED=''
    GREEN=''
    YELLOW=''
    BLUE=''
    NC=''
fi

# Target directories for Claude Code agents and skills
CLAUDE_AGENTS_DIR="$HOME/.claude/agents"
CLAUDE_SKILLS_DIR="$HOME/.claude/skills"

# Agent definitions: "folder:main_file:extra_files(comma-separated)"
AGENT_DEFS=(
    "alf-problem-analyzer:alf-problem-analyzer.md:"
    "alf-user-story-writer:alf-user-story-writer.md:"
    "alf-atdd-developer:alf-atdd-developer.md:"
    "alf-clean-coder:alf-clean-coder.md:"
    "alf-code-smell-detector:alf-code-smell-detector.md:"
    "alf-refactoring-advisor:alf-refactoring-advisor.md:"
    "alf-legacy-code-analyzer:alf-legacy-code-analyzer.md:"
    "alf-ddd-assessor:alf-ddd-assessor.md:ddd-expert-knowledge-base.md"
    "alf-cognitive-load-analyzer:alf-cognitive-load-analyzer.md:"
    "alf-test-design-reviewer:alf-test-design-reviewer.md:"
    "alf-system-explorer:alf-system-explorer.md:"
    "alf-system-auditor:alf-system-auditor.md:"
    "alf-accessibility-assessor:alf-accessibility-assessor.md:"
    "alf-security-assessor:alf-security-assessor.md:"
    "alf-error-handling-reviewer:alf-error-handling-reviewer.md:"
    "alf-api-design-reviewer:alf-api-design-reviewer.md:"
    "alf-dependency-auditor:alf-dependency-auditor.md:"
    "alf-concurrency-analyzer:alf-concurrency-analyzer.md:"
    "alf-documentation-reviewer:alf-documentation-reviewer.md:"
    "alf-dead-code-detector:alf-dead-code-detector.md:"
    "alf-devops-evaluator:alf-devops-evaluator.md:"
    "alf-ownership-analyzer:alf-ownership-analyzer.md:"
    "alf-consistency-checker:alf-consistency-checker.md:"
    "alf-data-layer-reviewer:alf-data-layer-reviewer.md:"
    "alf-observability-assessor:alf-observability-assessor.md:"
)

# Skill definitions: "agent-folder:skill-folder-name"
SKILL_DEFS=(
    "alf-cognitive-load-analyzer:alf-cognitive-load-analyzer"
    "alf-test-design-reviewer:alf-test-design-reviewer"
    "alf-system-explorer:alf-system-explorer"
    "alf-system-auditor:alf-system-auditor"
    "alf-accessibility-assessor:alf-accessibility-assessor"
)

# Parse agent definition fields
get_agent_folder() { echo "$1" | cut -d: -f1; }
get_agent_file()   { echo "$1" | cut -d: -f2; }
get_agent_extras() { echo "$1" | cut -d: -f3; }

# Find agent definition by folder name
find_agent_def() {
    local search="$1"
    for def in "${AGENT_DEFS[@]}"; do
        local folder=$(get_agent_folder "$def")
        if [[ "$folder" == "$search" ]]; then
            echo "$def"
            return 0
        fi
    done
    return 1
}

# Find skill definition by agent folder name
find_skill_def() {
    local search="$1"
    for def in "${SKILL_DEFS[@]}"; do
        local agent_folder="${def%%:*}"
        if [[ "$agent_folder" == "$search" ]]; then
            echo "$def"
            return 0
        fi
    done
    return 1
}

print_header() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║     Claude Code Agents Uninstaller             ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}"
    echo ""
}

print_usage() {
    echo "Usage: $0 [options] [agent-name]"
    echo ""
    echo "Options:"
    echo "  -f, --force       Remove without prompting for confirmation"
    echo "  -n, --dry-run     Show what would be removed without removing"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                              # Remove all agents (interactive)"
    echo "  $0 -f                           # Remove all agents (no prompt)"
    echo "  $0 alf-code-smell-detector      # Remove a specific agent"
    echo "  $0 -n                           # Preview what would be removed"
    echo ""
}

uninstall_agent() {
    local agent="$1"
    local dry_run="$2"

    local def=$(find_agent_def "$agent")
    if [[ -z "$def" ]]; then
        echo -e "${RED}Error: Unknown agent '$agent'${NC}"
        return 1
    fi

    local folder=$(get_agent_folder "$def")
    local file=$(get_agent_file "$def")
    local extras=$(get_agent_extras "$def")
    local target_path="$CLAUDE_AGENTS_DIR/$file"

    if [[ -f "$target_path" ]]; then
        if [[ "$dry_run" == "true" ]]; then
            echo -e "  ${YELLOW}~${NC} Would remove $target_path"
        else
            rm "$target_path"
            echo -e "  ${GREEN}✓${NC} Removed $folder"
        fi

        # Remove extra files if any
        if [[ -n "$extras" ]]; then
            IFS=',' read -ra EXTRA_FILES <<< "$extras"
            for extra_file in "${EXTRA_FILES[@]}"; do
                local extra_target="$CLAUDE_AGENTS_DIR/$extra_file"
                if [[ -f "$extra_target" ]]; then
                    if [[ "$dry_run" == "true" ]]; then
                        echo -e "    ${YELLOW}~${NC} Would remove $extra_target"
                    else
                        rm "$extra_target"
                        echo -e "    ${GREEN}-${NC} Removed $extra_file"
                    fi
                fi
            done
        fi

        # Remove associated skills if any
        local skill_def=$(find_skill_def "$folder" 2>/dev/null || true)
        if [[ -n "$skill_def" ]]; then
            local skill_name="${skill_def#*:}"
            local skill_target="$CLAUDE_SKILLS_DIR/$skill_name"
            if [[ -d "$skill_target" ]]; then
                if [[ "$dry_run" == "true" ]]; then
                    echo -e "    ${YELLOW}~${NC} Would remove $skill_target/"
                else
                    rm -rf "$skill_target"
                    echo -e "    ${GREEN}-${NC} Removed skill: $skill_name"
                fi
            fi
        fi
    else
        echo -e "  ${YELLOW}!${NC} $folder was not installed"
    fi
}

uninstall_all() {
    local dry_run="$1"

    if [[ "$dry_run" == "true" ]]; then
        echo -e "${YELLOW}Dry run — showing what would be removed from:${NC}"
    else
        echo -e "${YELLOW}Removing all agents from: $CLAUDE_AGENTS_DIR${NC}"
    fi
    echo ""

    local removed=0
    local skipped=0
    for def in "${AGENT_DEFS[@]}"; do
        local folder=$(get_agent_folder "$def")
        local file=$(get_agent_file "$def")
        if [[ -f "$CLAUDE_AGENTS_DIR/$file" ]]; then
            uninstall_agent "$folder" "$dry_run"
            ((removed++)) || true
        else
            ((skipped++)) || true
        fi
    done

    echo ""
    if [[ "$dry_run" == "true" ]]; then
        echo -e "${BLUE}Would remove: $removed agents ($skipped not installed)${NC}"
    else
        echo -e "${GREEN}Uninstallation complete!${NC} Removed $removed agents ($skipped were not installed)."
        echo ""
        echo -e "Restart Claude Code or start a new session for changes to take effect."
    fi
    echo ""
}

# Main script
main() {
    local force="false"
    local dry_run="false"
    local specific_agent=""

    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case "$1" in
            -f|--force)
                force="true"
                shift
                ;;
            -n|--dry-run)
                dry_run="true"
                shift
                ;;
            -h|--help)
                print_header
                print_usage
                exit 0
                ;;
            *)
                if [[ -z "$specific_agent" ]]; then
                    specific_agent="$1"
                fi
                shift
                ;;
        esac
    done

    print_header

    if [[ -n "$specific_agent" ]]; then
        uninstall_agent "$specific_agent" "$dry_run"
    else
        if [[ "$force" != "true" && "$dry_run" != "true" ]]; then
            echo -ne "${YELLOW}Remove ALL installed agents? [y/N] ${NC}"
            read -r response
            if [[ ! "$response" =~ ^[Yy]$ ]]; then
                echo "Cancelled."
                exit 0
            fi
        fi
        uninstall_all "$dry_run"
    fi
}

main "$@"

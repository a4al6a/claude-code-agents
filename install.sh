#!/usr/bin/env bash

# Claude Code Agents Installer
# Installs agents from this repository to ~/.claude/agents/
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
# Can be overridden with --prefix to install project-local
CLAUDE_PREFIX="$HOME"
CLAUDE_AGENTS_DIR=""
CLAUDE_SKILLS_DIR=""

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Agent definitions: "folder:main_file:extra_files(comma-separated)"
AGENT_DEFS=(
    "problem-analyst:problem-analyst.md:"
    "user-story-writer:user-story-writer.md:"
    "atdd-developer:atdd-developer.md:"
    "clean-coder:clean-coder-agent.md:"
    "code-smell-detector:code-smell-detector.md:"
    "refactoring-expert:refactoring-expert.md:"
    "legacy-code-expert:legacy-code-expert.md:"
    "domain-driven-design:ddd-architect-agent.md:ddd-expert-knowledge-base.md"
    "cognitive-load-analyzer:cognitive-load-analyzer.md:"
    "test-design-reviewer:test-design-reviewer.md:"
    "system-walkthrough:system-walkthrough.md:"
    "software-system-auditor:software-system-auditor.md:"
    "accessibility-assessor:accessibility-assessor.md:"
)

# Skill definitions: "agent-folder:skill-folder-name"
# Maps agents to their skill directories under <agent-folder>/skills/<skill-folder>/
SKILL_DEFS=(
    "cognitive-load-analyzer:cognitive-load-analyzer"
    "test-design-reviewer:test-design-reviewer"
    "system-walkthrough:system-walkthrough"
    "software-system-auditor:software-system-auditor"
    "accessibility-assessor:accessibility-assessor"
)

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

# Parse agent definition
get_agent_folder() {
    echo "$1" | cut -d: -f1
}

get_agent_file() {
    echo "$1" | cut -d: -f2
}

get_agent_extras() {
    echo "$1" | cut -d: -f3
}

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

print_header() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║     Claude Code Agents Installer               ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}"
    echo ""
}

print_usage() {
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  list              List all available agents"
    echo "  install           Install all agents (default)"
    echo "  install <name>    Install a specific agent"
    echo "  uninstall         Remove all installed agents"
    echo "  uninstall <name>  Remove a specific agent"
    echo "  status            Show installation status"
    echo ""
    echo "Options:"
    echo "  --prefix <path>   Install to <path>/.claude/ instead of ~/.claude/"
    echo "  -f, --force       Overwrite existing agents without prompting"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                     # Install all agents to ~/.claude/"
    echo "  $0 install -f                          # Install all agents (overwrite existing)"
    echo "  $0 install code-smell-detector         # Install specific agent"
    echo "  $0 --prefix ~/repos/my-app             # Install to a project's .claude/ directory"
    echo "  $0 --prefix ~/repos/my-app uninstall   # Uninstall from that project"
    echo "  $0 list                                # List available agents"
    echo "  $0 status                              # Check what's installed"
    echo ""
}

list_agents() {
    echo -e "${YELLOW}Available agents:${NC}"
    echo ""
    for def in "${AGENT_DEFS[@]}"; do
        local folder=$(get_agent_folder "$def")
        local file=$(get_agent_file "$def")
        local extras=$(get_agent_extras "$def")
        local source_path="$SCRIPT_DIR/$folder/$file"

        if [[ -f "$source_path" ]]; then
            echo -e "  ${GREEN}●${NC} $folder"
            if [[ -n "$extras" ]]; then
                echo -e "    ${BLUE}(includes: $extras)${NC}"
            fi
            local skill_def=$(find_skill_def "$folder" 2>/dev/null || true)
            if [[ -n "$skill_def" ]]; then
                local skill_name="${skill_def#*:}"
                echo -e "    ${BLUE}(skill: $skill_name)${NC}"
            fi
        else
            echo -e "  ${RED}○${NC} $folder (file not found)"
        fi
    done
    echo ""
}

check_status() {
    echo -e "${YELLOW}Installation status:${NC}"
    echo ""
    local installed=0
    local not_installed=0

    for def in "${AGENT_DEFS[@]}"; do
        local folder=$(get_agent_folder "$def")
        local file=$(get_agent_file "$def")
        local target_path="$CLAUDE_AGENTS_DIR/$file"

        if [[ -f "$target_path" ]]; then
            local skill_info=""
            local skill_def=$(find_skill_def "$folder" 2>/dev/null || true)
            if [[ -n "$skill_def" ]]; then
                local skill_name="${skill_def#*:}"
                if [[ -d "$CLAUDE_SKILLS_DIR/$skill_name" ]]; then
                    skill_info=" + skill"
                else
                    skill_info=" (skill missing)"
                fi
            fi
            echo -e "  ${GREEN}✓${NC} $folder - installed${skill_info}"
            ((installed++)) || true
        else
            echo -e "  ${RED}✗${NC} $folder - not installed"
            ((not_installed++)) || true
        fi
    done
    echo ""
    echo -e "Installed: ${GREEN}$installed${NC} | Not installed: ${RED}$not_installed${NC}"
    echo -e "Agents directory: ${BLUE}$CLAUDE_AGENTS_DIR${NC}"
    echo -e "Skills directory: ${BLUE}$CLAUDE_SKILLS_DIR${NC}"
    echo ""
}

ensure_target_dir() {
    if [[ ! -d "$CLAUDE_AGENTS_DIR" ]]; then
        echo -e "${YELLOW}Creating agents directory: $CLAUDE_AGENTS_DIR${NC}"
        mkdir -p "$CLAUDE_AGENTS_DIR"
    fi
    if [[ ! -d "$CLAUDE_SKILLS_DIR" ]]; then
        echo -e "${YELLOW}Creating skills directory: $CLAUDE_SKILLS_DIR${NC}"
        mkdir -p "$CLAUDE_SKILLS_DIR"
    fi
}

install_agent() {
    local agent="$1"
    local force="$2"

    local def=$(find_agent_def "$agent")
    if [[ -z "$def" ]]; then
        echo -e "${RED}Error: Unknown agent '$agent'${NC}"
        echo "Use '$0 list' to see available agents."
        return 1
    fi

    local folder=$(get_agent_folder "$def")
    local file=$(get_agent_file "$def")
    local extras=$(get_agent_extras "$def")
    local source_path="$SCRIPT_DIR/$folder/$file"
    local target_path="$CLAUDE_AGENTS_DIR/$file"

    if [[ ! -f "$source_path" ]]; then
        echo -e "${RED}Error: Agent file not found: $source_path${NC}"
        return 1
    fi

    # Check if already exists
    if [[ -f "$target_path" && "$force" != "true" ]]; then
        echo -ne "  ${YELLOW}$folder${NC} already exists. Overwrite? [y/N] "
        read -r response
        if [[ ! "$response" =~ ^[Yy]$ ]]; then
            echo -e "  ${YELLOW}Skipped${NC} $folder"
            return 0
        fi
    fi

    # Copy main agent file
    cp "$source_path" "$target_path"
    echo -e "  ${GREEN}✓${NC} Installed $folder"

    # Copy extra files if any
    if [[ -n "$extras" ]]; then
        IFS=',' read -ra EXTRA_FILES <<< "$extras"
        for extra_file in "${EXTRA_FILES[@]}"; do
            local extra_source="$SCRIPT_DIR/$folder/$extra_file"
            local extra_target="$CLAUDE_AGENTS_DIR/$extra_file"
            if [[ -f "$extra_source" ]]; then
                cp "$extra_source" "$extra_target"
                echo -e "    ${GREEN}+${NC} Installed $extra_file"
            fi
        done
    fi

    # Install associated skills if any
    local skill_def=$(find_skill_def "$folder" 2>/dev/null || true)
    if [[ -n "$skill_def" ]]; then
        local skill_name="${skill_def#*:}"
        local skill_source="$SCRIPT_DIR/$folder/skills/$skill_name"
        local skill_target="$CLAUDE_SKILLS_DIR/$skill_name"
        if [[ -d "$skill_source" ]]; then
            rm -rf "$skill_target"
            cp -r "$skill_source" "$skill_target"
            echo -e "    ${GREEN}+${NC} Installed skill: $skill_name"
        fi
    fi
}

install_all() {
    local force="$1"
    echo -e "${YELLOW}Installing agents to: $CLAUDE_AGENTS_DIR${NC}"
    echo ""

    ensure_target_dir

    local count=0
    for def in "${AGENT_DEFS[@]}"; do
        local folder=$(get_agent_folder "$def")
        install_agent "$folder" "$force"
        ((count++)) || true
    done

    echo ""
    echo -e "${GREEN}Installation complete!${NC} Installed $count agents."
    echo ""
    echo -e "Restart Claude Code or start a new session to use the agents."
    echo ""
}

uninstall_agent() {
    local agent="$1"

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
        rm "$target_path"
        echo -e "  ${GREEN}✓${NC} Removed $folder"

        # Remove extra files if any
        if [[ -n "$extras" ]]; then
            IFS=',' read -ra EXTRA_FILES <<< "$extras"
            for extra_file in "${EXTRA_FILES[@]}"; do
                local extra_target="$CLAUDE_AGENTS_DIR/$extra_file"
                if [[ -f "$extra_target" ]]; then
                    rm "$extra_target"
                    echo -e "    ${GREEN}-${NC} Removed $extra_file"
                fi
            done
        fi

        # Remove associated skills if any
        local skill_def=$(find_skill_def "$folder" 2>/dev/null || true)
        if [[ -n "$skill_def" ]]; then
            local skill_name="${skill_def#*:}"
            local skill_target="$CLAUDE_SKILLS_DIR/$skill_name"
            if [[ -d "$skill_target" ]]; then
                rm -rf "$skill_target"
                echo -e "    ${GREEN}-${NC} Removed skill: $skill_name"
            fi
        fi
    else
        echo -e "  ${YELLOW}!${NC} $folder was not installed"
    fi
}

uninstall_all() {
    echo -e "${YELLOW}Removing all agents from: $CLAUDE_AGENTS_DIR${NC}"
    echo ""

    for def in "${AGENT_DEFS[@]}"; do
        local folder=$(get_agent_folder "$def")
        uninstall_agent "$folder"
    done

    echo ""
    echo -e "${GREEN}Uninstallation complete!${NC}"
    echo ""
}

# Main script
main() {
    local command="install"
    local force="false"
    local specific_agent=""

    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case "$1" in
            -f|--force)
                force="true"
                shift
                ;;
            --prefix)
                if [[ -z "$2" || "$2" == -* ]]; then
                    echo -e "${RED}Error: --prefix requires a path argument${NC}"
                    exit 1
                fi
                CLAUDE_PREFIX="$(cd "$2" && pwd)"
                shift 2
                ;;
            -h|--help)
                print_header
                print_usage
                exit 0
                ;;
            list|status|install|uninstall)
                command="$1"
                shift
                ;;
            *)
                if [[ -z "$specific_agent" ]]; then
                    specific_agent="$1"
                fi
                shift
                ;;
        esac
    done

    # Resolve target directories after parsing --prefix
    CLAUDE_AGENTS_DIR="$CLAUDE_PREFIX/.claude/agents"
    CLAUDE_SKILLS_DIR="$CLAUDE_PREFIX/.claude/skills"

    print_header

    case "$command" in
        list)
            list_agents
            ;;
        status)
            check_status
            ;;
        install)
            ensure_target_dir
            if [[ -n "$specific_agent" ]]; then
                install_agent "$specific_agent" "$force"
            else
                install_all "$force"
            fi
            ;;
        uninstall)
            if [[ -n "$specific_agent" ]]; then
                uninstall_agent "$specific_agent"
            else
                echo -ne "${YELLOW}Remove ALL installed agents? [y/N] ${NC}"
                read -r response
                if [[ "$response" =~ ^[Yy]$ ]]; then
                    uninstall_all
                else
                    echo "Cancelled."
                fi
            fi
            ;;
        *)
            echo -e "${RED}Unknown command: $command${NC}"
            echo ""
            print_usage
            exit 1
            ;;
    esac
}

main "$@"

---
name: evidence-bundle-schema
description: Framework-agnostic JSON schema for the codebase-wide evidence bundle produced in the discovery pass. Framework skills consume this bundle to map evidence to their specific controls.
---

# Evidence Bundle Schema

The evidence bundle is a framework-agnostic JSON artifact produced once during the DISCOVER + COLLECT phases. Every framework-specific mapping pass reads this bundle and maps its facts to framework controls. The bundle is persisted so that multi-framework audits do not repeat scans.

## Why a bundle

- **Single-pass collection**: scan the codebase once regardless of framework count.
- **Auditable trail**: each framework report cites into the bundle, not back into raw code — reviewers can trace every finding to a stable evidence row.
- **Reusable across agents**: `alf-security-assessor`, `alf-dependency-auditor`, `alf-observability-assessor` can contribute to or read from the bundle when co-run with `alf-system-auditor`.
- **Cache-friendly**: repeat audits can diff against the previous bundle (REMEDIATED / UNCHANGED / REGRESSED).

## File location

```
{project-root}/audit-reports/{YYYY-MM-DD}-evidence-bundle.json
```

Write atomically. Include the git SHA at the time of scan in the metadata (`codebase.git_sha`) so the bundle is pinned to a specific snapshot.

## Schema

```json
{
  "schema_version": "1.0.0",
  "generated_at": "2026-04-18T14:32:11Z",
  "generated_by": "alf-system-auditor",
  "model_id": "claude-opus-4-7",

  "codebase": {
    "root": "/absolute/path",
    "git_sha": "abc123...",
    "git_branch": "main",
    "file_count_total": 1247,
    "file_count_analyzed": 412,
    "loc_total": 184302,
    "languages": {"python": 0.62, "typescript": 0.31, "yaml": 0.07},
    "frameworks_detected": ["fastapi", "sqlalchemy", "react", "terraform"],
    "package_managers": ["pip", "npm"],
    "sampling_strategy": "full | deterministic-30pct | hotspot-only",
    "sampling_seed": "sha256",
    "sensitive_paths_scanned": ["src/auth/", "src/crypto/", "config/", "infrastructure/"]
  },

  "facts": {
    "access_control": {
      "authn_mechanisms": [
        {"type": "password", "library": "bcrypt", "evidence": [{"file": "src/auth/password.py", "line": 14}]},
        {"type": "oauth2", "library": "authlib", "evidence": [{"file": "src/auth/oauth.py", "line": 8}]}
      ],
      "mfa_present": false,
      "mfa_evidence": [],
      "rbac_present": true,
      "rbac_evidence": [{"file": "src/auth/rbac.py", "line": 22}],
      "session_management": {"library": "starlette-session", "httpOnly": true, "secure": true, "sameSite": "lax"},
      "password_policy": {"min_length": 8, "complexity_rules": false, "evidence": [{"file": "src/auth/policy.py", "line": 4}]},
      "privileged_endpoints_without_auth": [
        {"file": "src/admin/routes.py", "line": 42, "endpoint": "/admin/users", "method": "GET"}
      ]
    },

    "encryption": {
      "at_rest": {
        "detected": true,
        "algorithms": ["AES-256-GCM"],
        "key_management": "aws-kms",
        "evidence": [{"file": "src/db/encryption.py", "line": 18}]
      },
      "in_transit": {
        "tls_enforced": true,
        "min_version": "TLS 1.2",
        "weak_ciphers_present": false,
        "evidence": [{"file": "infrastructure/alb.tf", "line": 22}]
      },
      "weak_crypto_usage": [
        {"algorithm": "MD5", "file": "src/utils/hash.py", "line": 7, "context": "non-security checksum"}
      ]
    },

    "secrets": {
      "hardcoded_detected": [
        {"type": "api_key", "file": ".env.example", "line": 3, "confidence": "low", "pattern": "STRIPE_KEY=sk_test_..."}
      ],
      "secret_manager": "aws-secretsmanager",
      "secret_manager_evidence": [{"file": "src/config/secrets.py", "line": 12}],
      "env_files_committed": [".env.example"]
    },

    "audit_logging": {
      "logger_library": "structlog",
      "structured_pct": 0.87,
      "auth_events_logged": true,
      "data_access_logged": false,
      "log_tamper_protection": false,
      "retention_configured": "14d",
      "pii_in_logs": [
        {"file": "src/api/users.py", "line": 88, "field": "email", "confidence": "high"}
      ],
      "evidence": [{"file": "src/logging/config.py", "line": 3}]
    },

    "change_management": {
      "vcs": "git",
      "branch_protection": {
        "protected_branches": ["main"],
        "required_reviews": 2,
        "required_checks": ["ci-tests", "lint"],
        "signed_commits_required": false,
        "evidence": ".github/branch-protection.yaml"
      },
      "ci_providers": ["github-actions"],
      "deployment_gates": ["manual-approval-prod"],
      "evidence": [{"file": ".github/workflows/deploy.yml", "line": 18}]
    },

    "vulnerability_management": {
      "dependency_audit_tools_present": ["npm audit", "pip-audit"],
      "sbom_generated": false,
      "known_cves": [
        {"ecosystem": "npm", "package": "lodash", "version": "4.17.20", "cve": "CVE-2021-23337", "severity": "high"}
      ],
      "scanning_in_ci": true,
      "patch_cadence_evidence": ".github/workflows/security.yml"
    },

    "data_protection": {
      "pii_fields_detected": [
        {"field": "email", "file": "src/models/user.py", "line": 12, "encrypted": false},
        {"field": "ssn", "file": "src/models/patient.py", "line": 18, "encrypted": true}
      ],
      "phi_handling_detected": true,
      "cardholder_data_detected": false,
      "data_retention_policy": {"documented": false, "enforced_in_code": false},
      "consent_mechanism": {"present": false, "evidence": []},
      "right_to_erasure": {"implemented": false, "evidence": []},
      "data_portability": {"implemented": true, "evidence": [{"file": "src/api/export.py", "line": 1}]},
      "data_classification_tags": []
    },

    "incident_response": {
      "runbook_present": true,
      "runbook_path": "docs/incident-response.md",
      "breach_notification_procedure": true,
      "oncall_config_detected": ["pagerduty"],
      "post_mortem_template": true
    },

    "backup_recovery": {
      "backup_config_detected": true,
      "backup_evidence": [{"file": "infrastructure/backup.tf", "line": 3}],
      "rto_documented": "4h",
      "rpo_documented": "1h",
      "dr_tested_evidence": null,
      "backup_encryption": true
    },

    "supply_chain": {
      "sbom": {"generated": false, "format": null, "path": null},
      "dependency_signing": false,
      "lockfiles_present": ["package-lock.json", "poetry.lock"],
      "lockfiles_fresh_days": 12,
      "typosquat_candidates": [],
      "unmaintained_dependencies": [
        {"package": "requestsmock", "last_release_days": 1204}
      ]
    },

    "configuration_security": {
      "iac_tools": ["terraform"],
      "overly_permissive_network_rules": [
        {"file": "infrastructure/sg.tf", "line": 18, "rule": "0.0.0.0/0 on :22"}
      ],
      "container_hygiene": {
        "multi_stage_builds": true,
        "non_root_user": false,
        "unpinned_base_images": ["node:latest"]
      },
      "debug_in_production": [
        {"file": "config/prod.yaml", "line": 6, "setting": "DEBUG=true"}
      ],
      "default_credentials": []
    },

    "api_security": {
      "authenticated_endpoints": 42,
      "unauthenticated_endpoints": 5,
      "rate_limiting": {"present": true, "library": "slowapi"},
      "input_validation": {"library": "pydantic", "coverage_estimate": 0.91},
      "cors_policy": {"wildcard_origin": false, "credentials_allowed": true}
    },

    "secure_sdlc": {
      "code_review_required": true,
      "automated_tests_in_ci": true,
      "security_testing_in_ci": ["dependabot", "codeql"],
      "test_coverage_reported": true,
      "coverage_threshold": 0.7
    },

    "third_party_risk": {
      "vendor_registry_path": null,
      "contract_review_evidence": null,
      "data_processing_agreements_detected": false
    }
  },

  "absence_claims": [
    {"category": "data_protection", "control": "right_to_erasure", "reason": "no route, service, or script matching deletion semantics found"},
    {"category": "supply_chain", "control": "sbom", "reason": "no CycloneDX/SPDX file found, no generator step in CI"}
  ],

  "scan_coverage": {
    "dimensions_covered": 13,
    "dimensions_skipped": 0,
    "skipped_reason": {}
  },

  "limitations": [
    "No runtime DAST performed.",
    "Secret manager contents not inspected — only integration patterns.",
    "Container base-image CVEs not enumerated without registry access."
  ]
}
```

## Field rules

**Absence is evidence.** When a control is not found, emit an `absence_claims[]` entry rather than silently omitting the category. Framework skills rely on absence claims to classify "not implemented" vs "not assessed".

**Every fact carries an evidence array.** Shape: `[{"file": "...", "line": N}]`. If the evidence is a whole-file observation, use `line: 1` and note in the finding. If the fact is derived from tool output (e.g. `npm audit`), cite the tool invocation in `limitations` and use a synthetic evidence entry like `{"file": "<tool:npm audit>", "line": 0}`.

**Confidence is implicit.** Direct file:line references imply high confidence. Tool-derived facts imply medium. Absence-based facts imply low. Framework skills may downgrade confidence in their own mapping.

**No PII in the bundle itself.** When recording PII-in-logs findings, store the *field name* and location, never the value.

## How framework skills consume the bundle

Each `framework-{id}.md` skill defines a mapping table:

```markdown
| Control | Evidence path (JSONPath) | Rule | Mapped to |
|---|---|---|---|
| HIPAA 164.312(a)(1) | facts.access_control.rbac_present == true AND facts.access_control.mfa_present == true | pass | 164.312(a)(1) |
| HIPAA 164.312(b) | facts.audit_logging.auth_events_logged == true AND facts.audit_logging.log_tamper_protection == true | pass | 164.312(b) |
| HIPAA 164.312(e)(1) | facts.encryption.in_transit.tls_enforced == true | pass | 164.312(e)(1) |
```

The framework skill's scoring is computed entirely from JSONPath queries into the bundle plus its own weight table. No re-scanning.

## Versioning

`schema_version` follows semver.
- MINOR bump: additive fields that framework skills may ignore safely.
- MAJOR bump: removed/renamed fields. Framework skills declare the MAJOR version they require in their frontmatter (`evidence_schema: "1"`).

## Validation

Before each framework mapping pass, validate the bundle has:
- `schema_version` present
- `codebase.git_sha` present
- `facts` object present with at least one non-empty category
- `limitations` array (may be empty)

Fail fast with a `{CLARIFICATION_NEEDED: true, questions: [...]}` response if validation fails.

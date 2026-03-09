# New Analysis Agents -- Creation Plan

12 new agents to expand the Codebase Analyzer into a comprehensive evaluation tool. Each section is structured as input for `nw:forge`.

---

## Agent 1: alf-security-posture

**Purpose**: Detect security vulnerabilities and assess the overall security hygiene of a codebase.

**Analysis Scope**:
- OWASP Top 10 vulnerability patterns (injection, broken auth, XSS, CSRF, etc.)
- Hardcoded secrets, API keys, tokens, passwords in source
- Input validation boundaries -- where user input enters the system and whether it's sanitized
- Authentication and authorization patterns -- consistency, missing checks
- Dependency vulnerabilities -- known CVEs in direct and transitive dependencies
- Cryptographic usage -- weak algorithms, improper key management
- Sensitive data exposure -- logging PII, error messages leaking internals

**JSON Output Contract**:
- `overall_score` (0-100)
- `owasp_findings[]` with category, severity, file, line, description
- `secrets_detected[]` with type, file, line, confidence
- `input_validation_coverage` -- ratio of entry points with validation
- `auth_pattern_consistency` score
- `dependency_cve_count` by severity (critical/high/medium/low)
- `recommendations[]` prioritized by risk

---

## Agent 2: alf-error-handling-reviewer

**Purpose**: Evaluate exception handling consistency, resilience patterns, and failure mode coverage.

**Analysis Scope**:
- Exception handling patterns -- catch-all vs specific, swallowed exceptions, empty catch blocks
- Error propagation -- how errors flow through layers, consistent error types
- Resilience patterns -- retry logic, circuit breakers, timeouts, bulkheads
- Graceful degradation -- fallback behaviors when dependencies fail
- Missing error paths -- operations that can fail but have no error handling
- Logging on failure -- are errors logged with sufficient context?
- User-facing error quality -- are error messages helpful or generic?

**JSON Output Contract**:
- `overall_score` (0-100)
- `exception_patterns` -- { catch_all_count, empty_catch_count, specific_catch_count }
- `swallowed_exceptions[]` with file, line, context
- `missing_error_handling[]` -- operations that can fail without handling
- `resilience_patterns` -- { retries, circuit_breakers, timeouts, fallbacks } with locations
- `error_propagation_consistency` score
- `recommendations[]`

---

## Agent 3: alf-api-design-reviewer

**Purpose**: Evaluate API contracts for consistency, usability, and evolution readiness.

**Analysis Scope**:
- REST/GraphQL/gRPC contract consistency -- naming conventions, HTTP methods, status codes
- Versioning strategy -- URL, header, or no versioning; consistency across endpoints
- Backward compatibility -- breaking changes, deprecation practices
- Request/response naming conventions -- consistent casing, field naming patterns
- Pagination patterns -- cursor vs offset, consistency across list endpoints
- Error response uniformity -- same error shape across all endpoints
- API documentation alignment -- do specs match implementation?
- Rate limiting and throttling patterns

**JSON Output Contract**:
- `overall_score` (0-100)
- `endpoint_inventory[]` with method, path, versioned, documented
- `naming_consistency` score with violations[]
- `error_format_uniformity` score
- `pagination_consistency` score
- `versioning_assessment` -- strategy, coverage, gaps
- `breaking_change_risks[]`
- `recommendations[]`

---

## Agent 4: alf-dependency-health

**Purpose**: Assess the health, risk, and hygiene of project dependencies.

**Analysis Scope**:
- Outdated dependencies -- how far behind latest, major/minor/patch drift
- Abandoned dependencies -- last publish date, maintenance status, bus factor
- License compliance -- incompatible licenses, copyleft contamination risk
- Transitive dependency risk -- deep trees, conflicting versions, phantom dependencies
- Dependency fan-out -- total count, direct vs transitive ratio
- Duplicate functionality -- multiple libraries solving the same problem
- Pinning strategy -- exact pins, ranges, lock file freshness
- Supply chain signals -- download counts, known maintainer compromises

**JSON Output Contract**:
- `overall_score` (0-100)
- `direct_count`, `transitive_count`, `total_count`
- `outdated[]` with name, current, latest, drift_severity
- `abandoned[]` with name, last_publish, last_commit, risk
- `license_issues[]` with name, license, conflict_reason
- `duplicates[]` with function, libraries[]
- `pinning_strategy` -- { exact, range, unpinned } counts
- `recommendations[]`

---

## Agent 5: alf-concurrency-analyzer

**Purpose**: Detect concurrency issues, performance anti-patterns, and scalability risks.

**Analysis Scope**:
- Thread safety -- shared mutable state, missing synchronization, lock ordering
- Race conditions -- TOCTOU patterns, unprotected concurrent access
- Async/await correctness -- blocking in async paths, missing awaits, fire-and-forget
- N+1 query patterns -- ORM loops, unbatched database access
- Memory leak patterns -- unbounded caches, event listener accumulation, circular references
- Connection pool management -- exhaustion risks, leak patterns
- Blocking I/O in hot paths -- synchronous calls where async is expected
- Resource cleanup -- unclosed connections, file handles, streams

**JSON Output Contract**:
- `overall_score` (0-100)
- `thread_safety_issues[]` with type, file, line, severity, description
- `race_conditions[]` with pattern, location, risk
- `async_issues[]` with type (blocking_in_async, missing_await, fire_and_forget), location
- `n_plus_one[]` with location, entity, estimated_impact
- `memory_leak_risks[]` with pattern, location
- `resource_leaks[]` with resource_type, location
- `recommendations[]`

---

## Agent 6: alf-documentation-health

**Purpose**: Assess documentation coverage, accuracy, and alignment with code complexity.

**Analysis Scope**:
- Doc coverage vs complexity -- are the hardest parts documented?
- Stale comments -- comments that contradict the code they describe
- README accuracy -- do setup instructions actually work? Are sections outdated?
- API documentation completeness -- undocumented public interfaces
- Inline documentation quality -- useful context vs noise (e.g., `i++ // increment i`)
- Architecture decision records -- presence, currency, coverage of key decisions
- Changelog discipline -- exists, kept current, follows a standard format
- Onboarding path -- can a new developer get from zero to running in < 30 minutes?

**JSON Output Contract**:
- `overall_score` (0-100)
- `doc_coverage` -- { documented_public_apis, total_public_apis, ratio }
- `complexity_doc_gap[]` -- high-complexity files with no/minimal docs
- `stale_comments[]` with file, line, comment_snippet, contradiction
- `readme_assessment` -- { exists, sections[], freshness, setup_completeness }
- `adr_assessment` -- { count, latest_date, coverage }
- `changelog_assessment` -- { exists, format, last_entry_date }
- `recommendations[]`

---

## Agent 7: alf-dead-code-detector

**Purpose**: Find unreachable code, unused exports, orphaned files, and feature drift.

**Analysis Scope**:
- Unreachable code -- dead branches, post-return statements, impossible conditions
- Unused exports -- public functions/classes never imported elsewhere
- Orphaned files -- files not imported or referenced by any other file
- Feature flags left on -- toggles that are always true/false, never evaluated
- Unused configuration -- config keys defined but never read
- Zombie dependencies -- declared in manifest but never imported
- Commented-out code -- large blocks of commented code left behind
- Dead routes/endpoints -- defined but unreachable from any entry point

**JSON Output Contract**:
- `overall_score` (0-100)
- `unreachable_code[]` with file, line_range, type, confidence
- `unused_exports[]` with file, name, type (function/class/constant)
- `orphaned_files[]` with path, last_modified, size
- `stale_feature_flags[]` with name, location, always_value
- `unused_config[]` with key, defined_in
- `zombie_dependencies[]` with name, declared_in
- `commented_code_blocks[]` with file, line_range, line_count
- `recommendations[]`

---

## Agent 8: alf-devops-maturity

**Purpose**: Evaluate CI/CD pipeline quality, build reproducibility, and deployment readiness.

**Analysis Scope**:
- CI/CD pipeline quality -- stages, parallelism, failure handling, run times
- Build reproducibility -- deterministic builds, lock files, pinned tool versions
- Environment parity -- dev/staging/prod drift, config management approach
- Secret management -- how secrets are stored, rotated, accessed
- Deployment strategy -- blue/green, canary, rolling; rollback capability
- Containerization hygiene -- Dockerfile best practices, image size, layer optimization
- Infrastructure as Code -- presence, coverage, drift detection
- Branch protection and merge policies

**JSON Output Contract**:
- `overall_score` (0-100)
- `ci_pipeline` -- { provider, stages[], avg_duration, failure_rate_signals }
- `build_reproducibility` -- { lock_files, pinned_tools, deterministic } booleans
- `secret_management` -- { method, rotation_signals, hardcoded_risk }
- `deployment` -- { strategy, rollback_capable, zero_downtime }
- `containerization` -- { dockerfile_quality, image_size_signals, layer_efficiency }
- `iac` -- { tool, coverage, drift_detection }
- `recommendations[]`

---

## Agent 9: alf-code-ownership

**Purpose**: Analyze knowledge distribution, bus factor, and collaboration health using git history.

**Analysis Scope**:
- Bus factor -- files/modules with a single contributor (knowledge silos)
- Hotspot analysis -- files with high churn AND high complexity (risk magnifiers)
- Knowledge concentration -- percentage of code owned by top N contributors
- Orphan code -- files with no recent committer (last touch > 1 year)
- Review coverage -- do PRs get reviewed? By how many people?
- Team coupling -- which teams/people frequently change the same files?
- Contributor onboarding -- are new contributors touching diverse areas or siloed?
- Churn velocity -- areas changing too fast (instability) or never (potential staleness)

**JSON Output Contract**:
- `overall_score` (0-100)
- `bus_factor` -- { overall, per_module[] with module, bus_factor, top_contributor }
- `hotspots[]` with file, churn_count, complexity, risk_score
- `knowledge_concentration` -- { top_1_pct, top_3_pct, gini_coefficient }
- `orphan_code[]` with file, last_touch_date, last_contributor
- `team_coupling[]` with file, contributors[], coupling_score
- `churn_velocity` -- { high_churn[], stale[] }
- `recommendations[]`

---

## Agent 10: alf-consistency-checker

**Purpose**: Evaluate adherence to project conventions, naming patterns, and structural uniformity.

**Analysis Scope**:
- Naming conventions -- consistent casing (camelCase, snake_case), abbreviation usage
- Project structure patterns -- do similar things live in similar places?
- Import ordering -- consistent grouping and sorting
- Logging format uniformity -- structured vs unstructured, consistent levels
- Configuration approach -- consistent config access pattern across modules
- Error handling style -- consistent exception types, error wrapping patterns
- Code formatting -- consistent style (even without a formatter)
- Pattern adherence -- if the codebase uses a pattern (repository, service, etc.), is it used everywhere?

**JSON Output Contract**:
- `overall_score` (0-100)
- `naming_violations[]` with file, name, expected_convention, actual
- `structure_consistency` score with anomalies[]
- `import_consistency` score with violation_count
- `logging_uniformity` -- { structured_pct, level_consistency, format_variations }
- `config_patterns` -- { dominant_pattern, deviations[] }
- `pattern_adherence` -- { patterns_detected[], inconsistencies[] }
- `recommendations[]`

---

## Agent 11: alf-data-layer-reviewer

**Purpose**: Evaluate database access patterns, schema hygiene, and data layer correctness.

**Analysis Scope**:
- Schema migration hygiene -- sequential, reversible, no data loss risks
- ORM usage patterns -- misuse, lazy loading traps, over-fetching
- Raw SQL safety -- injection risk in dynamic queries
- Transaction boundary correctness -- missing transactions, nested transaction issues
- Index coverage -- queries without supporting indexes (from schema analysis)
- N+1 detection -- ORM-specific loop patterns
- Connection management -- pool configuration, leak potential
- Data validation at boundary -- input sanitization before DB operations

**JSON Output Contract**:
- `overall_score` (0-100)
- `migration_health` -- { total, reversible_count, naming_consistent, gap_free }
- `orm_issues[]` with type (lazy_load, over_fetch, n_plus_one), location, entity
- `raw_sql_risks[]` with file, line, query_snippet, injection_risk
- `transaction_issues[]` with type, location, description
- `index_gaps[]` with table, query_location, missing_index_suggestion
- `connection_management` -- { pool_configured, leak_risks[] }
- `recommendations[]`

---

## Agent 12: alf-observability-readiness

**Purpose**: Assess logging, tracing, metrics, and monitoring instrumentation for production readiness.

**Analysis Scope**:
- Logging coverage -- are key decision points, errors, and boundaries logged?
- Structured logging adoption -- JSON/structured vs printf-style
- Tracing instrumentation -- distributed tracing spans at service boundaries
- Metric emission -- business and technical metrics exposed
- Health check completeness -- liveness, readiness, dependency checks
- Alert readiness -- are there clear signals to alert on?
- Correlation IDs -- request tracing across service boundaries
- Log level appropriateness -- DEBUG in production paths, ERROR for non-errors

**JSON Output Contract**:
- `overall_score` (0-100)
- `logging` -- { coverage_score, structured_pct, level_appropriateness, sensitive_data_risks[] }
- `tracing` -- { instrumented, span_coverage, correlation_id_present }
- `metrics` -- { emission_points[], business_metrics[], technical_metrics[] }
- `health_checks` -- { liveness, readiness, dependency_checks[] }
- `alert_signals` -- { identifiable_failure_modes[], missing_signals[] }
- `recommendations[]`

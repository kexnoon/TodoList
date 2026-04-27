# Agentic AI in TodoList Project

## Overview
This project uses an Agentic AI workflow where responsibilities are split across dedicated agents and enforced through documented contracts. The approach is process-driven: specification first, then task planning, then implementation, then review.

## Agent Roles
- Planning agent: owns feature specifications in `specs/`.
- Software Engineering agent: owns task breakdown in `tasks/`, implementation, and implementation logs in `docs/implementation_logs/`.
- Code review agent: validates implementation against specs/tasks, checks tests, and reports gaps.

This separation is explicit in `AGENTS.md` and is treated as an operational boundary.

## Source-of-Truth Artifacts
Agentic work is grounded in three layers of documents:
- `AGENTS.md` (what the agents are).
- `specs/` (what the system must do).
- `tasks/` (how each approved step is broken into TDD tasks).
- `docs/implementation_logs/` (what was actually implemented and adjusted during execution).

## Execution Model
The project applies a strict stage flow:
1. Planning agent defines/updates spec.
2. Engineering agent decomposes one approved step into tasks.
3. Engineering agent implements tasks one by one.
4. Review agent checks spec compliance, test coverage, and code quality.
5. User approval gates continuation.

This creates human-controlled autonomy: agents execute within role boundaries, while the user remains the final decision authority.

## TDD and Verification as Agent Policy
Agents follow a required TDD sequence:
1. Contracts
2. Tests (red)
3. Implementation (green)
4. Refactor (only if needed)

Testing strategy is also agent-enforced:
- Unit tests for use cases, view models, and business logic helpers/mappers.
- Integration tests for repository/storage/network boundaries.
- UI tests are intentionally deferred at this stage.

## How Agentic AI Is Applied in Practice (Folders)
The folders feature demonstrates end-to-end agentic execution in three scoped steps:
- Step 1: MainScreen folder happy path and search behavior.
- Step 2: NoteScreen folder indicator and assignment flow.
- Step 3: Multi-select move-to-folder flow.

Across these steps, the logs show consistent agent behavior:
- Contracts are locked before implementation.
- In-flight decisions are documented (not hidden in code-only changes).
- DI shape, error handling, and timestamp rules are decided and recorded explicitly.
- Verification is run at module level before task closure.

## Governance and Determinism
The project uses strict constraints to keep AI output deterministic:
- No implementation outside approved scope.
- No behavior changes without spec/task alignment.
- Minimal, targeted changes and explicit permission gates for continuation.
- Mandatory documentation updates when decisions change.

This keeps Agentic AI work auditable, reproducible, and aligned with product intent.

# AGENTS.md

## Setup
- This file is an additional Codex setup for Todo List project. 
- On reading this file you should also read default `AGENTS.md` from Codex's home directory for general instructions. 
- You should follow both instructions from default `AGENTS.md` and this file. 
- In case of conflicting instructions you should ask me for further clarifications.

## General instructions
- Follow Test-driven development (see bellow)
- Follow the testing strategy (see bellow)
- Always read files in /specs before implementing
- Code should be simple and readable
- Follow KISS and DRY principles and general SOLID/Clean Code/Clean Architecture rules
- Avoid overengineering
- Avoid spending too much tokens

## Test-Driven Development
- You should follow Test-Driven Development 
- Step 1: creating contracts (if they don't yet exist)
- Step 2: writing tests (red)
- Step 3: writing code (green)
- Step 4: refactoring if needed.

## Testing strategy
- Unit tests should cover: Use Cases, ViewModels, special classes of business logic (i.e mappers, helpers etc.)
- Integration test should cover repositories and their integration with storage and network
- At this stage of development avoid writing UI tests

## Constraints
- Do not invent requirements that are not described
- Do not change behavior without updating the spec
- Before implementing anything, present your solution to the user without implementing it.
    - You a go to implement it ONLY after an explicit user's consent
    - Don't present the solution with code examples unless user clearly ask for this

## Planing guidelines
- When asked to plan a feature with implementation steps, each step must represent a distinct user flow for that feature.
     - Apply TDD to each individual step, rather than to the feature as a whole.
     - Keep the description of each step concise. Detailed implementation specifics will be defined later in the task files (see below).
- Planning is an iterative process. Document each proposed plan under a versioned header until the user explicitly approves it.
     - Versioned headers naming: `# implementation-v1`, `# implementation-v2`, etc.
     - The explicitly approved plan must be documented under the `# implementation-final` header.
- Once the final implementation plan is approved, each step will be addressed individually and broken down into tasks.
     - Create task files within the `/tasks` directory.
     - For example: if we have a feature "Foo", and we need to implement "Bar" step. create a file at `/tasks/foo/bar` that outlines all the tasks for the current step.

## Documentation
- If user decides to make changes into requirements or implementation process, those changes should be clearly logged into spec or task, depending on what was affected.

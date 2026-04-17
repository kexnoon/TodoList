# AGENTS.md

## Setup
- This file is an additional Codex setup for Todo List project. 
- On reading this file you should also read default `AGENTS.md` from home directory for general instructions. 
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
- Avoid constantly re-running tests while working with UI layer

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
- If you create a new unit or integration test file for functionality that had no tests before (UseCase, ViewModel, Repository), you must cover the full behavior of that functionality, even when part of that behavior is outside the current task, step, or feature scope.

## Constraints
- Do not invent requirements that are not described
- Do not change behavior without updating the spec
- Before implementing anything, present your solution to the user without implementing it.
  - You a go to implement it ONLY after an explicit user's consent
  - Don't present the solution with code examples unless user clearly ask for this

## Steps planing guidelines
- When asked to plan a feature we break it down into steps. Each step must represent a distinct user flow for that feature.
  - Keep the description of each step concise. Detailed implementation specifics will be defined later in the task files (see below).
- Planning of the steps is an iterative process. Document each proposed plan under a versioned header until the user explicitly approves it.
  - Versioned headers naming: `# implementation-v1`, `# implementation-v2`, etc.
  - The explicitly approved plan must be documented under the `# implementation-final` header.

## Tasks planning guidelines
- Once the final implementation plan for a feature is approved, each step will be addressed individually and broken down into tasks.
  - Create task files within the `/tasks` directory.
  - For example: if we have a feature `Foo`, and we need to implement `Bar` step, create a file at `/tasks/foo/bar.md` that outlines all the tasks for the `Bar`.
- Tasks planning may be iterative. Document a list of tasks into a file only when the user explicitly approves it.
  - In contrast to the steps planning you don't need any headers like `# implementation-v1` in this file
- Breaking down steps into tasks should follow TDD principles (see above)
  - First goes a task with contracts. 
    - If you need needed to generate new business models and/or update Database and DAO, you should do it in this step too.
    - Setting up DI is also a part of this step. No temporary constructors and stuff.
  - Then goes a task with writing tests (red), follow the testing strategy (see above)
  - Then you should go for implementation of data and domain layer so it could pass the tests (green)
  - Then you should implement UI logic (ViewModels, States, Events) so it could pass the tests for ViewModel (green)
  - Then you should implement Screens and Views
    - Composables needed for the screen are also the part of this step
  - Finally you should refactor the code where needed, but it still should pass the tests.
- Tasks should be implemented ONE BY ONE.
- After a completion of a task you should stop, present user with the results of task implementation and wait for an explicit user's command to start doing the next task.

## Documentation
- If user decides to make changes into requirements or implementation process, those changes should be clearly logged into spec or task, depending on what was affected.
- Steps and plans should be documented in English only.

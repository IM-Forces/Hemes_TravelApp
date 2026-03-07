# Hemes: Travel App – Contributing Guidelines

This document describes our branching strategy and general collaboration workflow.

---

## Branching Strategy

Our team follows a **flexible branching strategy** based on simplicity and stability.

We keep the `main` branch untouched during development. All work is done in separate branches created as needed. The `main` branch is only updated when a fully stable version of the application is ready.

### Core Principles

- `main` always contains a **stable, production-ready version**.
- No direct development occurs on `main`.
- New branches are created depending on the team’s needs (features, fixes, refactors, experiments, etc.).
- A branch is merged into `main` only when it is tested and confirmed stable.

---

## Creating Branches

Branches are created from `main` unless the team decides otherwise.

### Naming Convention

Use clear, descriptive names that reflect the purpose of the branch.

### Examples

- `feature/trip-itinerary-builder`
- `fix/login-validation-error`
- `refactor/api-structure`
- `experiment/map-performance-test`

Branch names should clearly communicate the goal of the work.

---

## Development Workflow

1. Create a new branch from `main`.
2. Implement the required changes.
3. Make sure:
   - The project builds successfully.
   - No errors or warnings remain.
   - New functionality is properly tested.
4. Open a Pull Request (PR) targeting `main`.
5. At least one team member must review the PR.
6. Once approved and confirmed stable, merge into `main`.
7. Delete the branch after merging.

---

## Pull Request Guidelines

Each Pull Request must include:

- A clear and descriptive title.
- A summary of the changes made.
- Description of how the changes were tested.
- Screenshots (if UI changes are included).
- Any relevant notes for reviewers.

---

## Stability Rule

The most important rule of our workflow:

`main` must always remain stable and deployable.

If a branch is not fully stable, it must not be merged.

---

## Commit Message Guidelines

Use clear and structured commit messages:

Format:

type: short description

### Examples:

- `feat: add itinerary creation module`
- `fix: correct budget calculation bug`
- `refactor: simplify authentication logic`
- `docs: update project documentation`

### Common Types

- `feat` – New feature  
- `fix` – Bug fix  
- `refactor` – Code improvements without changing behavior  
- `docs` – Documentation updates  
- `chore` – Maintenance tasks

---

## Code Quality Standards

- Write clean and readable code.
- Keep branches focused on a single purpose.
- Avoid large unrelated changes in one branch.
- Test changes before submitting a PR.
- Ensure the application remains stable before merging.

---

## Final Notes

Our strategy is intentionally simple:

We create branches as needed and merge into `main` only when a stable version is ready.

This approach maintains flexibility during development while ensuring that `main` always reflects a reliable version of Hermes.

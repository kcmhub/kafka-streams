# Contributing to Kafka Streams Skeleton
Thank you for considering contributing to this Kafka Streams template.
Contributions of all kinds are welcome: bug fixes, documentation, examples, and small features.
## Ways to contribute
### Reporting issues
If you find a bug or have a question:
- Open an issue in the repository.
- Include:
  - A clear description of the problem
  - Steps to reproduce
  - Expected vs actual behavior
  - Your environment (OS, JDK version, Maven version, Kafka version)
  - Relevant logs or stack traces if available
### Submitting changes (Pull Requests)
1. Fork the repository.
2. Create a branch for your change:
   - `feature/my-new-feature`
   - or `fix/issue-123`
3. Make your changes in small, focused commits.
4. Ensure the project builds and tests pass locally:
   ```powershell
   mvn test
   ```
5. Update documentation if your change affects behavior or configuration.
6. Open a pull request and describe:
   - What the change does
   - Why it is needed
   - How it was tested
   - Link to any related issues (e.g. "Fixes #123")
## Development setup
Requirements:
- Java 21 (JDK 21)
- Maven 3.x
- Access to a Kafka broker for running the application or integration tests
Useful Maven commands:
```powershell
mvn clean compile     # compile the project
mvn test              # run unit tests
mvn spring-boot:run   # run the application
```
## Coding style and conventions
- Follow standard Java and Spring Boot conventions.
- Use meaningful class and method names.
- Keep classes small and focused.
- Reuse the existing package structure (`io.kcmhub` and `io.kcmhub.streams`) unless there is a good reason to change it.
- Prefer constructor injection for Spring beans.
- Keep Kafka Streams configuration centralized (for example, in `StreamsTopologyConfig`).
If you use an IDE formatter (e.g. IntelliJ default settings), run it before committing to keep the style consistent.
## Tests and quality
- New features or behavior changes should be covered by tests when reasonable.
- Make sure all tests pass before submitting a PR:
  ```powershell
  mvn test
  ```
- Prefer fast, deterministic tests.
## Commit messages and PR checklist
Write clear commit messages, for example:
- `Add environment variable docs for Spring Kafka`
- `Fix null handling in UppercaseTopologyBuilder`
Before opening a PR, check:
- [ ] Code builds successfully
- [ ] Tests pass (`mvn test`)
- [ ] Documentation is updated if needed

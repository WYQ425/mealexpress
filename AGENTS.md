# Repository Guidelines

## Project Structure & Module Organization
- `sky-server/`: Spring Boot backend delivering REST APIs; bootstrap class located at `src/main/java/com/sky/SkyApplication.java`.
- `sky-common/` and `sky-pojo/`: Shared utility code, configuration primitives, and DTO/entity models referenced across backend modules.
- `project-sky-admin-vue-ts/`: Vue 2 + TypeScript admin console; routed pages under `src/views`, reusable widgets in `src/components`, icon system in `src/icons`.
- `sky.sql` and the root-level Chinese database design markdown: Canonical schema and ER notes; update both whenever persistence models or migrations change.

## Build, Test, and Development Commands
- `mvn clean package`: Build all Maven modules and emit artifacts in each module's `target/` directory.
- `mvn spring-boot:run -pl sky-server`: Start the backend locally with Spring DevTools hot reload of classpath resources.
- In `project-sky-admin-vue-ts/`, run `npm install` followed by `npm run serve` to launch the Vue CLI dev server.
- `npm run build` or `npm run build:uat`: Produce optimized frontend bundles; ensure the matching `.env.*` points at the intended backend host.
- `npm run lint`: Execute ESLint and Prettier checks so formatting stays consistent before opening a pull request.

## Coding Style & Naming Conventions
- Java code uses 4-space indentation, the `com.sky` package root, and Lombok for boilerplate; place controllers in `controller`, services in `service`, and MyBatis mappers in `mapper`.
- Vue/TypeScript files follow the `.editorconfig` 2-space indentation; Prettier enforces single quotes and no semicolons (`npm run lint`).
- Name transport objects with the `*DTO` suffix, persistence classes with `*Entity`, and Vue components with `PascalCase` filenames that mirror their default export.
- Keep configuration values externalized via `application*.yml` or `.env.*` files and avoid embedding secrets inside source files.

## Testing Guidelines
- Backend tests reside in `sky-*/src/test/java`; run them with `mvn test` and favor Spring `@SpringBootTest` for service orchestration coverage.
- Frontend unit tests execute through Jest (`npm run test:unit`); end-to-end scenarios rely on Cypress (`npm run test:e2e`).
- When adding features, extend fixtures or mocks to exercise edge cases, and document any required seed data so failures can be reproduced quickly.

## Commit & Pull Request Guidelines
- Write imperative, scoped commit subjects (e.g., `Add coupon expiry validation`) and avoid batching unrelated changes.
- Reference affected modules in the commit body (`sky-server`, `project-sky-admin-vue-ts`) and call out schema adjustments alongside updates to `sky.sql`.
- Pull requests should include a concise summary, the verification commands you ran, related issue IDs, and visuals or payload samples for UI- or API-facing changes.
- Rebase or merge the latest `main` before requesting review to minimize integration conflicts.

## Security & Configuration Tips
- Never commit populated `.env.*` files; rely on the templates provided and list required environment variables in your pull request description.
- Rotate credentials for OSS, Redis, and external integrations regularly, sourcing them from environment variables or a secrets manager rather than hard-coding them.

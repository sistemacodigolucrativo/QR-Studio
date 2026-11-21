# Arquitetura mestre

Fluxo de dependências permitido:

```
:app ---> :feature:home ---> :core:common / :core:ui / :core:designsystem
  +----> :core:network / :core:database
```

Não introduza dependências de feature para feature. Componentes visuais genéricos vão em `:core:ui`; estilos e valores semânticos exclusivamente em `:core:designsystem`. A funcionalidade exemplo `:feature:home` demonstra separação de `ui` (Compose + ViewModel), `domain` (interface e use cases) e `data` (implementação de repositório). Estado é imutável e exposto via `StateFlow`, eventos são encaminhados ao ViewModel.

A configuração da identidade fica em `app-config.properties`. `namespace` Kotlin do template permanece estável; o `applicationId` Android muda via Gradle sem renomear pacotes do código-fonte.

Ambientes: `dev` com package `.dev`, `staging` com `.staging`, `prod` sem sufixo.

`core:network` disponibiliza Retrofit/OkHttp com timeout e logging BASIC apenas em debug. Não adicionamos permissão de internet no manifesto do app base, porque não realiza chamadas; apps derivados devem declarar `android.permission.INTERNET` quando ativarem rede.

`core:database` inclui Room, o schema inicial `saved_items` e `DatabaseMigrations.ALL`, vazio na versão 1. Evolução de schema exige uma migração declarada por versão e testes. Não usar fallbackToDestructiveMigration em produção.

O próprio app é deliberadamente mínimo; o foco é provar compilação, isolamento, layout adaptativo, tokens, DI e padrão de código.

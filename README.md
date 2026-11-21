# Kotlin Master Boilerplate 🚀

**Base mestre estrutural para qualquer aplicativo Android Kotlin, sem copiar templates visuais ou acoplar regras de negócio.**

Projeto independente em **Kotlin + Jetpack Compose**, arquitetura **MVVM**, camadas **ui / domain / data**, **Koin**, **Room**, **Retrofit/OkHttp**, **Version Catalog**, **Gradle Kotlin DSL** e **JDK 17**.

> **Este projeto NÃO é uma fábrica de templates visuais.** É um ponto de partida de código para aplicativos de diferentes finalidades. Cada aplicativo precisa implementar suas próprias funcionalidades em módulos `:feature:*`, usando a mesma infraestrutura compartilhada.

## Estrutura invariável

```text
Kotlin-Master-Boilerplate/
├── app-config.properties              # Identidade, versões, SDK, API e marca
├── gradle/libs.versions.toml           # Todas as versões de bibliotecas e plugins
├── gradle/wrapper/                     # Gradle Wrapper verificado e commitado
├── app/                                # Bootstrap, flavors e navegação do app
├── core/
│   ├── common/                         # UiState, contratos e utilitários
│   ├── designsystem/                   # Color, Type, Shape, Dimens, Theme
│   ├── network/                        # Retrofit e OkHttp
│   ├── database/                       # Room, DAOs, migrações
│   └── ui/                             # Estados e componentes genéricos
├── feature/
│   └── home/                           # Exemplo MVVM: ui/domain/data
├── tools/                               # Alteração segura da configuração
├── tests/                               # Validações de configuração
└── .github/workflows/                  # CI Android e release segura
```

**Dependências:** `:app` pode integrar `:feature:*` e `:core:*`. Cada feature pode depender apenas de módulos `:core:*` (e bibliotecas externas); **uma feature não importa outra feature**. Não coloque regras de negócio em Composables.

## Criar um aplicativo a partir da Base Mestre — pelo celular

1. No GitHub, abra **Use this template → Create a new repository** (o proprietário precisa primeiro habilitar **Settings → General → Template repository**).
2. Abra `app-config.properties` no repositório derivado. Altere `appName`, `applicationId`, `versionCode`, `versionName`, `baseApiUrl`, `brandColor` e `iconName`. `minSdk`, `targetSdk` e `compileSdk` também ficam neste arquivo.
3. Se desejar, atualize o ícone `app/src/main/res/mipmap-anydpi/ic_launcher.xml`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` e o recurso de foreground. Ao escolher outro `iconName`, forneça o recurso correspondente; não deixe manifesto apontando para nome inexistente.
4. Implemente as telas e os casos de uso em `feature/<nome>/ui`, `domain`, `data`. Registre o módulo novo em `settings.gradle.kts` e a dependência no `:app`. Se usar rede, acrescente a permissão `android.permission.INTERNET` ao manifesto derivado.
5. Configure os módulos Koin no `:app` e as rotas tipadas Navigation Compose. Strings devem ficar em `res/values/strings.xml` e `res/values-pt-rBR/strings.xml`.
6. Na aba **Actions**, execute **Kotlin Master - Gate de compilacao**. A execução instala o Android SDK na nuvem, compila o APK `devDebug` e o AAB `prodRelease` sem precisar de computador.
7. Faça o download do APK em **Artifacts** e instale para testar no aparelho. Os testes de instrumentação precisam ser executados em emulador ou dispositivo; a compilação CI não os substitui.
8. Para publicação, configure os quatro secrets de assinatura no ambiente **release** do repositório derivado e execute **Kotlin Master - Release assinada**. Somente o AAB assinado deve ser enviado à Play Store.

Não é necessário renomear manualmente pacotes Kotlin: o **`applicationId` Android** é lido de `app-config.properties` pelo Gradle. É deliberadamente independente do `namespace` estável das classes do motor.

## Comandos equivalentes (opcional; nunca exigem Android Studio local)

```bash
./gradlew validateAppConfig
./gradlew :app:assembleDevDebug
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
./gradlew :app:bundleProdRelease
./gradlew :app:bundleRelease
./gradlew :feature:home:testDebugUnitTest
./gradlew ktlintCheck detekt
```

Os comandos `assembleDebug`, `assembleRelease` e `bundleRelease` são agregadores de flavors. O aplicativo não usa nenhuma chave de assinatura em código; builds release **sem secrets geram AAB unsigned**, que **não pode ser publicado**.

### Flavors e pacotes

| Flavor | Application ID | Finalidade |
|---|---|---|
| `dev` | `<applicationId>.dev` | Depuração |
| `staging` | `<applicationId>.staging` | Homologação |
| `prod` | `<applicationId>` | Distribuição |

## Recursos padronizados

- **Design tokens:** `core/designsystem/theme/{Color,Type,Shape,Dimens,Theme}.kt`. Cor da marca chega pelo arquivo de configuração. Material You dinâmico é **opcional** e desativado por padrão.
- **Estado:** `StateFlow` e `UiState` imutáveis; estados de loading, vazio, offline e erro têm componentes com textos traduzidos.
- **Compatibilidade:** `WindowSizeClass`, layout adaptativo, edge-to-edge, alvo de toque 48dp e telas responsivas.
- **Segurança:** HTTPS obrigatório na configuração, cleartext bloqueado, backup desativado, R8 em release, sem permissão desnecessária ou logs de payload em produção.
- **Monetização, analytics e crash reporting:** desligados e **não implementados** na base mínima; podem ser integrados depois como módulos opcionais, com consentimento antes da ativação.
- **Qualidade:** lint Android, testes de domínio e UI (instrumentados), ktlint e detekt. Dependabot para dependências e GitHub Actions.

## Assinatura segura

Cadastre **no projeto derivado**, preferencialmente em **Settings → Environments → release → Environment secrets**:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

A chave é decodificada em arquivo temporário no runner; não é commitada. É obrigatório proteger a chave de upload real e fazer backup seguro fora do GitHub. O workflow de release não deve rodar em pull requests não confiáveis.

## Integração com o console Universal Build

A integração de build é fornecida por dois workflows instalados neste repositório: `.github/workflows/universal-build.yml` (despachado pelo console) e `.github/workflows/universal-sign.yml` (assinatura posterior de AAB). O terceiro workflow, `universal-build-integration.yml`, executa um smoke usando o mesmo motor externo para prevenir regressões.

1. Abra [Universal Build Console](https://sistemacodigolucrativo.github.io/Universal-Build/) e conecte sua conta GitHub com um PAT de escopo restrito e acesso ao repositório. Não compartilhe o PAT.
2. Selecione `Kotlin-Master-Boilerplate` na lista de projetos Android. O console reconhece `settings.gradle.kts`, `app/build.gradle.kts` e `app/src/main/AndroidManifest.xml`.
3. Selecione `Automático` no campo **Variante Android**, diretório `.`, módulo `app` e Java `17`.
4. Para APK de teste use **APK (debug)** → `assembleDevDebug`. Para AAB sem assinatura, use **AAB unsigned** → `bundleProdRelease`. O modo combinado compila ambos e coleta os arquivos da variante correta.
5. Abra o histórico no console para encontrar o run, os logs e os artefatos. O workflow de integração executa também um smoke cross-repo independente da interface, sem PAT no workflow.
6. Para assinar AAB: cadastre os quatro `ANDROID_KEYSTORE_*` secrets no repositório derivado e use o fluxo de assinatura. Sem secrets, a base **não** gera AAB pronto para publicação.

O motor Universal Build mantém compatibilidade com projetos Android sem flavors. A escolha explícita `dev/staging/prod` permite gerar uma única variante quando necessário.

**Validação:** a compilação local do Kotlin Master pelo seu próprio GitHub Actions foi aprovada. O workflow `universal-build-integration.yml` é o gate para provar que o motor externo consegue produzir ambos os artefatos. Um teste de clique autenticado na interface GitHub Pages requer a sessão GitHub do proprietário; não é substituído pelo smoke.

## Documentação

- [Arquitetura](docs/ARQUITETURA.md)
- [Integração operacional com Universal Build](docs/INTEGRACAO-UNIVERSAL-BUILD.md)
- [Matriz de compatibilidade e limitações](docs/COMPATIBILIDADE-E-TESTES.md)
- [Changelog](CHANGELOG.md)
- [Licença Apache-2.0](LICENSE)

**Condição de pronto:** apenas declare este template compilável depois de um CI realmente aprovado. Testes em fabricantes/ROMs e Play Store necessitam de evidência externa.

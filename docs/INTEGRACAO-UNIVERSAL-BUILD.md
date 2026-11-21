# Integração com Universal Build Console

## Contrato de repositório

O Kotlin Master é um projeto Kotlin/Compose multi-módulo com Gradle Wrapper na raiz, módulo de aplicativo `app`, flavors `dev/staging/prod` e configuração `app-config.properties`. O console detecta o projeto pela árvore Git, não apenas pelo nome. Projetos derivados recebem os workflows junto com o código.

## Fluxo de compilação

| Seleção no console | Tarefa do Gradle | Resultado |
|---|---|---|
| APK, flavor automático | `:app:assembleDevDebug` | `app/build/outputs/apk/dev/debug/*.apk` |
| AAB unsigned, automático | `:app:bundleProdRelease` | `app/build/outputs/bundle/prodRelease/*.aab` |
| APK + AAB unsigned, automático | ambas | dois artefatos |
| APK, flavor staging | `:app:assembleStagingDebug` | `app/build/outputs/apk/staging/debug/*.apk` |
| AAB unsigned, flavor staging | `:app:bundleStagingRelease` | `app/build/outputs/bundle/stagingRelease/*.aab` |

O console publica os binários em **Actions → Universal Build - Compilar Android → Artifacts**. Arquivos são selecionados por pasta exata de flavor, nunca por ordem arbitrária.

## Origem e assinatura

O console envia `build_target`, `project_dir=.`, `android_module=app`, `android_flavor=auto` e `java_version=17` ao workflow local `universal-build.yml`, que usa o motor público:

```
sistemacodigolucrativo/Universal-Build/.github/workflows/android-reusable.yml@main
```

O motor exporta os contratos `UNIVERSAL_*` e `ANDROID_KEYSTORE_*` necessários para assinar release; segredos permanecem nos GitHub Secrets e nunca no Git. O workflow de assinatura posterior continua separado.

## Evidências e limites

- Base Kotlin compilável: o CI independente já foi validado antes desta integração.
- Integração cross-repo: `.github/workflows/universal-build-integration.yml` executa `both-unsigned` usando o **mesmo motor** e deve concluir com APK dev e AAB prod.
- O workflow não efetua login nem clica no console hospedado. A verificação visual e autenticada do painel deve ser feita no navegador do proprietário.
- Assinatura com chaves reais e publicação Play Store são etapas separadas, não simuladas no smoke de teste.

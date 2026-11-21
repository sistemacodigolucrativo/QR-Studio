# QR Studio — MVP 0.1.0

Aplicativo Android derivado do Kotlin Master Boilerplate. A base mestre permanece intacta.

## Entrega inicial

- Geração de QR Code para links e textos, offline, com ZXing.
- Interface Kotlin e Jetpack Compose com ViewModel, Koin e arquitetura MVVM.
- Histórico local persistente com Room, com reabertura e exclusão.
- Compartilhamento de texto. A imagem do QR ainda não é exportada.
- Validação da entrada (máximo 1024 bytes UTF-8).
- Testes unitários da geração e teste instrumental inicial da interface.
- Identificador de publicação reservado: br.com.sistemacodigolucrativo.qrstudio.

## Próximos trabalhos

- Leitor de QR Code com CameraX/ML Kit e permissões apropriadas.
- Compartilhar/exportar imagem (FileProvider), favoritos, categorias, formatos Wi-Fi/VCard.
- Identidade visual e ícone próprios, testes reais e acessibilidade.
- Configuração de assinatura release com chave do proprietário, políticas e envio AAB à Play.
- Validar consentimento AdMob/Google UMP, formatos monetizados e políticas da Play Store.

Os recursos de câmera e exportação de imagem ainda não estão implementados. **AdMob nativo existe nesta branch de integração**, com modo `off` por padrão. A `main` ainda pode conter a versão inicial sem AdMob; verificar a PR.

## AdMob e Universal Build

Esta branch contém `:core:ads` nativo, Google UMP e `config/universal-build-admob.json` em `mode=off`. O painel Universal Build lê/grava a configuração **por branch**. Selecionar uma branch funcional é necessário para testar o QR Code + banner; não use automaticamente `main` enquanto o MVP e a integração estiverem em PRs.

Para testar banner demonstrativo, ver [experimento PR 8](https://github.com/sistemacodigolucrativo/QR-Studio/pull/8). `devDebug` usa unidades de teste oficiais; `prodRelease` não usa anúncios demonstrativos; App ID e unidades reais só entram quando o projeto estiver autorizado para `mode=live`. Intersticial e recompensado exigem pontos de exibição apropriados e validação antes de ativar.

[Contrato e limitações](docs/ADMOB-NATIVO.md) · [Console Universal Build](https://github.com/sistemacodigolucrativo/Universal-Build/blob/feat/console-admob-build-ux/docs/CONSOLE-UX-ADMOB-PROGRESSO.md).

## Como testar

A pipeline do template executa verificações Android, testes de unidade, lint, compilação de APK devDebug e AAB prodRelease não assinado. Acompanhe a aba Actions. A assinatura de produção exige secrets próprios, nunca incluídos neste repositório.

O template fonte é: https://github.com/sistemacodigolucrativo/Kotlin-Master-Boilerplate
O painel de build é: https://sistemacodigolucrativo.github.io/Universal-Build/

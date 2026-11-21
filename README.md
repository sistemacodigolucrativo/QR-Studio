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
- Eventual AdMob após decisão sobre consentimento, monetização e revisão da conta/app.

Os recursos de câmera, exportação de imagem e AdMob NÃO estão implementados nesta entrega.

## Como testar

A pipeline do template executa verificações Android, testes de unidade, lint, compilação de APK devDebug e AAB prodRelease não assinado. Acompanhe a aba Actions. A assinatura de produção exige secrets próprios, nunca incluídos neste repositório.

O template fonte é: https://github.com/sistemacodigolucrativo/Kotlin-Master-Boilerplate
O painel de build é: https://sistemacodigolucrativo.github.io/Universal-Build/

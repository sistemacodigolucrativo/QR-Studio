# Matriz de validação

## Comprovável via CI

- Validação de configuração central.
- Build nativo Android APK debug e AAB release (unsigned se sem secrets).
- Unit tests de caso de uso e ViewModel, lint, ktlint e detekt.
- Fontes em en e pt-BR e recursos básicos de acessibilidade.

## Testes manuais ainda necessários

| Categoria | Verificações |
|---|---|
| Android 7-16 | Instalação, abertura, mudança de orientação, navegação por gesto/botões |
| Samsung One UI e Xiaomi HyperOS | retomada após suspensão, bateria, status/navigation bar |
| OPPO, realme, Motorola, OnePlus | process death, retorno ao app, permissões |
| Tablets e dobráveis | largura compacta/expanded, rotação, redimensionamento |
| Acessibilidade | TalkBack, fonte ampliada, contrastes AA, 48dp de toque |
| Tema | claro/escuro, cores dinâmicas Android 12+ |
| Banco e rede | migrações, offline, autorização e TLS quando os módulos forem usados |

Não considerar plataformas nem ROMs testadas sem evidências de aparelhos. A base não pede permissões desnecessárias.

# AdMob nativo integrado ao Universal Build

O painel salva config/universal-build-admob.json em cada projeto Android. O Gradle valida e converte as opções em BuildConfig e recursos Android.

## Modos
- off ou ausência de arquivo: nenhum anúncio é requisitado.
- dev e staging: quando habilitado, sempre IDs oficiais de teste do Google (inclusive quando a configuração do console está em live).
- prod: ativa IDs reais somente quando o arquivo está em live, com App ID e unidades válidos; mode=test é convertido para off em produção.
- Google UMP atualiza consentimento a cada abertura. SDK Next-Gen só inicializa com permissão de solicitar anúncios.

## Formatos
- Banner: AdMobFooter() aparece no rodapé quando banner estiver ativo e houver consentimento.
- Intersticial: AdMobRuntime.showInterstitialAtNaturalBreak(activity) deve ser chamado apenas em pausa/transição natural; obedece ao cooldown.
- Recompensado: AdMobRuntime.showRewardedAfterOptIn(activity, onEarned) requer opt-in e não credita moedas automaticamente. Para saldo real é necessário backend com SSV e idempotência.
- Acesso às opções de privacidade é exibido quando UMP exigir.

O QR Studio atualmente não possui uma transição que justifique um intersticial nem carteira de moedas. Portanto não há disparo automático desses formatos na tela de geração de QR Codes. As APIs nativas ficam prontas para futuras telas apropriadas.

## Operação
Selecione o projeto Android no Universal Build, salve IDs e formatos e faça nova compilação. A alteração no GitHub não atualiza APK já instalado. App ID permanece no AndroidManifest para UMP e é também passado programaticamente ao Next-Gen SDK.

Fontes:
https://developers.google.com/admob/android/next-gen/quick-start
https://developers.google.com/admob/android/next-gen/privacy
https://developers.google.com/admob/android/next-gen/banner
https://developers.google.com/admob/android/next-gen/interstitial/single-load
https://developers.google.com/admob/android/next-gen/rewarded/single-load

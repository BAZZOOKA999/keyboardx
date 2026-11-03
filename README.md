# KeyboardX

**KeyboardX** é um teclado Android personalizável que reúne digitação, tradução de textos e ferramentas de escrita em um só lugar. Inclui um modo voltado ao **Termux**, com teclas extras para facilitar o uso do terminal pelo celular.

## Recursos

- Teclado QWERTY, números, símbolos e seletor de emojis.
- Temas escuros e ajustes de cores, tamanho das teclas, som e vibração.
- Modo Termux com atalhos e teclas para terminal e programação.
- Tradução de textos com ML Kit, com uso offline após baixar os modelos necessários.
- Digitação por voz usando o serviço de reconhecimento disponível no Android.
- Textos rápidos e painel de área de transferência, com histórico desativado por padrão.
- Ferramentas de IA para corrigir ortografia, reescrever, resumir, ajustar o tom e gerar respostas.

As ferramentas de IA dependem de um projeto Firebase AI Logic configurado. Este repositório não inclui essa configuração; por isso, a IA pode aparecer como indisponível no APK de teste. O reconhecimento de voz depende do serviço e dos idiomas disponíveis no aparelho.

## Baixar e ativar

[Baixar KeyboardX.apk](https://github.com/BAZZOOKA999/keyboardx/releases/download/apk-latest/KeyboardX.apk)

Requer **Android 7.0 ou superior**. O APK é uma versão de teste assinada com chave de depuração.

1. Baixe e instale o APK, permitindo a instalação pelo navegador quando solicitado.
2. Abra o KeyboardX e use a opção para ativar o teclado nas configurações do Android.
3. Selecione KeyboardX como teclado atual e ajuste suas preferências.

## Compilação

O GitHub Actions compila o APK e executa os testes existentes a cada atualização da branch `main`. O arquivo é publicado em Releases e nos artefatos da execução.

Para compilar localmente, use Java 21, Android SDK e Gradle 9.3.1. Crie `debug.keystore` na raiz com alias `androiddebugkey` e senhas `android`, depois execute:

```sh
gradle assembleDebug testDebugUnitTest
```

O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

# App-thusbiro — WA Robot Safe (Android)

Projeto Android local para organizar contatos e preparar mensagens para WhatsApp Business.

## Recursos
- Cadastro, edição e importação individual de contato pela agenda Android.
- Separação entre contatos pessoais e clientes.
- Registro de consentimento e evidência de autorização.
- Lista de bloqueio / opt-out.
- Personalização de mensagens com `{nome}` e `{telefone}`, com prévia.
- Fila e lembretes agendados.
- Modo de sequência assistida: ordena pendências por horário, permite abrir o próximo item manualmente e aplica o intervalo configurado entre aberturas enquanto estiver ativo.
- Pausa da sequência e registro local do horário da última conversa aberta.
- Backup local em JSON.
- Abertura da conversa no WhatsApp Business com o texto preenchido.

## Envio e conformidade
O app não envia mensagens em segundo plano, não simula cliques e não contorna proteções. O modo de sequência apenas organiza a fila e controla o intervalo entre aberturas de conversas; cada abertura depende de uma ação do usuário. O usuário revisa o conteúdo e toca manualmente em Enviar no WhatsApp. O intervalo não é um mecanismo de automação de disparo. Para contatos empresariais, registre consentimento e respeite pedidos de interrupção.

## Compilação automática do APK
O workflow **Build Android APK** está em `.github/workflows/android-apk.yml`.
1. Abra a aba **Actions** deste repositório.
2. Selecione **Build Android APK** e, se necessário, clique em **Run workflow** na branch `main`.
3. Aguarde a execução terminar com sucesso.
4. Abra a execução e baixe o artefato **WA-Robot-Safe-debug-apk**.
5. Extraia o ZIP do artefato e instale `app-debug.apk` no Android.

O APK gerado é debug para teste, não é uma versão assinada para publicação. Ainda é necessário testar no aparelho real, especialmente importação da agenda, cadastro, edição, personalização e abertura do WhatsApp Business.

## Compilação local
- JDK 17
- Android SDK 36
- Gradle 8.13
- Android Gradle Plugin 8.13.2
- Kotlin 2.2.21

Abra a raiz do projeto no Android Studio e use **Build > Build APK(s)**.

## Privacidade
O manifesto não solicita permissão ampla para ler a agenda. O seletor nativo permite escolher um contato específico. Os dados do app ficam no armazenamento local do WebView. Exporte um backup antes de limpar os dados ou desinstalar.

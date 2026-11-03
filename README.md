# WA Robot Safe — projeto Android local

## Recursos
- Cadastro de contatos pessoais e clientes.
- Registro de consentimento e evidência.
- Lista de bloqueio / opt-out.
- Mensagens personalizadas com {nome}.
- Fila de mensagens e lembretes agendados.
- Intervalo configurável apenas como lembrete operacional.
- Abertura da conversa oficial por `wa.me`, com mensagem preenchida.
- Confirmação final obrigatória pelo usuário no WhatsApp.
- Dados locais no WebView e exportação de backup JSON.

## Limite de conformidade
O app NÃO envia mensagens em segundo plano, não simula cliques e não contorna proteções. A Central de Ajuda do WhatsApp diz para não enviar mensagens automáticas ou em massa no WhatsApp. A Política Business exige consentimento para contato empresarial e respeito a opt-out. Um intervalo não torna o disparo não autorizado permitido.

## Compilar APK
1. Extraia o ZIP e abra a pasta `WA-Robot-Safe-Android` no Android Studio.
2. Use JDK 17 e instale o Android SDK indicado pelo Gradle.
3. Aguarde a sincronização.
4. Build > Build APK(s).
5. APK debug: `app/build/outputs/apk/debug/app-debug.apk`.

Este ambiente não dispõe do Android SDK para compilar e testar um APK. O arquivo entregue é o projeto-fonte.

## Privacidade
O app não solicita internet, contatos nem localização. Os dados ficam no armazenamento local do WebView. Exporte um backup antes de limpar dados ou desinstalar.

## Atualização de revisão
- Cadastro brasileiro agora exibe +55 e acrescenta o código do país automaticamente.
- Aceita DDD + número com 10 ou 11 dígitos; também aceita número completo iniciado em 55.
- A abertura é direcionada ao pacote do WhatsApp Business (`com.whatsapp.w4b`). Se ele não estiver instalado, o app informa o usuário.
- O envio continua manual: o usuário revisa e toca em Enviar no WhatsApp.
- Ainda é necessário compilar e testar em aparelho Android real; este ZIP continua sendo projeto-fonte, não APK.

## Revisão 3 — importar da agenda
- Adicionado botão "Escolher da agenda" no cadastro.
- Usa o seletor nativo de contatos do Android (`ActivityResultContracts.PickContact`).
- O usuário escolhe um contato individualmente; o app não solicita permissão para ler toda a agenda.
- Nome e telefone são preenchidos no formulário; o usuário confirma e salva.
- Números brasileiros com +55 são normalizados para DDD + número no campo, mantendo o prefixo visual +55.
- Requer AndroidX Activity com suporte a `PickContact`; validar compilação e comportamento em aparelho real.

## Revisão 4 — edição de contatos
- Adicionado botão "Editar contato" na lista interna.
- O formulário é preenchido com os dados atuais e passa a salvar alterações no mesmo registro/ID.
- Contatos já vinculados à fila continuam associados ao registro editado.
- Validação impede telefone duplicado e impede salvar números bloqueados.
- Incluído botão para cancelar a edição.
- Ainda requer compilação e testes no Android real.

## Gerar o APK pelo GitHub Actions (sem Android Studio)
1. Abra a aba **Actions**. O workflow `Build Android APK` inicia após o push; também pode ser iniciado em **Run workflow**.
2. Quando terminar com sucesso, abra a execução e baixe o artefato `WA-Robot-Safe-debug-apk`.
3. Extraia o artefato e instale `app-debug.apk` no Android. O APK debug é para teste, não é uma versão de distribuição assinada para publicação.
4. Antes de uso real, teste instalação, cadastro, agenda, edição, personalização e abertura do WhatsApp Business.

O workflow usa Java 17, Android SDK e Gradle 8.13 no runner do GitHub. Nenhuma chave ou segredo é necessário para compilar o APK debug.

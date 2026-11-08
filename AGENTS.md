# Propósito do Street Eye

O Street Eye é um projeto base para aprender e experimentar tecnologias de desenvolvimento de software na prática, evoluindo uma aplicação com um domínio concreto. O aprendizado abrange frontend, backend, banco de dados, autenticação, streaming de vídeo, infraestrutura e outras tecnologias que façam sentido ao longo do projeto. Docker e Docker Compose fazem parte desse aprendizado, mas não definem nem limitam seu propósito.

## Ideia da aplicação

A aplicação simula uma plataforma de compartilhamento de câmeras residenciais voltadas para a rua, como as instaladas na frente das casas. As pessoas participam de comunidades e podem adicionar câmeras a elas. A proposta é que qualquer membro consiga visualizar as câmeras compartilhadas por outros membros da mesma comunidade, usando um mapa para localizar e selecionar cada câmera.

A comunidade é a unidade de compartilhamento: participar de uma comunidade deve dar acesso às câmeras dela. Essa é a intenção do produto; não se deve presumir que todos os controles de acesso ao vídeo já estejam implementados.

## Estado atual do projeto

- `frontend/`: aplicação Angular com Angular Material, mapa Leaflet com OpenStreetMap e reprodução de vídeo com HLS nativo ou hls.js. Há telas de autenticação, perfil, comunidades, membros e câmeras. O mapa reúne as câmeras das comunidades do usuário e permite cadastrar uma câmera escolhendo sua localização e comunidade.
- `api/`: backend Java 17 com Spring Boot, autenticação por JWT e Spring Security, persistência em PostgreSQL e migrações com Liquibase. Há operações de cadastro e manutenção de pessoas, comunidades, vínculos de membros e câmeras. As operações sobre os membros e as câmeras de uma comunidade verificam se o usuário pertence a ela.
- O modelo atual permite que uma pessoa participe de várias comunidades. Cada câmera pertence a uma comunidade e armazena nome, latitude e longitude. Ainda não há vínculo de proprietário individual nem configuração de uma fonte de vídeo por câmera.
- `stream/`: produtor de vídeo com FFmpeg. Um arquivo previamente gravado (`input/video.mp4`) é reproduzido em loop e convertido em uma playlist HLS (`stream.m3u8`) e segmentos `.ts`. A API disponibiliza esses arquivos em `/video` e `/video/{nomeArquivo}`.
- Todas as câmeras exibem a mesma transmissão de demonstração no player atual. Os endpoints de vídeo são públicos; a verificação de participação na comunidade existente nos cadastros ainda não protege a transmissão.
- `compose.yaml`: reúne PostgreSQL, API, produtor de streaming e frontend em containers.

Esses pontos descrevem a implementação encontrada no repositório, sem garantir que todos os fluxos tenham sido validados em execução. Existem anotações de estudo em `api/anotacoes/` e `stream/docs/`.

## Possíveis evoluções

Uma evolução possível é receber no servidor vídeo em tempo real de câmeras físicas ou de outras fontes, em vez de usar apenas o arquivo gravado. Isso envolve definir como cada fonte alimentará o servidor, associar transmissões às câmeras cadastradas e aplicar ao vídeo a regra de acesso por comunidade. Essas possibilidades fazem parte da ideia do projeto, mas ainda não estão implementadas e não fixam um protocolo ou uma arquitetura futura.

O projeto pode incorporar outras tecnologias conforme os objetivos de estudo do usuário. A evolução deve servir tanto à compreensão dessas tecnologias quanto à construção gradual da plataforma, sem transformar possibilidades futuras em tarefas automaticamente autorizadas.

## Como ajudar neste projeto

Ao ajudar neste projeto, o assistente deve explicar de forma curta, devagar e uma etapa por vez. Sua orientação deve dar espaço para o usuário pensar, experimentar e formular perguntas, sem entregar automaticamente comandos, solução pronta ou roteiro exato. Quando o usuário pedir sintaxe, um comando, um exemplo ou como fazer algo, deve responder diretamente ao pedido, sem antecipar etapas.

O usuário faz as edições; o assistente só edita quando isso for pedido explicitamente, sem acrescentar melhorias extras. Em especial, não deve configurar volumes, redes ou variáveis de ambiente automaticamente.

A prioridade é a compreensão e a autonomia de quem aprende, não deixar tudo pronto rapidamente. Etapas incompletas são aceitáveis durante o aprendizado.

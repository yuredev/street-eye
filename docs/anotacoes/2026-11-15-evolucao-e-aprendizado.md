# Evolução do Street Eye com aprendizado na prática

Atualizado em **15/11/2026**, a partir do roteiro de 08/11/2026.

O usuário já estudou aplicações multimódulo e integração contínua. Este roteiro registra o que existe e sugere os próximos estudos, incluindo ambientes, segredos, deploy, OAuth2, mensageria e orquestração. As etapas futuras não são funcionalidades implementadas nem tarefas automaticamente autorizadas.

## O que já aprendemos e temos no projeto

- **Multimódulo:** a API foi organizada nos módulos Maven `autenticacao`, `aplicacao` e `video`, reunidos pelo `api/pom.xml`. São módulos da mesma aplicação, não três microsserviços independentes.
- **CI:** existe o workflow `.github/workflows/ci-api.yml`, disparado por PRs destinados à `main`. Ele busca o código, prepara Java 17 e executa `mvn test` na pasta `api`.
- **Testes:** há testes de autenticação com Mockito. O teste padrão `contextLoads`, que carregava o contexto completo e exigia configuração de banco, foi removido nesta etapa. Isso não equivale a ter testes de integração com banco.
- **Execução local:** o Compose reúne PostgreSQL, API, produtor de vídeo e frontend. Isso ainda não representa uma configuração de produção.

A CI atual cobre a API; compilar e testar o frontend continua sendo uma extensão possível. O workflow não faz deploy. Os conceitos e a investigação da primeira falha estão em [Anotações sobre CI/CD](ci-cd.md).

## Ordem sugerida

Ambientes e segredos entram **agora, antes do primeiro deploy**. Primeiro se aprende a publicar a aplicação atual manualmente; depois se automatiza esse processo. Mensageria fica depois dessa base, para não misturar a primeira publicação com a criação de outro serviço.

| Ordem | Estudo | Situação e aplicação no Street Eye |
|---|---|---|
| 1 | **Aplicação multimódulo** | Já estudado e aplicado na organização da API. |
| 2 | **CI com GitHub Actions** | Já estudado e aplicado aos testes da API em PRs para `main`. |
| 3 | **Ambientes dev/prod e segredos** | Próximo estudo: separar configurações, dados, credenciais e recursos dos ambientes; definir o que pode ir para o Git. |
| 4 | **Primeiro deploy manual e segurança de exposição** | Publicar uma versão em ambiente de estudo, entender HTTPS, portas, acesso ao banco e persistência. Antes de usar dados reais, resolver as permissões de vídeo. |
| 5 | **CD com GitHub Actions** | Automatizar build, publicação de imagens e atualização do ambiente, com segredos de deploy e possibilidade de voltar à versão anterior. |
| 6 | **OAuth2 e OpenID Connect** | Aprender login com provedor de identidade e proteção da API, aproveitando a base de ambientes, URLs e segredos. |
| 7 | **Mensageria e um segundo serviço** | Criar notificações a partir de eventos de câmeras adicionadas, com aplicações executadas independentemente. |
| 8 | **Kubernetes ou Swarm** | Estudar orquestração depois de dominar a execução e o deploy dos serviços com Compose. |

Essa ordem é uma sugestão de aprendizado, não uma dependência técnica rígida. O primeiro experimento de OAuth2 pode ser local e não exige concluir CD; ele aparece nessa posição para aproveitar a compreensão de callbacks, HTTPS e credenciais por ambiente.

## Próximo estudo: separar dev e prod

Vamos considerar apenas **dev** e **prod**, sem homologação. Ambiente não é sinônimo de branch: uma branch pode gerar um artefato, enquanto o ambiente define onde e com quais configurações ele executa.

O objetivo é usar o mesmo código e, quando viável, a mesma imagem, fornecendo a configuração adequada na execução. Aprender profiles do Spring e configuração externa ajuda a entender essa separação; profiles não protegem segredos por si só. [Configuração externa do Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)

Pontos para estudar aos poucos:

- **Dados e recursos:** bancos, usuários do banco, arquivos de vídeo e credenciais separados; testes e experimentos de dev não devem acessar prod.
- **Configuração:** URLs da API e do frontend, conexão com banco, origens permitidas e nível de logs por ambiente. Nem toda configuração é secreta.
- **Comportamento:** dev pode ter ferramentas e mensagens de diagnóstico que não devem ser expostas em prod.
- **Inicialização:** entender como a aplicação recebe seus valores e como deve falhar se faltar um segredo obrigatório em prod, sem recorrer a uma senha padrão de desenvolvimento.

Não é necessário escolher agora uma ferramenta complexa de infraestrutura nem criar uma branch permanente para cada ambiente.

## O que deve ir para o repositório e o que deve ficar fora

O repositório pode conter código, migrações, receitas de build/deploy e exemplos de configuração com nomes das variáveis e valores fictícios. Os valores reais devem ser fornecidos fora do Git.

| Informação | Tratamento a estudar |
|---|---|
| Senha do banco, chave privada ou segredo de assinatura JWT, client secret OAuth e credenciais de deploy | Manter fora do repositório e fornecer apenas ao processo que precisa delas. |
| Arquivo local com valores reais, como `.env` | Não versionar; um arquivo de exemplo pode documentar os campos. `.env` é um arquivo de texto, não um cofre. |
| URL pública da aplicação, identificador OAuth do cliente e chave pública de verificação | Podem ser públicos quando o protocolo e o uso permitirem; não confundir com segredos. |
| Configurações do Angular entregues ao navegador | Considerar públicas, inclusive valores inseridos durante o build. Segredos do servidor não pertencem ao frontend. |
| Logs, imagens de container, relatórios e artefatos de CI | Evitar incluir senhas, tokens, arquivos privados ou valores reais de configuração. |

O estudo deve incluir como injetar segredos em runtime, limitar permissões e trocar credenciais. Variáveis de ambiente são um mecanismo de fornecimento; a segurança também depende de quem acessa o host, o processo e a plataforma.

O Compose atual contém credenciais de desenvolvimento versionadas. Antes de prod, revisar também arquivos de configuração e a origem da chave JWT. Credenciais de produção devem ser próprias e não reutilizar valores já publicados. Adicionar ao `.gitignore` não remove arquivos já rastreados nem segredos do histórico; um segredo real exposto precisa ser revogado ou trocado.

No GitHub Actions, estudar a diferença entre variáveis comuns e **Secrets**, além de um Environment `prod` para separar os valores e restringir o acesso no job de deploy. A disponibilidade das proteções depende do plano e da visibilidade do repositório. A CI de PR não deve precisar de credenciais de produção. [Secrets do Actions](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets), [Environments e proteções](https://docs.github.com/en/actions/how-tos/deploy/configure-and-manage-deployments/manage-environments)

## Primeiro deploy: entender antes de automatizar

O primeiro deploy pode usar um servidor de estudo com Compose, sem Kubernetes. O aprendizado é saber qual versão está rodando, como ela recebe configuração e como observar e recuperar sua execução.

Separar frontend e API públicos dos recursos internos: banco e administração da infraestrutura não precisam de portas abertas à internet. Estudar HTTPS, proxy reverso, acesso administrativo restrito, logs sem segredos, persistência, backup e restauração do banco.

O objetivo não é tornar tudo invisível: a URL da aplicação e o código enviado ao navegador podem ser públicos. O que precisa permanecer privado são credenciais, dados e recursos sem autorização. Guardar segredos fora do Git é uma parte da segurança, não uma garantia de que prod está segura.

No Street Eye há um ponto concreto antes de disponibilizar câmeras reais: `/video` e `/video/**` são públicos e todas as câmeras usam o mesmo stream de demonstração. Autenticar o usuário nos cadastros não protege automaticamente playlist e segmentos HLS. O estudo de produção deve incluir autorização do vídeo por comunidade; enquanto isso, usar apenas conteúdo de demonstração sem dados sensíveis.

Depois de compreender esse deploy manual, estudar CD: construir e identificar imagens, publicá-las em um registry, atualizar o servidor e verificar a saúde da aplicação. Aprender a voltar à imagem anterior e tratar migrações com cuidado, pois voltar uma imagem não desfaz automaticamente mudanças no banco.

## OAuth2 e OpenID Connect: proposta de aprendizado

**OAuth2** trata de autorização delegada; **OpenID Connect (OIDC)** acrescenta uma camada de identidade para login. JWT é um formato de token: ter login que emite JWT hoje não significa já implementar OAuth2/OIDC.

Para aprender no Street Eye, começar com um provedor pronto, sem construir um servidor de autorização próprio. Há dois experimentos possíveis, a fazer separadamente:

### Primeiro experimento: login externo com Google via backend

A sugestão inicial é experimentar OIDC com Google e o suporte OAuth2 Client do Spring Security. Isso permite observar o redirecionamento ao provedor, o retorno para uma URL de callback e a identificação do usuário. [OAuth2 Login no Spring Security](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html)

Uma abordagem a estudar é o backend conduzir o login e manter os tokens do provedor no servidor, expondo ao Angular uma sessão por cookie `HttpOnly`, com `Secure` em prod. Isso exige repensar conscientemente a configuração atual, que é stateless e usa JWT: não basta acrescentar um botão. Incluir proteção CSRF, política de cookies, expiração e logout no aprendizado.

Definir clientes e callbacks específicos para dev e prod. O client secret fica no backend, fora do Git; não no Angular. Usar Authorization Code com PKCE e as verificações adequadas de `state` e `nonce`, com suporte da biblioteca. Estudar essas proteções em vez de implementar o protocolo manualmente. [Boas práticas de segurança OAuth2, RFC 9700](https://www.rfc-editor.org/rfc/rfc9700.html)

Relacionar a identidade externa a uma pessoa do Street Eye usando o emissor e o identificador estável (`iss` e `sub`), sem assumir que um e-mail igual autoriza unir contas. Entender a diferença entre ID token, usado no login, e access token, destinado ao recurso autorizado. Não usar um token emitido para APIs do Google como se fosse autorização para a API do Street Eye.

### Segundo experimento: Keycloak e API como Resource Server

Depois, Keycloak é uma opção para controlar um provedor OIDC de estudo: criar um realm, clientes e usuários locais e observar a emissão de tokens. Ele próprio passa a ser mais um serviço para administrar. [OIDC no Keycloak](https://www.keycloak.org/securing-apps/oidc-layers)

Um experimento alternativo ao backend com sessão é ter Angular como cliente público usando Authorization Code com PKCE, sem client secret no navegador, e a API Spring como **Resource Server**. A API valida assinatura, emissor, validade e audiência esperada do access token. Estudar também o armazenamento e a renovação dos tokens no navegador e os riscos de XSS. [Resource Server JWT no Spring Security](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

Os dois desenhos são alternativas de estudo, não uma orientação para misturá-los nem para migrar tudo de uma vez. Em qualquer um, o provedor autentica a identidade; o Street Eye continua decidindo quais comunidades e câmeras essa pessoa pode acessar. Incluir testes de acesso negado e de usuário autenticado sem participação na comunidade.

As referências explicam os conceitos; ao implementar, conferir a documentação correspondente ao Spring Boot e Spring Security usados no projeto, sem presumir que exemplos da versão mais recente sejam diretamente compatíveis.

## Multimódulo e microsserviços

Multimódulo e microsserviços são coisas diferentes. Um projeto Maven pode ter vários módulos e gerar uma única aplicação. Microsserviços são aplicações executadas e implantadas independentemente.

A organização interna já foi explorada. A próxima decisão sobre serviços deve nascer de um fluxo concreto, depois de compreender ambientes e deploy.

No Street Eye, uma separação que merece investigação é entre cadastros de comunidades/câmeras e processamento de vídeo. As notificações continuam sendo uma boa primeira aplicação independente: a API poderia publicar um evento de câmera adicionada e outro serviço consumi-lo. Estudar falhas, tentativas e duplicidade de mensagens, sem presumir que publicar um evento e gravar no banco aconteçam atomicamente.

## Kubernetes ou Swarm

A sugestão é escolher Kubernetes como objetivo de estudo, depois de estar confortável executando os serviços pelo Compose.

Swarm seria uma alternativa para experimentar orquestração com uma transição menor a partir do Docker. Não há necessidade de estudar os dois inicialmente.

## Ponto de partida

A próxima pergunta a explorar é: **quais configurações do Street Eye mudam entre dev e prod, quais são secretas e de onde cada uma chega à aplicação hoje?**

Esse levantamento vem antes de editar configurações ou contratar infraestrutura. A prioridade continua sendo compreender cada decisão, uma etapa por vez, com o usuário realizando as configurações e experiências.

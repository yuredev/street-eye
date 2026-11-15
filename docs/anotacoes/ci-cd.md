# Anotações sobre CI/CD

Data das anotações: **14/11/2026**.

Registro das conversas sobre integração contínua, GitHub Actions e Jenkins. O foco foi CI; CD ainda não foi aprofundado. A primeira parte reúne conceitos e possibilidades; a seção de configuração registra o workflow criado pelo usuário no Street Eye.

## Integração contínua (CI)

CI significa *Continuous Integration*, ou integração contínua. É a prática de integrar pequenas mudanças ao código compartilhado com frequência e verificar se elas continuam funcionando juntas.

A automação ajuda a repetir essas verificações a cada mudança. Por exemplo, ao abrir um pull request (PR) que altera a autenticação do Street Eye, um processo pode compilar a API e executar os testes, exibindo o resultado no GitHub.

CI não significa publicar a aplicação. É possível começar apenas verificando a compilação e os testes. A entrega e a implantação contínuas, associadas à sigla CD, são assuntos para uma etapa posterior do estudo.

## Pipeline

Uma pipeline organiza as tarefas que a ferramenta deve executar. Um exemplo conceitual para a API seria:

```text
Buscar o código → Preparar o ambiente → Compilar → Executar os testes → Informar o resultado
```

Se uma verificação falhar, o resultado ajuda a identificar o problema. A automação verifica aquilo que foi configurado; passar nos testes não garante que toda a aplicação esteja correta.

## CI precisa de uma plataforma de DevOps?

Não. CI é uma prática, não uma plataforma específica. É possível automatizar as verificações em um computador próprio ou em um servidor da equipe, usando uma ferramenta como Jenkins ou scripts.

Executar testes manualmente antes de um commit é uma verificação local útil. Um processo de CI normalmente automatiza as verificações das mudanças destinadas ao repositório compartilhado. A prática também depende de integrar mudanças com frequência, não apenas de possuir uma ferramenta.

## O que é o Jenkins?

Jenkins é um servidor de automação: um software que pode ser instalado na própria máquina, em um servidor da equipe ou em uma máquina na nuvem. Ele tem uma interface web para configurar e acompanhar as tarefas.

Ele não é exclusivo para uso local. Também não fornece automaticamente uma máquina pronta ao criar uma conta: alguém precisa disponibilizar a infraestrutura e instalar o Jenkins, ou contratar um serviço de Jenkins hospedado.

O Jenkins coordena as tarefas. Elas podem executar na mesma máquina em que ele está instalado ou em outras máquinas conectadas a ele, chamadas agentes.

## Jenkins e pull requests no GitHub

É possível integrar Jenkins e GitHub para verificar um PR quando ele é aberto ou recebe novos commits. Um fluxo possível é:

1. O usuário abre ou atualiza um PR no GitHub.
2. O GitHub envia uma notificação ao Jenkins, chamada webhook.
3. O Jenkins busca o código e executa a pipeline configurada.
4. Com a integração e as permissões necessárias, o Jenkins informa o resultado ao GitHub, que o exibe no PR.

Nesse fluxo, o GitHub precisa conseguir alcançar o endereço que recebe o webhook. Uma instalação disponível apenas no computador local, sem acesso externo, não recebe diretamente essa notificação do GitHub.

## Jenkins e GitHub Actions

Os dois podem automatizar compilação, testes e publicação. São alternativas para problemas semelhantes, mas diferem na forma de oferecer e manter a infraestrutura.

| Aspecto | Jenkins | GitHub Actions |
| --- | --- | --- |
| Disponibilidade | Software independente que precisa ser instalado ou contratado como serviço hospedado. | Serviço integrado ao GitHub. |
| Administração da plataforma | Na instalação própria, fica sob responsabilidade de quem mantém o Jenkins. | A plataforma é administrada pelo GitHub. |
| Máquinas de execução | Você disponibiliza as máquinas ou configura recursos para os agentes. | Pode usar executores fornecidos pelo GitHub ou máquinas próprias. |
| Integração com PRs | Precisa ser configurada. | Pode ser configurada diretamente a partir dos eventos do GitHub. |

A comparação com Angular e React ajuda apenas no sentido de serem alternativas com propósitos semelhantes. Aqui, uma diferença central é a responsabilidade pela infraestrutura.

## O que significa manter a máquina de execução?

Compilar e executar testes exige processador, memória, disco e ferramentas instaladas em algum computador. Para a API do Street Eye, o ambiente precisa disponibilizar ferramentas como Java e Maven, além dos recursos exigidos pelos testes escolhidos.

Ao manter uma máquina própria para o Jenkins ou seus agentes, você cuida de pontos como:

- Disponibilidade da máquina quando as tarefas precisarem executar.
- Instalação e atualização das ferramentas necessárias.
- Espaço em disco e recursos suficientes para executar as tarefas.
- Investigação de falhas do ambiente que impeçam a execução.

Uma máquina na nuvem continua sendo um computador que precisa ser administrado. Contratar uma máquina virtual de um provedor não instala nem configura automaticamente o Jenkins nela.

No GitHub Actions, usando os executores hospedados padrão, o GitHub disponibiliza um ambiente temporário para cada job e o descarta depois. Você configura as etapas e as ferramentas necessárias, enquanto o GitHub mantém a infraestrutura do executor. Se optar por executores próprios, a manutenção dessas máquinas fica sob sua responsabilidade.

## Comparação com Firebase

Firebase é uma plataforma de serviços gerenciados: você cria um projeto e utiliza serviços cuja infraestrutura é administrada pelo Google.

Jenkins, por si só, é um software a ser instalado e operado. Ter uma interface web não significa que exista uma instância hospedada automaticamente para cada usuário.

Nesse aspecto, GitHub Actions se parece mais com Firebase: a plataforma já está disponível como serviço, e você configura seu uso. A comparação é sobre o modelo de disponibilização, pois as ferramentas têm funções diferentes.

## Aplicação ao Street Eye

O usuário criou `.github/workflows/ci-api.yml` para compilar e executar os testes da API em PRs destinados à `main`. O workflow não configura deploy.

## Estrutura do workflow no GitHub Actions

O GitHub reconhece arquivos YAML em `.github/workflows/`. O nome `ci-api.yml` é uma escolha do projeto.

```text
Workflow: CI da API
└── Job: testar-api
    ├── Step: buscar o código
    ├── Step: preparar Java 17
    └── Step: executar os testes
```

- **Workflow:** processo automatizado definido no arquivo.
- **Job:** grupo de etapas que compartilham um executor e seu ambiente de trabalho.
- **Step:** etapa que executa um comando ou uma action.
- **Runner:** máquina ou ambiente executor do job.

Isso já constitui uma pipeline de CI. Um workflow pode ter vários jobs, por exemplo, um para a API e outro para o frontend. Por padrão, jobs independentes podem executar em paralelo; os steps usados neste job executam em sequência.

## Gatilho e branch de destino

```yaml
on:
  pull_request:
    branches:
      - main
```

`on` define o gatilho. Por padrão, `pull_request` executa quando um PR é aberto, reaberto ou recebe novos commits. O filtro `branches` se refere à branch de destino: neste caso, PRs que querem integrar código na `main`, independentemente da branch de origem.

Esse filtro vale para o workflow inteiro. Um push isolado para a `main`, sem evento de PR, não dispara esse gatilho.

O arquivo precisa estar commitado e enviado ao GitHub na branch do PR. Os resultados ficam disponíveis nos checks do PR e na aba Actions, onde é possível abrir os logs de cada etapa.

## Escolha da máquina

```yaml
runs-on: ubuntu-latest
```

Escolhe um executor Ubuntu fornecido pelo GitHub. Também existem opções como `windows-latest` e `macos-latest`. A máquina da CI não precisa usar o mesmo sistema operacional do computador do desenvolvedor.

## Actions prontas e comandos

`uses` referencia uma action reutilizável, que pode ser mantida pelo GitHub, pela comunidade, por uma empresa ou pelo próprio usuário. `run` executa um comando escrito no workflow.

### Buscar o código

```yaml
- name: Buscar código do repositório
  uses: actions/checkout@v4
```

A máquina do job não recebe automaticamente os arquivos do projeto. A checkout os coloca na pasta de trabalho para as próximas etapas.

Simplificando, ela identifica a referência a verificar, busca os dados com Git e faz o checkout dessa versão. Por padrão, busca apenas um commit, em vez de todo o histórico. No evento `pull_request`, a referência padrão normalmente corresponde ao resultado de uma combinação de teste entre a branch do PR e a de destino.

A checkout prepara os arquivos; compilar e executar testes são tarefas de outras etapas.

### Preparar o Java

```yaml
- name: Preparar Java 17
  uses: actions/setup-java@v6
  with:
    distribution: temurin
    java-version: '17'
```

`with` fornece parâmetros à action. `temurin` escolhe a distribuição Eclipse Temurin; `'17'` escolhe a versão do Java usada pela API. A action disponibiliza o JDK e configura `JAVA_HOME` e `PATH` para os comandos seguintes.

### Versões das actions

Em `actions/setup-java@v6`, `v6` é a versão principal da action, não do Java. Cada action evolui separadamente da plataforma GitHub Actions.

O arquivo continua referenciando `v6` até ser alterado, mas esse marcador pode receber atualizações dentro da versão principal. Uma nova versão pode trazer funcionalidades, correções e mudanças de configuração.

Na comparação estudada entre setup-java v5 e v6, a v6 acrescentou suporte a distribuições, seleção de Java com `latest`, melhorias de cache e verificações de checksum quando disponíveis. Ambas atendem à configuração básica de Java 17 com Temurin. Isso não exige atualizar a versão do Java do projeto.

## Executar os testes com Maven

Não é necessária uma action específica de testes: o Maven pode ser chamado com `run`.

```yaml
- name: Executar testes da API
  working-directory: api
  run: mvn test
```

`working-directory` define a pasta de execução. O `pom.xml` em `api` reúne os módulos, e `mvn test` compila o código e executa os testes encontrados nesses módulos. Clicar para executar um teste na IDE pode executar apenas o teste selecionado; aqui o escopo é o conjunto dos módulos.

Se houver falha nos testes, o Maven retorna um código de erro e a etapa fica marcada como falha.

## Etapas após uma falha

Por padrão, uma etapa depende do sucesso das anteriores. Portanto, se `mvn test` falhar, um `echo` colocado depois será pulado.

Para emitir a mensagem final mesmo após falhas, foi usada a condição:

```yaml
- name: Informar fim
  if: always()
  run: echo "Fim da pipeline"
```

Essa condição não transforma a falha dos testes em sucesso. Ela permite executar a etapa de mensagem apesar do resultado anterior.

## Falha encontrada na primeira execução

O log enviado mostrou que os 10 testes de `AuthenticationControllerTest` passaram. A falha aconteceu em `StreetEyeBackendApplicationTests.contextLoads`, ao carregar o contexto Spring:

```text
Failed to determine a suitable driver class
```

A aplicação falhou ao preparar o acesso ao banco, antes de uma tentativa de conexão que resultasse em conexão recusada. No projeto, as configurações do banco são fornecidas pelo Compose. Executar `mvn test` diretamente no runner não inicia o Compose nem importa suas configurações automaticamente.

## Por que precisou de banco se os testes usam mocks?

Os mocks de `AuthenticationControllerTest` pertencem àquele teste. Eles não são compartilhados automaticamente com outras classes de teste.

Já o teste abaixo usava `@SpringBootTest`:

```java
@SpringBootTest
class StreetEyeBackendApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

Mesmo com o método vazio, o Spring tenta carregar o contexto antes de executá-lo. Isso inclui criar componentes, resolver suas dependências e configurar JPA e Liquibase, que dependem do banco.

Esse tipo de teste costuma acompanhar projetos gerados pelo Spring Initializr. Ele verifica se o contexto consegue iniciar, mas não verifica diretamente comportamentos como autenticar um usuário.

## Decisão sobre o contextLoads

O usuário removeu `api/aplicacao/src/test/java/yuredev/street_eye_backend/StreetEyeBackendApplicationTests.java` para manter o foco inicial nos testes com mocks e no aprendizado de CI.

Remover um arquivo de `src/test` não altera o funcionamento da aplicação, mas elimina essa verificação de inicialização. O teste tem valor quando existe um ambiente de testes preparado com as configurações e os serviços necessários.

Retomar essa verificação ao estudar testes de integração com banco é uma possibilidade futura, não uma tarefa automaticamente autorizada. A falha original foi diagnosticada; estas anotações não confirmam o resultado de uma execução posterior à remoção.

# Anotações sobre CI/CD

Data das anotações: **14/11/2026**.

Registro da conversa inicial sobre integração contínua, GitHub Actions e Jenkins. O foco desta conversa foi CI; CD ainda não foi aprofundado. Os exemplos abaixo são possibilidades para o Street Eye, não configurações implementadas.

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

Uma possibilidade inicial de estudo é verificar a compilação e os testes da API em PRs com GitHub Actions. Isso ainda precisa ser discutido e configurado pelo usuário, uma etapa por vez.

Estas anotações não definem uma pipeline, não configuram deploy e não tornam as possibilidades discutidas tarefas automaticamente autorizadas.

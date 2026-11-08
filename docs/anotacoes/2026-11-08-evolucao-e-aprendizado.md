# Evolução do Street Eye com aprendizado na prática

Data das anotações: **08/11/2026**.

Estas orientações registram a proposta discutida nesta data para estudar aplicações multimódulo, mensageria, microsserviços, CI/CD com GitHub Actions e orquestração com Kubernetes ou Docker Swarm. São possibilidades de evolução, não funcionalidades já implementadas nem um roteiro obrigatório.

## Ordem sugerida

Todos esses temas são viáveis no Street Eye. A ordem abaixo permite que cada etapa aproveite a anterior:

| Ordem | Estudo | Aplicação no Street Eye |
|---|---|---|
| 1 | **Aplicação multimódulo** | Organizar o backend em módulos Maven com responsabilidades claras, aprendendo dependências e separação de código. |
| 2 | **CI com GitHub Actions** | A cada PR, compilar e executar os testes existentes da API e do frontend. Ter uma verificação automática das próximas mudanças. |
| 3 | **Mensageria e um segundo serviço** | Criar o serviço de notificações, recebendo eventos de câmeras adicionadas. Aqui surge uma necessidade concreta de executar aplicações independentes. |
| 4 | **CD com GitHub Actions** | Construir e publicar as imagens dos serviços e automatizar a atualização de um ambiente de estudo. |
| 5 | **Kubernetes ou Swarm** | Gerenciar a execução dos serviços: configuração, comunicação, reinício após falhas e atualização das versões. |

## Multimódulo e microsserviços

Multimódulo e microsserviços são coisas diferentes. Um projeto Maven pode ter vários módulos e gerar uma única aplicação. Microsserviços são aplicações executadas e implantadas independentemente.

A proposta é explorar primeiro a organização interna da API, antes de decidir quais partes merecem virar serviços.

No Street Eye, uma separação que merece investigação é entre cadastros de comunidades/câmeras e processamento de vídeo. Já as notificações seriam uma boa primeira aplicação independente, porque têm um fluxo pequeno e compreensível.

## Kubernetes ou Swarm

A sugestão é escolher Kubernetes como objetivo de estudo, depois de estar confortável executando os serviços pelo Compose.

Swarm seria uma alternativa para experimentar orquestração com uma transição menor a partir do Docker. Não há necessidade de estudar os dois inicialmente.

## Começar o CD em um ambiente simples

O CD pode começar em um ambiente simples, antes de envolver Kubernetes. Assim, primeiro se aprende a publicar uma versão automaticamente e depois como o orquestrador a executa.

## Ponto de partida

A primeira pergunta a explorar é: **o que significa dividir a API em módulos e qual problema essa divisão resolveria nela?**

Isso dá uma base para evitar separar código apenas para aumentar a quantidade de projetos. A prioridade continua sendo compreender cada decisão e evoluir a aplicação gradualmente, com o usuário realizando as configurações e experiências.

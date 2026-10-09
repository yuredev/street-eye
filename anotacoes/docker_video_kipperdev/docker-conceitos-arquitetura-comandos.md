# Guia de Docker: Conceitos, Arquitetura e Comandos Fundamentais

## 1. O que é o Docker e por que ele foi criado?
O **Docker** é uma plataforma de virtualização a nível de sistema operacional que permite empacotar uma aplicação e todas as suas dependências dentro de uma unidade isolada chamada **contêiner**.

### A Analogia da Cozinha
Imagine um chefe de cozinha profissional que cria uma receita perfeita. Quando tenta reproduzir essa mesma receita em outra cozinha, o resultado falha porque os ingredientes variam, o forno aquece de maneira diferente ou utensílios estão ausentes. 

O Docker resolve esse problema garantindo **compatibilidade entre diferentes ambientes** (desenvolvimento, homologação e produção) e facilitando o **deploy** de aplicações. Ele elimina o clássico problema do *"na minha máquina funciona"*, pois o ambiente de execução é exatamente igual em qualquer máquina.

---

## 2. Docker (Contêineres) vs. Máquinas Virtuais (VMs)

| Característica | Máquinas Virtuais (VMs) | Contêineres Docker |
| :--- | :--- | :--- |
| **Sistema Operacional** | Exige a instalação de um SO completo por VM | Compartilha o SO da máquina hospedeira |
| **Leveza e Recursos** | Pesadas, consomem muita RAM, CPU e espaço em disco | Extremamente leves e rápidos para inicializar |
| **Isolamento de Recursos** | Garantido por um Hypervisor | Garantido por **Cgroups** e **Namespaces** do Kernel Linux |
| **Processo** | Roda como um sistema operacional inteiro | Roda como um processo isolado no hospedeiro |

### Como funciona o Isolamento do Kernel?
O Docker utiliza recursos nativos do kernel do Linux:
- **Namespaces**: Isolam a visão do contêiner em relação a interfaces de rede, PID de processos, montagens de sistemas de arquivos e variáveis de ambiente. Cada contêiner enxerga apenas a sua própria estrutura.
- **Cgroups (Control Groups)**: Gerenciam e limitam a alocação de recursos físicos (quantidade de memória RAM, uso de CPU, I/O) para cada contêiner de forma individual.

---

## 3. Taxonomia Básica do Docker
1. **Dockerfile**: Arquivo de texto que funciona como o blueprint ou projeto de arquitetura da aplicação. Contém todas as instruções e comandos necessários para construir a imagem.
2. **Imagem (Image)**: Snapshot estático e imutável que contém o código da aplicação, bibliotecas, dependências e configurações necessárias.
3. **Contêiner (Container)**: Instância em execução de uma imagem. É um processo ativo rodando de maneira isolada na máquina hospedeira. Podem existir várias instâncias ativas da mesma imagem.
4. **Registry / Hub**: Repositório central onde as imagens Docker são armazenadas e compartilhadas (ex.: Docker Hub, AWS ECR, Azure Container Registry).

---

## 4. Comandos Essenciais do Docker CLI

### Listagem e Diagnóstico de Contêineres
- `docker ps`: Lista apenas os contêineres que estão atualmente em execução.
- `docker ps -a`: Lista **todos** os contêineres da máquina (ativos e finalizados/exited).

### Execução e Ciclo de Vida
- `docker run hello-world`: Baixa a imagem de teste oficial do Docker Hub (se não existir localmente) e executa o contêiner.
- `docker run -it ubuntu bash`:
  - `-i` (interactive): Mantém o STDIN aberto para interação.
  - `-t` (tty): Aloca um terminal pseudo-TTY.
  - `bash`: Inicia o processo do shell Bash dentro do contêiner Ubuntu, mantendo o contêiner ativo enquanto o processo do Bash estiver rodando.
- `docker stop <CONTAINER_ID>`: Para a execução dos processos do contêiner de forma segura.
- `docker start <CONTAINER_ID>`: Reinicia a execução de um contêiner parado.
- `docker exec -it <CONTAINER_ID> bash`: Abre uma nova sessão interativa Bash dentro de um contêiner que já está em execução.

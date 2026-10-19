# Aprendizado sobre Docker

## Conceitos iniciais

- **Dockerfile:** receita usada pelo Docker para construir uma imagem.
- **Imagem:** pacote pronto que contém os arquivos, programas e configurações necessários.
- **Container:** uma instância em execução de uma imagem.

Uma comparação simples:

```text
Dockerfile -> receita
Imagem     -> resultado pronto da receita
Container  -> resultado sendo executado
```

## Função de um Dockerfile

O `Dockerfile` descreve como uma imagem deve ser construída. O Docker lê suas instruções de cima para baixo.

No container de streaming, o fluxo é:

```text
FROM -> começa com Alpine Linux
RUN  -> instala o FFmpeg
RUN  -> cria a pasta de saída
COPY -> coloca o vídeo na imagem
CMD  -> inicia o FFmpeg quando o container começar
```

### Resumo das instruções

- `FROM`: escolhe a imagem que será usada como base.
- `RUN`: executa um comando durante a construção da imagem.
- `COPY`: copia um arquivo do projeto para dentro da imagem.
- `CMD`: define o comando padrão executado quando o container inicia.

Exemplo:

```dockerfile
FROM alpine:3.22

RUN apk add --no-cache ffmpeg

RUN mkdir -p /output

COPY ["static/videos/video.mp4", "/input/video.mp4"]

CMD ["ffmpeg", "-i", "/input/video.mp4", "/output/video.m3u8"]
```

`RUN` e `CMD` são executados em momentos diferentes:

```text
RUN -> durante o docker build, quando a imagem é construída
CMD -> durante o docker run, quando o container é iniciado
```

## Construção da imagem

Um `Dockerfile` não é executado diretamente. Primeiro, ele é usado para construir uma imagem:

```text
Dockerfile -> docker build -> imagem
Imagem     -> docker run   -> container
```

Antes de executar os comandos, é necessário:

- estar com o Docker Desktop aberto;
- abrir o PowerShell na pasta que contém o `Dockerfile`;
- confirmar que os arquivos usados pelo `COPY` realmente existem.

### Comando para construir a imagem

```powershell
docker build -t street-eye-stream .
```

Significado de cada parte:

- `docker`: programa de linha de comando usado para conversar com o Docker.
- `build`: solicita a construção de uma imagem.
- `-t`: abreviação de `--tag`; permite atribuir um nome e, opcionalmente, uma versão à imagem.
- `street-eye-stream`: nome escolhido para a imagem.
- `.`: representa a pasta atual e define o contexto da construção.

O contexto da construção é o conjunto de arquivos que o Docker pode acessar durante o `build`. Por isso, um caminho usado em `COPY`, como `static/videos/evangelion.mp4`, é procurado a partir da pasta representada pelo `.`.

Como não foi informada uma versão depois do nome, o Docker usa automaticamente a tag `latest`:

```text
street-eye-stream:latest
```

Uma tag funciona como um rótulo de versão. Um exemplo com uma versão explícita seria:

```powershell
docker build -t street-eye-stream:1.0 .
```

### Onde a imagem fica

A imagem não aparece como um arquivo dentro do projeto. Ela fica no armazenamento interno administrado pelo Docker Desktop.

Para listar as imagens:

```powershell
docker image ls
```

Também é possível listar apenas a imagem criada:

```powershell
docker image ls street-eye-stream
```

Para consultar seus detalhes:

```powershell
docker image inspect street-eye-stream
```

Se o `Dockerfile` ou o vídeo copiado com `COPY` mudar, a imagem deve ser construída novamente:

```powershell
docker build -t street-eye-stream .
```

Reconstruir uma imagem não atualiza automaticamente um container que já foi criado. Para usar a imagem nova, o container antigo precisa ser removido e um novo precisa ser criado.

## Criação e execução do container

Para manter o streaming executando em segundo plano:

```powershell
docker run -d `
  --name street-eye-stream `
  --restart unless-stopped `
  --mount "type=bind,source=$($PWD.Path)\static\videos,target=/output" `
  street-eye-stream
```

No PowerShell, a crase no final da linha:

```powershell
`
```

indica que o comando continua na próxima linha. O comando acima também poderia ser escrito inteiro em uma única linha.

### `docker run`

```powershell
docker run
```

Cria um container novo a partir de uma imagem e inicia esse container. Isso é diferente de `docker start`, que apenas inicia novamente um container que já existe.

### `-d`

```powershell
-d
```

É a abreviação de `--detach`. Faz o container executar em segundo plano e devolve o controle do terminal ao usuário.

Sem `-d`, o terminal fica conectado à saída do FFmpeg até o container ser encerrado.

### `--name street-eye-stream`

```powershell
--name street-eye-stream
```

- `--name`: define um nome para o container.
- `street-eye-stream`: nome escolhido para o container.

O nome facilita comandos posteriores, como `docker stop street-eye-stream`. O nome do container pode ser igual ao nome da imagem, mas eles continuam sendo objetos diferentes.

### `--restart unless-stopped`

```powershell
--restart unless-stopped
```

Define a política de reinicialização do container:

- reinicia o container se o processo encerrar inesperadamente;
- volta a iniciá-lo quando o serviço do Docker for iniciado novamente;
- não o reinicia automaticamente depois que o usuário o interrompe de propósito com `docker stop`.

### `--mount`

```powershell
--mount "type=bind,source=$($PWD.Path)\static\videos,target=/output"
```

Compartilha uma pasta do computador com o container. As partes separadas por vírgulas têm estas funções:

- `type=bind`: informa que será usada uma pasta real do computador.
- `source=...`: informa qual pasta do computador será compartilhada.
- `target=/output`: informa onde essa pasta aparecerá dentro do container.

No PowerShell:

- `$PWD`: representa a pasta atual;
- `$PWD.Path`: obtém o caminho completo da pasta atual;
- `$($PWD.Path)`: avalia essa expressão dentro do texto do comando;
- `\static\videos`: acrescenta a pasta dos vídeos ao caminho atual.

O resultado é um compartilhamento como este:

```text
Computador                         Container
static/videos        <--------->  /output
```

Quando o FFmpeg grava `/output/stream.m3u8` dentro do container, o arquivo aparece em `static/videos/stream.m3u8` no computador.

Como os arquivos estão em uma pasta compartilhada, eles continuam no computador mesmo que o container seja removido.

### Último `street-eye-stream`

```powershell
street-eye-stream
```

O último elemento do comando é o nome da imagem usada para criar o container. Neste caso, equivale a:

```text
street-eye-stream:latest
```

### Por que não existe `-p` nesse comando

O argumento `-p` seria usado para publicar uma porta do container. Ele não é necessário nesse container porque o FFmpeg apenas gera arquivos e não disponibiliza uma API HTTP.

Neste projeto, a API Spring é responsável por ler os arquivos gerados e entregá-los ao frontend.

## Acompanhamento e controle do container

Listar somente containers em execução:

```powershell
docker ps
```

Listar containers em execução e parados:

```powershell
docker ps -a
```

Ver continuamente a saída do FFmpeg:

```powershell
docker logs -f street-eye-stream
```

- `logs`: mostra a saída produzida pelo programa do container.
- `-f`: abreviação de `--follow`; continua acompanhando novas mensagens.

Para sair do acompanhamento dos logs sem parar o container, usar `Ctrl + C`.

Parar o container:

```powershell
docker stop street-eye-stream
```

Iniciar novamente o mesmo container:

```powershell
docker start street-eye-stream
```

Reiniciar o container:

```powershell
docker restart street-eye-stream
```

### Recriar o container depois de mudar a imagem

O Docker não permite criar dois containers com o mesmo nome. Se `street-eye-stream` já existir, outro `docker run --name street-eye-stream ...` apresentará um erro de conflito de nome.

Depois de alterar o Dockerfile, o fluxo completo é:

```powershell
docker build -t street-eye-stream .
docker stop street-eye-stream
docker rm street-eye-stream
docker run -d `
  --name street-eye-stream `
  --restart unless-stopped `
  --mount "type=bind,source=$($PWD.Path)\static\videos,target=/output" `
  street-eye-stream
```

- `docker build`: constrói a nova imagem.
- `docker stop`: encerra o container antigo.
- `docker rm`: remove o container antigo, que já está parado.
- `docker run`: cria um container novo usando a imagem atualizada.

Remover o container não apaga a imagem nem os arquivos mantidos na pasta compartilhada `static/videos`.

## Containers temporários e `--rm`

Para um teste descartável, pode-se usar:

```powershell
docker run --rm street-eye-stream
```

`--rm` manda o Docker remover automaticamente o container quando seu processo terminar. Ele não remove:

- a imagem Docker;
- o Dockerfile;
- o vídeo original;
- arquivos gravados em uma pasta compartilhada com `--mount`.

O `--rm` evita acumular containers parados depois de testes rápidos. Ele não deve ser combinado com `--restart` e não é adequado para o container permanente de streaming.

Sem `--rm`, o container parado permanece listado em `docker ps -a` e pode ser iniciado novamente com `docker start`.

## Diferença entre os comandos principais

```text
docker build   -> constrói uma imagem usando o Dockerfile
docker image ls -> lista as imagens existentes
docker run     -> cria um container novo e já o inicia
docker ps      -> lista os containers em execução
docker ps -a   -> lista containers em execução e parados
docker logs    -> mostra a saída do programa do container
docker stop    -> para um container existente
docker start   -> inicia novamente um container existente
docker restart -> para e inicia novamente um container
docker rm      -> remove um container parado
```

## Função do Docker Compose

O Docker Compose serve para descrever e iniciar um ou mais containers em conjunto por meio de um arquivo YAML, normalmente chamado `compose.yaml`.

Ele pode registrar, por exemplo:

- quais containers devem existir;
- qual imagem ou Dockerfile cada container usa;
- quais portas devem ser disponibilizadas;
- quais pastas ou volumes serão compartilhados;
- quais containers devem ser reiniciados automaticamente.

Uma comparação resumida:

```text
Dockerfile     -> ensina a construir uma imagem
Docker Compose -> organiza como os containers serão executados
```

O Docker Compose não substitui o Dockerfile. Um pode construir a imagem, enquanto o outro usa essa imagem para criar e configurar o container.

### Elementos comuns do Compose

- `services`: lista os containers da aplicação.
- `build`: informa onde está o Dockerfile usado para construir uma imagem.
- `image`: informa uma imagem pronta que deve ser utilizada.
- `ports`: liga uma porta do computador a uma porta do container.
- `volumes`: compartilha ou preserva arquivos usados pelos containers.
- `restart`: define quando o Docker deve reiniciar um container.

Não é necessário usar Docker Compose para executar apenas um container durante os primeiros testes. Ele se torna útil quando é preciso executar e configurar vários containers juntos.

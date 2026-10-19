# Dockerfile, Mapeamento de Portas e Deploy em VPS Ubuntu

## 1. Construção de um Dockerfile Multi-Stage (Spring Boot / Java)

Para otimizar o tamanho final da imagem e separar o ambiente de compilação (build) do ambiente de execução (runtime), utiliza-se a técnica de **Multi-Stage Build**.

```dockerfile
# Estágio 1: Build da Aplicação
FROM maven:3.8.4-jdk-8 AS build

# Copiar arquivos do projeto para dentro do contêiner
COPY src /app/src
COPY pom.xml /app

# Definir diretório de trabalho
WORKDIR /app

# Executar a compilação e gerar o arquivo .jar executável
RUN mvn clean install

# Estágio 2: Runtime da Aplicação
FROM openjdk:8-jre-alpine

# Copiar o arquivo .jar compilado do estágio de build
COPY --from=build /app/target/springboot-hello-world-1.0-SNAPSHOT.jar /app/app.jar

# Definir diretório de trabalho no runtime
WORKDIR /app

# Declarar a porta que a aplicação expõe
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "app.jar"]
```

### Comandos de Build Local:
```bash
# Construir a imagem com nome e tag
docker build -t springboot-hello-world:1.0 .
```

---

## 2. Isolamento de Rede e Mapeamento de Portas

Como cada contêiner possui sua própria interface de rede isolada, acessar `localhost:8080` no computador hospedeiro não direciona o tráfego para a porta `8080` do contêiner sem um mapeamento explícito.

### Modos de Mapeamento:
1. **Mapeamento Automático (`-P`)**:
   ```bash
   docker run -P springboot-hello-world:1.0
   ```
   O Docker associa a porta exposta (`EXPOSE 8080`) a uma porta aleatória e disponível no host (ex.: `55000 -> 8080`).
2. **Mapeamento Manual (`-p`)**:
   ```bash
   docker run -p 8080:8080 springboot-hello-world:1.0
   ```
   Mapeia explicitamente a porta `8080` da máquina hospedeira para a porta `8080` do contêiner.

---

## 3. Publicação de Imagens no Docker Hub

1. **Autenticação**:
   - Gerar um *Access Token* nas configurações de segurança do Docker Hub.
   - Autenticar via terminal:
     ```bash
     docker login -u <SEU_USUARIO>
     ```
2. **Tag da Imagem**:
   ```bash
   docker build -t <SEU_USUARIO>/springboot-hello-world:1.0 .
   ```
3. **Push para o Registry**:
   ```bash
   docker push <SEU_USUARIO>/springboot-hello-world:1.0
   ```

---

## 4. Deploy em VPS Ubuntu (Hostinger)

### 4.1 Instalação do Docker na VPS via SSH
Conexão SSH com a VPS:
```bash
ssh root@<IP_DA_VPS>
```

Instalação do Docker no Ubuntu:
```bash
sudo apt update && sudo apt upgrade -y
sudo apt-get install ca-certificates curl gnupg lsb-release -y
# Adicionar repositório oficial e chave GPG do Docker
sudo apt install docker-ce -y
```

### 4.2 Problema de Compatibilidade de Arquitetura de CPU (ARM64 vs AMD64)
Ao tentar executar na VPS uma imagem construída em uma máquina Mac (arquitetura **ARM64**), ocorre o seguinte erro:
> `the requested image's platform (linux/arm64) does not match the detected host platform (linux/amd64)`

**Causa**: Os contêineres compartilham o kernel da máquina hospedeira. A VPS Ubuntu utiliza a arquitetura de CPU **AMD64 (x86_64)**, tornando incompatível a execução direta da imagem ARM64.

**Solução**: Realizar o build Cross-Platform especificando a arquitetura alvo (`--platform linux/amd64`):
```bash
# Build direcionado para a plataforma da VPS
docker build --platform linux/amd64 -t <SEU_USUARIO>/springboot-hello-world:1.0-amd64 .

# Push da imagem compatível para o Docker Hub
docker push <SEU_USUARIO>/springboot-hello-world:1.0-amd64
```

### 4.3 Execução Final na VPS
Na VPS Ubuntu, execute a imagem compilada para AMD64 com o mapeamento de porta:
```bash
docker run -d -p 8080:8080 <SEU_USUARIO>/springboot-hello-world:1.0-amd64
```
A aplicação ficará acessível publicamente via navegador através do endereço `http://<IP_DA_VPS>:8080/hello`.

# Stream de vídeo

## **FFMPEG**

É um programa capaz de ser rodado em CLI que cria streams de audio e vídeo,  
é uma ferramenta que converte um formato de vídeo em outro.  
Podemos usá-lo para criar streams de vídeo a partir de uma entrada.  
O **FFMPEG** pode dividir o vídeo em várias pequenas partes para podermos enviá-las pela web. É muito útilo quando trabalhamos com **HLS** para streaming.\\

## **HLS**

HTTP live streaming (HLS) é um protocolo de streaming de vídeo que permite que clientes se adaptem a mudanças nas condições da rede aumentando ou diminuindo a qualidade de transmissão. É usado tanto para streaming ao vivo quanto sob demanda.\\

O HLS divide arquivos de vídeo em arquivos HTTP menores para download. Os clientes carregam esses arquivos e reproduzem em forma de vídeo.  
O HLS foi desenvolvido pela Apple para ser usado nos produtos da empresa, mas atualmente vem sendo usado em uma ampla variedade de dispositivos.

Um streaming HLS se origina do servidor no qual (no caso de streaming sob demanda) o arquivo de mídia está armazenado ou no qual (no caso de transmissão ao vivo) o streaming está sendo criado. Como o HLS é baseado em HTTP, qualquer servidor web comum pode originar o streaming.

### **Regras defninidas pelo HLS**

1.  Segmentação do conteúdo
    *   O vídeo deve ser dividido em partes pequenas da mídia, geralmenet de 2 a 10 segundos.
    *   O padrão é usar arquivos **.ts (Transport Stream),** mas também é possível outros formatos como **.fmp4**.
2.  Arquivo de playlist **.m3u8**
    *   A playlist é um arquivo **.m3u8**
    *   Ela lista os arquivos e informa ao cliente/frontend qual a ordem correta de reprodução, duração dos segmentos e outras regras como qualidade alternativa

O comando usando **FFMPEG** abaixo usa o vídeo demoninado "16\_Doença\\ até\\ a\\ morte.mp4" (Episódio de Evangelion) como fonte a ser dividida em partes do vídeo de poucos segundos **.ts** (Transport Stream)

Podemos usá-lo se acessarmos o WSL (O Windows nao possui o ffmpeg)

```shell
ffmpeg -re -stream_loop -1 -i 16_Doença\ até\ a\ morte.mp4 -c:v libx264 -c:a aac -f hls -hls_time 4 -hls_list_size 5 -hls_flags delete_segments+append_list+discont_start -hls_segment_filename "%03d.ts" -hls_base_url "/video/" stream.m3u8
```

## Explicação por partes

### Entrada e leitura

*   `-re`: lê o arquivo de entrada em **tempo real**, ou seja, simula a taxa de bits como se fosse uma live (útil para streaming).
*   `-stream_loop -1`: faz **loop infinito** do vídeo de entrada.

### Codificação

*   `-c:v libx264`: codifica o vídeo usando o codec [**H.264 (libx264)**](https://www.cloudflare.com/pt-br/learning/video/what-is-h264-avc/)
*   `-c:a aac`: codifica o áudio usando o codec [**AAC**](https://canaltech.com.br/software/O-que-e-AAC/).

### Formato de saída

*   `-f hls`: define o **formato de saída** como **HLS (HTTP Live Streaming)**, que é o padrão da Apple para streaming de vídeo.
*   `-hls_time 4`: cada segmento `.ts` gerado terá **4 segundos de duração**.
*   `-hls_list_size 5`: o arquivo `stream.m3u8` (playlist) terá no máximo **5 segmentos listados** de cada vez.
*   `-hls_flags delete_segments`: **deleta automaticamente** os arquivos `.ts` antigos à medida que novos segmentos são gerados (isso ajuda a economizar espaço).
*   `-hls_segment_filename "segment_%03d.ts"`: define o **padrão de nome** dos segmentos gerados. Ex: `segment_000.ts`, `segment_001.ts`, etc.
*   `stream.m3u8`: é o **arquivo de saída**, a playlist HLS que players usam para reproduzir os segmentos `.ts`.

### Testando nosso stream

Existe uma forma de testar nosso stream disponibilizado pelo site da biblioteca do HLS, lá existe um player onde só colocamos o link de nossa playlist e ele exibe o streaming
[HLS.JS Demo](https://hlsjs.video-dev.org/)
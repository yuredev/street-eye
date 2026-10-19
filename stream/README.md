# Stream

Gera HLS a partir de um MP4 em loop. O FFmpeg produz arquivos; a API entrega a playlist e os segmentos em `/video`.

- `Dockerfile`: imagem do produtor.
- `input/video.mp4`: vídeo original, preservado e renomeado durante a migração.
- `output/`: playlist `.m3u8` e segmentos `.ts`, incluindo os arquivos existentes antes da migração.
- `run.sh`: execução local com FFmpeg instalado (`sh stream/run.sh`, a partir da raiz).
- `docs/stream_de_video.md`: notas sobre HLS.

## Docker Compose

A partir da raiz, `docker compose up --build -d stream` constrói e inicia o produtor. Nenhuma porta é necessária no produtor.

O vídeo e as saídas permanecem no computador, mas não são versionados. Para construir a imagem em outro checkout, forneça `input/video.mp4`.

O FFmpeg mantém apenas os segmentos recentes conforme sua configuração. Os arquivos antigos foram preservados nesta migração; iniciar o produtor voltará a atualizar a playlist e segmentos.

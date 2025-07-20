CREATE TABLE usuario_comunidade (
    id varchar(255) NOT NULL,
    id_usuario varchar(255) NOT NULL,
    id_comunidade varchar(255) NOT NULL,
    CONSTRAINT usuario_comunidade_pkey PRIMARY KEY (id),
    CONSTRAINT fk_id_usuario 
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id),
    CONSTRAINT fk_id_comunidade
        FOREIGN KEY (id_comunidade) 
        REFERENCES comunidade(id)
);

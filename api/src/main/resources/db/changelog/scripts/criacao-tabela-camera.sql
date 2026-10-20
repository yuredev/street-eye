CREATE TABLE camera (
    id varchar(255) PRIMARY KEY,
    id_comunidade varchar(255) NOT NULL REFERENCES comunidade(id),
    nome varchar(255) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL
);

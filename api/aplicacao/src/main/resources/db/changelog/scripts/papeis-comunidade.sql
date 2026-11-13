ALTER TABLE usuario_comunidade ADD COLUMN papel varchar(20);
UPDATE usuario_comunidade SET papel = 'ADMINISTRADOR';
ALTER TABLE usuario_comunidade ALTER COLUMN papel SET NOT NULL;
ALTER TABLE usuario_comunidade ALTER COLUMN papel SET DEFAULT 'MEMBRO';
ALTER TABLE usuario_comunidade ADD CONSTRAINT ck_usuario_comunidade_papel
    CHECK (papel IN ('ADMINISTRADOR', 'MEMBRO'));

-- Consolida eventuais vínculos repetidos antes de garantir sua unicidade.
DELETE FROM usuario_comunidade a USING usuario_comunidade b
WHERE a.id_usuario = b.id_usuario AND a.id_comunidade = b.id_comunidade AND a.id > b.id;
ALTER TABLE usuario_comunidade ADD CONSTRAINT uk_usuario_comunidade
    UNIQUE (id_usuario, id_comunidade);

-- public."_user" definition

-- Drop table

-- DROP TABLE public."_user";

CREATE TABLE usuario (
	id varchar(255) NOT NULL,
	login varchar(255) NULL,
	"senha" varchar(255) NULL,
	"papel" int2 NULL,
	CONSTRAINT "_usuario_pkey" PRIMARY KEY (id),
	CONSTRAINT "_usuario_papel_check" CHECK (((papel >= 0) AND (papel <= 1)))
);
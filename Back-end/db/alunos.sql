CREATE TABLE planos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(25) NOT NULL,
    duracao_meses INTEGER NOT NULL,
    PRECO NUMERIC(10, 2) NOT NULL
);

CREATE TABLE alunos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    id_plano INTEGER NOT NULL,
    FOREIGN KEY (id_plano) REFERENCES planos(id)
);
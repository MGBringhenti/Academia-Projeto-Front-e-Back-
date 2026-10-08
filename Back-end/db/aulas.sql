CREATE TABLE professores (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    especializacao VARCHAR(50) NOT NULL
);

CREATE TABLE aulas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    dias_semana VARCHAR(100) NOT NULL,
    capacidade INTEGER NOT NULL CHECK (capacidade > 0),
    horario TIME NOT NULL,
    id_professor INTEGER NOT NULL,
    FOREIGN KEY (id_professor) REFERENCES professores(id)
);

CREATE TABLE matriculas (
    id_aluno INTEGER NOT NULL,
    id_aula INTEGER NOT NULL,
    data_inscricao DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativa',

    PRIMARY KEY (id_aluno, id_aula),
    FOREIGN KEY (id_aula) REFERENCES aulas(id),
    CHECK (status IN ('Ativa', 'Cancelada'))
);

CREATE TABLE checkin (
    id SERIAL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_aluno INTEGER NOT NULL,
    id_aula INTEGER NOT NULL,

    FOREIGN KEY (id_aluno, id_aula)
        REFERENCES matriculas(id_aluno, id_aula)
);
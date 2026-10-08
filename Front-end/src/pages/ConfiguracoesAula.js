const BASE = '/api/aula-service'
const ALUNOS = '/api/alunos-service/alunos'

function nomeAluno(dados, id) {
  return dados.alunos?.find((aluno) => aluno.idAluno === id)?.nome
    ?? `Aluno #${id}`
}

function nomeAula(dados, id) {
  return dados.aulas?.find((aula) => aula.idAula === id)?.nomeAula
    ?? `Aula #${id}`
}

function nomeProfessor(dados, id) {
  return dados.professores?.find(
    (professor) => professor.idProfessor === id,
  )?.nomeProfessor ?? `Professor #${id}`
}

function formatarData(valor) {
  if (!valor) return '—'

  const [data, horario] = valor.replace('T', ' ').split(' ')
  const [ano, mes, dia] = data.split('-')

  return `${dia}/${mes}/${ano}${horario ? ` ${horario.slice(0, 8)}` : ''}`
}

const campoAluno = {
  nome: 'idAluno',
  rotulo: 'Aluno',
  tipo: 'select',
  opcoes: (dados) =>
    dados.alunos.map((aluno) => ({
      valor: aluno.idAluno,
      texto: `${aluno.nome} — #${aluno.idAluno}`,
    })),
}

const campoAula = {
  nome: 'idAula',
  rotulo: 'Aula',
  tipo: 'select',
  opcoes: (dados) =>
    dados.aulas.map((aula) => ({
      valor: aula.idAula,
      texto: `${aula.nomeAula} — ${aula.horarioAula}`,
    })),
}

export const configuracoesAula = {
  professores: {
    titulo: 'Professores',
    descricao: 'Cadastre a equipe e mantenha os contatos atualizados.',
    singular: 'professor',
    endpoint: `${BASE}/professores`,
    dependencias: {},
    chave: (item) => String(item.idProfessor),
    caminho: (item) => String(item.idProfessor),
    identificar: (item) => item.nomeProfessor,
    campos: [
      { nome: 'nomeProfessor', rotulo: 'Nome', maxLength: 100 },
      {
        nome: 'emailProfessor',
        rotulo: 'E-mail',
        tipo: 'email',
        maxLength: 50,
      },
      {
        nome: 'cpf',
        rotulo: 'CPF',
        maxLength: 11,
        pattern: '[0-9]{11}',
        dica: 'Informe os 11 números, sem pontuação.',
      },
      {
        nome: 'especializacao',
        rotulo: 'Especialização',
        maxLength: 50,
      },
    ],
    preparar: (formulario) => ({ ...formulario }),
    colunas: [
      { titulo: 'Nome', valor: (item) => item.nomeProfessor },
      { titulo: 'E-mail', valor: (item) => item.emailProfessor },
      { titulo: 'Especialização', valor: (item) => item.especializacao },
    ],
  },

  aulas: {
    titulo: 'Aulas',
    descricao: 'Organize horários, professores e capacidade das turmas.',
    singular: 'aula',
    endpoint: `${BASE}/aulas`,
    dependencias: {
      professores: `${BASE}/professores`,
    },
    chave: (item) => String(item.idAula),
    caminho: (item) => String(item.idAula),
    identificar: (item) => item.nomeAula,
    campos: [
      { nome: 'nomeAula', rotulo: 'Nome da aula', maxLength: 100 },
      { nome: 'diasSemana', rotulo: 'Dias da semana', maxLength: 100 },
      {
        nome: 'capacidadeAula',
        rotulo: 'Capacidade',
        tipo: 'number',
        min: 1,
        step: 1,
      },
      {
        nome: 'horarioAula',
        rotulo: 'Horário',
        tipo: 'time',
        step: 1,
      },
      {
        nome: 'idProfessor',
        rotulo: 'Professor',
        tipo: 'select',
        opcoes: (dados) =>
          dados.professores.map((professor) => ({
            valor: professor.idProfessor,
            texto: professor.nomeProfessor,
          })),
      },
    ],
    preparar: (formulario) => ({
      ...formulario,
      capacidadeAula: Number(formulario.capacidadeAula),
      idProfessor: Number(formulario.idProfessor),
    }),
    colunas: [
      { titulo: 'Aula', valor: (item) => item.nomeAula },
      { titulo: 'Dias', valor: (item) => item.diasSemana },
      { titulo: 'Horário', valor: (item) => item.horarioAula },
      { titulo: 'Capacidade', valor: (item) => item.capacidadeAula },
      {
        titulo: 'Professor',
        valor: (item, dados) => nomeProfessor(dados, item.idProfessor),
      },
    ],
  },

  matriculas: {
    titulo: 'Matrículas',
    descricao: 'Matricule alunos e acompanhe cancelamentos e reativações.',
    singular: 'matrícula',
    endpoint: `${BASE}/matriculas`,
    dependencias: {
      alunos: ALUNOS,
      aulas: `${BASE}/aulas`,
    },
    chave: (item) => `${item.idAluno}/${item.idAula}`,
    caminho: (item) => `${item.idAluno}/${item.idAula}`,
    identificar: (item, dados) =>
      `${nomeAluno(dados, item.idAluno)} em ${nomeAula(dados, item.idAula)}`,
    campos: [campoAluno, campoAula],
    camposEdicao: [
      {
        nome: 'status',
        rotulo: 'Status',
        tipo: 'select',
        opcoes: () => [
          { valor: 'Ativa', texto: 'Ativa' },
          { valor: 'Cancelada', texto: 'Cancelada' },
        ],
      },
    ],
    preparar: (formulario, editando) =>
      editando
        ? { status: formulario.status }
        : {
            idAluno: Number(formulario.idAluno),
            idAula: Number(formulario.idAula),
          },
    colunas: [
      {
        titulo: 'Aluno',
        valor: (item, dados) => nomeAluno(dados, item.idAluno),
      },
      {
        titulo: 'Aula',
        valor: (item, dados) => nomeAula(dados, item.idAula),
      },
      {
        titulo: 'Inscrição',
        valor: (item) => formatarData(item.dataInscricao),
      },
      { titulo: 'Status', valor: (item) => item.status },
    ],
  },

  checkin: {
    titulo: 'Check-in',
    descricao: 'Registre a presença de alunos com matrícula ativa.',
    singular: 'check-in',
    endpoint: `${BASE}/checkin`,
    permiteEdicao: false,
    dependencias: {
      alunos: ALUNOS,
      aulas: `${BASE}/aulas`,
      matriculas: `${BASE}/matriculas`,
    },
    chave: (item) => String(item.idCheckIn),
    caminho: (item) => String(item.idCheckIn),
    identificar: (item, dados) =>
      `${nomeAluno(dados, item.idAluno)} em ${nomeAula(dados, item.idAula)}`,
    campos: [
      {
        nome: 'matricula',
        rotulo: 'Matrícula ativa',
        tipo: 'select',
        opcoes: (dados) =>
          dados.matriculas
            .filter((matricula) => matricula.status === 'Ativa')
            .map((matricula) => ({
              valor: `${matricula.idAluno}/${matricula.idAula}`,
              texto:
                `${nomeAluno(dados, matricula.idAluno)} — ` +
                nomeAula(dados, matricula.idAula),
            })),
      },
    ],
    preparar: (formulario) => {
      const [idAluno, idAula] = formulario.matricula.split('/').map(Number)
      return { idAluno, idAula }
    },
    colunas: [
      {
        titulo: 'Aluno',
        valor: (item, dados) => nomeAluno(dados, item.idAluno),
      },
      {
        titulo: 'Aula',
        valor: (item, dados) => nomeAula(dados, item.idAula),
      },
      {
        titulo: 'Data e horário',
        valor: (item) => formatarData(item.dataHoraCheckIn),
      },
    ],
  },
}
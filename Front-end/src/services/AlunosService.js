export async function listarAlunos(signal) {
  const resposta = await fetch('/api/alunos-service/alunos', {
    signal,
  })

  if (!resposta.ok) {
    throw new Error(
      `Não foi possível carregar os alunos. Código HTTP: ${resposta.status}`,
    )
  }

  const dados = await resposta.json()

  if (!Array.isArray(dados)) {
    throw new Error('A API retornou uma resposta inesperada.')
  }

  return dados
}

export async function listarPlanos(signal) {
  const resposta = await fetch('/api/alunos-service/planos', { signal })

  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os planos.')
  }

  return resposta.json()
}

export async function cadastrarAluno(aluno, usuario, senha) {
  // Converte as credenciais para o formato do HTTP Basic.
  const bytes = new TextEncoder().encode(`${usuario}:${senha}`)
  const credenciais = btoa(
    Array.from(bytes, (byte) => String.fromCharCode(byte)).join(''),
  )

  const resposta = await fetch('/api/alunos-service/alunos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Basic ${credenciais}`,
    },
    body: JSON.stringify(aluno),
  })

  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null)

    if (resposta.status === 401) {
      throw new Error('Usuário ou senha inválidos.')
    }

    throw new Error(
      erro?.mensagem ?? `Não foi possível cadastrar. HTTP ${resposta.status}`,
    )
  }

  return resposta.json()
}

async function alterarAluno(metodo, id, aluno, usuario, senha) {
  const bytes = new TextEncoder().encode(`${usuario}:${senha}`)
  const credenciais = btoa(
    Array.from(bytes, (byte) => String.fromCharCode(byte)).join(''),
  )

  const resposta = await fetch(`/api/alunos-service/alunos/${id}`, {
    method: metodo,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Basic ${credenciais}`,
    },
    ...(aluno !== null && { body: JSON.stringify(aluno) }),
  })

  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null)

    if (resposta.status === 401) {
      throw new Error('Usuário ou senha inválidos.')
    }

    throw new Error(
      erro?.mensagem ?? `Não foi possível concluir. HTTP ${resposta.status}`,
    )
  }

  if (resposta.status === 204) return

  return resposta.json()
}

export function atualizarAluno(id, aluno, usuario, senha) {
  return alterarAluno('PUT', id, aluno, usuario, senha)
}

export function excluirAluno(id, usuario, senha) {
  return alterarAluno('DELETE', id, null, usuario, senha)
}
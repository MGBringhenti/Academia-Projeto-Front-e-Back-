const URL = '/api/alunos-service/planos'

async function verificarResposta(resposta) {
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

function criarCabecalhos(usuario, senha) {
  const bytes = new TextEncoder().encode(`${usuario}:${senha}`)
  const credenciais = btoa(
    Array.from(bytes, (byte) => String.fromCharCode(byte)).join(''),
  )

  return {
    'Content-Type': 'application/json',
    Authorization: `Basic ${credenciais}`,
  }
}

export async function listarPlanos(signal) {
  const resposta = await fetch(URL, { signal })
  const dados = await verificarResposta(resposta)

  if (!Array.isArray(dados)) {
    throw new Error('A API retornou uma lista de planos inválida.')
  }

  return dados
}

export async function salvarPlano(id, plano, usuario, senha) {
  const resposta = await fetch(id == null ? URL : `${URL}/${id}`, {
    method: id == null ? 'POST' : 'PUT',
    headers: criarCabecalhos(usuario, senha),
    body: JSON.stringify(plano),
  })

  return verificarResposta(resposta)
}

export async function excluirPlano(id, usuario, senha) {
  const resposta = await fetch(`${URL}/${id}`, {
    method: 'DELETE',
    headers: criarCabecalhos(usuario, senha),
  })

  return verificarResposta(resposta)
}
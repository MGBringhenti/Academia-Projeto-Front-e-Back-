export async function consultarLista(url, signal) {
  const resposta = await fetch(url, { signal })

  if (!resposta.ok) {
    throw new Error(`Não foi possível carregar os dados. HTTP ${resposta.status}`)
  }

  const dados = await resposta.json()

  if (!Array.isArray(dados)) {
    throw new Error('A API não retornou uma lista válida.')
  }

  return dados
}

export async function enviarOperacao(url, metodo, dados, usuario, senha) {
  const bytes = new TextEncoder().encode(`${usuario}:${senha}`)
  const credenciais = btoa(
    Array.from(bytes, (byte) => String.fromCharCode(byte)).join(''),
  )

  const resposta = await fetch(url, {
    method: metodo,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Basic ${credenciais}`,
    },
    ...(dados !== null && { body: JSON.stringify(dados) }),
  })

  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null)

    if (resposta.status === 401) {
      throw new Error('Usuário ou senha da API de aulas inválidos.')
    }

    if (resposta.status === 409) {
      throw new Error(
        erro?.mensagem ??
          'Conflito: verifique duplicidades, vagas e registros vinculados.',
      )
    }

    throw new Error(
      erro?.mensagem ??
        erro?.detail ??
        `Não foi possível concluir a operação. HTTP ${resposta.status}`,
    )
  }

  if (resposta.status === 204) return null

  return resposta.json()
}
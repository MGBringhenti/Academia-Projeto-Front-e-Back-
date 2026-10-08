import { useEffect, useState } from 'react'
import { listarAlunos, excluirAluno } from '../services/AlunosService'
import FormularioAluno from '../components/FormularioAluno'

function AlunosPage() {
  const [alunos, setAlunos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [alunoEmEdicao, setAlunoEmEdicao] = useState(null)
  const [alunoParaExcluir, setAlunoParaExcluir] = useState(null)
  const [usuarioExclusao, setUsuarioExclusao] = useState('')
  const [senhaExclusao, setSenhaExclusao] = useState('')
  const [excluindo, setExcluindo] = useState(false)
  const [erroExclusao, setErroExclusao] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function carregarAlunos() {
      try {
        const dados = await listarAlunos(controller.signal)

        if (!controller.signal.aborted) {
          setAlunos(dados)
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setErro(error.message)
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false)
        }
      }
    }

    carregarAlunos()

    return () => controller.abort()
  }, [])

  function salvarAlunoNaLista(aluno) {
    setAlunos((anteriores) => {
      const existe = anteriores.some(
        (item) => item.idAluno === aluno.idAluno,
      )

      return existe
        ? anteriores.map((item) =>
            item.idAluno === aluno.idAluno ? aluno : item,
          )
        : [...anteriores, aluno]
    })

    setMensagem(
      alunoEmEdicao
        ? 'Aluno atualizado com sucesso!'
        : 'Aluno cadastrado com sucesso!',
    )

    setAlunoEmEdicao(null)
  }

  function prepararExclusao(aluno) {
    setAlunoParaExcluir(aluno)
    setUsuarioExclusao('')
    setSenhaExclusao('')
    setErroExclusao('')
    setMensagem('')
  }

  async function confirmarExclusao(event) {
    event.preventDefault()

    if (excluindo || !alunoParaExcluir) return

    setExcluindo(true)
    setErroExclusao('')

    try {
      await excluirAluno(
        alunoParaExcluir.idAluno,
        usuarioExclusao,
        senhaExclusao,
      )

      setAlunos((anteriores) =>
        anteriores.filter(
          (aluno) => aluno.idAluno !== alunoParaExcluir.idAluno,
        ),
      )

      if (alunoEmEdicao?.idAluno === alunoParaExcluir.idAluno) {
        setAlunoEmEdicao(null)
      }

      setAlunoParaExcluir(null)
      setUsuarioExclusao('')
      setSenhaExclusao('')
      setMensagem('Aluno excluído com sucesso!')
    } catch (error) {
      setErroExclusao(error.message)
    } finally {
      setExcluindo(false)
    }
  }

  function cancelarExclusao() {
    setAlunoParaExcluir(null)
    setUsuarioExclusao('')
    setSenhaExclusao('')
    setErroExclusao('')
  }

  return (
    <>
      {mensagem && (
        <p className="mensagem-sucesso" role="status">
          {mensagem}
        </p>
      )}

      {!carregando && !erro && !alunoParaExcluir && (
        <FormularioAluno
          key={alunoEmEdicao?.idAluno ?? 'novo'}
          alunoEmEdicao={alunoEmEdicao}
          onAlunoSalvo={salvarAlunoNaLista}
          onCancelarEdicao={() => setAlunoEmEdicao(null)}
        />
      )}

      {alunoParaExcluir && (
        <section className="painel">
          <h2>Confirmar exclusão</h2>

          <p>
            Deseja excluir o aluno{' '}
            <strong>{alunoParaExcluir.nome}</strong>?
            {' '}Essa ação não pode ser desfeita pela interface.
          </p>

          <form onSubmit={confirmarExclusao}>
            <fieldset className="formulario" disabled={excluindo}>
              <legend>Autenticação na API de alunos</legend>

              <label>
                Usuário
                <input
                  value={usuarioExclusao}
                  onChange={(event) =>
                    setUsuarioExclusao(event.target.value)
                  }
                  autoComplete="username"
                  required
                />
              </label>

              <label>
                Senha
                <input
                  type="password"
                  value={senhaExclusao}
                  onChange={(event) =>
                    setSenhaExclusao(event.target.value)
                  }
                  autoComplete="current-password"
                  required
                />
              </label>
            </fieldset>

            {erroExclusao && (
              <p className="mensagem-erro" role="alert">
                {erroExclusao}
              </p>
            )}

            <div className="acoes">
              <button
                type="submit"
                className="botao-perigo"
                disabled={excluindo}
              >
                {excluindo ? 'Excluindo...' : 'Confirmar exclusão'}
              </button>

              <button
                type="button"
                className="botao-secundario"
                disabled={excluindo}
                onClick={cancelarExclusao}
              >
                Cancelar
              </button>
            </div>
          </form>
        </section>
      )}

      <section className="painel">
        <h2>Alunos</h2>

        {carregando && (
          <p role="status">Carregando alunos...</p>
        )}

        {erro && (
          <p className="mensagem-erro" role="alert">
            {erro}
          </p>
        )}

        {!carregando && !erro && alunos.length === 0 && (
          <p>Nenhum aluno cadastrado.</p>
        )}

        {!carregando && !erro && alunos.length > 0 && (
          <div className="tabela-container">
            <table>
              <caption>Alunos cadastrados</caption>

              <thead>
                <tr>
                  <th scope="col">ID</th>
                  <th scope="col">Nome</th>
                  <th scope="col">E-mail</th>
                  <th scope="col">ID do plano</th>
                  <th scope="col">Ações</th>
                </tr>
              </thead>

              <tbody>
                {alunos.map((aluno) => (
                  <tr key={aluno.idAluno}>
                    <td>{aluno.idAluno}</td>
                    <td>{aluno.nome}</td>
                    <td>{aluno.email}</td>
                    <td>{aluno.idPlano ?? 'Sem plano'}</td>

                    <td>
                      <div className="acoes">
                        <button
                          type="button"
                          disabled={Boolean(alunoParaExcluir)}
                          onClick={() => {
                            setAlunoEmEdicao(aluno)
                            setMensagem('')
                          }}
                        >
                          Editar
                        </button>

                        <button
                          type="button"
                          className="botao-perigo"
                          disabled={Boolean(alunoParaExcluir)}
                          onClick={() => prepararExclusao(aluno)}
                        >
                          Excluir
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  )
}

export default AlunosPage
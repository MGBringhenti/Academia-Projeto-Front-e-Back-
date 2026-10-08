import { useEffect, useState } from 'react'
import {
  listarPlanos,
  salvarPlano,
  excluirPlano,
} from '../services/PlanosService'

const formularioVazio = {
  nome: '',
  duracaoMeses: '',
  preco: '',
}

const moeda = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

function PlanosPage() {
  const [planos, setPlanos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erroLista, setErroLista] = useState('')
  const [formulario, setFormulario] = useState({ ...formularioVazio })
  const [idEmEdicao, setIdEmEdicao] = useState(null)
  const [planoParaExcluir, setPlanoParaExcluir] = useState(null)
  const [usuario, setUsuario] = useState('')
  const [senha, setSenha] = useState('')
  const [ocupado, setOcupado] = useState(false)
  const [erro, setErro] = useState('')
  const [mensagem, setMensagem] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function carregar() {
      try {
        const dados = await listarPlanos(controller.signal)

        if (!controller.signal.aborted) {
          setPlanos(dados)
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setErroLista(error.message)
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false)
        }
      }
    }

    carregar()

    return () => controller.abort()
  }, [])

  function atualizarCampo(event) {
    const { name, value } = event.target

    setFormulario((anterior) => ({
      ...anterior,
      [name]: value,
    }))
  }

  function cancelar() {
    setIdEmEdicao(null)
    setPlanoParaExcluir(null)
    setFormulario({ ...formularioVazio })
    setSenha('')
    setErro('')
    setMensagem('')
  }

  function editar(plano) {
    setIdEmEdicao(plano.idPlano)
    setPlanoParaExcluir(null)
    setFormulario({
      nome: plano.nome,
      duracaoMeses: String(plano.duracaoMeses),
      preco: String(plano.preco),
    })
    setSenha('')
    setErro('')
    setMensagem('')
  }

  function prepararExclusao(plano) {
    setPlanoParaExcluir(plano)
    setSenha('')
    setErro('')
    setMensagem('')
  }

  async function enviar(event) {
    event.preventDefault()

    if (ocupado) return

    setOcupado(true)
    setErro('')
    setMensagem('')

    try {
      if (planoParaExcluir) {
        await excluirPlano(planoParaExcluir.idPlano, usuario, senha)

        setPlanos((anteriores) =>
          anteriores.filter(
            (plano) => plano.idPlano !== planoParaExcluir.idPlano,
          ),
        )

        setMensagem('Plano excluído com sucesso!')
      } else {
        const dados = {
          nome: formulario.nome.trim(),
          duracaoMeses: Number(formulario.duracaoMeses),
          preco: Number(formulario.preco),
        }

        const planoSalvo = await salvarPlano(
          idEmEdicao,
          dados,
          usuario,
          senha,
        )

        setPlanos((anteriores) =>
          idEmEdicao == null
            ? [...anteriores, planoSalvo]
            : anteriores.map((plano) =>
                plano.idPlano === idEmEdicao ? planoSalvo : plano,
              ),
        )

        setMensagem(
          idEmEdicao == null
            ? 'Plano cadastrado com sucesso!'
            : 'Plano atualizado com sucesso!',
        )
      }

      setIdEmEdicao(null)
      setPlanoParaExcluir(null)
      setFormulario({ ...formularioVazio })
      setSenha('')
    } catch (error) {
      setErro(error.message)
    } finally {
      setOcupado(false)
    }
  }

  return (
    <>
      <section className="painel">
        <h2>
          {planoParaExcluir
            ? 'Excluir plano'
            : idEmEdicao == null
              ? 'Cadastrar plano'
              : 'Editar plano'}
        </h2>

        <form onSubmit={enviar}>
          {planoParaExcluir ? (
            <p>
              Deseja excluir o plano{' '}
              <strong>{planoParaExcluir.nome}</strong>?
              Essa ação não pode ser desfeita pela interface.
            </p>
          ) : (
            <fieldset className="formulario" disabled={ocupado}>
              <legend>Dados do plano</legend>

              <label>
                Nome
                <input
                  name="nome"
                  value={formulario.nome}
                  onChange={atualizarCampo}
                  maxLength={25}
                  required
                />
              </label>

              <label>
                Duração em meses
                <input
                  type="number"
                  name="duracaoMeses"
                  value={formulario.duracaoMeses}
                  onChange={atualizarCampo}
                  min="1"
                  step="1"
                  required
                />
              </label>

              <label>
                Preço (R$)
                <input
                  type="number"
                  name="preco"
                  value={formulario.preco}
                  onChange={atualizarCampo}
                  min="0.01"
                  step="0.01"
                  required
                />
              </label>
            </fieldset>
          )}

          <fieldset className="formulario" disabled={ocupado}>
            <legend>Autenticação na API de alunos</legend>

            <label>
              Usuário
              <input
                value={usuario}
                onChange={(event) => setUsuario(event.target.value)}
                autoComplete="username"
                required
              />
            </label>

            <label>
              Senha
              <input
                type="password"
                value={senha}
                onChange={(event) => setSenha(event.target.value)}
                autoComplete="current-password"
                required
              />
            </label>
          </fieldset>

          {erro && (
            <p className="mensagem-erro" role="alert">
              {erro}
            </p>
          )}

          {mensagem && (
            <p className="mensagem-sucesso" role="status">
              {mensagem}
            </p>
          )}

          <div className="acoes">
            <button
              type="submit"
              className={planoParaExcluir ? 'botao-perigo' : ''}
              disabled={ocupado || carregando || Boolean(erroLista)}
            >
              {ocupado
                ? 'Processando...'
                : planoParaExcluir
                  ? 'Confirmar exclusão'
                  : idEmEdicao == null
                    ? 'Cadastrar plano'
                    : 'Salvar alterações'}
            </button>

            {(idEmEdicao != null || planoParaExcluir) && (
              <button
                type="button"
                className="botao-secundario"
                onClick={cancelar}
                disabled={ocupado}
              >
                Cancelar
              </button>
            )}
          </div>
        </form>
      </section>

      <section className="painel">
        <h2>Planos</h2>

        {carregando && <p role="status">Carregando planos...</p>}

        {erroLista && (
          <p className="mensagem-erro" role="alert">
            {erroLista}
          </p>
        )}

        {!carregando && !erroLista && planos.length === 0 && (
          <p>Nenhum plano cadastrado.</p>
        )}

        {!carregando && !erroLista && planos.length > 0 && (
          <div className="tabela-container">
            <table>
              <caption>Planos cadastrados</caption>

              <thead>
                <tr>
                  <th scope="col">ID</th>
                  <th scope="col">Nome</th>
                  <th scope="col">Duração</th>
                  <th scope="col">Preço</th>
                  <th scope="col">Ações</th>
                </tr>
              </thead>

              <tbody>
                {planos.map((plano) => (
                  <tr key={plano.idPlano}>
                    <td>{plano.idPlano}</td>
                    <td>{plano.nome}</td>
                    <td>{plano.duracaoMeses} meses</td>
                    <td>{moeda.format(plano.preco)}</td>
                    <td>
                      <div className="acoes">
                        <button
                          type="button"
                          onClick={() => editar(plano)}
                          disabled={ocupado}
                        >
                          Editar
                        </button>

                        <button
                          type="button"
                          className="botao-perigo"
                          onClick={() => prepararExclusao(plano)}
                          disabled={ocupado}
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

export default PlanosPage
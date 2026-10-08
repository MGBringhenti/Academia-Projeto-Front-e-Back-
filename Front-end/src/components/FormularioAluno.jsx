import { useEffect, useState } from 'react'
import {
  cadastrarAluno,
  atualizarAluno,
  listarPlanos,
} from '../services/AlunosService'

const formularioVazio = {
  nome: '',
  cpf: '',
  email: '',
  dataNascimento: '',
  idPlano: '',
}

function FormularioAluno({
  alunoEmEdicao,
  onAlunoSalvo,
  onCancelarEdicao,
}) {
  const [formulario, setFormulario] = useState(() =>
    alunoEmEdicao
      ? {
          nome: alunoEmEdicao.nome ?? '',
          cpf: alunoEmEdicao.cpf ?? '',
          email: alunoEmEdicao.email ?? '',
          dataNascimento: alunoEmEdicao.dataNascimento ?? '',
          idPlano: String(alunoEmEdicao.idPlano ?? ''),
        }
      : { ...formularioVazio },
  )

  const [planos, setPlanos] = useState([])
  const [carregandoPlanos, setCarregandoPlanos] = useState(true)
  const [erroPlanos, setErroPlanos] = useState('')
  const [usuario, setUsuario] = useState('')
  const [senha, setSenha] = useState('')
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function carregarPlanos() {
      try {
        const dados = await listarPlanos(controller.signal)

        if (!Array.isArray(dados)) {
          throw new Error('A API retornou uma lista de planos inválida.')
        }

        if (!controller.signal.aborted) {
          setPlanos(dados)
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setErroPlanos(error.message)
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregandoPlanos(false)
        }
      }
    }

    carregarPlanos()

    return () => controller.abort()
  }, [])

  function atualizarCampo(event) {
    const { name, value } = event.target

    setFormulario((anterior) => ({
      ...anterior,
      [name]: value,
    }))
  }

  async function enviarFormulario(event) {
    event.preventDefault()

    if (salvando) return

    setErro('')
    setSucesso('')
    setSalvando(true)

    try {
      const dados = {
        ...formulario,
        idPlano: Number(formulario.idPlano),
      }

      const alunoSalvo = alunoEmEdicao
        ? await atualizarAluno(
            alunoEmEdicao.idAluno,
            dados,
            usuario,
            senha,
          )
        : await cadastrarAluno(dados, usuario, senha)

      onAlunoSalvo(alunoSalvo)
      setFormulario({ ...formularioVazio })
      setSenha('')
      setSucesso(
        alunoEmEdicao
          ? 'Aluno atualizado com sucesso!'
          : 'Aluno cadastrado com sucesso!',
      )
    } catch (error) {
      setErro(error.message)
    } finally {
      setSalvando(false)
    }
  }

  return (
    <section className="painel">
      <h2>{alunoEmEdicao ? 'Editar aluno' : 'Cadastrar aluno'}</h2>

      {carregandoPlanos && (
        <p role="status">Carregando planos...</p>
      )}

      {erroPlanos && (
        <p className="mensagem-erro" role="alert">
          {erroPlanos}
        </p>
      )}

      {!carregandoPlanos && !erroPlanos && planos.length === 0 && (
        <p>Cadastre um plano pela API antes de cadastrar alunos.</p>
      )}

      <form onSubmit={enviarFormulario}>
        <fieldset className="formulario" disabled={salvando}>
          <legend>Dados do aluno</legend>

          <label>
            Nome
            <input
              name="nome"
              value={formulario.nome}
              onChange={atualizarCampo}
              maxLength={100}
              required
            />
          </label>

          <label>
            CPF
            <input
              name="cpf"
              value={formulario.cpf}
              onChange={atualizarCampo}
              inputMode="numeric"
              maxLength={11}
              pattern="[0-9]{11}"
              title="Informe os 11 números do CPF, sem pontuação."
              required
            />
          </label>

          <label>
            E-mail
            <input
              type="email"
              name="email"
              value={formulario.email}
              onChange={atualizarCampo}
              maxLength={100}
              required
            />
          </label>

          <label>
            Data de nascimento
            <input
              type="date"
              name="dataNascimento"
              value={formulario.dataNascimento}
              onChange={atualizarCampo}
              required
            />
          </label>

          <label>
            Plano
            <select
              name="idPlano"
              value={formulario.idPlano}
              onChange={atualizarCampo}
              disabled={carregandoPlanos || Boolean(erroPlanos)}
              required
            >
              <option value="">Selecione um plano</option>

              {planos.map((plano) => (
                <option key={plano.idPlano} value={plano.idPlano}>
                  {plano.nome} — {plano.duracaoMeses} meses
                </option>
              ))}
            </select>
          </label>
        </fieldset>

        <fieldset className="formulario" disabled={salvando}>
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

        {sucesso && (
          <p className="mensagem-sucesso" role="status">
            {sucesso}
          </p>
        )}

        <div className="acoes">
          <button
            type="submit"
            disabled={
              salvando ||
              carregandoPlanos ||
              Boolean(erroPlanos) ||
              planos.length === 0
            }
          >
            {salvando
              ? 'Salvando...'
              : alunoEmEdicao
                ? 'Salvar alterações'
                : 'Cadastrar aluno'}
          </button>

          {alunoEmEdicao && (
            <button
              type="button"
              className="botao-secundario"
              onClick={onCancelarEdicao}
              disabled={salvando}
            >
              Cancelar edição
            </button>
          )}
        </div>
      </form>
    </section>
  )
}

export default FormularioAluno
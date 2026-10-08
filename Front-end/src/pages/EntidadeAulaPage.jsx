import { useEffect, useState } from 'react'
import { consultarLista, enviarOperacao } from '../services/AulasService'

function criarFormulario(campos, item = {}) {
    return Object.fromEntries(
        campos.map((campo) => [
            campo.nome,
            String(item[campo.nome] ?? ''),
        ]),
    )
}

function EntidadeAulaPage({ config }) {
    const [itens, setItens] = useState([])
    const [dados, setDados] = useState({})
    const [carregando, setCarregando] = useState(true)
    const [erroCarga, setErroCarga] = useState('')
    const [versao, setVersao] = useState(0)
    const [edicao, setEdicao] = useState(null)
    const [exclusao, setExclusao] = useState(null)
    const [formulario, setFormulario] = useState(() =>
        criarFormulario(config.campos),
    )
    const [usuario, setUsuario] = useState('')
    const [senha, setSenha] = useState('')
    const [ocupado, setOcupado] = useState(false)
    const [erro, setErro] = useState('')
    const [mensagem, setMensagem] = useState('')

    useEffect(() => {
        const controller = new AbortController()

        async function carregar() {
            try {
                const dependencias = Object.entries(config.dependencias)

                const [lista, ...listasRelacionadas] = await Promise.all([
                    consultarLista(config.endpoint, controller.signal),
                    ...dependencias.map(([, url]) =>
                        consultarLista(url, controller.signal),
                    ),
                ])

                if (!controller.signal.aborted) {
                    setItens(lista)
                    setDados(
                        Object.fromEntries(
                            dependencias.map(([nome], indice) => [
                                nome,
                                listasRelacionadas[indice],
                            ]),
                        ),
                    )
                }
            } catch (error) {
                if (!controller.signal.aborted) {
                    setErroCarga(error.message)
                }
            } finally {
                if (!controller.signal.aborted) {
                    setCarregando(false)
                }
            }
        }

        carregar()

        return () => controller.abort()
    }, [config, versao])

    const campos = edicao
        ? config.camposEdicao ?? config.campos
        : config.campos

    function limparFormulario() {
        setEdicao(null)
        setExclusao(null)
        setFormulario(criarFormulario(config.campos))
        setSenha('')
        setErro('')
    }

    function recarregar() {
        limparFormulario()
        setMensagem('')
        setErroCarga('')
        setCarregando(true)
        setVersao((anterior) => anterior + 1)
    }

    function editar(item) {
        setEdicao(item)
        setExclusao(null)
        setFormulario(
            criarFormulario(config.camposEdicao ?? config.campos, item),
        )
        setSenha('')
        setErro('')
        setMensagem('')
    }

    function prepararExclusao(item) {
        setExclusao(item)
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
            if (exclusao) {
                await enviarOperacao(
                    `${config.endpoint}/${config.caminho(exclusao)}`,
                    'DELETE',
                    null,
                    usuario,
                    senha,
                )

                setItens((anteriores) =>
                    anteriores.filter(
                        (item) => config.chave(item) !== config.chave(exclusao),
                    ),
                )

                setMensagem('Exclusão concluída.')
            } else {
                const salvo = await enviarOperacao(
                    edicao
                        ? `${config.endpoint}/${config.caminho(edicao)}`
                        : config.endpoint,
                    edicao ? 'PUT' : 'POST',
                    config.preparar(formulario, Boolean(edicao)),
                    usuario,
                    senha,
                )

                setItens((anteriores) =>
                    edicao
                        ? anteriores.map((item) =>
                            config.chave(item) === config.chave(edicao) ? salvo : item,
                        )
                        : [...anteriores, salvo],
                )

                setMensagem(
                    edicao ? 'Alterações salvas.' : 'Cadastro concluído.',
                )
            }

            limparFormulario()
        } catch (error) {
            setErro(error.message)
        } finally {
            setOcupado(false)
        }
    }

    return (
        <>
            {mensagem && (
                <p className="mensagem-sucesso" role="status">
                    {mensagem}
                </p>
            )}

            {carregando && <p role="status">Carregando dados...</p>}

            {erroCarga && (
                <div role="alert">
                    <p className="mensagem-erro">
                        {erroCarga} Confira se as APIs estão funcionando.
                    </p>

                    <button
                        type="button"
                        className="botao-atualizar"
                        onClick={recarregar}
                        disabled={carregando}
                    >
                        Tentar novamente
                    </button>
                </div>
            )}

            {!carregando && !erroCarga && (
                <>
                    <section className="painel">
                        <h2>
                            {exclusao
                                ? 'Confirmar exclusão'
                                : edicao
                                    ? `Editar ${config.singular}`
                                    : `Cadastrar ${config.singular}`}
                        </h2>

                        {edicao && !exclusao && (
                            <p>{config.identificar(edicao, dados)}</p>
                        )}

                        <form onSubmit={enviar}>
                            {exclusao ? (
                                <p>
                                    Excluir <strong>{config.identificar(exclusao, dados)}</strong>?
                                    {' '}A exclusão é permanente. Registros vinculados podem
                                    impedir essa operação.
                                </p>
                            ) : (
                                <fieldset className="formulario" disabled={ocupado}>
                                    <legend>Dados de {config.singular}</legend>

                                    {campos.map((campo) => {
                                        const opcoes = campo.tipo === 'select'
                                            ? campo.opcoes(dados)
                                            : []

                                        return (
                                            <label key={campo.nome}>
                                                {campo.rotulo}

                                                {campo.tipo === 'select' ? (
                                                    <>
                                                        <select
                                                            name={campo.nome}
                                                            value={formulario[campo.nome] ?? ''}
                                                            onChange={(event) =>
                                                                setFormulario((anterior) => ({
                                                                    ...anterior,
                                                                    [campo.nome]: event.target.value,
                                                                }))
                                                            }
                                                            required
                                                        >
                                                            <option value="">Selecione</option>

                                                            {opcoes.map((opcao) => (
                                                                <option
                                                                    key={opcao.valor}
                                                                    value={opcao.valor}
                                                                >
                                                                    {opcao.texto}
                                                                </option>
                                                            ))}
                                                        </select>

                                                        {opcoes.length === 0 && (
                                                            <small>
                                                                Nenhuma opção disponível. Cadastre o registro
                                                                necessário e atualize os dados.
                                                            </small>
                                                        )}
                                                    </>
                                                ) : (
                                                    <input
                                                        type={campo.tipo ?? 'text'}
                                                        name={campo.nome}
                                                        value={formulario[campo.nome] ?? ''}
                                                        onChange={(event) =>
                                                            setFormulario((anterior) => ({
                                                                ...anterior,
                                                                [campo.nome]: event.target.value,
                                                            }))
                                                        }
                                                        maxLength={campo.maxLength}
                                                        min={campo.min}
                                                        step={campo.step}
                                                        pattern={campo.pattern}
                                                        title={campo.dica}
                                                        required
                                                    />
                                                )}
                                            </label>
                                        )
                                    })}
                                </fieldset>
                            )}

                            <fieldset className="formulario" disabled={ocupado}>
                                <legend>Confirme suas credenciais</legend>

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
                                <p className="mensagem-erro" role="alert">{erro}</p>
                            )}

                            <div className="acoes">
                                <button
                                    type="submit"
                                    className={exclusao ? 'botao-perigo' : ''}
                                    disabled={ocupado}
                                >
                                    {ocupado
                                        ? 'Processando...'
                                        : exclusao
                                            ? 'Confirmar exclusão'
                                            : edicao
                                                ? 'Salvar alterações'
                                                : 'Cadastrar'}
                                </button>

                                {(edicao || exclusao) && (
                                    <button
                                        type="button"
                                        className="botao-secundario"
                                        onClick={limparFormulario}
                                        disabled={ocupado}
                                    >
                                        Cancelar
                                    </button>
                                )}
                            </div>
                        </form>
                    </section>

                    <section className="painel">
                        <h2>{config.titulo}</h2>

                        {itens.length === 0 ? (
                            <p>Nenhum registro encontrado.</p>
                        ) : (
                            <div className="tabela-container">
                                <table>
                                    <caption>Registros cadastrados</caption>
                                    <thead>
                                        <tr>
                                            {config.colunas.map((coluna) => (
                                                <th key={coluna.titulo} scope="col">
                                                    {coluna.titulo}
                                                </th>
                                            ))}
                                            <th scope="col">Ações</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {itens.map((item) => (
                                            <tr key={config.chave(item)}>
                                                {config.colunas.map((coluna) => (
                                                    <td key={coluna.titulo}>
                                                        {coluna.valor(item, dados) ?? '—'}
                                                    </td>
                                                ))}

                                                <td>
                                                    <div className="acoes">
                                                        {config.permiteEdicao !== false && (
                                                            <button
                                                                type="button"
                                                                onClick={() => editar(item)}
                                                                disabled={ocupado}
                                                            >
                                                                Editar
                                                            </button>
                                                        )}

                                                        <button
                                                            type="button"
                                                            className="botao-perigo"
                                                            onClick={() => prepararExclusao(item)}
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
            )}
        </>
    )
}

export default EntidadeAulaPage
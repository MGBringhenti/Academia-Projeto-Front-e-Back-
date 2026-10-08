import { useState } from 'react'
import AlunosPage from './pages/AlunosPage'
import PlanosPage from './pages/PlanosPage'
import EntidadeAulaPage from './pages/EntidadeAulaPage'
import { configuracoesAula } from './pages/ConfiguracoesAula'
import './App.css'

const paginas = {
  alunos: {
    titulo: 'Alunos',
    descricao: 'Cadastre alunos e mantenha os dados de cada um em dia.',
  },
  planos: {
    titulo: 'Planos',
    descricao: 'Organize os valores e a duração dos planos.',
  },
  ...configuracoesAula,
}

function App() {
  const [pagina, setPagina] = useState('alunos')
  const atual = paginas[pagina]

  return (
    <div className="aplicacao">
      <a className="pular-conteudo" href="#conteudo">
        Pular para o conteúdo
      </a>

      <aside className="barra-lateral">
        <div className="marca">
          <svg
            className="marca-simbolo"
            viewBox="0 0 40 40"
            fill="none"
            aria-hidden="true"
          >
            <path
              d="M5 16v8m6-14v20m18-20v20m6-14v8M11 20h18"
              stroke="currentColor"
              strokeWidth="4"
              strokeLinecap="round"
            />
          </svg>

          <div>
            <span className="marca-nome">Academia</span>
            <span className="marca-descricao">Gestão da recepção</span>
          </div>
        </div>

        <nav className="navegacao" aria-label="Navegação principal">
          {Object.entries(paginas).map(([nome, dados]) => (
            <button
              key={nome}
              type="button"
              className="item-navegacao"
              onClick={() => setPagina(nome)}
              aria-pressed={pagina === nome}
            >
              {dados.titulo}
            </button>
          ))}
        </nav>

        <div className="rodape-lateral">
          <span className="linha-quadra" aria-hidden="true" />
          <p>Mais tempo para cuidar de quem treina.</p>
        </div>
      </aside>

      <main id="conteudo" className="pagina" tabIndex={-1}>
        <header className="cabecalho-pagina">
          <div>
            <h1>{atual.titulo}</h1>
            <p>{atual.descricao}</p>
          </div>
        </header>

        <div className="conteudo-pagina">
          {pagina === 'alunos' ? (
            <AlunosPage />
          ) : pagina === 'planos' ? (
            <PlanosPage />
          ) : (
            <EntidadeAulaPage
              key={pagina}
              config={configuracoesAula[pagina]}
            />
          )}
        </div>

        <footer className="rodape-pagina">
          Academia — gestão da recepção
        </footer>
      </main>
    </div>
  )
}

export default App
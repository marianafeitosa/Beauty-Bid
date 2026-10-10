import {
  useEffect,
  useState,
} from "react";

import {
  useNavigate,
} from "react-router-dom";

import { api } from "../services/api";

import "./RelatoriosAdmin.css";

function RelatoriosAdmin() {
  const navigate = useNavigate();

  const [resumo, setResumo] =
    useState(null);

  const [eventos, setEventos] =
    useState([]);

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] =
    useState("");

  useEffect(() => {
    carregarRelatorios();
  }, []);

  async function carregarRelatorios() {
    try {
      const [
        dadosResumo,
        dadosEventos,
      ] = await Promise.all([
        api("/relatorios/resumo"),
        api("/relatorios/eventos"),
      ]);

      setResumo(dadosResumo);
      setEventos(dadosEventos);

    } catch (error) {
      setErro(
        "Não foi possível carregar os relatórios."
      );
    } finally {
      setCarregando(false);
    }
  }

  function formatarDinheiro(
    centavos
  ) {
    return new Intl.NumberFormat(
      "pt-BR",
      {
        style: "currency",
        currency: "BRL",
      }
    ).format(
      (centavos || 0) / 100
    );
  }

  function formatarData(data) {
    if (!data) {
      return "-";
    }

    return new Date(
      data
    ).toLocaleString(
      "pt-BR"
    );
  }

  async function logout() {
    try {
      await api(
        "/auth/logout",
        {
          method: "POST",
        }
      );
    } finally {
      navigate("/login");
    }
  }

  if (carregando) {
    return (
      <div className="relatorio-loading">
        Carregando relatórios...
      </div>
    );
  }

  return (
    <div className="relatorio-page">

      <aside className="relatorio-sidebar">

        <div>

          <div
            className="relatorio-logo"
            onClick={() =>
              navigate("/admin")
            }
          >
            💄 Beauty Bid
          </div>

          <span className="relatorio-label">
            ADMINISTRAÇÃO
          </span>

          <nav className="relatorio-menu">

            <button
              onClick={() =>
                navigate("/admin")
              }
            >
              📊 Dashboard
            </button>

            <button
              onClick={() =>
                navigate(
                  "/admin/leiloes/novo"
                )
              }
            >
              ➕ Novo leilão
            </button>

            <button
              onClick={() =>
                navigate("/admin")
              }
            >
              💄 Leilões
            </button>

            <button
              onClick={() =>
                navigate(
                  "/admin/usuarios"
                )
              }
            >
              👥 Usuários
            </button>

            <button
              className="ativo"
            >
              📈 Relatórios
            </button>

          </nav>

        </div>

        <button
          className="relatorio-sair"
          onClick={logout}
        >
          Sair
        </button>

      </aside>

      <main className="relatorio-content">

        <header className="relatorio-header">

          <div>
            <h1>
              Relatórios
            </h1>

            <p>
              Acompanhe os eventos e
              indicadores da plataforma.
            </p>
          </div>

          <button
            className="atualizar-relatorio"
            onClick={
              carregarRelatorios
            }
          >
            ↻ Atualizar
          </button>

        </header>

        {erro && (
          <div className="relatorio-error">
            {erro}
          </div>
        )}

        {resumo && (
          <section className="relatorio-cards">

            <div className="relatorio-card">
              <span>
                Leilões criados
              </span>

              <strong>
                {
                  resumo
                    .leiloesCriados
                }
              </strong>
            </div>

            <div className="relatorio-card">
              <span>
                Leilões encerrados
              </span>

              <strong>
                {
                  resumo
                    .leiloesEncerrados
                }
              </strong>
            </div>

            <div className="relatorio-card">
              <span>
                Lances recebidos
              </span>

              <strong>
                {
                  resumo
                    .lancesRecebidos
                }
              </strong>
            </div>

            <div className="relatorio-card">
              <span>
                Aprovadas
              </span>

              <strong>
                {
                  resumo
                    .transacoesAprovadas
                }
              </strong>
            </div>

            <div className="relatorio-card">
              <span>
                Recusadas
              </span>

              <strong>
                {
                  resumo
                    .transacoesRecusadas
                }
              </strong>
            </div>

            <div className="relatorio-card">
              <span>
                Usuários criados
              </span>

              <strong>
                {
                  resumo
                    .usuariosCriados
                }
              </strong>
            </div>

            <div className="relatorio-card valor">
              <span>
                Valor vendido
              </span>

              <strong>
                {formatarDinheiro(
                  resumo
                    .valorTotalVendidoCentavos
                )}
              </strong>
            </div>

          </section>
        )}

        <section className="eventos-section">

          <div className="eventos-header">

            <div>
              <h2>
                Histórico de eventos
              </h2>

              <p>
                Eventos recebidos através
                do RabbitMQ.
              </p>
            </div>

            <span>
              {eventos.length} eventos
            </span>

          </div>

          {eventos.length === 0 ? (

            <div className="eventos-vazio">
              Nenhum evento registrado.
            </div>

          ) : (

            <div className="eventos-table-wrapper">

              <table className="eventos-table">

                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Origem</th>
                    <th>Entidade</th>
                    <th>Ação</th>
                    <th>ID</th>
                  </tr>
                </thead>

                <tbody>

                  {eventos.map(
                    (evento) => (
                      <tr
                        key={
                          evento.id
                        }
                      >

                        <td>
                          {formatarData(
                            evento.timestamp
                          )}
                        </td>

                        <td>
                          <span className="origem-badge">
                            {
                              evento.origem
                            }
                          </span>
                        </td>

                        <td>
                          {
                            evento.entidade
                          }
                        </td>

                        <td>
                          <strong>
                            {evento.acao}
                          </strong>
                        </td>

                        <td>
                          <code>
                            {
                              evento.entidadeId
                            }
                          </code>
                        </td>

                      </tr>
                    )
                  )}

                </tbody>

              </table>

            </div>
          )}

        </section>

      </main>

    </div>
  );
}

export default RelatoriosAdmin;
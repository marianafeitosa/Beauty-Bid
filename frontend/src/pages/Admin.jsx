import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { api } from "../services/api";
import "./Admin.css";

function Admin() {
  const navigate = useNavigate();

  const [usuario, setUsuario] = useState(null);

  const [resumo, setResumo] = useState({
    leiloesEncerrados: 0,
    transacoesAprovadas: 0,
    valorTotalVendidoCentavos: 0,
    usuariosCriados: 0,
    leiloesCriados: 0,
    lancesRecebidos: 0,
    transacoesRecusadas: 0,
  });

  const [leiloes, setLeiloes] = useState([]);

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] =
    useState("");

  useEffect(() => {
    carregarDashboard();
  }, []);

  async function carregarDashboard() {
    try {
      const dadosUsuario =
        await api("/auth/me");

      if (dadosUsuario.role !== "ADM") {
        navigate("/leiloes");
        return;
      }

      setUsuario(dadosUsuario);

      const [dadosResumo, dadosLeiloes] =
        await Promise.all([
          api("/relatorios/resumo"),
          api("/leiloes"),
        ]);

      setResumo(dadosResumo);
      setLeiloes(dadosLeiloes);

    } catch (error) {
      setErro(
        "Não foi possível carregar o painel administrativo."
      );
    } finally {
      setCarregando(false);
    }
  }

  async function logout() {
    try {
      await api("/auth/logout", {
        method: "POST",
      });
    } finally {
      navigate("/login");
    }
  }
async function excluirLeilao(leilao) {
  const confirmou = window.confirm(
    `Tem certeza que deseja excluir o leilão "${leilao.titulo}"?`
  );

  if (!confirmou) {
    return;
  }

  try {
    await api(`/leiloes/${leilao.id}`, {
      method: "DELETE",
    });

    setLeiloes((anteriores) =>
      anteriores.filter(
        (item) => item.id !== leilao.id
      )
    );
  } catch (error) {
    setErro(
      error.message ||
        "Não foi possível excluir o leilão."
    );
  }
}
  function formatarDinheiro(
    valorCentavos
  ) {
    return new Intl.NumberFormat(
      "pt-BR",
      {
        style: "currency",
        currency: "BRL",
      }
    ).format(
      (valorCentavos || 0) / 100
    );
  }

  function formatarData(data) {
    return new Date(
      data
    ).toLocaleString("pt-BR");
  }

  const leiloesAbertos =
    leiloes.filter(
      (leilao) =>
        leilao.status === "ABERTO"
    ).length;

  if (carregando) {
    return (
      <div className="admin-loading">
        Carregando painel...
      </div>
    );
  }

  return (
    <div className="admin-page">

      <aside className="admin-sidebar">

        <div>
          <div className="admin-logo">
            💄 Beauty Bid
          </div>

          <span className="admin-label">
            ADMINISTRAÇÃO
          </span>

          <nav className="admin-menu">

            <button className="ativo">
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
            document
            .querySelector(".admin-section")
            ?.scrollIntoView({
                behavior: "smooth",
            })
        }
        >
        💄 Leilões
        </button>

          <button
  onClick={() =>
    navigate("/admin/usuarios")
  }
>
  👥 Usuários
</button>

            <button
  onClick={() =>
    navigate(
      "/admin/relatorios"
    )
  }
>
  📈 Relatórios
</button>

          </nav>
        </div>

        <button
          className="admin-sair"
          onClick={logout}
        >
          Sair
        </button>

      </aside>

      <main className="admin-content">

        <header className="admin-header">

          <div>
            <h1>Dashboard</h1>

            <p>
              Visão geral do Beauty Bid
            </p>
          </div>

          {usuario && (
            <div className="admin-user">
              <span>
                Administrador
              </span>

              <strong>
                {usuario.nome}
              </strong>
            </div>
          )}

        </header>

        {erro && (
          <div className="admin-error">
            {erro}
          </div>
        )}

        <section className="admin-cards">

          <div className="dashboard-card">
            <span>
              Leilões criados
            </span>

            <strong>
              {resumo.leiloesCriados}
            </strong>

            <small>
              {leiloesAbertos} em andamento
            </small>
          </div>

          <div className="dashboard-card">
            <span>
              Lances recebidos
            </span>

            <strong>
              {resumo.lancesRecebidos}
            </strong>

            <small>
              Total de ofertas
            </small>
          </div>

          <div className="dashboard-card">
            <span>
              Transações aprovadas
            </span>

            <strong>
              {
                resumo
                  .transacoesAprovadas
              }
            </strong>

            <small>
              {
                resumo
                  .transacoesRecusadas
              }{" "}
              recusadas
            </small>
          </div>

          <div className="dashboard-card destaque">
            <span>
              Valor total vendido
            </span>

            <strong>
              {formatarDinheiro(
                resumo
                  .valorTotalVendidoCentavos
              )}
            </strong>

            <small>
              {
                resumo
                  .leiloesEncerrados
              }{" "}
              leilões encerrados
            </small>
          </div>

        </section>

        <section className="admin-section">

          <div className="admin-section-header">

            <div>
              <h2>
                Leilões
              </h2>

              <p>
                Produtos cadastrados
                na plataforma.
              </p>
            </div>

            <button
              onClick={() =>
                navigate(
                  "/admin/leiloes/novo"
                )
              }
            >
              + Novo leilão
            </button>

          </div>

          <div className="admin-table-wrapper">

            <table className="admin-table">

              <thead>
                <tr>
                <th>Produto</th>
                <th>Marca</th>
                <th>Valor atual</th>
                <th>Status</th>
                <th>Encerramento</th>
                <th>Ações</th>
                </tr>
              </thead>

              <tbody>

                {leiloes.map(
                  (leilao) => (
                    <tr key={leilao.id}>

                      <td>
                        <strong>
                          {leilao.titulo}
                        </strong>
                      </td>

                      <td>
                        {leilao.marca}
                      </td>

                      <td>
                        {formatarDinheiro(
                          leilao
                            .valorAtualCentavos
                        )}
                      </td>

                      <td>
                        <span
                          className={`admin-status ${leilao.status.toLowerCase()}`}
                        >
                          {leilao.status}
                        </span>
                      </td>

                      <td>
                        {formatarData(
                          leilao.fimEm
                        )}
                      </td>
<td>
  <div className="acoes-leilao">

    <button
      className="editar-button"
      onClick={() =>
        navigate(
          `/admin/leiloes/${leilao.id}/editar`
        )
      }
    >
      Editar
    </button>

    <button
      className="excluir-button"
      onClick={() =>
        excluirLeilao(leilao)
      }
    >
      Excluir
    </button>

  </div>
</td>


                    </tr>
                  )
                )}

              </tbody>

            </table>

          </div>

        </section>

      </main>

    </div>
  );
}

export default Admin;
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { api } from "../services/api";
import "./MeusLances.css";

function MeusLances() {
  const navigate = useNavigate();

  const [lances, setLances] = useState([]);
  const [leiloes, setLeiloes] = useState({});
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    carregarLances();
  }, []);

  async function carregarLances() {
    try {
      const dadosLances =
        await api("/leiloes/lances/meus");

      setLances(dadosLances);

      const ids = [
        ...new Set(
          dadosLances.map(
            (lance) => lance.leilaoId
          )
        ),
      ];

      const detalhes = await Promise.all(
        ids.map(async (id) => {
          try {
            const leilao =
              await api(`/leiloes/${id}`);

            return [id, leilao];
          } catch {
            return [id, null];
          }
        })
      );

      setLeiloes(
        Object.fromEntries(detalhes)
      );
    } catch (error) {
      setErro(
        "Não foi possível carregar seus lances."
      );
    } finally {
      setCarregando(false);
    }
  }

  function formatarDinheiro(centavos) {
    return new Intl.NumberFormat(
      "pt-BR",
      {
        style: "currency",
        currency: "BRL",
      }
    ).format(centavos / 100);
  }

  function formatarData(data) {
    return new Date(data)
      .toLocaleString("pt-BR");
  }

  function textoStatus(status) {
    if (status === "NA_FILA") {
      return "Aguardando";
    }

    if (status === "ACEITO") {
      return "Aceito";
    }

    if (status === "RECUSADO") {
      return "Recusado";
    }

    return status;
  }

  if (carregando) {
    return (
      <div className="meus-loading">
        Carregando seus lances...
      </div>
    );
  }

  return (
    <div className="meus-page">

      <header className="meus-navbar">

        <div
          className="meus-logo"
          onClick={() =>
            navigate("/leiloes")
          }
        >
          💄 Beauty Bid
        </div>

        <div className="meus-nav-actions">

          <button
            onClick={() =>
              navigate("/leiloes")
            }
          >
            Leilões
          </button>

          <button className="ativo">
            Meus lances
          </button>

        </div>

      </header>

      <main className="meus-container">

        <div className="meus-title">

          <span>MINHA CONTA</span>

          <h1>Meus lances</h1>

          <p>
            Acompanhe todos os lances
            que você já realizou.
          </p>

        </div>

        {erro && (
          <div className="meus-error">
            {erro}
          </div>
        )}

        {lances.length === 0 ? (

          <div className="meus-vazio">

            <div>💄</div>

            <h2>
              Você ainda não deu nenhum lance
            </h2>

            <p>
              Explore os leilões disponíveis
              e participe.
            </p>

            <button
              onClick={() =>
                navigate("/leiloes")
              }
            >
              Ver leilões
            </button>

          </div>

        ) : (

          <div className="meus-lista">

            {lances.map((lance) => {

              const leilao =
                leiloes[lance.leilaoId];

              const liderando =
                leilao &&
                leilao.vencedorId ===
                  lance.usuarioId &&
                leilao.status === "ABERTO";

              const venceu =
                leilao &&
                leilao.vencedorId ===
                  lance.usuarioId &&
                leilao.status === "ENCERRADO";

              return (
                <article
                  className="meu-lance-card"
                  key={lance.id}
                >

                  <div className="meu-lance-produto">

                    <img
                      src={
                        leilao?.imagemUrl ||
                        "https://placehold.co/200x200?text=Beauty+Bid"
                      }
                      alt={
                        leilao?.titulo ||
                        "Produto"
                      }
                      onError={(event) => {
                        event.currentTarget.src =
                          "https://placehold.co/200x200?text=Beauty+Bid";
                      }}
                    />

                    <div>

                      <span className="meu-marca">
                        {leilao?.marca ||
                          "Leilão"}
                      </span>

                      <h3>
                        {leilao?.titulo ||
                          "Produto indisponível"}
                      </h3>

                      <small>
                        Lance realizado em{" "}
                        {formatarData(
                          lance.criadoEm
                        )}
                      </small>

                    </div>

                  </div>

                  <div className="meu-valor">

                    <span>
                      Seu lance
                    </span>

                    <strong>
                      {formatarDinheiro(
                        lance.valorCentavos
                      )}
                    </strong>

                  </div>

                  <div className="meu-status-area">

                    <span
                      className={`meu-status ${lance.status.toLowerCase()}`}
                    >
                      {textoStatus(
                        lance.status
                      )}
                    </span>

                    {liderando && (
                      <span className="meu-lider">
                        🏆 Liderando
                      </span>
                    )}

                    {venceu && (
                      <span className="meu-venceu">
                        🎉 Você venceu
                      </span>
                    )}

                  </div>

                  {leilao && (
                    <button
                      className="ver-produto"
                      onClick={() =>
                        navigate(
                          `/leiloes/${lance.leilaoId}`
                        )
                      }
                    >
                      Ver leilão
                    </button>
                  )}

                </article>
              );
            })}

          </div>

        )}

      </main>

    </div>
  );
}

export default MeusLances;
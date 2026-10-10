import { useEffect, useState } from "react";
import {
  useNavigate,
  useParams,
} from "react-router-dom";

import { api } from "../services/api";
import "./DetalheLeilao.css";

function DetalheLeilao() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [leilao, setLeilao] = useState(null);
  const [usuario, setUsuario] = useState(null);

  const [valorLance, setValorLance] =
    useState("");

  const [statusLance, setStatusLance] =
    useState("");

  const [mensagem, setMensagem] =
    useState("");

  const [erro, setErro] =
    useState("");

  const [carregando, setCarregando] =
    useState(true);

  const [enviandoLance, setEnviandoLance] =
    useState(false);

  useEffect(() => {
    carregarPagina();
  }, [id]);

  async function carregarPagina() {
    try {
      const [dadosLeilao, dadosUsuario] =
        await Promise.all([
          api(`/leiloes/${id}`),
          api("/auth/me"),
        ]);

      setLeilao(dadosLeilao);
      setUsuario(dadosUsuario);
    } catch (error) {
      setErro(
        "Não foi possível carregar o leilão."
      );
    } finally {
      setCarregando(false);
    }
  }

  function formatarDinheiro(valorCentavos) {
    return new Intl.NumberFormat(
      "pt-BR",
      {
        style: "currency",
        currency: "BRL",
      }
    ).format(valorCentavos / 100);
  }

  function formatarData(data) {
    return new Date(data).toLocaleString(
      "pt-BR"
    );
  }

  async function darLance(event) {
    event.preventDefault();

    setErro("");
    setMensagem("");
    setStatusLance("");

    const valorNumero = Number(
      valorLance.replace(",", ".")
    );

    if (
      Number.isNaN(valorNumero) ||
      valorNumero <= 0
    ) {
      setErro(
        "Digite um valor válido para o lance."
      );
      return;
    }

    const valorCentavos =
      Math.round(valorNumero * 100);

    if (
      valorCentavos <=
      leilao.valorAtualCentavos
    ) {
      setErro(
        `O lance deve ser maior que ${formatarDinheiro(
          leilao.valorAtualCentavos
        )}.`
      );
      return;
    }

    setEnviandoLance(true);

    try {
      const lance = await api(
        `/leiloes/${id}/lances`,
        {
          method: "POST",
          body: JSON.stringify({
            valorCentavos,
          }),
        }
      );

      setStatusLance(
        lance.status || "NA_FILA"
      );

      setMensagem(
        "Lance enviado. Aguardando processamento..."
      );

      await acompanharTransacao(lance.id);

    } catch (error) {
      setErro(
        error.message ||
          "Não foi possível enviar o lance."
      );

      setEnviandoLance(false);
    }
  }

  async function acompanharTransacao(
    lanceId
  ) {
    const maxTentativas = 15;

    for (
      let tentativa = 1;
      tentativa <= maxTentativas;
      tentativa++
    ) {
      await esperar(700);

      try {
        const transacao = await api(
          `/transacoes/lance/${lanceId}`
        );

        setStatusLance(
          transacao.status
        );

        if (
          transacao.status === "APROVADA"
        ) {
          setMensagem(
            "🎉 Seu lance foi aprovado!"
          );

          const leilaoAtualizado =
            await api(`/leiloes/${id}`);

          setLeilao(leilaoAtualizado);
          setValorLance("");
          setEnviandoLance(false);

          return;
        }

        if (
          transacao.status === "RECUSADA"
        ) {
          setMensagem(
            `Lance recusado: ${
              transacao.motivo ||
              "não foi aceito"
            }`
          );

          const leilaoAtualizado =
            await api(`/leiloes/${id}`);

          setLeilao(leilaoAtualizado);
          setEnviandoLance(false);

          return;
        }

        if (
          transacao.status === "ERRO"
        ) {
          setErro(
            transacao.motivo ||
              "Erro ao processar o lance."
          );

          setEnviandoLance(false);

          return;
        }

      } catch (error) {
        // Nos primeiros instantes a transação
        // ainda pode não existir.
      }
    }

    setErro(
      "O lance está demorando para ser processado."
    );

    setEnviandoLance(false);
  }

  function esperar(ms) {
    return new Promise((resolve) =>
      setTimeout(resolve, ms)
    );
  }

  if (carregando) {
    return (
      <div className="detalhe-loading">
        Carregando leilão...
      </div>
    );
  }

  if (!leilao) {
    return (
      <div className="detalhe-loading">
        Leilão não encontrado.
      </div>
    );
  }

  const souVencedor =
    usuario &&
    leilao.vencedorId === usuario.id;

  return (
    <div className="detalhe-page">

      <header className="detalhe-navbar">

        <div
          className="detalhe-logo"
          onClick={() =>
            navigate("/leiloes")
          }
        >
          💄 Beauty Bid
        </div>

        <button
          className="voltar-button"
          onClick={() =>
            navigate("/leiloes")
          }
        >
          ← Voltar aos leilões
        </button>

      </header>

      <main className="detalhe-container">

        <div className="produto-imagem">

          <img
            src={leilao.imagemUrl}
            alt={leilao.titulo}
            onError={(event) => {
              event.currentTarget.src =
                "https://placehold.co/700x700?text=Beauty+Bid";
            }}
          />

        </div>

        <div className="produto-info">

          <span className="produto-marca">
            {leilao.marca}
          </span>

          <h1>
            {leilao.titulo}
          </h1>

          <p className="produto-descricao">
            {leilao.descricao}
          </p>

          <div className="detalhe-status-area">

            <span
              className={`detalhe-status ${leilao.status.toLowerCase()}`}
            >
              {leilao.status}
            </span>

            {souVencedor && (
              <span className="liderando">
                🏆 Você está liderando
              </span>
            )}

          </div>

          <div className="valor-box">

            <span>
              Lance atual
            </span>

            <strong>
              {formatarDinheiro(
                leilao.valorAtualCentavos
              )}
            </strong>

            <small>
              Lance inicial:{" "}
              {formatarDinheiro(
                leilao.lanceInicialCentavos
              )}
            </small>

          </div>

          <div className="fim-leilao">
            <strong>
              Encerramento
            </strong>

            <span>
              {formatarData(
                leilao.fimEm
              )}
            </span>
          </div>

          {leilao.status === "ABERTO" ? (

            <form
              className="lance-form"
              onSubmit={darLance}
            >

              <label>
                Seu lance
              </label>

              <div className="lance-input-area">

                <span>R$</span>

                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="Ex: 180,00"
                  value={valorLance}
                  onChange={(event) =>
                    setValorLance(
                      event.target.value
                    )
                  }
                  disabled={enviandoLance}
                  required
                />

              </div>

              <p className="lance-ajuda">
                O valor precisa ser maior que{" "}
                <strong>
                  {formatarDinheiro(
                    leilao.valorAtualCentavos
                  )}
                </strong>
              </p>

              <button
                type="submit"
                disabled={enviandoLance}
              >
                {enviandoLance
                  ? "Processando..."
                  : "Dar lance"}
              </button>

            </form>

          ) : (

            <div className="leilao-indisponivel">
              Este leilão não está mais
              aceitando lances.
            </div>

          )}

          {statusLance && (
            <div className="processamento-box">

              <span>
                Status do lance
              </span>

              <strong>
                {statusLance}
              </strong>

            </div>
          )}

          {mensagem && (
            <div className="mensagem-sucesso">
              {mensagem}
            </div>
          )}

          {erro && (
            <div className="mensagem-erro">
              {erro}
            </div>
          )}

        </div>

      </main>

    </div>
  );
}

export default DetalheLeilao;
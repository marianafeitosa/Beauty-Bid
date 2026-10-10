import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { api } from "../services/api";
import "./NovoLeilao.css";

function NovoLeilao() {
  const navigate = useNavigate();

  const [titulo, setTitulo] =
    useState("");

  const [marca, setMarca] =
    useState("");

  const [descricao, setDescricao] =
    useState("");

  const [imagemUrl, setImagemUrl] =
    useState("");

  const [
    lanceInicial,
    setLanceInicial,
  ] = useState("");

  const [fimEm, setFimEm] =
    useState("");

  const [erro, setErro] =
    useState("");

  const [salvando, setSalvando] =
    useState(false);

  useEffect(() => {
    verificarAdmin();
  }, []);

  async function verificarAdmin() {
    try {
      const usuario =
        await api("/auth/me");

      if (usuario.role !== "ADM") {
        navigate("/leiloes");
      }
    } catch {
      navigate("/login");
    }
  }

  async function criarLeilao(event) {
    event.preventDefault();

    setErro("");
    setSalvando(true);

    try {
      const valor =
        Number(
          lanceInicial.replace(",", ".")
        );

      if (
        Number.isNaN(valor) ||
        valor <= 0
      ) {
        throw new Error(
          "Informe um lance inicial válido."
        );
      }

      if (!fimEm) {
        throw new Error(
          "Informe a data de encerramento."
        );
      }

      const dataFinal =
        new Date(fimEm);

      if (
        Number.isNaN(
          dataFinal.getTime()
        )
      ) {
        throw new Error(
          "Data de encerramento inválida."
        );
      }

      if (
        dataFinal <= new Date()
      ) {
        throw new Error(
          "O encerramento deve ser uma data futura."
        );
      }

      await api("/leiloes", {
        method: "POST",

        body: JSON.stringify({
          titulo,
          marca,
          descricao,
          imagemUrl,
          lanceInicialCentavos:
            Math.round(valor * 100),
          fimEm:
            dataFinal.toISOString(),
        }),
      });

      navigate("/admin");

    } catch (error) {
      setErro(
        error.message ||
          "Não foi possível criar o leilão."
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="novo-page">

      <header className="novo-header">

        <div
          className="novo-logo"
          onClick={() =>
            navigate("/admin")
          }
        >
          💄 Beauty Bid
        </div>

        <button
          onClick={() =>
            navigate("/admin")
          }
        >
          ← Voltar
        </button>

      </header>

      <main className="novo-container">

        <div className="novo-title">

          <span>
            ADMINISTRAÇÃO
          </span>

          <h1>
            Criar novo leilão
          </h1>

          <p>
            Cadastre um novo produto
            para receber lances.
          </p>

        </div>

        <form
          className="novo-form"
          onSubmit={criarLeilao}
        >

          <div className="novo-grid">

            <div className="novo-group">
              <label>
                Nome do produto
              </label>

              <input
                value={titulo}
                onChange={(event) =>
                  setTitulo(
                    event.target.value
                  )
                }
                placeholder="Ex: Batom MAC Ruby Woo"
                required
              />
            </div>

            <div className="novo-group">
              <label>
                Marca
              </label>

              <input
                value={marca}
                onChange={(event) =>
                  setMarca(
                    event.target.value
                  )
                }
                placeholder="Ex: MAC"
                required
              />
            </div>

          </div>

          <div className="novo-group">

            <label>
              Descrição
            </label>

            <textarea
              value={descricao}
              onChange={(event) =>
                setDescricao(
                  event.target.value
                )
              }
              rows="5"
              placeholder="Descrição do produto..."
              required
            />

          </div>

          <div className="novo-group">

            <label>
              URL da imagem
            </label>

            <input
              type="url"
              value={imagemUrl}
              onChange={(event) =>
                setImagemUrl(
                  event.target.value
                )
              }
              placeholder="https://..."
              required
            />

          </div>

          <div className="novo-grid">

            <div className="novo-group">

              <label>
                Lance inicial
              </label>

              <div className="novo-money">

                <span>
                  R$
                </span>

                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={lanceInicial}
                  onChange={(event) =>
                    setLanceInicial(
                      event.target.value
                    )
                  }
                  placeholder="100.00"
                  required
                />

              </div>

            </div>

            <div className="novo-group">

              <label>
                Encerramento
              </label>

              <input
                type="datetime-local"
                value={fimEm}
                onChange={(event) =>
                  setFimEm(
                    event.target.value
                  )
                }
                required
              />

            </div>

          </div>

          {imagemUrl && (
            <div className="novo-preview">

              <span>
                Pré-visualização
              </span>

              <img
                src={imagemUrl}
                alt="Prévia"
                onError={(event) => {
                  event.currentTarget.style.display =
                    "none";
                }}
              />

            </div>
          )}

          {erro && (
            <div className="novo-error">
              {erro}
            </div>
          )}

          <div className="novo-actions">

            <button
              type="button"
              className="cancelar"
              onClick={() =>
                navigate("/admin")
              }
            >
              Cancelar
            </button>

            <button
              type="submit"
              className="salvar"
              disabled={salvando}
            >
              {salvando
                ? "Criando..."
                : "Criar leilão"}
            </button>

          </div>

        </form>

      </main>

    </div>
  );
}

export default NovoLeilao;
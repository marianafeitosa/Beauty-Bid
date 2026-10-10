import { useEffect, useState } from "react";
import {
  useNavigate,
  useParams,
} from "react-router-dom";

import { api } from "../services/api";
import "./NovoLeilao.css";

function EditarLeilao() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [titulo, setTitulo] = useState("");
  const [marca, setMarca] = useState("");
  const [descricao, setDescricao] =
    useState("");
  const [imagemUrl, setImagemUrl] =
    useState("");
  const [lanceInicial, setLanceInicial] =
    useState("");
  const [fimEm, setFimEm] =
    useState("");

  const [carregando, setCarregando] =
    useState(true);

  const [salvando, setSalvando] =
    useState(false);

  const [erro, setErro] =
    useState("");

  useEffect(() => {
    carregarLeilao();
  }, [id]);

  async function carregarLeilao() {
    try {
      const usuario =
        await api("/auth/me");

      if (usuario.role !== "ADM") {
        navigate("/leiloes");
        return;
      }

      const leilao =
        await api(`/leiloes/${id}`);

      setTitulo(leilao.titulo || "");
      setMarca(leilao.marca || "");
      setDescricao(
        leilao.descricao || ""
      );
      setImagemUrl(
        leilao.imagemUrl || ""
      );

      setLanceInicial(
        (
          leilao.lanceInicialCentavos /
          100
        ).toString()
      );

      if (leilao.fimEm) {
        const data =
          new Date(leilao.fimEm);

        const local =
          new Date(
            data.getTime() -
              data.getTimezoneOffset() *
                60000
          )
            .toISOString()
            .slice(0, 16);

        setFimEm(local);
      }
    } catch (error) {
      setErro(
        "Não foi possível carregar o leilão."
      );
    } finally {
      setCarregando(false);
    }
  }

  async function salvar(event) {
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
          "Informe um valor válido."
        );
      }

      const dataFinal =
        new Date(fimEm);

      await api(`/leiloes/${id}`, {
        method: "PUT",

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
          "Não foi possível editar o leilão."
      );
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return (
      <div className="admin-loading">
        Carregando leilão...
      </div>
    );
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
            Editar leilão
          </h1>

          <p>
            Atualize as informações
            do produto.
          </p>

        </div>

        <form
          className="novo-form"
          onSubmit={salvar}
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
                required
              />

            </div>

          </div>

          <div className="novo-group">

            <label>
              Descrição
            </label>

            <textarea
              rows="5"
              value={descricao}
              onChange={(event) =>
                setDescricao(
                  event.target.value
                )
              }
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
                alt="Produto"
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
                ? "Salvando..."
                : "Salvar alterações"}
            </button>

          </div>

        </form>

      </main>

    </div>
  );
}

export default EditarLeilao;
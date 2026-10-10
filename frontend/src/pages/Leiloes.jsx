import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { api } from "../services/api";

import "./Leiloes.css";

function Leiloes() {
  const navigate = useNavigate();

  const [leiloes, setLeiloes] = useState([]);
  const [usuario, setUsuario] = useState(null);

  const [carregando, setCarregando] =
    useState(true);

  const [erro, setErro] =
    useState("");

  useEffect(() => {
    carregarPagina();
  }, []);

  async function carregarPagina() {
    try {
      const [dadosLeiloes, dadosUsuario] =
        await Promise.all([
          api("/leiloes"),
          api("/auth/me"),
        ]);

      setLeiloes(dadosLeiloes);
      setUsuario(dadosUsuario);
    } catch (error) {
      setErro(
        "Não foi possível carregar os leilões."
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

  if (carregando) {
    return (
      <div className="loading">
        Carregando leilões...
      </div>
    );
  }

  return (
    <div className="leiloes-page">

      <header className="navbar">

        <div className="logo">
          💄 Beauty Bid
        </div>

        <div className="navbar-user">

          {usuario && (
            <span>
              Olá, {usuario.nome}
            </span>
          )}
<button
  onClick={() =>
    navigate("/meus-lances")
  }
>
  Meus lances
</button>
          <button onClick={logout}>
            Sair
          </button>

        </div>

      </header>

      <section className="hero">

        <div>
          <span className="hero-tag">
            ✨ Beauty Auction
          </span>

          <h1>
            Encontre seu próximo
            <span> item favorito</span>
          </h1>

          <p>
            Participe dos nossos leilões
            de maquiagem e dê seu melhor
            lance.
          </p>
        </div>

      </section>

      <main className="leiloes-container">

        <div className="titulo-area">
          <h2>Leilões disponíveis</h2>

          <p>
            Escolha um produto e participe
            do leilão.
          </p>
        </div>

        {erro && (
          <p className="erro">
            {erro}
          </p>
        )}

        {leiloes.length === 0 ? (

          <div className="vazio">
            <h3>Nenhum leilão disponível</h3>

            <p>
              Novos produtos aparecerão
              aqui em breve.
            </p>
          </div>

        ) : (

          <div className="leiloes-grid">

            {leiloes.map((leilao) => (

              <article
                className="leilao-card"
                key={leilao.id}
              >

                <div className="imagem-area">

                  <img
                    src={leilao.imagemUrl}
                    alt={leilao.titulo}

                    onError={(event) => {
                      event.currentTarget.src =
                        "https://placehold.co/600x400?text=Beauty+Bid";
                    }}
                  />

                  <span
                    className={`status ${leilao.status.toLowerCase()}`}
                  >
                    {leilao.status}
                  </span>

                </div>

                <div className="card-content">

                  <span className="marca">
                    {leilao.marca}
                  </span>

                  <h3>
                    {leilao.titulo}
                  </h3>

                  <p className="descricao">
                    {leilao.descricao}
                  </p>

                  <div className="preco">

                    <small>
                      Lance atual
                    </small>

                    <strong>
                      {formatarDinheiro(
                        leilao.valorAtualCentavos
                      )}
                    </strong>

                  </div>

                  <div className="encerramento">
                    Encerra em:{" "}
                    {formatarData(
                      leilao.fimEm
                    )}
                  </div>

                  <button
                    onClick={() =>
                      navigate(
                        `/leiloes/${leilao.id}`
                      )
                    }
                  >
                    Ver leilão
                  </button>

                </div>

              </article>

            ))}

          </div>

        )}

      </main>

    </div>
  );
}

export default Leiloes;
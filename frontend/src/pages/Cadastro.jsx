import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../services/api";
import "./Cadastro.css";

function Cadastro() {
  const navigate = useNavigate();

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");

  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function cadastrar(event) {
    event.preventDefault();

    setErro("");

    if (senha !== confirmarSenha) {
      setErro("As senhas não coincidem.");
      return;
    }

    setCarregando(true);

    try {
      await api("/auth/register", {
        method: "POST",
        body: JSON.stringify({
          nome,
          email,
          senha,
        }),
      });

      // Depois do cadastro, já faz login
      await api("/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email,
          senha,
        }),
      });

      navigate("/leiloes");
    } catch (error) {
      setErro(
        error.message ||
          "Não foi possível realizar o cadastro."
      );
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="cadastro-page">
      <div className="cadastro-card">
        <div className="cadastro-brand">
          <span>💄</span>
          <h1>Beauty Bid</h1>
        </div>

        <p className="cadastro-subtitle">
          Crie sua conta para participar dos leilões
        </p>

        <form onSubmit={cadastrar}>
          <div className="form-group">
            <label>Nome</label>

            <input
              type="text"
              placeholder="Seu nome"
              value={nome}
              onChange={(event) =>
                setNome(event.target.value)
              }
              required
            />
          </div>

          <div className="form-group">
            <label>E-mail</label>

            <input
              type="email"
              placeholder="seu@email.com"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              required
            />
          </div>

          <div className="form-group">
            <label>Senha</label>

            <input
              type="password"
              placeholder="Mínimo 6 caracteres"
              value={senha}
              onChange={(event) =>
                setSenha(event.target.value)
              }
              minLength={6}
              required
            />
          </div>

          <div className="form-group">
            <label>Confirmar senha</label>

            <input
              type="password"
              placeholder="Digite sua senha novamente"
              value={confirmarSenha}
              onChange={(event) =>
                setConfirmarSenha(event.target.value)
              }
              required
            />
          </div>

          {erro && (
            <p className="cadastro-error">
              {erro}
            </p>
          )}

          <button
            type="submit"
            disabled={carregando}
          >
            {carregando
              ? "Cadastrando..."
              : "Criar conta"}
          </button>
        </form>

        <p className="login-texto">
          Já tem uma conta?{" "}
          <span onClick={() => navigate("/login")}>
            Entrar
          </span>
        </p>
      </div>
    </div>
  );
}

export default Cadastro;
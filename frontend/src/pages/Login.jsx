import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../services/api";
import "./Login.css";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function fazerLogin(event) {
    event.preventDefault();

    setErro("");
    setCarregando(true);

    try {
      const usuario = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email,
          senha,
        }),
      });

      if (usuario.role === "ADM") {
        navigate("/admin");
      } else {
        navigate("/leiloes");
      }
    } catch (error) {
      setErro("E-mail ou senha inválidos.");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-brand">
          <span>💄</span>
          <h1>Beauty Bid</h1>
        </div>

        <p className="login-subtitle">
          Seu leilão de maquiagem
        </p>

        <form onSubmit={fazerLogin}>
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
              placeholder="Sua senha"
              value={senha}
              onChange={(event) =>
                setSenha(event.target.value)
              }
              required
            />
          </div>

          {erro && (
            <p className="login-error">
              {erro}
            </p>
          )}

          <button
            type="submit"
            disabled={carregando}
          >
            {carregando
              ? "Entrando..."
              : "Entrar"}
          </button>
        </form>

        <p className="cadastro-texto">
          Ainda não tem uma conta?{" "}
          <span
            onClick={() => navigate("/cadastro")}
          >
            Cadastre-se
          </span>
        </p>
      </div>
    </div>
  );
}

export default Login;
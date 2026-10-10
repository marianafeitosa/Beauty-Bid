import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";

import { api } from "../services/api";

function ProtectedRoute({
  children,
  roles,
}) {
  const [usuario, setUsuario] =
    useState(null);

  const [carregando, setCarregando] =
    useState(true);

  const [naoAutorizado, setNaoAutorizado] =
    useState(false);

  useEffect(() => {
    verificarAcesso();
  }, []);

  async function verificarAcesso() {
    try {
      const dados =
        await api("/auth/me");

      // Salva primeiro o usuário autenticado
      setUsuario(dados);

      // Depois verifica a role
      if (
        roles &&
        !roles.includes(dados.role)
      ) {
        setNaoAutorizado(true);
      }

    } catch {
      // Não está autenticado
      setUsuario(null);
    } finally {
      setCarregando(false);
    }
  }

  if (carregando) {
    return (
      <div
        style={{
          minHeight: "100vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontFamily: "Arial, sans-serif",
        }}
      >
        Verificando acesso...
      </div>
    );
  }

  // Logado, mas sem permissão
  if (naoAutorizado) {
    return (
      <Navigate
        to="/leiloes"
        replace
      />
    );
  }

  // Nem sequer está logado
  if (!usuario) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return children;
}

export default ProtectedRoute;
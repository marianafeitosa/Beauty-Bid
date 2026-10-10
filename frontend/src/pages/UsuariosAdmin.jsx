import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { api } from "../services/api";
import "./UsuariosAdmin.css";

function UsuariosAdmin() {
  const navigate = useNavigate();

  const [adminAtual, setAdminAtual] =
    useState(null);

  const [usuarios, setUsuarios] =
    useState([]);

  const [editandoId, setEditandoId] =
    useState(null);

  const [formEdicao, setFormEdicao] =
    useState({
      nome: "",
      email: "",
      role: "CLIENTE",
    });

  const [carregando, setCarregando] =
    useState(true);

  const [salvando, setSalvando] =
    useState(false);

  const [erro, setErro] =
    useState("");

  const [mensagem, setMensagem] =
    useState("");

  useEffect(() => {
    carregarUsuarios();
  }, []);

  async function carregarUsuarios() {
    try {
      setErro("");

      const usuarioLogado =
        await api("/auth/me");

      if (usuarioLogado.role !== "ADM") {
        navigate("/leiloes");
        return;
      }

      setAdminAtual(usuarioLogado);

      const dados =
        await api("/auth/usuarios");

      setUsuarios(dados);
    } catch (error) {
      setErro(
        "Não foi possível carregar os usuários."
      );
    } finally {
      setCarregando(false);
    }
  }

  function iniciarEdicao(usuario) {
    setMensagem("");
    setErro("");

    setEditandoId(usuario.id);

    setFormEdicao({
      nome: usuario.nome,
      email: usuario.email,
      role: usuario.role,
    });
  }

  function cancelarEdicao() {
    setEditandoId(null);

    setFormEdicao({
      nome: "",
      email: "",
      role: "CLIENTE",
    });
  }

  async function salvarUsuario(id) {
    setErro("");
    setMensagem("");
    setSalvando(true);

    try {
      const atualizado =
        await api(
          `/auth/usuarios/${id}`,
          {
            method: "PUT",
            body: JSON.stringify({
              nome: formEdicao.nome,
              email: formEdicao.email,
              role: formEdicao.role,
            }),
          }
        );

      setUsuarios((anteriores) =>
        anteriores.map((usuario) =>
          usuario.id === id
            ? atualizado
            : usuario
        )
      );

      setEditandoId(null);

      setMensagem(
        "Usuário atualizado com sucesso."
      );
    } catch (error) {
      setErro(
        error.message ||
          "Não foi possível atualizar o usuário."
      );
    } finally {
      setSalvando(false);
    }
  }

  async function excluirUsuario(usuario) {
    setErro("");
    setMensagem("");

    if (
      adminAtual &&
      usuario.id === adminAtual.id
    ) {
      setErro(
        "Você não pode excluir o usuário administrador que está logado."
      );
      return;
    }

    const confirmou = window.confirm(
      `Deseja realmente excluir "${usuario.nome}"?`
    );

    if (!confirmou) {
      return;
    }

    try {
      await api(
        `/auth/usuarios/${usuario.id}`,
        {
          method: "DELETE",
        }
      );

      setUsuarios((anteriores) =>
        anteriores.filter(
          (item) =>
            item.id !== usuario.id
        )
      );

      setMensagem(
        "Usuário excluído com sucesso."
      );
    } catch (error) {
      setErro(
        error.message ||
          "Não foi possível excluir o usuário."
      );
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

  if (carregando) {
    return (
      <div className="usuarios-loading">
        Carregando usuários...
      </div>
    );
  }

  return (
    <div className="usuarios-page">

      <aside className="usuarios-sidebar">

        <div>

          <div
            className="usuarios-logo"
            onClick={() =>
              navigate("/admin")
            }
          >
            💄 Beauty Bid
          </div>

          <span className="usuarios-label">
            ADMINISTRAÇÃO
          </span>

          <nav className="usuarios-menu">

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

            <button className="ativo">
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
          className="usuarios-sair"
          onClick={logout}
        >
          Sair
        </button>

      </aside>

      <main className="usuarios-content">

        <header className="usuarios-header">

          <div>
            <h1>
              Usuários
            </h1>

            <p>
              Gerencie os usuários
              cadastrados na plataforma.
            </p>
          </div>

          <div className="usuarios-total">
            <span>
              Total
            </span>

            <strong>
              {usuarios.length}
            </strong>
          </div>

        </header>

        {erro && (
          <div className="usuarios-error">
            {erro}
          </div>
        )}

        {mensagem && (
          <div className="usuarios-success">
            {mensagem}
          </div>
        )}

        <section className="usuarios-card">

          <div className="usuarios-card-header">

            <div>
              <h2>
                Contas cadastradas
              </h2>

              <p>
                Clientes e administradores
                do Beauty Bid.
              </p>
            </div>

          </div>

          <div className="usuarios-table-wrapper">

            <table className="usuarios-table">

              <thead>
                <tr>
                  <th>Nome</th>
                  <th>E-mail</th>
                  <th>Perfil</th>
                  <th>Ações</th>
                </tr>
              </thead>

              <tbody>

                {usuarios.map(
                  (usuario) => {

                    const editando =
                      editandoId ===
                      usuario.id;

                    const souEu =
                      adminAtual &&
                      adminAtual.id ===
                        usuario.id;

                    return (
                      <tr key={usuario.id}>

                        <td>

                          {editando ? (
                            <input
                              className="usuario-input"
                              value={
                                formEdicao.nome
                              }
                              onChange={(
                                event
                              ) =>
                                setFormEdicao(
                                  {
                                    ...formEdicao,
                                    nome:
                                      event
                                        .target
                                        .value,
                                  }
                                )
                              }
                            />
                          ) : (
                            <div className="usuario-nome">

                              <strong>
                                {
                                  usuario.nome
                                }
                              </strong>

                              {souEu && (
                                <small>
                                  Você
                                </small>
                              )}

                            </div>
                          )}

                        </td>

                        <td>

                          {editando ? (
                            <input
                              className="usuario-input"
                              type="email"
                              value={
                                formEdicao.email
                              }
                              onChange={(
                                event
                              ) =>
                                setFormEdicao(
                                  {
                                    ...formEdicao,
                                    email:
                                      event
                                        .target
                                        .value,
                                  }
                                )
                              }
                            />
                          ) : (
                            usuario.email
                          )}

                        </td>

                        <td>

                          {editando ? (
                            <select
                              className="usuario-input"
                              value={
                                formEdicao.role
                              }
                              disabled={
                                souEu
                              }
                              onChange={(
                                event
                              ) =>
                                setFormEdicao(
                                  {
                                    ...formEdicao,
                                    role:
                                      event
                                        .target
                                        .value,
                                  }
                                )
                              }
                            >
                              <option value="CLIENTE">
                                CLIENTE
                              </option>

                              <option value="ADM">
                                ADM
                              </option>
                            </select>
                          ) : (
                            <span
                              className={`usuario-role ${usuario.role.toLowerCase()}`}
                            >
                              {usuario.role}
                            </span>
                          )}

                        </td>

                        <td>

                          {editando ? (

                            <div className="usuario-acoes">

                              <button
                                className="salvar-usuario"
                                disabled={
                                  salvando
                                }
                                onClick={() =>
                                  salvarUsuario(
                                    usuario.id
                                  )
                                }
                              >
                                {salvando
                                  ? "Salvando..."
                                  : "Salvar"}
                              </button>

                              <button
                                className="cancelar-usuario"
                                onClick={
                                  cancelarEdicao
                                }
                              >
                                Cancelar
                              </button>

                            </div>

                          ) : (

                            <div className="usuario-acoes">

                              <button
                                className="editar-usuario"
                                onClick={() =>
                                  iniciarEdicao(
                                    usuario
                                  )
                                }
                              >
                                Editar
                              </button>

                              <button
                                className="excluir-usuario"
                                disabled={
                                  souEu
                                }
                                onClick={() =>
                                  excluirUsuario(
                                    usuario
                                  )
                                }
                              >
                                Excluir
                              </button>

                            </div>

                          )}

                        </td>

                      </tr>
                    );
                  }
                )}

              </tbody>

            </table>

          </div>

        </section>

      </main>

    </div>
  );
}

export default UsuariosAdmin;
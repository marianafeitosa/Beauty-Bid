const API_URL = "http://localhost:8080/api";

export async function api(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    let mensagem = "Erro na requisição";

    try {
      const erro = await response.json();
      mensagem = erro.message || erro.error || mensagem;
    } catch {
      // resposta sem JSON
    }

    throw new Error(mensagem);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
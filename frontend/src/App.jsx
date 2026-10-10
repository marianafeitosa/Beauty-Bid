import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import Login from "./pages/Login";
import Cadastro from "./pages/Cadastro";
import Leiloes from "./pages/Leiloes";
import DetalheLeilao from "./pages/DetalheLeilao";

import Admin from "./pages/Admin";
import NovoLeilao from "./pages/NovoLeilao";
import EditarLeilao from "./pages/EditarLeilao";
import UsuariosAdmin from "./pages/UsuariosAdmin";
import RelatoriosAdmin from "./pages/RelatoriosAdmin";
import MeusLances from "./pages/MeusLances";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>

      <Route
        path="/"
        element={
          <Navigate
            to="/login"
          />
        }
      />

      <Route
        path="/login"
        element={<Login />}
      />

      <Route
        path="/cadastro"
        element={<Cadastro />}
      />

      <Route
        path="/leiloes"
        element={
          <ProtectedRoute>
            <Leiloes />
          </ProtectedRoute>
        }
      />

      <Route
        path="/leiloes/:id"
        element={
          <ProtectedRoute>
            <DetalheLeilao />
          </ProtectedRoute>
        }
      />
<Route
  path="/meus-lances"
  element={
    <ProtectedRoute>
      <MeusLances />
    </ProtectedRoute>
  }
/>
      <Route
        path="/admin"
        element={
          <ProtectedRoute
            roles={["ADM"]}
          >
            <Admin />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/leiloes/novo"
        element={
          <ProtectedRoute
            roles={["ADM"]}
          >
            <NovoLeilao />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/leiloes/:id/editar"
        element={
          <ProtectedRoute
            roles={["ADM"]}
          >
            <EditarLeilao />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/usuarios"
        element={
          <ProtectedRoute
            roles={["ADM"]}
          >
            <UsuariosAdmin />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/relatorios"
        element={
          <ProtectedRoute
            roles={["ADM"]}
          >
            <RelatoriosAdmin />
          </ProtectedRoute>
        }
      />

    </Routes>
  );
}

export default App;
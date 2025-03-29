import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RegisterAdmin from './pages/RegisterAdmin';
import AuthPage from './pages/AuthUser';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/register_admin" element={<RegisterAdmin />} />
        <Route path="/auth_user" element={<AuthPage />} />
      </Routes>
    </Router>
  );
}

export default App;

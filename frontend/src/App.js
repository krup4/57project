import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RegisterAdmin from './pages/RegisterAdmin';
import AuthPage from './pages/AuthUser';
import MainPage from './pages/MainPage'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/register_admin" element={<RegisterAdmin />} />
        <Route path="/auth_user" element={<AuthPage />} />
        <Route path="/" element={<MainPage />} />
      </Routes>
    </Router>
  );
}

export default App;

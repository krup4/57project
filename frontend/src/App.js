import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RegisterAdmin from './pages/RegisterAdmin';
import AuthPage from './pages/AuthUser';
import MainPage from './pages/MainPage'
import SendFile from './pages/SendFile';
import RegisterUser from './pages/RegisterUser';
import AcceptUsers from './pages/AcceptUsers';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/register_admin" element={<RegisterAdmin />} />
        <Route path="/auth_user" element={<AuthPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/send_file" element={<SendFile />} />
        <Route path="/register_user" element={<RegisterUser />} />
        <Route path="/accept_users" element={<AcceptUsers />} />
      </Routes>
    </Router>
  );
}

export default App;

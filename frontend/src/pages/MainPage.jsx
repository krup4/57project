import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Button,
  Container,
  Typography,
  Box,
  Paper,
  IconButton
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';

const HomePage = () => {
  const navigate = useNavigate();
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const token = sessionStorage.getItem('token');
    const adminStatus = sessionStorage.getItem('isAdmin') === 'true';

    if (!token) {
      navigate('/auth_user');
    }

    setIsAdmin(adminStatus);
  }, [navigate]);

  const handlePrintFiles = () => {
    navigate('/send_file');
  };

  const handleAcceptUsers = () => {
    navigate('/accept_users');
  };

  const handleLogout = () => {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('isAdmin');
    window.location.reload();
  };

  return (
    <Container maxWidth="md">
      <Paper elevation={3} sx={{ p: 4, mt: 4, position: 'relative', backgroundColor: '#121212', color: '#ffffff' }}>
        <IconButton
          onClick={handleLogout}
          sx={{
            position: 'absolute',
            right: 16,
            top: 16,
            color: '#d4d4d4'
          }}
          title="Выход"
        >
          <LogoutIcon />
        </IconButton>

        <Typography variant="h4" component="h1" gutterBottom align="center">
          Главная страница
        </Typography>

        <Box sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: 2,
          justifyContent: 'center',
          mt: 4
        }}>
          <Button
            variant="contained"
            size="large"
            onClick={handlePrintFiles}
            sx={{
              px: 4,
              py: 2,
              fontSize: '1rem'
            }}
          >
            Печать файлов
          </Button>

          {isAdmin && (
            <Button
              variant="contained"
              size="large"
              onClick={handleAcceptUsers}
              sx={{
                px: 4,
                py: 2,
                fontSize: '1rem'
              }}
            >
              Подтверждение регистрации пользователей
            </Button>
          )}
        </Box>
      </Paper>
    </Container>
  );
};

export default HomePage;
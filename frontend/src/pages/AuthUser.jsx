import React, { useState } from 'react';
import {
  Box,
  Button,
  TextField,
  Typography,
  Container,
  Paper,
  Alert,
  CircularProgress,
  Divider,
  IconButton
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { ArrowBack } from '@mui/icons-material';


const AuthPage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    login: '',
    password: '',
    secret: '',
    name: null
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      const response = await fetch('http://localhost:8080/api/v1/user/authorize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (!response.ok) {
        console.log('ERROR: ', data);
        throw new Error(`${data.message}`);
      }
      console.log('Success:', data);
      sessionStorage.setItem("token", data.token)
      sessionStorage.setItem("isAdmin", data.isAdmin)
      setSuccess(true);
      navigate('/')
    } catch (error) {
      console.error('Error:', error);
      setError(error.message || 'Error submitting form');
    } finally {
      setLoading(false);
    }
  };

  const navigateToUserRegister = () => {
    navigate('/register_user');
  };

  const navigateToAdminRegister = () => {
    navigate('/register_admin');
  };

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <Container maxWidth="sm">
      <Paper elevation={3} sx={{ p: 4, mt: 4, position: 'relative', backgroundColor: '#121212',color: '#ffffff' }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Вход
        </Typography>

        <IconButton
          onClick={handleGoHome}
          sx={{
            position: 'absolute',
            left: 16,
            top: 16,
            color: '#d4d4d4'
          }}
          title="На главную"
        >
          <ArrowBack />
        </IconButton>

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Успешный вход!
            </Alert>
          )}

          <TextField
            label="Login"
            name="login"
            value={formData.login}
            onChange={handleChange}
            fullWidth
            required
            margin="normal"
          />

          <TextField
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            fullWidth
            required
            margin="normal"
          />

          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              size="large"
              disabled={loading}
              sx={{ width: '100%' }}
            >
              {loading ? (
                <>
                  <CircularProgress size={24} color="inherit" />
                  <Box component="span" sx={{ ml: 1 }}>Вход...</Box>
                </>
              ) : (
                'Войти'
              )}
            </Button>
          </Box>

          <Divider sx={{ my: 3 }}>Или</Divider>

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Button
              variant="outlined"
              color="secondary"
              size="large"
              onClick={navigateToUserRegister}
              sx={{ width: '100%' }}
            >
              Регистрация пользователя
            </Button>

            <Button
              variant="outlined"
              color="secondary"
              size="large"
              onClick={navigateToAdminRegister}
              sx={{ width: '100%' }}
            >
              Регистрация администратора
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default AuthPage;
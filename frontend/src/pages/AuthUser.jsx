import React, { useState } from 'react';
import {
  Box,
  Button,
  TextField,
  Typography,
  Container,
  Paper,
  Alert,
  CircularProgress
} from '@mui/material';
import { useNavigate } from 'react-router-dom';

const AuthPage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    login: '',
    password: '',
    secret: '',
    name: ''
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
      setSuccess(true);
      navigate('/send_file')
    } catch (error) {
      console.error('Error:', error);
      setError(error.message || 'Error submitting form');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Paper elevation={3} sx={{ p: 4, mt: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Authentication Form
        </Typography>

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Form submitted successfully!
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
            >
              {loading ? (
                <>
                  <CircularProgress size={24} color="inherit" />
                  <Box component="span" sx={{ ml: 1 }}>Submitting...</Box>
                </>
              ) : (
                'Submit'
              )}
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default AuthPage;
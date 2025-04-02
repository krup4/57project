import React, { useEffect, useState } from 'react';
import { 
  Container,
  Typography,
  Paper,
  Box,
  Button,
  Alert
} from '@mui/material';

const TextFromStoragePage = () => {
  const [storedText, setStoredText] = useState('');
  const [error, setError] = useState('');

  // Загружаем текст из sessionStorage при монтировании компонента
  useEffect(() => {
    try {
      const text = sessionStorage.getItem('token') || 'Текст не найден в sessionStorage';
      setStoredText(text);
    } catch (e) {
      setError('Ошибка при чтении из sessionStorage');
      console.error(e);
    }
  }, []);

  // Функция для обновления текста (пример использования)
  const handleUpdateText = () => {
    const newText = `Обновленный текст: ${new Date().toLocaleTimeString()}`;
    sessionStorage.setItem('token', newText);
    setStoredText(newText);
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Текст из sessionStorage
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <Box sx={{ 
          p: 2,
          border: '1px dashed grey',
          borderRadius: 1,
          minHeight: 100,
          mb: 3
        }}>
          <Typography variant="body1">
            {storedText}
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button 
            variant="contained" 
            onClick={handleUpdateText}
          >
            Обновить текст
          </Button>

          <Button 
            variant="outlined" 
            onClick={() => sessionStorage.removeItem('token')}
          >
            Очистить storage
          </Button>
        </Box>

        <Typography variant="body2" sx={{ mt: 3, color: 'text.secondary' }}>
          Проверьте консоль разработчика (Application → Session Storage) чтобы увидеть сохраненные данные
        </Typography>
      </Paper>
    </Container>
  );
};

export default TextFromStoragePage;
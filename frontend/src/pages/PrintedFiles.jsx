import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  List,
  ListItem,
  ListItemText,
  Typography,
  Paper,
  CircularProgress,
  Alert,
  Box,
  IconButton
} from '@mui/material';
import { Refresh } from '@mui/icons-material';
import { ArrowBack } from '@mui/icons-material';

const PrintedFiles = () => {
  const navigate = useNavigate();
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchFiles = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await fetch('http://localhost:8080/api/v1/printer/printed', {
        headers: {
          'Authorization': `${sessionStorage.getItem("token")}`
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setFiles(data);
    } catch (err) {
      setError(err.message);
      console.error("Error fetching files:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, []);

  const handleRefresh = () => {
    fetchFiles();
  };

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <Container maxWidth="md">
      <Paper elevation={3} sx={{ p: 4, mt: 4, backgroundColor: '#121212', color: '#ffffff' }}>
        <Box display="flex" alignItems="center" mb={3}>
          <IconButton
            onClick={handleGoHome}
            sx={{
              color: '#d4d4d4',
              mr: 2
            }}
            title="На главную"
          >
            <ArrowBack />
          </IconButton>
          <Typography variant="h4" component="h1" sx={{ flexGrow: 1 }}>
            Список распечатанных файлов
          </Typography>
          <IconButton
            onClick={handleRefresh}
            color="primary"
            disabled={loading}
          >
            <Refresh />
          </IconButton>
        </Box>

        {loading && (
          <Box display="flex" justifyContent="center" my={4}>
            <CircularProgress />
          </Box>
        )}

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            Ошибка при загрузке файлов: {error}
          </Alert>
        )}

        {!loading && !error && files.length === 0 && (
          <Typography variant="body1" color="text.secondary">
            Файлы не найдены
          </Typography>
        )}

        {!loading && !error && files.length > 0 && (
          <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
            {files.map((file) => (
              <ListItem key={file.id} divider>
                <ListItemText
                  primary={file.filePath}
                />
              </ListItem>
            ))}
          </List>
        )}
      </Paper>
    </Container>
  );
};

export default PrintedFiles;
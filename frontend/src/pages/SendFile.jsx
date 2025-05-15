import React, { useState } from 'react';
import {
  Button,
  Container,
  Box,
  Typography,
  LinearProgress,
  Paper,
  IconButton
} from '@mui/material';
import { CloudUpload, Delete, ArrowBack } from '@mui/icons-material';
import { styled } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

const SendFile = () => {
  const navigate = useNavigate();
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);
  const [uploadSuccess, setUploadSuccess] = useState(false);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setSelectedFile(file);
      setUploadSuccess(false);
      setUploadError(null);
    }
  };

  const handleRemoveFile = () => {
    setSelectedFile(null);
    setUploadProgress(0);
    setUploadError(null);
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append('file', selectedFile);

    setIsUploading(true);
    setUploadProgress(0);
    setUploadError(null);

    try {
      const response = await fetch('http://45.43.89.85:8080/api/v1/printer/print', {
        method: 'POST',
        body: formData,
        headers: {
          'Authorization': `${sessionStorage.getItem("token")}`
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setUploadProgress(percentCompleted);
        },
      });

      if (!response.ok) {
        throw new Error(`Ошибка загрузки: ${response.statusText}`);
      }

      const result = await response.json();
      console.log('Успешная загрузка:', result);
      setUploadSuccess(true);
    } catch (error) {
      console.error('Ошибка при загрузке файла:', error);
      setUploadError(error.message || 'Произошла ошибка при загрузке файла');
    } finally {
      setIsUploading(false);
    }
  };

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 4, position: 'relative', backgroundColor: '#121212', color: '#ffffff' }}>
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

        <Typography variant="h4" component="h1" gutterBottom align="center" sx={{ pt: 1, mb: 4 }}>
          Загрузка файла
        </Typography>

        <Box sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '200px',
          mb: 4
        }}>
          {!selectedFile ? (
            <Button
              component="label"
              variant="contained"
              startIcon={<CloudUpload sx={{ fontSize: '2rem' }} />}
              sx={{
                fontSize: '1.5rem',
                padding: '16px 32px',
                minHeight: '80px',
                borderRadius: '8px'
              }}
            >
              Выберите файл
              <VisuallyHiddenInput
                type="file"
                onChange={handleFileChange}
              />
            </Button>
          ) : (
            <Box sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 3,
              width: '100%'
            }}>
              <Typography variant="h6" sx={{ fontSize: '1.25rem' }}>
                Выбран файл: {selectedFile.name}
              </Typography>
              <Typography variant="body1" sx={{ fontSize: '1.1rem' }}>
                Размер: {Math.round(selectedFile.size / 1024)} KB
              </Typography>
              <IconButton
                onClick={handleRemoveFile}
                color="error"
                sx={{ fontSize: '2rem' }}
              >
                <Delete fontSize="inherit" />
              </IconButton>
            </Box>
          )}
        </Box>

        {isUploading && (
          <Box sx={{ width: '100%', mb: 3 }}>
            <LinearProgress
              variant="determinate"
              value={uploadProgress}
              sx={{ height: '10px', borderRadius: '5px' }}
            />
            <Typography variant="h6" sx={{ mt: 2, textAlign: 'center' }}>
              {uploadProgress}% загружено
            </Typography>
          </Box>
        )}

        {selectedFile && !isUploading && (
          <Button
            variant="contained"
            color="primary"
            onClick={handleUpload}
            disabled={isUploading}
            fullWidth
            sx={{
              mb: 3,
              fontSize: '1.25rem',
              padding: '12px 24px',
              minHeight: '45px'
            }}
          >
            Загрузить файл
          </Button>
        )}

        {uploadError && (
          <Typography
            color="error"
            sx={{
              mt: 2,
              fontSize: '1.1rem',
              textAlign: 'center'
            }}
          >
            {uploadError}
          </Typography>
        )}

        {uploadSuccess && (
          <Typography
            color="success.main"
            sx={{
              mt: 2,
              fontSize: '1.1rem',
              textAlign: 'center'
            }}
          >
            Файл успешно загружен!
          </Typography>
        )}
      </Paper>
    </Container>
  );
};

export default SendFile;
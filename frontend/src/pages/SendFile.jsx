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
import { CloudUpload, Delete } from '@mui/icons-material';
import { styled } from '@mui/material/styles';

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
      const response = await fetch('http://localhost:8080/api/v1/printer/print', {
        method: 'POST',
        body: formData,
        // Если ваш бэкенд требует авторизацию:
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

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <Typography variant="h5" component="h1" gutterBottom>
          Загрузка файла
        </Typography>
        
        <Box sx={{ mb: 3 }}>
          {!selectedFile ? (
            <Button
              component="label"
              variant="contained"
              startIcon={<CloudUpload />}
            >
              Выберите файл
              <VisuallyHiddenInput 
                type="file" 
                onChange={handleFileChange} 
              />
            </Button>
          ) : (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Typography variant="body1">
                Выбран файл: {selectedFile.name} ({Math.round(selectedFile.size / 1024)} KB)
              </Typography>
              <IconButton onClick={handleRemoveFile} color="error">
                <Delete />
              </IconButton>
            </Box>
          )}
        </Box>

        {isUploading && (
          <Box sx={{ width: '100%', mb: 2 }}>
            <LinearProgress variant="determinate" value={uploadProgress} />
            <Typography variant="body2" sx={{ mt: 1 }}>
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
            sx={{ mb: 2 }}
          >
            Загрузить файл
          </Button>
        )}

        {uploadError && (
          <Typography color="error" sx={{ mt: 2 }}>
            {uploadError}
          </Typography>
        )}

        {uploadSuccess && (
          <Typography color="success.main" sx={{ mt: 2 }}>
            Файл успешно загружен!
          </Typography>
        )}
      </Paper>
    </Container>
  );
};

export default SendFile;
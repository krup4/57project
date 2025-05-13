import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Typography,
  Container,
  Paper,
  List,
  ListItem,
  ListItemText,
  Divider,
  Alert,
  CircularProgress
} from '@mui/material';

const AcceptUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const fetchUsers = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/v1/user/get_users', {
        headers: {
          'Authorization': `${sessionStorage.getItem("token")}`
        }
      });
      if (!response.ok) {
        throw new Error('Failed to fetch users');
      }
      const data = await response.json();
      setUsers(data.users || []);
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleUserAction = async (login, isConfirmed) => {
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const response = await fetch('http://localhost:8080/api/v1/user/accept_user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `${sessionStorage.getItem("token")}`
        },
        body: JSON.stringify({ login, isConfirmed })
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to update user status');
      }

      setSuccess(`User ${login} has been ${isConfirmed ? 'approved' : 'rejected'}`);
      
      // Перезагружаем список пользователей после успешного действия
      await fetchUsers();
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md">
      <Paper elevation={3} sx={{ p: 4, mt: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Users Management
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }}>
            {success}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <List>
            {users.map((user) => (
              <React.Fragment key={user.login}>
                <ListItem>
                  <ListItemText
                    primary={user.login}
                    secondary={user.name || 'No name provided'}
                    sx={{ flexGrow: 1 }}
                  />
                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button
                      variant="contained"
                      color="success"
                      disabled={loading}
                      onClick={() => handleUserAction(user.login, true)}
                    >
                      Approve
                    </Button>
                    <Button
                      variant="outlined"
                      color="error"
                      disabled={loading}
                      onClick={() => handleUserAction(user.login, false)}
                    >
                      Reject
                    </Button>
                  </Box>
                </ListItem>
                <Divider component="li" />
              </React.Fragment>
            ))}
          </List>
        )}
      </Paper>
    </Container>
  );
};

export default AcceptUsers;
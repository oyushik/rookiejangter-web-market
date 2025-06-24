import { useEffect } from 'react';
import { Container, Typography } from '@mui/material';
import LoginForm from '../components/LoginForm';
import Layout from '../components/Layout';
const LoginPage = () => {
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    document.body.style.overflowY = 'auto';
    document.body.style.overflowX = 'auto';
    return () => {
      document.body.style.overflowY = 'auto';
      document.body.style.overflowX = 'auto';
    };
  }, []);

  return (
    <Layout>
      <Container>
        <Typography variant="h4" sx={{ mt: 4 }}>
          로그인
        </Typography>
      </Container>
      <LoginForm />
    </Layout>
  );
};

export default LoginPage;

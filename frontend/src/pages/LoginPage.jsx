import React from 'react';
import { Container, Typography } from '@mui/material';
import LoginForm from "../components/LoginForm";
import Layout from '../components/Layout';

const LoginPage = () => {

  return (
      <Layout>
        <Container>
          <Typography variant="h4" sx={{ mt: 4 }}>로그인 페이지</Typography>
        </Container>
        <LoginForm />
      </Layout>
    )
};

export default LoginPage;
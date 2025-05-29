import React from 'react';
import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import RegisterPage from './pages/RegisterPage';
import ProductsPage from './pages/ProductsPage';
import SignupPage from "./pages/SignupPage";
import LoginPage from "./pages/LoginPage";
import MyPage from "./pages/MyPage";
import './App.css';
import Header from './components/Header';
import { Box } from '@mui/material';
import ProductDetailPage from "./pages/ProductDetailPage";

function App() {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '1000vh' }}>
      <Header />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          mt: '10px', // 헤더 두 개 합친 높이만큼 여백 줘야 내용이 가려지지 않음
          px: 0,        // 좌우 패딩(optional)
        }}
      >
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/products/:product_id" element={<ProductDetailPage />} />
        </Routes>
      </Box>
    </Box>
  );
}

export default App;

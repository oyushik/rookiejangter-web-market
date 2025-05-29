import React from 'react';
import { BrowserRouter,Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import RegisterPage from './pages/RegisterPage';
import ProductsPage from './pages/ProductsPage';
import SignupPage from "./pages/SignupPage";
import ProductDetailPage from "./pages/ProductDetailPage";
import './App.css'


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/products/:product_id" element={<ProductDetailPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
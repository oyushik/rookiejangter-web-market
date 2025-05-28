import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const ProductsPage = () => {
  const { search } = useLocation();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  // 쿼리 파라미터 파싱
  const params = new URLSearchParams(search);
  const area = params.get('area') || '';
  const keyword = params.get('keyword') || '';
  const category = params.get('category') || '';
  const minPrice = params.get('minPrice') || '';
  const maxPrice = params.get('maxPrice') || '';

  useEffect(() => {
    setLoading(true);
    axios
      .get(`/api/products${search}`)
      .then((res) => {
        const data = res.data.content || res.data;
        setProducts(Array.isArray(data) ? data : []);
      })
      .catch(() => setProducts([]))
      .finally(() => setLoading(false));
  }, [search]);

  return (
    <div>
      <h3>지역: {area || '-'}</h3>
      <h3>키워드: {keyword || '-'}</h3>
      <h3>카테고리: {category || '-'}</h3>
      <h3>최소가격: {minPrice || '-'} ~ 최대가격: {maxPrice || '-'}</h3>
      <h1>검색 결과</h1>
      {loading ? (
        <p>로딩 중...</p>
      ) : !Array.isArray(products) || products.length === 0 ? (
        <h2>검색 결과가 없습니다.</h2>
      ) : (
        <ul>
          {products.map((product) => (
            <li key={product.id || product._id}>
              {product.name} - {product.price}원
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ProductsPage;
import { Typography, Box, Button } from '@mui/material';
import { products as allProducts } from "../constants/ExpProductDB";
// import { useEffect, useState } from "react"; // 백엔드 연동 시 필요
// import axios from "axios"; // 백엔드 연동 시 필요
import { useLocation, useNavigate } from "react-router-dom";
import Header from '../components/Header';
import { FilterProducts } from '../utils/FilterProducts';
import { FormatTime } from '../utils/FormatTime';
import ProductsList from '../components/ProductsList';
import PaginationBar from '../components/PaginationBar';


const ProductsPage = () => {
  // 백엔드 연동 시 아래 코드로 대체
  // const [allProducts, setAllProducts] = useState([]);
  // useEffect(() => {
  //   axios.get("/api/products")
  //     .then(res => setAllProducts(res.data))
  //     .catch(err => console.error("상품 목록 불러오기 실패", err));
  // }, []);

  const location = useLocation();
  const navigate = useNavigate();
  const params = new URLSearchParams(location.search);

  // 파라미터 추출 (기본값 없이)
  const keyword = params.get('keyword');
  const area = params.get('area');
  const category = params.get('category');
  const minPrice = params.get('minPrice');
  const maxPrice = params.get('maxPrice');
  const page = Number(params.get('page'));
  const size = Number(params.get('size'));

  // 필터링: status가 SALE이고, 파라미터와 일치하는 값만
  const filteredProducts = FilterProducts( allProducts,
    { keyword, area, category, minPrice, maxPrice });

  // createdAt 기준 내림차순 정렬
  const sortedProducts = filteredProducts.slice().sort(
    (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
  );
  console.log(sortedProducts.map(p => p.createdAt));

  // 페이지네이션 적용
  const pagedProducts = sortedProducts.slice(page * size, (page + 1) * size);
  const totalPages = Math.ceil(sortedProducts.length / size);

  // 페이지 이동 함수
  const goToPage = (newPage) => {
    params.set('page', newPage);
    navigate({ search: params.toString() });
  };

  return (
    <Box sx={{ p: 3, minHeight: 'calc(100vh - 80px)', pb: 10 }}>
      <Header />
      <Box sx={{ height: 72 }} />
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'center',
            width: 1100,
            mx: 'auto',
            pt: 2,
            pb: 2,
            pl: 5,
            position: 'relative',
          }}
        >
        {pagedProducts.length === 0 ? (
          <Box sx={{ width: '100%', py: 10, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <Typography variant="h6" color="text.secondary">
              해당하는 상품을 찾을 수 없습니다.
            </Typography>
          </Box>
        ) : (
          <ProductsList
          products={pagedProducts}
          onProductClick={(id) => navigate(`/products/${id}`)}
          formatTime={FormatTime}
        />
        )}
      </Box>
      {/* 페이지네이션 버튼을 제품 박스 아래로 이동 */}
      {totalPages > 1 && (
        <PaginationBar page={page} totalPages={totalPages} goToPage={goToPage} />
      )}
    </Box>
  );
};

export default ProductsPage;
import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Divider,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import AddPhotoAlternateIcon from '@mui/icons-material/AddPhotoAlternate';
import MenuItem from '@mui/material/MenuItem';
import CloseIcon from '@mui/icons-material/Close';
import { PriceInput } from '../utils/PriceInput';
import UnprocessableEntity from '../err/UnprocessableEntity';
import { getCategories } from '../api/category';

const MAX_IMAGES = 10;
const MAX_PRODUCTS = 5; // 사용자당 최대 상품 수

// 금지 품목 예시
const BANNED_WORDS = ['마약', '위조', '불법'];

const ProductRegisterPage = () => {
  const [form, setForm] = useState({
    title: '',
    content: '',
    price: '',
    categoryId: '',
    images: [],
  });
  const [loading, setLoading] = useState(false);
  const [userProfile, setUserProfile] = useState(false);
  const [categories, setCategories] = useState([]); // 백엔드에서 받아올 카테고리 목록 상태
  const fileInputRef = useRef();
  const navigate = useNavigate();

  // accessToken에서 userId 추출
  const token = localStorage.getItem('accessToken');
  let userId = '';
  if (token) {
    try {
      const payload = jwtDecode(token);
      userId = payload.sub; // JWT의 sub가 userId
    } catch (e) {
      e.stopPropagation();
      userId = '';
    }
  }

  // 사용자 정보 및 카테고리 목록 불러오기
  useEffect(() => {
    window.scrollTo(0, 0);

    // 사용자 정보 불러오기
    if (userId) {
      axios
        .get(`/api/users/profile`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((res) => {
          setUserProfile({
            is_banned: String(res.data.is_banned),
            userProductCount: res.data.userProductCount,
          });
        })
        .catch(() => {
          setUserProfile({
            is_banned: 'false',
            userProductCount: 0,
          });
        });
    }

    // 카테고리 목록 불러오기
    const fetchCategories = async () => {
      try {
        const responseData = await getCategories();
        if (responseData && Array.isArray(responseData.data)) {
          setCategories(responseData.data);
        } else {
          console.error('Unexpected categories data format:', responseData);
          setCategories([]);
        }
      } catch (error) {
        console.error('카테고리 목록을 불러오는데 실패했습니다.', error);
        setCategories([]);
      }
    };
    fetchCategories();
  }, [userId, token]);

  // 금지 품목 검사 함수 - 인자를 form 상태의 키에 맞춰 수정
  const isBannedProduct = (title, content) => {
    return BANNED_WORDS.some((word) => title.includes(word) || content.includes(word));
  };

  // 이미지 파일 선택 핸들러
  const handleImageSelect = (e) => {
    const files = Array.from(e.target.files);
    if (form.images.length + files.length > MAX_IMAGES) {
      UnprocessableEntity(`이미지는 최대 ${MAX_IMAGES}장까지 등록할 수 있습니다.`);
      return;
    }
    const newImages = files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
    }));
    setForm((prev) => ({
      ...prev,
      images: [...prev.images, ...newImages],
    }));
  };

  // 이미지 삭제
  const handleRemoveImage = (idx) => {
    setForm((prev) => ({
      ...prev,
      images: prev.images.filter((_, i) => i !== idx),
    }));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'category') {
      // TextField의 name은 'category'로 유지됩니다.
      setForm((prev) => ({ ...prev, categoryId: value })); // form.categoryId에 저장
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 1. 이미지 개수 검사
    if (form.images.length > MAX_IMAGES) {
      UnprocessableEntity(`이미지는 최대 ${MAX_IMAGES}장까지 등록할 수 있습니다.`);
      return;
    }

    // 2. 사용자당 등록 상품 최대 5개 검사
    if (userProfile.userProductCount >= MAX_PRODUCTS) {
      UnprocessableEntity('사용자당 등록 상품은 최대 5개입니다.');
      return;
    }

    // 3. 금지 품목 검사 - form.content를 인자로 전달
    if (isBannedProduct(form.title, form.content)) {
      // form.description -> form.content로 변경
      UnprocessableEntity('금지 품목은 등록할 수 없습니다.');
      return;
    }

    // 4. 정지 계정 검사
    if (userProfile.is_banned === 'true') {
      UnprocessableEntity('정지된 계정은 상품을 등록할 수 없습니다.');
      return;
    }

    setLoading(true);
    let productId = null; // 생성된 상품의 ID를 저장할 변수

    try {
      // 1. 상품 정보만 백엔드로 POST (이미지 제외)
      const productFormData = new FormData();
      productFormData.append('title', form.title);
      productFormData.append('content', form.content);
      productFormData.append('price', form.price);
      productFormData.append('categoryId', form.categoryId);

      const productRes = await axios.post(
        `http://localhost:8080/api/users/products`,
        productFormData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        }
      );

      // 백엔드 응답에서 생성된 productId 추출 (이 부분은 이전과 동일하게 백엔드 응답 구조에 맞게)
      if (productRes.data && productRes.data.id) {
        productId = productRes.data.id;
        console.log('생성된 상품 ID:', productId);
      } else {
        console.warn(
          '상품 등록 성공했으나 productId를 응답에서 찾을 수 없습니다.',
          productRes.data
        );
        alert('상품은 등록되었으나 이미지 연결에 문제가 있을 수 있습니다.');
        setLoading(false);
        navigate('/');
        return;
      }

      // 2. 이미지가 있다면 모든 이미지를 하나의 요청으로 '/images' 엔드포인트로 POST
      if (form.images.length > 0 && productId) {
        const imageUploadFormData = new FormData();
        imageUploadFormData.append('productId', productId); // 생성된 productId 전달

        // 모든 이미지 파일을 'images'라는 이름으로 FormData에 추가
        form.images.forEach((img) => {
          imageUploadFormData.append('images', img.file); // 백엔드 컨트롤러의 @RequestPart("images")와 매칭
        });

        console.log(`이미지들 POST 요청 시작 (productId: ${productId})`);

        try {
          const imageRes = await axios.post(
            `http://localhost:8080/images`, // ImageController의 엔드포인트
            imageUploadFormData,
            {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'multipart/form-data',
              },
            }
          );
          console.log(`모든 이미지 등록 성공:`, imageRes.data);
        } catch (imageError) {
          console.error(
            '이미지 등록 실패:',
            imageError.response ? imageError.response.data : imageError.message
          );
          alert('이미지 등록 중 오류가 발생했습니다.');
          // 이미지 등록 실패 시 상품 등록 전체를 취소할지는 비즈니스 로직에 따라 결정
          // 여기서는 일단 상품은 등록되었으니 진행
        }
      }

      // 모든 과정 성공 시 최종 알림 및 리다이렉트
      alert('상품 등록이 완료되었습니다.');
      navigate('/');
    } catch (e) {
      console.error('상품 등록 최종 오류 발생:', e.response ? e.response.data : e.message);
      if (e.response && e.response.status === 422) {
        // UnprocessableEntity(); // 422 처리
        alert('입력 값이 유효하지 않습니다.'); // 사용자에게 보여줄 메시지
      } else {
        alert('상품 등록 중 오류가 발생했습니다. 다시 시도해 주세요.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ width: 1100, mx: 'auto', p: 3 }}>
      <Typography variant="h4" fontWeight={700} mb={4} ml={4} align="left">
        상품 등록
      </Typography>
      <Divider sx={{ mb: 4, borderColor: '#222', borderWidth: 2 }} />

      {/* 상품이미지 */}
      <Box sx={{ mb: 4 }}>
        <Box display="flex" alignItems="flex-start">
          {/* 상품이미지 텍스트 */}
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              minWidth: 120,
              width: 120,
              height: 200,
              mr: 2,
              flexShrink: 0,
            }}
          >
            <Typography fontWeight={700} fontSize={18}>
              상품이미지{' '}
              <Typography component="span" color="#888" fontWeight={400} display="inline">
                ({form.images.length}/{MAX_IMAGES})
              </Typography>
            </Typography>
          </Box>
          {/* 이미지 등록 버튼 + 미리보기 이미지들 */}
          <Box
            sx={{ flex: 1, display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'flex-start' }}
          >
            <Paper
              variant="outlined"
              sx={{
                width: 200,
                height: 200,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexDirection: 'column',
                cursor: 'pointer',
                position: 'relative',
                minWidth: 200,
                minHeight: 200,
                bgcolor: '#fafafa',
                borderRadius: 0,
              }}
              onClick={() => fileInputRef.current.click()}
            >
              <input
                type="file"
                accept="image/*"
                multiple
                ref={fileInputRef}
                style={{ display: 'none' }}
                onChange={handleImageSelect}
              />
              <AddPhotoAlternateIcon sx={{ fontSize: 48, color: '#bbb' }} />
              <Typography mt={1.5} fontSize={18} color="#888">
                이미지 등록
              </Typography>
            </Paper>
            {form.images.map((img, idx) => (
              <Paper
                key={idx}
                variant="outlined"
                sx={{
                  width: 200,
                  height: 200,
                  minWidth: 200,
                  minHeight: 200,
                  position: 'relative',
                  overflow: 'hidden',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  bgcolor: '#fafafa',
                  borderRadius: 0,
                }}
              >
                {idx === 0 && (
                  <Box
                    sx={{
                      position: 'absolute',
                      top: 8,
                      left: 8,
                      background: 'rgba(0,0,0,0.6)',
                      color: '#fff',
                      fontSize: 13,
                      px: 1.5,
                      py: 0.5,
                      borderRadius: 2,
                      zIndex: 2,
                    }}
                  >
                    대표이미지
                  </Box>
                )}
                <Box
                  component="img"
                  src={img.url}
                  alt={`상품이미지${idx + 1}`}
                  sx={{
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                  }}
                />
                <IconButton
                  size="small"
                  onClick={() => handleRemoveImage(idx)}
                  sx={{
                    position: 'absolute',
                    top: 8,
                    right: 8,
                    background: 'rgba(0,0,0,0.6)',
                    color: '#fff',
                    '&:hover': { background: 'rgba(0,0,0,0.8)' },
                    zIndex: 2,
                  }}
                >
                  <CloseIcon fontSize="medium" />
                </IconButton>
              </Paper>
            ))}
          </Box>
        </Box>
      </Box>
      <Divider sx={{ my: 3, borderColor: '#eee', borderWidth: 1.5 }} />

      {/* 상품명 */}
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 2 }}>
        <Typography fontWeight={700} sx={{ minWidth: 100 }}>
          상품명
        </Typography>
        <TextField
          name="title"
          value={form.title}
          onChange={handleChange}
          required
          fullWidth
          placeholder="상품명을 입력해 주세요."
          inputProps={{ maxLength: 40 }}
          sx={{ flex: 1 }}
        />
        <Typography variant="body2" color="#888" sx={{ ml: 1 }}>
          {form.title.length}/40
        </Typography>
      </Box>
      <Divider sx={{ my: 3, borderColor: '#eee', borderWidth: 1.5 }} />

      {/* 카테고리 */}
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'flex-start', gap: 2 }}>
        <Typography fontWeight={700} sx={{ minWidth: 100, mt: 2 }}>
          카테고리
        </Typography>
        <Box sx={{ flex: 1 }}>
          <TextField
            select
            name="category"
            value={form.category} // form.category의 초기값이 ""이므로 문제없음
            onChange={handleChange}
            fullWidth
            SelectProps={{
              displayEmpty: true,
              renderValue: (selected) => {
                if (selected === '') {
                  return '카테고리 선택';
                }
                const selectedCategory = categories.find((opt) => opt.categoryId === selected);
                return selectedCategory ? selectedCategory.categoryName : '카테고리 선택';
              },
            }}
          >
            {/* 첫 번째 MenuItem은 선택 유도를 위한 빈 값 */}
            <MenuItem value="" disabled>
              카테고리 선택
            </MenuItem>
            {/* categories 배열이 로드되면 MenuItem을 렌더링 */}
            {categories.map((opt) => (
              <MenuItem key={opt.categoryId} value={opt.categoryId}>
                {opt.categoryName}
              </MenuItem>
            ))}
          </TextField>
        </Box>
      </Box>
      <Divider sx={{ my: 3, borderColor: '#eee', borderWidth: 1.5 }} />

      {/* 설명 */}
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'flex-start', gap: 2 }}>
        <Typography fontWeight={700} sx={{ minWidth: 100, mt: 1 }}>
          설명
        </Typography>
        <TextField
          name="content"
          value={form.content}
          onChange={handleChange}
          required
          fullWidth
          multiline
          minRows={4}
          placeholder="상품 설명을 입력해 주세요."
          sx={{ flex: 1 }}
        />
      </Box>
      <Divider sx={{ my: 3, borderColor: '#eee', borderWidth: 1.5 }} />

      {/* 가격 */}
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 2 }}>
        <Typography fontWeight={700} sx={{ minWidth: 100 }}>
          가격
        </Typography>
        <TextField
          name="price"
          type="number"
          value={form.price}
          onChange={(e) =>
            setForm((prev) => ({
              ...prev,
              price: PriceInput(e.target.value),
            }))
          }
          required
          fullWidth
          InputProps={{
            startAdornment: <InputAdornment position="start">₩</InputAdornment>,
            inputProps: { min: 0 },
          }}
          sx={{ flex: 1 }}
        />
      </Box>

      {/* 등록 버튼 */}
      <Box display="flex" justifyContent="center">
        <Button
          type="submit"
          variant="contained"
          color="primary"
          size="large"
          disabled={loading}
          sx={{ width: 180, height: 48, fontWeight: 700, fontSize: 18, mt: 2 }}
        >
          {loading ? '등록 중...' : '상품 등록'}
        </Button>
      </Box>
    </Box>
  );
};

export default ProductRegisterPage;

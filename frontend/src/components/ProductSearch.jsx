import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import AreaSelectModal from './AreaSelectModal';
import KeywordSearch from './KeywordSearch';
import CategorySelect from './CategorySelect';
import { CATEGORY_OPTIONS } from '../constants/CategoryOptions';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import PriceToggleButton from './PriceToggleButton';
import SearchIcon from '@mui/icons-material/Search';
import { Box, Button, IconButton, Paper } from '@mui/material';

const ProductSearch = () => {
    const [modalOpen, setModalOpen] = useState(false);
    const [selected, setSelected] = useState(null);
    const [keyword, setKeyword] = useState('');
    const [category, setCategory] = useState('');
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [showPriceInputs, setShowPriceInputs] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    React.useEffect(() => {
        const params = new URLSearchParams(location.search);
        setKeyword(params.get('keyword') || '');
        setCategory(params.get('category') || '');
        setMinPrice(params.get('minPrice') || '');
        setMaxPrice(params.get('maxPrice') || '');
    }, [location.search]);

    const handleSearch = () => {
        const params = new URLSearchParams();
        params.set('page', 0);
        params.set('size', 10);
        params.set('sort', 'createdAt,desc');
        if (selected) params.set(
            'area',
            [selected.시도명, selected.시군구명, selected.읍면동명].filter(Boolean).join(' ')
        );
        if (keyword) params.set('keyword', keyword);
        if (category) params.set('category', category);
        if (minPrice) params.set('minPrice', minPrice);
        if (maxPrice) params.set('maxPrice', maxPrice);
        if (minPrice && maxPrice && Number(minPrice) > Number(maxPrice)) {
            alert('최소 가격은 최대 가격보다 작거나 같아야 합니다.');
            return;
        }
        navigate(`/products?${params.toString()}`);
    };

    return (
        <Box sx={{ display: 'flex', gap: 1.5, alignItems: 'flex-start', mt: 1.25, mr: 4, position: 'relative' }}>
            {/* 카테고리 입력 */}
            <CategorySelect
                value={category}
                onChange={e => setCategory(e.target.value)}
                options={CATEGORY_OPTIONS}
            />
            {/* 지역 선택 버튼 */}
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                <Button
                    onClick={() => setModalOpen(true)}
                    variant="outlined"
                    sx={{
                        borderRadius: 25,
                        background: '#fff',
                        color: '#222',
                        fontWeight: 700,
                        textTransform: 'none',
                        borderColor: '#eee',
                        fontSize: 16,
                        display: 'flex',
                        alignItems: 'center',
                        '&:hover': { borderColor: '#EA002C', background: '#fafafa' }
                    }}
                >
                    <LocationOnIcon sx={{ color: '#EA002C', fontSize: 26, ml:-1 }} />
                    {selected ? selected.읍면동명 : '지역 선택'}
                </Button>
            </Box>
            {/* 검색 입력 영역 */}
            <Paper
                elevation={1}
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    minWidth: 550,
                    borderRadius: 25,
                    px: 0.2,
                    py: 0.5,
                    mr: 4,
                    boxShadow: '0 1px 4px rgba(0,0,0,0.07)',
                    position: 'relative',
                    bgcolor: '#fff',
                    height: 42
                }}
            >
                {/* ₩ 버튼 (왼쪽 끝) */}
                <PriceToggleButton
                    showPriceInputs={showPriceInputs}
                    setShowPriceInputs={setShowPriceInputs}
                    minPrice={minPrice}
                    setMinPrice={setMinPrice}
                    maxPrice={maxPrice}
                    setMaxPrice={setMaxPrice}
                />
                {/* 키워드 입력창 (가운데) */}
                <KeywordSearch
                    value={keyword}
                    onChange={e => setKeyword(e.target.value)}
                    onKeyDown={e => {
                        if (e.key === 'Enter') handleSearch();
                    }}
                    style={{
                        zIndex: 1,
                        background: 'transparent',
                        height: 32,
                        fontSize: 16
                    }}
                />
                {/* 검색 버튼 (오른쪽 끝) */}
                <IconButton
                    onClick={handleSearch}
                    sx={{
                        height: 40,
                        width: 40,
                        bgcolor: '#EA002C',
                        color: '#fff',
                        border: 'none',
                        borderRadius: 25,
                        '&:hover': { bgcolor: '#c40024' }
                    }}
                    aria-label="검색"
                >
                    <SearchIcon />
                </IconButton>
            </Paper>
            {/* 지역 선택 모달 */}
            {modalOpen && (
                <AreaSelectModal
                    onSelect={area => {
                        setSelected(area);
                        setModalOpen(false);
                    }}
                    onClose={() => setModalOpen(false)}
                    onReset={() => {
                        setSelected(null);
                        setModalOpen(false);
                    }}
                />
            )}
        </Box>
    );
};

export default ProductSearch;
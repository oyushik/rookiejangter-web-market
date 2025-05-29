import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import AreaSelectModal from './AreaSelectModal';
import KeywordSearch from './KeywordSearch';
import CategorySelect from './CategorySelect';
import { CATEGORY_OPTIONS } from '../constants/CategoryOptions';
import PriceInput from './PriceInput';


const ProductSearch = () => {
    const [modalOpen, setModalOpen] = useState(false); // 지역 선택 모달 열림/닫힘
    const [selected, setSelected] = useState(null);    // 선택된 지역 객체
    const [keyword, setKeyword] = useState('');        // 키워드 검색어
    const [category, setCategory] = useState('');      // 카테고리
    const [minPrice, setMinPrice] = useState('');      // 최소 가격
    const [maxPrice, setMaxPrice] = useState('');      // 최대 가격
    const navigate = useNavigate();
    const location = useLocation();

    // URL 쿼리 파라미터가 변경될 때 입력값 동기화
    React.useEffect(() => {
        const params = new URLSearchParams(location.search);
        setKeyword(params.get('keyword') || '');
        setCategory(params.get('category') || '');
        setMinPrice(params.get('minPrice') || '');
        setMaxPrice(params.get('maxPrice') || '');
    }, [location.search]);

    // 검색 버튼 클릭 시 쿼리 파라미터로 이동
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
        // 최소 =< 최대 가격 유효성 검사
        if (minPrice && maxPrice && Number(minPrice) > Number(maxPrice)) {
            alert('최소 가격은 최대 가격보다 작거나 같아야 합니다.');
            return;
        }

        navigate(`/products?${params.toString()}`);
    };

    return (
        <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
            {/* 지역 선택 버튼 */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                <button onClick={() => setModalOpen(true)}>
                    {selected ? selected.읍면동명 : '지역 선택'}
                </button>
                {/* 카테고리 입력 */}
                <CategorySelect
                    value={category}
                    onChange={e => setCategory(e.target.value)}
                    options={CATEGORY_OPTIONS}
                />
            </div>
            {/* 키워드 검색 입력창 */}
            <KeywordSearch
                value={keyword}
                onChange={e => setKeyword(e.target.value)}
                onSearch={handleSearch}
            />
            {/* 최소 가격 입력 */}
            <PriceInput
                value={minPrice}
                onChange={setMinPrice}
                placeholder="최소 가격"
            />
            {/* 최대 가격 입력 */}
            <PriceInput
                value={maxPrice}
                onChange={setMaxPrice}
                placeholder="최대 가격"
            />
            {/* 검색 버튼 */}
            <button onClick={handleSearch}>검색</button>
            {/* 지역 선택 모달 */}
            {modalOpen && (
                <AreaSelectModal
                    onSelect={area => {
                        setSelected(area);
                        setModalOpen(false);
                    }}
                    onClose={() => setModalOpen(false)}
                />
            )}
        </div>
    );
};

export default ProductSearch;
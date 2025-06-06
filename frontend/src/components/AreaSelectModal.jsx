import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import FormErrorSnackbar from '../components/FormErrorSnackbar';

const PAGE_SIZE = 20;
const KAKAO_REST_API_KEY = import.meta.env.VITE_KAKAO_REST_API_KEY; // 실제 키로 교체 필요

// region_1depth_name → areaName 변환 맵
const REGION_TO_AREANAME = {
    '서울': '서울특별시',
    '부산': '부산광역시',
    '대구': '대구광역시',
    '인천': '인천광역시',
    '광주': '광주광역시',
    '대전': '대전광역시',
    '울산': '울산광역시',
    '세종': '세종특별자치시',
    '경기': '경기도',
    '강원': '강원특별자치도',
    '충북': '충청북도',
    '충남': '충청남도',
    '전북': '전북특별자치도',
    '전남': '전라남도',
    '경북': '경상북도',
    '경남': '경상남도',
    '제주': '제주특별자치도',
};

const AreaSelectModal = ({ onSelect, onClose, onReset }) => {
    const [area, setArea] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [page, setPage] = useState(0);
    const [myLocation, setMyLocation] = useState(null);
    const [locLoading, setLocLoading] = useState(false);
    const [locError, setLocError] = useState('');
    const [resetHover, setResetHover] = useState(false);
    const [hoveredIdx, setHoveredIdx] = useState(-1);

    // 에러 스낵바 상태
    const [fetchError, setFetchError] = useState(false);

    // 시도 데이터 백엔드에서 불러오기, 실패 시 에러 스낵바 표시
    const [sidoList, setSidoList] = useState([]);
    useEffect(() => {
        axios.get('http://localhost:8080/areas')
            .then(res => {
                const arr = Array.isArray(res.data.data) ? res.data.data : [];
                setSidoList(arr.map(item => ({
                    areaId: item.areaId ?? item.value,
                    areaName: item.areaName ?? item.label
                })));
            })
            .catch(() => {
                setFetchError(true);
            });
    }, []);

    const handleAreaChange = (e) => {
        const value = e.target.value.replace(/\s+/g, ' ').trim().toLowerCase();
        setArea(e.target.value);
        setPage(0);

        if (value.length < 1) {
            setSuggestions([]);
            setShowSuggestions(false);
            return;
        }

        // areaName만 필터
        const filtered = sidoList.filter(item => {
            if (!item.areaName || item.areaName.length === 0) return false;
            return item.areaName.toLowerCase().includes(value);
        });
        setSuggestions(filtered);
        setShowSuggestions(true);
    };

    const handleSuggestionClick = (suggestion) => {
        onSelect({ areaName: suggestion.areaName });
    };

    // 내 위치 찾기 버튼 클릭 시 (areaName만 추출, 정식명칭 변환)
    const handleFindMyLocation = async () => {
        setLocLoading(true);
        setLocError('');
        setMyLocation(null);
        try {
            const getPosition = () =>
                new Promise((resolve, reject) => {
                    if (!navigator.geolocation) {
                        reject(new Error('이 브라우저에서는 위치 정보를 지원하지 않습니다.'));
                    } else {
                        navigator.geolocation.getCurrentPosition(
                            pos => resolve(pos),
                            err => reject(err)
                        );
                    }
                });

            const pos = await getPosition();
            const lat = pos.coords.latitude;
            const lng = pos.coords.longitude;

            const kakaoRes = await axios.get(
                `https://dapi.kakao.com/v2/local/geo/coord2address.json?x=${lng}&y=${lat}`,
                {
                    headers: {
                        Authorization: `KakaoAK ${KAKAO_REST_API_KEY}`,
                    },
                }
            );

            const addressInfo = kakaoRes.data.documents[0]?.address;
            if (!addressInfo) throw new Error('주소 정보를 찾을 수 없습니다.');

            const region1 = addressInfo.region_1depth_name;
            const areaName = REGION_TO_AREANAME[region1] || region1;

            setMyLocation({
                areaName,
                lat,
                lng,
            });
        } catch (err) {
            setLocError('내 위치를 찾을 수 없습니다.');
            console.error(err);
        } finally {
            setLocLoading(false);
        }
    };

    return (
        <div
            style={{
                position: 'fixed',
                top: 0, left: 0, right: 0, bottom: 0,
                background: 'rgba(0,0,0,0.3)',
                zIndex: 1000,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
            }}
            onClick={onClose}
        >
            <div
                style={{
                    background: '#fff',
                    padding: 24,
                    borderRadius: 8,
                    minWidth: 350,
                    maxWidth: 400,
                    boxShadow: '0 2px 12px rgba(0,0,0,0.15)',
                    position: 'relative'
                }}
                onClick={e => e.stopPropagation()}
            >
                <input
                    type="text"
                    placeholder="시도명 입력"
                    value={area}
                    onChange={handleAreaChange}
                    style={{ width: '100%', marginBottom: 8, height: 40, borderRadius: 5 }}
                    autoFocus
                />
                
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    {/* 내 위치 찾기 버튼 */}
                    <button
                        style={{ width: '100%', marginBottom: 8, background: '#f0f0f0', padding: 8, borderRadius: 4 }}
                        onClick={handleFindMyLocation}
                        disabled={locLoading}
                    >
                        {locLoading ? '내 위치 찾는 중...' : '내 위치 찾기'}
                    </button>

                    {/* 지역 선택 초기화 버튼 */}
                    <button
                        style={{
                            width: '100%',
                            marginBottom: 8,
                            background: '#fff',
                            padding: 8,
                            borderRadius: 4,
                            border: `1px solid ${resetHover ? '#EA002C' : '#ccc'}`,
                            color: '#EA002C',
                            transition: 'border-color 0.2s'
                        }}
                        onMouseEnter={() => setResetHover(true)}
                        onMouseLeave={() => setResetHover(false)}
                        onClick={() => {
                            setArea('');
                            setSuggestions([]);
                            setShowSuggestions(false);
                            setMyLocation(null);
                            if (onReset) onReset();
                        }}
                    >
                        지역 선택 초기화
                    </button>
                </div>

                {/* 내 위치 결과 시각화 */}
                {locError && <div style={{ color: 'red', marginBottom: 8 }}>{locError}</div>}
                {myLocation && (
                    <div style={{ marginBottom: 8, background: '#f9f9f9', padding: 8, borderRadius: 4 }}>
                        <button
                            style={{fontSize: 20, width: 350 }}
                            onClick={() => onSelect({ areaName: myLocation.areaName })}>
                            내 위치: {myLocation.areaName}
                        </button>
                    </div>
                )}
                <ul
                    style={{
                        border: '1px solid #ccc',
                        maxHeight: 200,
                        overflowY: 'auto',
                        margin: 0,
                        padding: 0,
                        listStyle: 'none',
                        background: '#fff',
                        position: 'relative'
                    }}
                >
                    {(area.trim().length > 0 ? suggestions : sidoList).map((s, idx) => (
                        <li
                            key={s.areaId || idx}
                            style={{
                                padding: 8,
                                cursor: 'pointer',
                                background: hoveredIdx === idx ? '#f0f0f0' : '#fff',
                                transition: 'background 0.15s'
                            }}
                            onMouseEnter={() => setHoveredIdx(idx)}
                            onMouseLeave={() => setHoveredIdx(-1)}
                            onClick={() => handleSuggestionClick(s)}
                        >
                            {s.areaName}
                        </li>
                    ))}
                </ul>

                <button
                    style={{
                        position: 'absolute',
                        top: 4,
                        right: 4,
                        padding: 0,
                        background: 'none',
                        border: 'none',
                        cursor: 'pointer',
                    }}
                    onClick={onClose}
                >
                    <CloseRoundedIcon fontSize="medium" />
                </button>
                <FormErrorSnackbar
                    open={fetchError}
                    message="지역 정보를 불러올 수 없습니다."
                    onClose={() => setFetchError(false)}
                />
            </div>
        </div>
    );
};

export default AreaSelectModal;
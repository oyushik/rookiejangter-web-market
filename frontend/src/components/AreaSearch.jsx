import { useEffect, useRef, useState } from 'react';

// 예제 품목 데이터
const exampleItemsByArea = {
  '서초동': ['중고 책', '전자기기', '자전거'],
  '삼성동': ['스마트폰', '노트북', '카메라'],
  '잠실동': ['운동기구', '의자', '책상'],
  '역삼동': ['프린터', 'PC 부품'],
  '노원구': [],
};

const areaHierarchy = {
  '서울': ['서초동', '삼성동', '잠실동', '역삼동', '노원구'],
};

const SearchArea = () => {
  const [input, setInput] = useState('');
  const [selectedArea, setSelectedArea] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [mapLoaded, setMapLoaded] = useState(false);
  const mapRef = useRef(null);

  // Kakao SDK 로드
  useEffect(() => {
    const kakaoKey = import.meta.env.VITE_KAKAO_API_KEY;

    if (!window.kakao) {
      const script = document.createElement('script');
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoKey}&autoload=false&libraries=services`;
      script.onload = () => {
        window.kakao.maps.load(() => setMapLoaded(true));
      };
      document.head.appendChild(script);
    } else {
      window.kakao.maps.load(() => setMapLoaded(true));
    }
  }, []);

  // 내 위치 받아서 selectedArea 초기 세팅
  useEffect(() => {
    if (!mapLoaded) return;

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          const geocoder = new window.kakao.maps.services.Geocoder();

          geocoder.coord2RegionCode(longitude, latitude, (result, status) => {
            if (status === window.kakao.maps.services.Status.OK) {
              // 동/읍/리 명 가져오기
              const region = result.find(
                (r) => r.region_type === 'H' || r.region_type === 'B'
              );
              if (region) {
                setSelectedArea(region.region_3depth_name);
                setInput('');
              }
            }
          });
        },
        () => {
          alert('위치 정보를 가져올 수 없습니다.');
        }
      );
    }
  }, [mapLoaded]);

  const fetchProducts = async () => {
    const area = input.trim() || selectedArea;
    if (!area) return;

    setLoading(true);
    setSelectedArea(area);

    // 더미 데이터 기반 필터링
    let result = [];

    // 1. 상위 지역 키워드 ("서울" 등) 매칭
    if (areaHierarchy[area]) {
      areaHierarchy[area].forEach((subArea) => {
        if (exampleItemsByArea[subArea]) {
          exampleItemsByArea[subArea].forEach((item) => {
            result.push({ area: subArea, name: item });
          });
        }
      });
    } else {
      // 2. 입력된 주소에서 하위 동네명 추출 후 매칭
      Object.entries(exampleItemsByArea).forEach(([areaName, items]) => {
        if (area.includes(areaName) || areaName.includes(area)) {
          items.forEach((item) => {
            result.push({ area: areaName, name: item });
          });
        }
      });
    }

    // 실제 API 호출 예시
    /*
    const query = new URLSearchParams({
      page: 0,
      size: 10,
      area: area,
      keyword: input,
    });

    try {
      const res = await fetch(`/api/products?${query.toString()}`);
      const data = await res.json();
      setProducts(data.content);
    } catch (err) {
      console.error('API 요청 실패:', err);
    }
    */

    setProducts(result);
    setLoading(false);
  };

  const handleSearch = () => {
    fetchProducts();
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') handleSearch();
  };

  // selectedArea가 바뀌면 지도도 업데이트 + 자동 검색 실행
  useEffect(() => {
    if (!mapLoaded || !selectedArea) return;

    // 지도 표시
    const geocoder = new window.kakao.maps.services.Geocoder();
    geocoder.addressSearch(selectedArea, (result, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        const coords = new window.kakao.maps.LatLng(result[0].y, result[0].x);
        const map = new window.kakao.maps.Map(mapRef.current, {
          center: coords,
          level: 6,
        });

        new window.kakao.maps.Marker({ map, position: coords });

        new window.kakao.maps.CustomOverlay({
          map,
          position: coords,
          content: `<div style="padding:6px 12px; background:white; border:1px solid #333; border-radius:4px;">
            📍 ${selectedArea}
          </div>`,
          yAnchor: 1.5,
        });
      } else {
        alert('해당 지역을 찾을 수 없습니다.');
      }
    });

    fetchProducts();
  }, [mapLoaded, selectedArea]);

  // products 배열을 지역별로 그룹핑
  const groupedProducts = products.reduce((acc, cur) => {
    if (!acc[cur.area]) acc[cur.area] = [];
    acc[cur.area].push(cur.name);
    return acc;
  }, {});

  return (
    <div style={{ maxWidth: 600, margin: 'auto' }}>
      <h2>지역 검색</h2>

      {/* 검색창 */}
      <div style={{ display: 'flex', marginBottom: 12 }}>
        <input
          type="text"
          placeholder="지역명 (예: 서울, 봉담읍 등)"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyPress}
          style={{ flexGrow: 1, padding: 8, fontSize: 16 }}
        />
        <button onClick={handleSearch} style={{ marginLeft: 8, padding: '8px 14px' }}>
          🔍
        </button>
      </div>

      {loading ? (
        <div>🔄 로딩 중...</div>
      ) : (
        selectedArea && (
          <div style={{ marginBottom: 16 }}>
            <h3>📦 "{selectedArea}" 지역 관련 품목</h3>
            {Object.keys(groupedProducts).length > 0 ? (
              Object.entries(groupedProducts).map(([area, items]) => (
                <div key={area} style={{ marginBottom: 12 }}>
                  <strong style={{ fontSize: '18px' }}>{area}</strong>
                  {items.length > 0 ? (
                    <ul style={{ marginTop: 4, paddingLeft: 0 }}>
                      {items.map((item, idx) => (
                        <li key={idx}>{item}</li>
                      ))}
                    </ul>
                  ) : (
                    <div style={{ color: 'gray', marginTop: 4 }}>등록된 품목이 없습니다!</div>
                  )}
                </div>
              ))
            ) : (
              <div style={{ color: 'gray' }}>등록된 품목이 없습니다!</div>
            )}
          </div>
        )
      )}

      {/* 지도 */}
      <div
        ref={mapRef}
        id="map"
        style={{ width: 350, height: 350, border: '1px solid #ccc' }}
      />
    </div>
  );
};

export default SearchArea;

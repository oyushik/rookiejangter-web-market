import { useEffect, useRef, useState } from 'react';

// 예제 품목 데이터
const exampleItemsByArea = {
  '봉담읍': ['중고 가전제품', '중고 의류', '가구'],
  '서초동': ['중고 책', '전자기기', '자전거'],
  '삼성동': ['스마트폰', '노트북', '카메라'],
  '잠실동': ['운동기구', '의자', '책상'],
  '역삼동': ['프린터', 'PC 부품'],
};

const areaHierarchy = {
  '서울': ['서초동', '삼성동', '잠실동', '역삼동'],
  '경기': ['봉담읍'],
};

const SearchArea = () => {
  const [input, setInput] = useState('');
  const [selectedArea, setSelectedArea] = useState(null); // 초기 null
  const [filteredAreas, setFilteredAreas] = useState({});
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
              // 'H' 또는 'B' 타입인 동/읍/리 명 가져오기
              const region = result.find(
                (r) => r.region_type === 'H' || r.region_type === 'B'
              );
              if (region) {
                setSelectedArea(region.region_3depth_name); // 예: 봉담읍
                setInput(''); // 검색창 초기화
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

  const handleSearch = () => {
    // 검색창이 비어있으면 selectedArea 기준으로 검색
    const area = input.trim() || selectedArea;
    if (!area) return;

    setSelectedArea(area);

    let result = {};

    if (areaHierarchy[area]) {
      areaHierarchy[area].forEach((subArea) => {
        if (exampleItemsByArea[subArea]) {
          result[subArea] = exampleItemsByArea[subArea];
        }
      });
    } else {
      Object.entries(exampleItemsByArea).forEach(([areaName, items]) => {
        if (areaName.includes(area)) {
          result[areaName] = items;
        }
      });
    }

    setFilteredAreas(result);
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

    // 자동 검색 실행
    handleSearch();
  }, [mapLoaded, selectedArea]);

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

      {/* 지역별 품목 */}
      {Object.keys(filteredAreas).length > 0 && (
        <div style={{ marginBottom: 16 }}>
          <h3>📦 "{selectedArea}" 지역 관련 품목</h3>
          {Object.entries(filteredAreas).map(([area, items]) => (
            <div key={area} style={{ marginBottom: 12 }}>
              <strong style={{ fontSize: '18px' }}>{area}</strong>
              <ul style={{ marginTop: 4 }}>
                {items.map((item, idx) => (
                  <li key={idx}>{item}</li>
                ))}
              </ul>
            </div>
          ))}
        </div>
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
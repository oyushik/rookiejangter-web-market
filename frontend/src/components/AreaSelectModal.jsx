import React, { useState } from 'react';
import emdData from '../json/emd_code.json';

const PAGE_SIZE = 20;

const AreaSelectModal = ({ onSelect, onClose }) => {
    const [area, setArea] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [page, setPage] = useState(0);

    const handleAreaChange = (e) => {
        const value = e.target.value.replace(/\s+/g, ' ').trim().toLowerCase();
        setArea(e.target.value);
        setPage(0);

        if (value.length < 2) {
            setSuggestions([]);
            setShowSuggestions(false);
            return;
        }

        const filtered = emdData.filter(item => {
            if (!item.읍면동명 || item.읍면동명.length === 0) return false;
            if (item.삭제일자 && item.삭제일자 !== "") return false;
            const full = [item.시도명, item.시군구명, item.읍면동명]
                .filter(Boolean)
                .join(' ')
                .replace(/\s+/g, ' ')
                .toLowerCase();
            return full.includes(value);
        });
        setSuggestions(filtered);
        setShowSuggestions(true);
    };

    const handleSuggestionClick = (suggestion) => {
        onSelect(suggestion); // 부모에 값 전달
    };

    const pagedSuggestions = suggestions.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);
    const hasNext = (page + 1) * PAGE_SIZE < suggestions.length;
    const hasPrev = page > 0;

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
                    placeholder="지역명 입력"
                    value={area}
                    onChange={handleAreaChange}
                    style={{ width: '100%', marginBottom: 8 }}
                    autoFocus
                />
                {showSuggestions && pagedSuggestions.length > 0 && (
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
                        {pagedSuggestions.map((s, idx) => (
                            <li
                                key={idx}
                                style={{ padding: 8, cursor: 'pointer' }}
                                onClick={() => handleSuggestionClick(s)}
                            >
                                {[s.시도명, s.시군구명, s.읍면동명].filter(Boolean).join(' ')}
                            </li>
                        ))}
                        <li
                            style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                padding: 4,
                                background: '#f9f9f9',
                                position: 'sticky',
                                bottom: 0,
                                borderTop: '1px solid #eee',
                                zIndex: 1
                            }}
                        >
                            <div>
                                {hasPrev && (
                                    <button
                                        type="button"
                                        onMouseDown={e => {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            setPage(page - 1);
                                        }}>
                                        이전
                                    </button>
                                )}
                            </div>
                            <div style={{ marginLeft: 'auto' }}>
                                {hasNext && (
                                    <button
                                        type="button"
                                        onMouseDown={e => {
                                            e.preventDefault();
                                            e.stopPropagation();
                                            setPage(page + 1);
                                        }}>
                                        다음
                                    </button>
                                )}
                            </div>
                        </li>
                    </ul>
                )}
                <button
                    style={{ position: 'absolute', top: 8, right: 8 }}
                    onClick={onClose}
                >닫기</button>
            </div>
        </div>
    );
};

export default AreaSelectModal;
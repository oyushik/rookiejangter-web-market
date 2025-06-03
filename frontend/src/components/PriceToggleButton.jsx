import React from 'react';
import { PriceInput } from '../utils/PriceInput';

const PriceToggleButton = ({
  showPriceInputs,
  setShowPriceInputs,
  minPrice,
  setMinPrice,
  maxPrice,
  setMaxPrice
}) => (
  <div style={{ position: 'relative' }}>
    <button
      style={{
        width: 40,
        height: 40,
        border: 'none',
        background: showPriceInputs ? 'white' : 'black',
        color: showPriceInputs ? 'black' : 'white',
        borderRadius: 25,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        cursor: 'pointer',
        fontSize: 17,
        boxShadow: '0 1px 4px rgba(0,0,0,0.07)',
        transition: 'background 0.2s, color 0.2s'
      }}
      onClick={() => setShowPriceInputs(v => !v)}
      aria-label="가격 입력창 열기"
      type="button"
    >
      ₩
    </button>
    {showPriceInputs && (
      <div style={{
        display: 'flex',
        gap: 4,
        background: '#fafafa',
        border: 'solid 1px #ccc',
        borderRadius: 8,
        padding: 10,
        position: 'absolute',
        top: 50,
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 10,
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
      }}>
        <input
          type="text"
          value={minPrice}
          onChange={e => setMinPrice(PriceInput(e.target.value))}
          placeholder="최소 가격"
          style={{ width: 120, height: 30, border: 'solid 1px', borderRadius: 5 }}
        />
        ~
        <input
          type="text"
          value={maxPrice}
          onChange={e => setMaxPrice(PriceInput(e.target.value))}
          placeholder="최대 가격"
          style={{ width: 120, height: 30, border: 'solid 1px', borderRadius: 5 }}
        />
      </div>
    )}
  </div>
);

export default PriceToggleButton;
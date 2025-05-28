import React from 'react';

const PriceInput = ({ value, onChange, placeholder }) => {
    const handleChange = e => {
        const val = e.target.value;
        // 숫자만 입력, 음수/소수점 방지
        if (/^\d*$/.test(val)) {
            onChange(val);
        }
    };
    return (
        <input
            type="text"
            placeholder={placeholder}
            value={value}
            onChange={handleChange}
            style={{ width: 80 }}
        />
    );
};

export default PriceInput;
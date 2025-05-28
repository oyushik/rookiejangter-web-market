import React from 'react';

const CategorySelect = ({ value, onChange, options }) => (
    <select value={value} onChange={onChange} style={{ width: 100 }}>
        <option value="">카테고리</option>
        {options.map(opt => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
    </select>
);

export default CategorySelect;
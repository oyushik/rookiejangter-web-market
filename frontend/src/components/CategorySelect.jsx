import { useState } from 'react';

const CategorySelect = ({ value, onChange, options }) => {
    const [open, setOpen] = useState(false);

    return (
        <div
            style={{ position: 'relative', display: 'inline-block', width: 120 }}
            onMouseEnter={() => setOpen(true)}
            onMouseLeave={() => setOpen(false)}
        >
            {/* 모달 버튼과 동일한 스타일 */}
            <button
                style={{
                    width: '100%',
                    padding: 4,
                    fontSize: 13,
                    border: '1px solid #ccc',
                    borderRadius: 4,
                    background: '#fff',
                    cursor: 'pointer',
                    textAlign: 'center',
                    userSelect: 'none'
                }}
                type="button"
            >
                {value
                    ? options.find(opt => opt.value === value)?.label || '카테고리'
                    : '카테고리'}
            </button>
            {open && (
                <div
                    style={{
                        position: 'absolute',
                        top: '100%',
                        left: 0,
                        background: '#fff',
                        border: '1px solid #ccc',
                        borderRadius: 4,
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                        zIndex: 100,
                        minWidth: 120,
                        width: 120,
                    }}
                >
                    {options.map(opt => (
                        <div
                            key={opt.value}
                            style={{
                                padding: 4,
                                fontSize: 13,
                                cursor: 'pointer',
                                background: value === opt.value ? '#f0f0f0' : '#fff',
                                textAlign: 'center'
                            }}
                            onClick={() => {
                                onChange({ target: { value: opt.value } });
                                setOpen(false);
                            }}
                        >
                            {opt.label}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default CategorySelect;
import { useState } from 'react';
import MenuIcon from '@mui/icons-material/Menu';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';

const CategorySelect = ({ value, onChange, options }) => {
    const [anchorEl, setAnchorEl] = useState(null);

    // 현재 선택된 카테고리 라벨 찾기
    const selectedLabel = options.find(opt => opt.value === value)?.label || '카테고리';

    const handleOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleSelect = (optValue) => {
        if (value === optValue) {
            // 이미 선택된 값을 다시 누르면 해제
            onChange({ target: { value: "" } });
        } else {
            onChange({ target: { value: optValue } });
        }
        handleClose();
    };

    return (
        <div style={{ display: 'inline-block', paddingBottom: 12 }}>
            <button
                style={{
                    width: 36,
                    height: 36,
                    padding: 0,
                    marginTop: 3,
                    border: 'none',
                    background: 'none',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    boxShadow: 'none',
                }}
                type="button"
                aria-haspopup="true"
                aria-controls={anchorEl ? "category-menu" : undefined}
                aria-expanded={Boolean(anchorEl)}
                onClick={handleOpen}
            >
                <MenuIcon fontSize="large" />
            </button>
            <Menu
                id="category-menu"
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleClose}
                MenuListProps={{
                    'aria-labelledby': 'category-menu-button',
                }}
            >
                <MenuItem
                    disabled
                    selected={!value}
                    sx={{
                        fontWeight: 600,
                        color: value ? "#1976d2" : "#888",
                        fontSize: 15,
                    }}
                >
                    {selectedLabel}
                </MenuItem>
                {options.map(opt => (
                    <MenuItem
                        key={opt.value}
                        selected={value === opt.value}
                        onClick={() => handleSelect(opt.value)}
                        sx={{
                            fontSize: 14,
                            color: value === opt.value ? "#1976d2" : "inherit",
                            fontWeight: value === opt.value ? 700 : 400,
                        }}
                    >
                        {opt.label}
                    </MenuItem>
                ))}
            </Menu>
        </div>
    );
};

export default CategorySelect;
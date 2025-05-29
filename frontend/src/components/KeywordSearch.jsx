const KeywordSearch = ({ value, onChange, onSearch }) => {
    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && onSearch) {
            onSearch();
        }
    };

    return (
        <input
            type="text"
            placeholder="키워드 검색"
            value={value}
            onChange={onChange}
            onKeyDown={handleKeyDown}
            style={{ flex: 1, minWidth: 120 }}
        />
    );
};

export default KeywordSearch;
const KeywordSearch = ({ value, onChange, onKeyDown, style }) => (
  <input
    type="text"
    placeholder="키워드 검색"
    value={value}
    onChange={onChange}
    onKeyDown={onKeyDown}
    style={{
      flex: 1,
      border: 0,
      outline: 'none',
      padding: '0 12px',
      height: '100%',
      fontSize: 16,
      background: 'transparent',
      ...style
    }}
  />
);

export default KeywordSearch;
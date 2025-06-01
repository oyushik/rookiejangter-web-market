const buttonStyle = {
  width: 200,
  height: 60,
  padding: '8px 20px',
  borderRadius: 0,
  cursor: 'pointer',
  fontWeight: 700,
};

const ProductActions = () => (
  <div
    style={{
      position: 'absolute',
      left: 0,
      bottom: 0,
      width: '100%',
      display: 'flex',
      justifyContent: 'flex-end',
      gap: 12,
      paddingRight: 40,
      boxSizing: 'border-box',
      fontSize: 22,
      fontWeight: 700,
    }}
  >
    <button style={{ ...buttonStyle, border: '1px solid #e0e0e0', background: '#fff' }}>
      찜하기
    </button>
    <button style={{ ...buttonStyle, border: '1px solid #1976d2', background: '#1976d2', color: '#fff' }}>
      대화하기
    </button>
    <button style={{ ...buttonStyle, border: '1px solid #43a047', background: '#43a047', color: '#fff' }}>
      바로구매
    </button>
  </div>
);

export default ProductActions;
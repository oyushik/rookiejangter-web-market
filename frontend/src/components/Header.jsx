import { Box } from '@mui/material';
import { Link } from 'react-router-dom';
import ProductSearch from './ProductSearch';

const Header = () => (
  <Box
    sx={{
      mb: 3,
      display: 'flex',
      alignItems: 'center',
      gap: 2,
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      zIndex: 1200,
      bgcolor: '#fff',
      px: 3,
      py: 2,
      boxShadow: 1,
      justifyContent: 'flex-start',
    }}
  >
    <Box sx={{ flex: 1 }} />
    <Box
      component={Link}
      to="/"
      sx={{
        color: 'black',
        px: 2,
        py: 1,
        borderRadius: 1,
        fontWeight: 'bold',
        fontSize: 20,
        textDecoration: 'none',
        mr: 2,
      }}
    >
      루키장터
    </Box>
    <ProductSearch />
    <Box sx={{ flex: 2 }} />
  </Box>
);

export default Header;
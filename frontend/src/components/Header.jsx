import React from "react";
import { AppBar, Toolbar, Typography, Button, Box, InputBase } from "@mui/material";
import { useNavigate } from 'react-router-dom';
import { logoutUser } from '../api/auth';
import { Link } from 'react-router-dom';
import ProductSearch from './ProductSearch';

const Header = () => {
    const navigate = useNavigate();

    return (
        <>
            {/* 최상단 바: 마이페이지 / 로그아웃 */}
            <AppBar position="static" color="default" elevation={0} sx={{ height: 2, justifyContent: 'end' }}>
                <Toolbar sx={{ minHeight: '36px !important', px: 2, justifyContent: 'flex-end', gap: 2 }}>
                    <Button color="inherit" size="small" onClick={() => navigate('/mypage')}>마이페이지</Button>
                    <Button color="inherit" size="small" onClick={logoutUser}>로그아웃</Button>
                </Toolbar>
            </AppBar>

            {/* 고정 헤더 */}
            <AppBar position="sticky" color="primary" elevation={1} sx={{ top: 0, zIndex: theme => theme.zIndex.appBar }}>
                <Toolbar sx={{ justifyContent: 'space-between' }}>
                    <Typography variant="h6" component="div" onClick={() => navigate('/')} sx={{ cursor: 'pointer' }}>
                        루키장터
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                        <InputBase
                            placeholder="검색어 입력"
                            sx={{ background: '#fff', px: 20, borderRadius: 3 }}
                        />
                        {/* <ProductSearch /> */}
                        <Button color="inherit" onClick={() => navigate('/login')}>로그인</Button>
                        <Button color="inherit" onClick={() => navigate('/signup')}>회원가입</Button>
                    </Box>
                </Toolbar>
            </AppBar>

            {/* 헤더 높이 보정용 마진 */}
            {/* <Toolbar sx={{ height: 36 }} />  */}
            {/* <Toolbar /> */}
        </>
    );
};

// const Header = () => (
//   <Box
//     sx={{
//       mb: 3,
//       display: 'flex',
//       alignItems: 'center',
//       gap: 2,
//       position: 'fixed',
//       top: 0,
//       left: 0,
//       width: '100%',
//       zIndex: 1200,
//       bgcolor: '#fff',
//       px: 3,
//       py: 2,
//       boxShadow: 1,
//       justifyContent: 'flex-start',
//     }}
//   >
//     <Box sx={{ flex: 1 }} />
//     <Box
//       component={Link}
//       to="/"
//       sx={{
//         color: 'black',
//         px: 2,
//         py: 1,
//         borderRadius: 1,
//         fontWeight: 'bold',
//         fontSize: 20,
//         textDecoration: 'none',
//         mr: 2,
//       }}
//     >
//       루키장터
//     </Box>
//     <ProductSearch />
//     <Box sx={{ flex: 2 }} />
//   </Box>
// );
export default Header;
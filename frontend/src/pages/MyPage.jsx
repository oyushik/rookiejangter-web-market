import React from 'react';
import { Container, Typography, Box, Avatar, Button, Divider, List, ListItem, ListItemText } from '@mui/material';

const MyPage = () => {
  // 예시 유저 정보
  const user = {
    name: '홍길동',
    email: 'hong@example.com',
    avatarUrl: 'https://via.placeholder.com/100',
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Box display="flex" alignItems="center" flexDirection="column">
        <Avatar src={user.avatarUrl} sx={{ width: 100, height: 100, mb: 2 }} />
        <Typography variant="h5">{user.name}</Typography>
        <Typography variant="body2" color="text.secondary">
          {user.email}
        </Typography>
        <Button variant="outlined" sx={{ mt: 2 }}>
          프로필 수정
        </Button>
      </Box>

      <Divider sx={{ my: 4 }} />

      <List>
        <ListItem button>
          <ListItemText primary="내가 올린 상품" />
        </ListItem>
        <ListItem button>
          <ListItemText primary="찜한 상품" />
        </ListItem>
        <ListItem button>
          <ListItemText primary="거래 내역" />
        </ListItem>
        <ListItem button>
          <ListItemText primary="설정" />
        </ListItem>
        <ListItem button>
          <ListItemText primary="로그아웃" />
        </ListItem>
      </List>
    </Container>
  );
};

export default MyPage;

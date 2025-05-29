import React, { useState } from 'react';
import { TextField, Button } from '@mui/material';
import { loginUser } from '../api/auth';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = async () => {
        try {
            await loginUser({ username, password });
            alert('로그인 완료');
        } catch (err) {
            console.error(err);
            alert('로그인 실패');
        }
    };

    return (
        <div>
            <TextField
                label="아이디"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                fullWidth
                margin="normal"
            />
            <TextField
                label="비밀번호"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                fullWidth
                margin="normal"
            />
            <Button onClick={handleSubmit} variant="contained">
                로그인
            </Button>
        </div>
    );
};

export default LoginForm;

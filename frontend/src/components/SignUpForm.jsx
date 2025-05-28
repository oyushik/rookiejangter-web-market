import React, { useState } from 'react';
import { TextField, Button } from '@mui/material';
import { registerUser } from '../api/auth';

const SignUpForm = ({ phoneNumber }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = async () => {
        try {
            await registerUser({ phoneNumber, username, password });
            alert('회원가입 완료');
        } catch (err) {
            console.error(err);
            alert('회원가입 실패');
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
                회원가입
            </Button>
        </div>
    );
};

export default SignUpForm;

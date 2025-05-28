import React, { useState } from 'react';
import { TextField, Button } from '@mui/material';
import { requestSmsCode, verifySmsCode } from '../api/auth';

const PhoneAuthForm = ({ onVerified }) => {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [code, setCode] = useState('');
    const [step, setStep] = useState(1);

    const handleSendCode = async () => {
        try {
            await requestSmsCode(phoneNumber);
            setStep(2);
        } catch (err) {
            console.error(err);
            alert('인증번호 전송 실패');
        }
    };

    const handleVerifyCode = async () => {
        try {
            await verifySmsCode({ phoneNumber, code });
            onVerified(phoneNumber);
        } catch (err) {
            console.error(err);
            alert('인증 실패');
        }
    };  

    return (
        <div>
            <TextField
                label="휴대폰 번호"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                disabled={step > 1}
                fullWidth
                margin="normal"
            />
            {step === 1 ? (
                <Button onClick={handleSendCode} variant="contained">
                    인증번호 요청
                </Button>
            ) : (
                <>
                    <TextField
                        label="인증번호"
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        fullWidth
                        margin="normal"
                    />
                    <Button onClick={handleVerifyCode} variant="contained">
                        인증 확인
                    </Button>
                </>
            )}
        </div>
    );
};

export default PhoneAuthForm;

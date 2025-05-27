import React, { useState } from 'react';
import PhoneAuthForm from '../components/PhoneAuthForm';
import SignUpForm from '../components/SignUpForm';

const RegisterPage = () => {
    const [verifiedPhone, setVerifiedPhone] = useState(null);

    return (
        <div>
            <h2>회원가입</h2>
            {verifiedPhone ? (
                <SignUpForm phoneNumber={verifiedPhone} />
            ) : (
                <PhoneAuthForm onVerified={setVerifiedPhone} />
            )}
        </div>
    );
};

export default RegisterPage;
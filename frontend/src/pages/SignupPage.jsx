import { useState, useEffect } from 'react';
import PhoneVerification from '../components/PhoneVerification';
import SignUpForm from '../components/SignUpForm';
import { Container, Typography } from '@mui/material';

const SignUpPage = () => {
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    document.body.style.overflowY = 'auto';
    document.body.style.overflowX = 'auto';
    return () => {
      document.body.style.overflowY = 'auto';
      document.body.style.overflowX = 'auto';
    };
  }, []);

  const [userName, setUserName] = useState('');
  const [phone, setPhone] = useState('');
  const [isVerified, setIsVerified] = useState(false);

  const handlePhoneVerified = (certifiedPhone, name) => {
    setUserName(name);
    setPhone(certifiedPhone);
    setIsVerified(true);
  };

  return (
    <div>
      <Container>
        <Typography variant="h4" sx={{ mt: 4 }}>
          회원가입
        </Typography>
        <PhoneVerification onSuccess={handlePhoneVerified} />

        {/* {isVerified && ( */}
        <>
          <SignUpForm defaultName={userName} defaultPhone={phone} />
        </>
        {/* )} */}
      </Container>
    </div>
  );
};

export default SignUpPage;

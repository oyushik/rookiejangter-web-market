import React, { useState } from "react";
import PhoneVerification from "../components/PhoneVerification";
import SignUpForm from "../components/SignUpForm";
import { Container, Typography } from '@mui/material';

const SignupPage = () => {
  const [userName, setUserName] = useState("");
  const [phone, setPhone] = useState("");
  const [isVerified, setIsVerified] = useState(false);

  const handlePhoneVerified = (certifiedPhone, name) => {
    setUserName(name);
    setPhone(certifiedPhone);
    setIsVerified(true);
  };

  return (
    <div>
      <Container>
        <Typography variant="h4" sx={{ mt: 4 }}>회원가입 페이지</Typography>
        <PhoneVerification onSuccess={handlePhoneVerified} />

        {/* {isVerified && ( */}
          <>
            <SignUpForm
              defaultName={userName}
              defaultPhone={phone}
            />
          </>
        {/* )} */}

        <div>본인인증된 이름: {userName}</div>
      </Container>
    </div>
  );
};

export default SignupPage;

import React, { useState } from "react";
import { TextField, Button, Box } from "@mui/material";
import { signup } from "../api/auth"; // ✅ API 호출 함수 불러오기

const SignUpForm = ({ defaultName, defaultPhone }) => {
  const [formData, setFormData] = useState({
    userId: "",
    password: "",
    name: defaultName || "",
    phone: defaultPhone || "",
    areaCode: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess(false);

    try {
      await signup(formData); // ✅ API 호출
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || "회원가입에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 4 }}>
      <TextField
        fullWidth
        label="아이디"
        name="userId"
        value={formData.userId}
        onChange={handleChange}
        margin="normal"
        required
      />
      <TextField
        fullWidth
        type="password"
        label="비밀번호"
        name="password"
        value={formData.password}
        onChange={handleChange}
        margin="normal"
        required
      />
      <TextField
        fullWidth
        label="이름"
        name="name"
        value={formData.name}
        onChange={handleChange}
        margin="normal"
        required
      />
      <TextField
        fullWidth
        label="전화번호"
        name="phone"
        value={formData.phone}
        onChange={handleChange}
        margin="normal"
        required
      />
      <TextField
        fullWidth
        label="지역번호"
        name="areaCode"
        value={formData.areaCode}
        onChange={handleChange}
        margin="normal"
        required
      />

      <Button
        type="submit"
        variant="contained"
        sx={{ mt: 2 }}
        disabled={loading}
      >
        {loading ? "회원가입 중..." : "회원가입"}
      </Button>

      {success && (
        <Box sx={{ mt: 2, color: "green" }}>회원가입이 완료되었습니다!</Box>
      )}
      {error && (
        <Box sx={{ mt: 2, color: "red" }}>{error}</Box>
      )}
    </Box>
  );
};

export default SignUpForm;

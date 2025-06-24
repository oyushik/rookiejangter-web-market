import React, { useState, useEffect, useMemo } from 'react';
import {
  TextField,
  Button,
  Box,
  Typography,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Snackbar, // Snackbar import
  Alert, // Alert for better Snackbar content
} from '@mui/material';
import { signup } from '../api/auth';
import { getAreas } from '../api/area';
import { useNavigate } from 'react-router-dom';

const SignUpForm = ({ defaultName, defaultPhone }) => {
  const [formData, setFormData] = useState({
    loginId: '',
    password: '',
    userName: defaultName || '',
    phone: defaultPhone || '',
    areaId: '',
  });

  const [areas, setAreas] = useState([]);

  const [errors, setErrors] = useState({
    loginId: '',
    password: '',
    userName: '',
    phone: '',
    areaId: '',
    submit: '',
  });

  const [loading, setLoading] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false); // Snackbar open/close 상태 관리
  const [snackbarMessage, setSnackbarMessage] = useState(''); // Snackbar 메시지 상태 관리
  const [snackbarSeverity, setSnackbarSeverity] = useState('success'); // Snackbar 심각도 (success, error 등)

  const navigate = useNavigate();

  const validateField = (name, value) => {
    let message = '';
    switch (name) {
      case 'loginId':
        if (!value) {
          message = '로그인 ID는 필수입니다.';
        } else if (value.length < 4 || value.length > 20) {
          message = '로그인 ID는 4~20자 이내로 입력해야 합니다.';
        } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
          message = '로그인 ID는 영문과 숫자 조합만 가능합니다.';
        }
        break;
      case 'password':
        if (!value) {
          message = '비밀번호는 필수입니다.';
        } else if (value.length < 8 || value.length > 20) {
          message = '비밀번호는 8~20자 이내로 입력해야 합니다.';
        } else if (!/^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).*$/.test(value)) {
          message = '비밀번호는 영문, 숫자, 특수문자를 각 1개 이상 포함해야 합니다.';
        }
        break;
      case 'userName':
        if (!value) {
          message = '이름은 필수입니다.';
        } else if (value.length < 2 || value.length > 12) {
          message = '이름은 2~12자 이내로 입력해야 합니다.';
        } else if (!/^[가-힣a-zA-Z]+$/.test(value)) {
          message = '이름은 한글과 영문만 가능합니다.';
        }
        break;
      case 'phone':
        if (!value) {
          message = '전화번호는 필수입니다.';
        } else if (value.length < 9 || value.length > 12) {
          message = '유효한 전화번호를 입력해야 합니다.';
        } else if (!/^010\d{8}$/.test(value)) {
          message = '전화번호 형식은 010XXXXOOOO 입니다.';
        }
        break;
      case 'areaId':
        if (!value) {
          message = '지역은 필수 선택 항목입니다.';
        }
        break;
      default:
        break;
    }
    return message;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: validateField(name, value),
      submit: '',
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    let formHasErrors = false;
    const newErrors = {};
    for (const key in formData) {
      const message = validateField(key, formData[key]);
      if (message) {
        newErrors[key] = message;
        formHasErrors = true;
      }
    }

    if (formHasErrors) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      await signup(formData);
      setSnackbarMessage('회원가입이 완료되었습니다!');
      setSnackbarSeverity('success');
      setSnackbarOpen(true);
      // 성공 시 즉시 리다이렉트하지 않고, Snackbar가 닫힌 후 리다이렉트하도록 변경
    } catch (err) {
      let submitErrorMessage = '회원가입에 실패했습니다.';
      if (err.response?.data?.message) {
        const errorMessage = err.response.data.message;
        if (errorMessage.includes('로그인 ID')) {
          submitErrorMessage = '이미 사용 중인 아이디입니다.';
        } else if (errorMessage.includes('이름')) {
          submitErrorMessage = '이미 사용 중인 유저명입니다.';
        } else if (errorMessage.includes('전화번호')) {
          submitErrorMessage = '이미 사용 중인 전화번호입니다.';
        }
        setErrors((prevErrors) => ({
          ...prevErrors,
          submit: submitErrorMessage,
        }));
      } else {
        setErrors((prevErrors) => ({
          ...prevErrors,
          submit: submitErrorMessage,
        }));
      }
      setSnackbarMessage(submitErrorMessage); // 실패 메시지도 Snackbar로 표시
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    } finally {
      setLoading(false);
    }
  };

  // Snackbar가 닫힐 때 호출될 핸들러
  const handleSnackbarClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarOpen(false);
    // 성공 메시지일 경우에만 리다이렉트
    if (snackbarSeverity === 'success') {
      navigate('/login');
    }
  };

  const isFormValid = useMemo(() => {
    for (const key in errors) {
      if (errors[key]) {
        return false;
      }
    }
    // 모든 필드가 채워져 있고, 에러가 없는지 확인
    return Object.values(formData).every((value) => value !== '');
  }, [errors, formData]);

  useEffect(() => {
    const fetchAreas = async () => {
      try {
        const response = await getAreas();
        setAreas(response?.data || []);
      } catch (error) {
        console.error('지역 목록을 가져오는 데 실패했습니다.', error);
      }
    };
    fetchAreas();
  }, []);

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 4 }}>
      <TextField
        label="아이디"
        name="loginId"
        value={formData.loginId}
        onChange={handleChange}
        margin="normal"
        required
        error={!!errors.loginId}
        helperText={errors.loginId || '4~20자의 영문과 숫자 조합'}
        FormHelperTextProps={{
          style: { color: errors.loginId ? 'red' : 'grey' },
        }}
        sx={{
          maxWidth: '400px',
          width: '100%',
        }}
      />
      <br />

      <TextField
        type="password"
        label="비밀번호"
        name="password"
        value={formData.password}
        onChange={handleChange}
        margin="normal"
        required
        error={!!errors.password}
        helperText={errors.password || '8~20자의 영문, 숫자, 특수문자(!@#$%^&+=) 각 1개 이상 포함'}
        FormHelperTextProps={{
          style: { color: errors.password ? 'red' : 'grey' },
        }}
        sx={{
          maxWidth: '400px',
          width: '100%',
        }}
      />
      <br />

      <TextField
        label="이름"
        name="userName"
        value={formData.userName}
        onChange={handleChange}
        margin="normal"
        required
        error={!!errors.userName}
        helperText={errors.userName || '2~12자의 한글과 영문'}
        FormHelperTextProps={{
          style: { color: errors.userName ? 'red' : 'grey' },
        }}
        sx={{
          maxWidth: '400px',
          width: '100%',
        }}
      />
      <br />

      <TextField
        label="전화번호"
        name="phone"
        value={formData.phone}
        onChange={handleChange}
        margin="normal"
        required
        error={!!errors.phone}
        helperText={errors.phone || '010XXXXOOOO 형식'}
        FormHelperTextProps={{
          style: { color: errors.phone ? 'red' : 'grey' },
        }}
        sx={{
          maxWidth: '400px',
          width: '100%',
        }}
      />
      <br />

      <FormControl
        margin="normal"
        error={!!errors.areaId}
        required
        sx={{
          maxWidth: '400px',
          width: '100%',
        }}
      >
        <InputLabel id="areaId-label">지역</InputLabel>
        <Select
          labelId="areaId-label"
          id="areaId"
          name="areaId"
          value={formData.areaId}
          label="지역"
          onChange={handleChange}
        >
          {areas.map((area) => (
            <MenuItem key={area?.areaId} value={area?.areaId}>
              <Typography align="left">{area?.areaName}</Typography>
            </MenuItem>
          ))}
        </Select>
        {errors.areaId && (
          <Typography variant="caption" color="red">
            {errors.areaId}
          </Typography>
        )}
      </FormControl>
      <br />
      <br />

      <Button type="submit" variant="contained" disabled={loading || !isFormValid}>
        {loading ? '회원가입 중...' : '회원가입'}
      </Button>

      {errors.submit && <Box sx={{ mt: 2, color: 'red' }}>{errors.submit}</Box>}

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={1000} // 스낵바 표시 시간 3초로 설정
        onClose={handleSnackbarClose} // 스낵바 닫힐 때 호출될 함수
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity={snackbarSeverity} // 'success' 또는 'error'
          sx={{ width: '100%' }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default SignUpForm;

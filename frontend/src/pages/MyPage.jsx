import React, { useEffect, useMemo, useState } from 'react';
import {
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  Box,
  Tab,
  Tabs,
  Modal,
  IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { setIdentityInfo, clearAuthState } from '../features/auth/authSlice';
import { useNavigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import { getAreas } from '../api/area';
import FormSnackbar from '../components/FormSnackbar';

import {
  getReservationsByBuyer,
  getReservationsBySeller,
  getReservationById,
} from '../api/reservationService';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4,
};

const MyPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { identityInfo } = useSelector((state) => state.auth);

  const [editing, setEditing] = useState(false);
  const [areas, setAreas] = useState([]);
  const [formData, setFormData] = useState({
    userName: '',
    phone: '',
    areaId: '',
  });

  const [errors, setErrors] = useState({});
  const [password, setPassword] = useState('');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  // New states for reservations and modal
  const [currentReservationTab, setCurrentReservationTab] = useState(0); // 0 for buyer, 1 for seller
  const [buyerReservations, setBuyerReservations] = useState([]);
  const [sellerReservations, setSellerReservations] = useState([]);
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [openModal, setOpenModal] = useState(false);

  // Snackbar 상태
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'info',
  });

  const handleSnackbarClose = () => setSnackbar({ ...snackbar, open: false });

  useEffect(() => {
    window.scrollTo(0, 0);
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const res = await axios.get('http://localhost:8080/api/users/profile', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        dispatch(setIdentityInfo(res.data));
        setFormData({
          userName: res.data.userName,
          phone: res.data.phone,
          areaId: res.data.area?.areaId || '',
        });
      } catch (e) {
        console.error('프로필 불러오기 실패', e);
      }
    };

    const fetchAreas = async () => {
      try {
        const res = await getAreas();
        setAreas(res.data);
      } catch (e) {
        console.error('지역 목록 로딩 실패', e);
      }
    };

    const fetchReservations = async () => {
      try {
        const buyerRes = await getReservationsByBuyer();
        if (buyerRes.success) {
          setBuyerReservations(buyerRes.data || []);
        } else {
          throw new Error(buyerRes.error || '구매자 예약 목록 조회 실패');
        }

        const sellerRes = await getReservationsBySeller();
        if (sellerRes.success) {
          setSellerReservations(sellerRes.data || []);
        } else {
          throw new Error(sellerRes.error || '판매자 예약 목록 조회 실패');
        }
      } catch (error) {
        console.error('예약 목록 불러오기 실패:', error);
        setSnackbar({
          open: true,
          message: `예약 목록을 불러오는 데 실패했습니다: ${error.message}`,
          severity: 'error',
        });
      }
    };

    fetchProfile();
    fetchAreas();
    fetchReservations();
  }, [dispatch]);

  const validateField = (name, value) => {
    let msg = '';
    switch (name) {
      case 'userName':
        if (!value) msg = '이름은 필수입니다.';
        else if (value.length < 2 || value.length > 12) msg = '이름은 2~12자 이내여야 합니다.';
        else if (!/^[가-힣a-zA-Z]+$/.test(value)) msg = '한글 또는 영문만 입력 가능합니다.';
        break;
      case 'phone':
        if (!value) msg = '전화번호는 필수입니다.';
        else if (!/^010-\d{4}-\d{4}$/.test(value)) msg = '010-XXXX-XXXX 형식으로 입력해주세요.';
        break;
      case 'areaId':
        if (!value) msg = '지역을 선택해주세요.';
        break;
      default:
        break;
    }
    return msg;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setErrors((prev) => ({
      ...prev,
      [name]: validateField(name, value),
    }));
  };

  const isFormValid = useMemo(() => {
    return (
      formData.userName &&
      formData.phone &&
      formData.areaId &&
      !Object.values(errors).some((e) => e)
    );
  }, [formData, errors]);

  const handleSave = async () => {
    const newErrors = {};
    Object.keys(formData).forEach((key) => {
      const err = validateField(key, formData[key]);
      if (err) newErrors[key] = err;
    });
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.put(
        'http://localhost:8080/api/users/profile',
        {
          userName: formData.userName,
          phone: formData.phone,
          areaId: formData.areaId,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      dispatch(setIdentityInfo(response.data));
      setEditing(false);
      setSnackbar({
        open: true,
        message: '프로필이 성공적으로 업데이트되었습니다.',
        severity: 'success',
      });
    } catch (error) {
      console.error('프로필 업데이트 실패:', error);
      setSnackbar({
        open: true,
        message: '프로필 업데이트에 실패했습니다.',
        severity: 'error',
      });
    }
  };

  const handleDeleteAccount = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      await axios.delete('http://localhost:8080/api/auth/delete', {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        data: { password },
      });

      setSnackbar({
        open: true,
        message: '계정이 삭제되었습니다.',
        severity: 'success',
      });
      dispatch(clearAuthState());
      useAuthStore.getState().logout();
      navigate('/login');
    } catch (error) {
      console.error('계정 삭제 실패:', error);
      setSnackbar({
        open: true,
        message: '비밀번호가 일치하지 않아서 삭제에 실패했습니다.',
        severity: 'error',
      });
    }
  };

  // Handle tab change for reservations
  const handleReservationTabChange = (event, newValue) => {
    setCurrentReservationTab(newValue);
  };

  // Handle click on a reservation to show details
  const handleReservationClick = async (reservationId) => {
    try {
      const res = await getReservationById(reservationId);
      if (res.success) {
        setSelectedReservation(res.data);
        setOpenModal(true);
      } else {
        throw new Error(res.error || '예약 상세 정보 조회 실패');
      }
    } catch (error) {
      console.error(`Error fetching reservation details for ID ${reservationId}:`, error);
      setSnackbar({
        open: true,
        message: `예약 상세 정보를 불러오는 데 실패했습니다: ${error.message}`,
        severity: 'error',
      });
    }
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setSelectedReservation(null);
  };

  // '채팅으로 이동' 버튼 클릭 핸들러
  const handleGoToChat = (chatId) => {
    if (chatId) {
      navigate(`/chats/${chatId}`);
      handleCloseModal(); // 채팅 페이지로 이동 후 모달 닫기
    } else {
      setSnackbar({
        open: true,
        message: '채팅방 ID를 찾을 수 없습니다.',
        severity: 'warning',
      });
    }
  };

  // '상품 상세 보기' 버튼 클릭 핸들러
  const handleGoToProductDetail = (productId) => {
    if (productId) {
      navigate(`/products/${productId}`); // 상품 상세 페이지 경로로 이동
      handleCloseModal(); // 상품 페이지로 이동 후 모달 닫기
    } else {
      setSnackbar({
        open: true,
        message: '상품 ID를 찾을 수 없습니다.',
        severity: 'warning',
      });
    }
  };

  // Determine which list to display based on the current tab
  const displayedReservations =
    currentReservationTab === 0 ? buyerReservations : sellerReservations;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        마이페이지
      </Typography>
      <Box my={4}>
        <Button
          variant="contained"
          color="success"
          sx={{ ml: 2 }}
          onClick={() => navigate('/products/register')}
        >
          상품 등록
        </Button>
        <Button
          variant="contained"
          color="info"
          sx={{ ml: 2 }}
          onClick={() => navigate('/my-products')}
        >
          My 상품
        </Button>
        <Button
          variant="contained"
          color="secondary"
          sx={{ ml: 2 }}
          onClick={() => navigate('/my-products', { state: { dibs: true } })}
        >
          찜한 상품
        </Button>
        <Button variant="contained" color="error" sx={{ ml: 2 }} onClick={() => navigate('/chats')}>
          내 채팅
        </Button>
      </Box>

      {editing ? (
        <>
          <TextField
            fullWidth
            label="이름"
            name="userName"
            value={formData.userName}
            onChange={handleChange}
            error={!!errors.userName}
            helperText={errors.userName}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            label="전화번호"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            error={!!errors.phone}
            helperText={errors.phone}
            sx={{ mb: 2 }}
          />
          <FormControl fullWidth error={!!errors.areaId} sx={{ mb: 2 }}>
            <InputLabel>지역</InputLabel>
            <Select name="areaId" value={formData.areaId} onChange={handleChange} label="지역">
              {areas.map((area) => (
                <MenuItem key={area.areaId} value={area.areaId}>
                  {area.areaName}
                </MenuItem>
              ))}
            </Select>
            {errors.areaId && (
              <Typography variant="caption" color="error">
                {errors.areaId}
              </Typography>
            )}
          </FormControl>
          <Button variant="contained" onClick={handleSave} disabled={!isFormValid}>
            저장
          </Button>{' '}
          <Button variant="outlined" onClick={() => setEditing(false)}>
            취소
          </Button>
        </>
      ) : (
        <>
          <Typography>이름: {identityInfo?.userName}</Typography>
          <Typography>전화번호: {identityInfo?.phone}</Typography>
          <Typography>지역: {identityInfo?.area?.areaName}</Typography>
          <Button variant="contained" sx={{ mt: 2 }} onClick={() => setEditing(true)}>
            수정
          </Button>
        </>
      )}
      <br />
      <br />

      {!showDeleteConfirm ? (
        <Button onClick={() => setShowDeleteConfirm(true)} color="error">
          계정 삭제
        </Button>
      ) : (
        <Box sx={{ mt: 2 }}>
          <Typography>계정을 삭제하려면 비밀번호를 입력하세요:</Typography>
          <TextField
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호"
            sx={{ my: 2, maxWidth: '400px', width: '100%' }}
          />
          <br />
          <Button onClick={handleDeleteAccount} color="error" variant="contained">
            삭제 확인
          </Button>{' '}
          <br />
          <Button onClick={() => setShowDeleteConfirm(false)}>취소</Button>
        </Box>
      )}

      <hr style={{ margin: '30px 0' }} />

      <Typography variant="h5" gutterBottom>
        예약된 거래
      </Typography>
      <Tabs
        value={currentReservationTab}
        onChange={handleReservationTabChange}
        aria-label="reservation tabs"
        sx={{ mb: 2 }}
      >
        <Tab label="구매 요청한 예약" />
        <Tab label="판매하는 예약" />
      </Tabs>

      {displayedReservations.length > 0 ? (
        displayedReservations.map((reservation) => (
          <Box
            key={reservation.reservationId}
            sx={{ mb: 1, p: 1, border: '1px solid #ccc', borderRadius: 2, cursor: 'pointer' }}
            onClick={() => handleReservationClick(reservation.reservationId)}
          >
            <Typography>상품명: {reservation.product?.title}</Typography>
            <Typography variant="caption" color="text.secondary">
              예약 상태: {reservation.product?.isReserved ? '예약 중' : '거래 가능'}
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ ml: 2 }}>
              생성일: {new Date(reservation.createdAt).toLocaleDateString()}
            </Typography>
          </Box>
        ))
      ) : (
        <Typography color="text.secondary">진행 중인 거래가 없습니다.</Typography>
      )}

      {/* Reservation Detail Modal */}
      <Modal
        open={openModal}
        onClose={handleCloseModal}
        aria-labelledby="reservation-detail-modal-title"
        aria-describedby="reservation-detail-modal-description"
      >
        <Box sx={style}>
          <IconButton
            aria-label="close"
            onClick={handleCloseModal}
            sx={{
              position: 'absolute',
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
          <Typography id="reservation-detail-modal-title" variant="h6" component="h2">
            예약 상세 정보
          </Typography>
          {selectedReservation && (
            <Box id="reservation-detail-modal-description" sx={{ mt: 2 }}>
              <Typography>상품명: {selectedReservation.product?.title}</Typography>
              <Typography>구매자: {selectedReservation.buyer?.userName}</Typography>
              <Typography>판매자: {selectedReservation.seller?.userName}</Typography>
              <Typography>
                생성일: {new Date(selectedReservation.createdAt).toLocaleString()}
              </Typography>
              <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                {/* '채팅으로 이동' 버튼 추가 */}
                <Button
                  variant="outlined"
                  color="primary"
                  onClick={() => handleGoToChat(selectedReservation.chatId)}
                  // chatId가 없는 경우 버튼 비활성화
                  disabled={!selectedReservation.chatId}
                >
                  채팅으로 이동
                </Button>

                {/* '상품 상세 보기' 버튼 추가 */}
                <Button
                  variant="outlined"
                  color="info"
                  onClick={() => handleGoToProductDetail(selectedReservation.product?.productId)}
                  // productId가 없는 경우 버튼 비활성화
                  disabled={!selectedReservation.product?.productId}
                >
                  상품 상세 보기
                </Button>
              </Box>
            </Box>
          )}
        </Box>
      </Modal>

      {/* Snackbar 팝업 */}
      <FormSnackbar
        open={snackbar.open}
        message={snackbar.message}
        severity={snackbar.severity}
        onClose={handleSnackbarClose}
      />
    </Box>
  );
};

export default MyPage;

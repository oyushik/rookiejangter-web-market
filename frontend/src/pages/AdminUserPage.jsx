import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  Box,
  Button,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import FormSnackbar from '../components/FormSnackbar';

const AdminUserPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // 신고 관리용 state
  const [reports, setReports] = useState([]);
  const [reportUserMap, setReportUserMap] = useState({});
  const [reportLoading, setReportLoading] = useState(false);

  // 신고 상세 다이얼로그 state
  const [selectedReport, setSelectedReport] = useState(null);
  const [reportDetailLoading, setReportDetailLoading] = useState(false);

  // snackbar 상태 추가
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'info',
  });

  const fetchUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8080/api/admin/users');
      setUsers(response.data.content || []);
    } catch (err) {
      setError('유저 목록을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 신고 목록 + 사용자 이름 매핑
  const fetchReports = async () => {
    setReportLoading(true);
    try {
      const token = localStorage.getItem('accessToken');
      const res = await axios.get('http://localhost:8080/api/reports/admin/unprocessed', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setReports(res.data);

      // 신고자/대상 id 목록 추출
      const ids = [
        ...new Set(
          res.data
            .flatMap(r => [r.reporterId, r.targetId])
            .filter(id => id != null)
        ),
      ];

      // 사용자 이름 매핑
      const map = {};
      await Promise.all(
        ids.map(async (id) => {
          try {
            const userRes = await axios.get(`http://localhost:8080/api/admin/users/${id}`, {
              headers: { Authorization: `Bearer ${token}` }
            });
            map[id] = userRes.data.userName;
          } catch (e) {
            map[id] = undefined;
          }
        })
      );
      setReportUserMap(map);
    } catch (err) {
      setSnackbar({
        open: true,
        message: '신고 목록을 불러오지 못했습니다.',
        severity: 'error',
      });
    } finally {
      setReportLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
    fetchReports();
  }, []);

  const fetchUserDetail = async (userId) => {
    setDetailLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/admin/users/${userId}`);
      setSelectedUser(response.data);
    } catch (err) {
      setSnackbar({
        open: true,
        message: '유저 상세정보를 불러오지 못했습니다.',
        severity: 'error',
      });
    } finally {
      setDetailLoading(false);
    }
  };

  const handleViewUser = (userId) => {
    fetchUserDetail(userId);
  };

  const handleBanUser = async (userId) => {
    if (!window.confirm('정말로 해당 유저를 Ban 처리하시겠습니까?')) return;
    try {
      await axios.put(`http://localhost:8080/api/admin/users/${userId}/status`, {
        status: 'BANNED',
      });
      setSnackbar({
        open: true,
        message: '유저가 Ban 처리되었습니다.',
        severity: 'success',
      });
      fetchUsers();
    } catch (err) {
      setSnackbar({
        open: true,
        message: '유저 Ban 처리에 실패했습니다.',
        severity: 'error',
      });
    }
  };

  // 신고 상세정보 불러오기
  const handleViewReport = async (report) => {
    setReportDetailLoading(true);
    setSelectedReport(report);
    setReportDetailLoading(false);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        관리자 - 유저 관리
      </Typography>

      {/* 유저 관리 테이블 */}
      {loading ? (
        <Box sx={{ textAlign: 'center', mt: 5 }}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Typography color="error">{error}</Typography>
      ) : (
        <Paper sx={{ mb: 5 }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>로그인 ID</TableCell>
                <TableCell>이름</TableCell>
                <TableCell>전화번호</TableCell>
                <TableCell>관리자 여부</TableCell>
                <TableCell>차단 여부</TableCell>
                <TableCell>가입일</TableCell>
                <TableCell>지역</TableCell>
                <TableCell>액션</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {users.length > 0 ? (
                users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>{user.id}</TableCell>
                    <TableCell>{user.loginId}</TableCell>
                    <TableCell>{user.userName}</TableCell>
                    <TableCell>{user.phone || '없음'}</TableCell>
                    <TableCell>{user.isAdmin ? 'O' : 'X'}</TableCell>
                    <TableCell>{user.isBanned ? '차단됨' : '정상'}</TableCell>
                    <TableCell>{new Date(user.createdAt).toLocaleString()}</TableCell>
                    <TableCell>{user.area?.areaName || '미지정'}</TableCell>
                    <TableCell>
                      <Button size="small" onClick={() => handleViewUser(user.id)}>
                        보기
                      </Button>
                      {!user.isBanned && !user.isAdmin && (
                        <Button
                          size="small"
                          color="error"
                          onClick={() => handleBanUser(user.id)}
                          sx={{ ml: 1 }}
                        >
                          Ban
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={9} align="center">
                    등록된 유저가 없습니다.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Paper>
      )}

      {/* 신고 관리 테이블 */}
      <Typography variant="h4" gutterBottom sx={{ mt: 6 }}>
        신고 관리
      </Typography>
      {reportLoading ? (
        <Box sx={{ textAlign: 'center', mt: 3 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Paper>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>신고ID</TableCell>
                <TableCell>신고사유</TableCell>
                <TableCell>신고자</TableCell>
                <TableCell>대상</TableCell>
                <TableCell>신고일시</TableCell>
                <TableCell>상세 내용</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {reports.length > 0 ? (
                reports.map((report) => (
                  <TableRow key={report.reportId}>
                    <TableCell>{report.reportId}</TableCell>
                    <TableCell>{report.reportReasonType}</TableCell>
                    <TableCell>
                      {reportUserMap[report.reporterId]} (ID={report.reporterId})
                    </TableCell>
                    <TableCell>
                      {reportUserMap[report.targetId]} (ID={report.targetId})
                    </TableCell>
                    <TableCell>{new Date(report.createdAt).toLocaleString()}</TableCell>
                    <TableCell>
                      <Button size="small" onClick={() => handleViewReport(report)}>
                        보기
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    신고 내역이 없습니다.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Paper>
      )}

      {/* 신고 상세 다이얼로그 */}
      <Dialog
        open={!!selectedReport}
        onClose={() => {
          setSelectedReport(null);
          setSelectedReportUser({ reporter: null, target: null });
        }}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>신고 상세 내용</DialogTitle>
        <DialogContent dividers>
          {reportDetailLoading ? (
            <Box sx={{ textAlign: 'center', p: 3 }}>
              <CircularProgress />
            </Box>
          ) : selectedReport ? (
            <>
              <Typography>
                <strong>신고 사유:</strong> {selectedReport.reportReasonType}
              </Typography>
              <Typography
                sx={{
                  mt: 2,
                  whiteSpace: 'pre-line',
                  wordBreak: 'break-all',
                  overflowWrap: 'break-word',
                }}
              >
                <strong>신고 상세 이유:</strong> {selectedReport.reportDetail}
              </Typography>
            </>
          ) : null}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => {
            setSelectedReport(null);
            setSelectedReportUser({ reporter: null, target: null });
          }}>닫기</Button>
        </DialogActions>
      </Dialog>

      {/* 유저 상세 다이얼로그 */}
      <Dialog
        open={!!selectedUser}
        onClose={() => setSelectedUser(null)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>유저 상세정보</DialogTitle>
        <DialogContent dividers>
          {detailLoading ? (
            <Box sx={{ textAlign: 'center', p: 3 }}>
              <CircularProgress />
            </Box>
          ) : selectedUser ? (
            <>
              <Typography><strong>ID:</strong> {selectedUser.id}</Typography>
              <Typography><strong>로그인 ID:</strong> {selectedUser.loginId}</Typography>
              <Typography><strong>이름:</strong> {selectedUser.userName}</Typography>
              <Typography><strong>전화번호:</strong> {selectedUser.phone || '없음'}</Typography>
              <Typography><strong>관리자 여부:</strong> {selectedUser.isAdmin ? 'O' : 'X'}</Typography>
              <Typography><strong>차단 여부:</strong> {selectedUser.isBanned ? '차단됨' : '정상'}</Typography>
              <Typography><strong>가입일:</strong> {new Date(selectedUser.createdAt).toLocaleString()}</Typography>
              <Typography><strong>지역:</strong> {selectedUser.area?.areaName || '미지정'}</Typography>
              {selectedUser.bannedAt && (
                <Typography><strong>차단 일시:</strong> {new Date(selectedUser.bannedAt).toLocaleString()}</Typography>
              )}
              {selectedUser.banReason && (
                <Typography><strong>차단 사유:</strong> {selectedUser.banReason}</Typography>
              )}
            </>
          ) : null}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSelectedUser(null)}>닫기</Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar 알림 */}
      <FormSnackbar
        open={snackbar.open}
        message={snackbar.message}
        severity={snackbar.severity}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      />
    </Box>
  );
};

export default AdminUserPage;
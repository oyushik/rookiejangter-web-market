import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from 'axios';
import { fetchVerificationResult } from "../../api/auth";

const BASE_URL = 'http://localhost:8080/api/auth';
const USER_ID = 1;
export const fetchVerificationData = createAsyncThunk(
  'auth/fetchVerificationData',
  async (identityVerificationId, thunkAPI) => {
    try {
      const response = await fetchVerificationResult(identityVerificationId);
      return response.data;
    } catch (err) {
      return thunkAPI.rejectWithValue(err.response?.data?.message || '인증 실패');
    }
  }
);

// export const fetchIdentityInfo = createAsyncThunk(
//   'auth/fetchIdentityInfo',
//   async (_, { rejectWithValue }) => {
//     try {
//       const response = await axios.get(`http://localhost:8080/api/users/1/profile `); 
//       return response.data;
//     } catch (error) {
//       return rejectWithValue(error.response?.data || error.message);
//     }
//   }
// );
export const fetchIdentityInfo = createAsyncThunk(
  "auth/fetchIdentityInfo",
  async (_, { rejectWithValue }) => {
    try {
      const token = localStorage.getItem("accessToken");
      console.log("로컬 accessToken:", token);
      if (!token) throw new Error("Access token not found");

      const userId = 1; // 임시 ID
      const response = await axios.get(`http://localhost:8080/api/users/profile`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      console.log("API 응답:", response.data);

      return response.data;
    } catch (err) {
      console.error("유저 정보 요청 실패:", err);
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);
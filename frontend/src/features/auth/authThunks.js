import { createAsyncThunk } from "@reduxjs/toolkit";
import { fetchVerificationResult } from "../../api/auth";

export const fetchIdentityInfo = createAsyncThunk(
  "auth/fetchIdentityInfo",
  async (identityVerificationId, thunkAPI) => {
    try {
      const response = await fetchVerificationResult(identityVerificationId);
      return response.data;
    } catch (err) {
      return thunkAPI.rejectWithValue(err.response?.data?.message || "인증 실패");
    }
  }
);

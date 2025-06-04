import { createSlice } from "@reduxjs/toolkit";
import { fetchIdentityInfo } from "./authThunks";

const initialState = {
  identityInfo: null,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setIdentityInfo(state, action) {
      state.identityInfo = action.payload;
    },
    setLoading(state, action) {
      state.loading = action.payload;
    },
    setError(state, action) {
      state.error = action.payload;
    },
    clearAuthState: (state) => {
      state.identityInfo = null;
      state.loading = false;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchIdentityInfo.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchIdentityInfo.fulfilled, (state, action) => {
        state.loading = false;
        state.identityInfo = action.payload;
      })
      .addCase(fetchIdentityInfo.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.identityInfo = null;
      });
  },
});

export const { setIdentityInfo, setLoading, setError, clearAuthState } = authSlice.actions;
export default authSlice.reducer;

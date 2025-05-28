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
  reducers: {},
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
      });
  },
});

export default authSlice.reducer;

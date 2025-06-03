import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';

export const user = configureStore({
  reducer: {
    auth: authReducer,
  },
});

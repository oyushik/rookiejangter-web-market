// src/store/authStore.js
import { create } from 'zustand';

const useAuthStore = create((set, get) => ({
  // get 함수도 인자로 받음
  isAuthenticated: localStorage.getItem('accessToken') ? true : false,
  userName: null,
  login: (token, userName) => {
    localStorage.setItem('accessToken', token);
    set({ isAuthenticated: true, userName });
    console.log('Zustand 로그인 상태 업데이트:', get()); // 업데이트 후 상태 로깅
  },
  logout: () => {
    localStorage.removeItem('accessToken');
    set({ isAuthenticated: false, userName: null });
    console.log('Zustand 로그아웃 상태 업데이트:', get()); // 업데이트 후 상태 로깅
  },
}));

export default useAuthStore;
// 이 코드는 Zustand를 사용하여 인증 상태를 관리하는 스토어를 정의합니다.
// create 함수를 사용하여 스토어를 생성하고, 상태와 액션을 정의합니다.
// isAuthenticated는 로컬 스토리지에 accessToken이 있는지 여부에 따라 초기화됩니다.

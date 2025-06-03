// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { Provider } from 'react-redux';
import store from './redux/store';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>
);

// 이 코드는 Redux 스토어를 생성하고, React 애플리케이션에 Provider로 전달하여 전역 상태 관리를 가능하게 합니다.
// Redux 스토어는 애플리케이션의 상태를 관리하며, Provider를 통해 하위 컴포넌트에서 이 상태에 접근할 수 있게 됩니다.
// 이 구조는 Redux를 사용하여 상태 관리를 구현하는 기본적인 패턴을 따릅니다.

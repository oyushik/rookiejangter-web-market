import React from "react";
import ReactDOM from "react-dom/client";
import App from './App.jsx'
import { Provider } from "react-redux";
import { user } from "./redux/user";
import './index.css'

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <Provider store={user}>
      <App />
    </Provider>
  </React.StrictMode>
);

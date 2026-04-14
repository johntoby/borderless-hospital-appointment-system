import React from 'react';
import ReactDOM from 'react-dom/client';
import './App.css';
import App from './App';

/**
 * index.js — The entry point of the React application.
 *
 * HOW IT CONNECTS TO index.html:
 *   1. The browser loads index.html
 *   2. index.html has <div id="root"></div>
 *   3. This file finds that div and "mounts" the React app inside it
 *   4. From this point, React controls everything inside #root
 *
 * React.StrictMode:
 *   A development tool that highlights potential problems.
 *   It renders components twice in development to catch side effects.
 *   Has no effect in production builds.
 */
const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

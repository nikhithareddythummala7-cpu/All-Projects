import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import { BrowserRouter } from "react-router-dom";
import "./index.scss";
import { ThemeContextProvider } from "./context/themeContext.jsx";
import ThemeProvider from "./providers/themeProvider.jsx";
import { SearchContextProvider } from "./context/searchContext.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <ThemeContextProvider>
      <SearchContextProvider>
        <ThemeProvider>
          <App />
        </ThemeProvider>
      </SearchContextProvider>
    </ThemeContextProvider>
  </BrowserRouter>
);

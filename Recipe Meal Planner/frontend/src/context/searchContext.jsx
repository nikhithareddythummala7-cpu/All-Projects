import { createContext, useState } from "react";

export const searchContext = createContext();

export const SearchContextProvider = ({ children }) => {
  const [query, setQuery] = useState("burger");
  return (
    <searchContext.Provider value={{ query, setQuery }}>
      {children}
    </searchContext.Provider>
  );
};

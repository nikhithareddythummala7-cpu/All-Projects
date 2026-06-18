import { useContext, useEffect, useState } from "react";
import { fetchDataFromApi } from "../utils/api";
import { searchContext } from "../context/searchContext";

const useFetch = (url, params) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(null);
  const [error, setError] = useState(null);
  const { query } = useContext(searchContext);

  useEffect(() => {
    try {
      setLoading("Loading...");
      setData(null);
      setError(null);

      fetchDataFromApi(url, params)
        .then((res) => {
          setLoading(false);
          setData(res);
        })
        .catch((err) => {
          setLoading(false);
          setError("Something went wrong!");
        });
    } catch (error) {
      console.log(error);
    }
  }, [url, query]);

  return { data, loading, error };
};

export default useFetch;

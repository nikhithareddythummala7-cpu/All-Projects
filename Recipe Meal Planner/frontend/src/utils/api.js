import axios from "axios";

const BASE_URL = "https://api.spoonacular.com";

export const fetchDataFromApi = async (url, params) => {
  try {
    //params will be object
    const queryParams = {
      apiKey: import.meta.env.VITE_SPOONACULAR_API_KEY ||"" ,
      ...params,
    };
    // console.log(queryParams);
    // console.log(BASE_URL + url);
    const { data } = await axios.get(BASE_URL + url, {
      params: queryParams,
    });
    return data;
  } catch (error) {
    return error;
  }
};

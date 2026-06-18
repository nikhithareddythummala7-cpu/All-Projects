import React, { useEffect, useState } from "react";
import "./style.scss";
import toast, { Toaster } from "react-hot-toast";
import { MdOutlineNoFood } from "react-icons/md";
import Cookies from "js-cookie";
import axios from "axios";
import Loader from "../../components/loader/Loader";

import { CiImageOff } from "react-icons/ci";
import { useNavigate } from "react-router-dom";

const Favorites = () => {
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  const toastOptions = {
    duration: 1000,
  };

  const getSavedRecipes = async () => {
    const host = "http://localhost:3000/api/favorites/getsavedrecipes";
    try {
      const jwtToken = Cookies.get("recipeJwtToken");
      const { data } = await axios.get(host, {
        headers: {
          "auth-token": jwtToken,
        },
      });
      if (data?.status) {
        setRecipes(data?.favRecipes);
        setLoading(false);
      } else {
        toast.error(data?.msg, toastOptions);
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getSavedRecipes();
  }, []);

  return (
    <>
      {loading ? (
        <div className="loadingContainer">
          <Loader />
        </div>
      ) : (
        <div className="favoritesContainer">
          {recipes.length > 0 ? (
            <>
              <h1>Favorite Recipes</h1>
              <ul className="favorites">
                {recipes.map((r) => {
                  const { recipeId, image, recipe } = r;
                  return (
                    <li
                      onClick={() => navigate(`/recipe/${recipeId}`)}
                      key={recipeId}
                    >
                      <div className="image">
                        {image ? (
                          <img src={image} alt={recipe} />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>

                      <p>{recipe}</p>
                    </li>
                  );
                })}
              </ul>
            </>
          ) : (
            <div className="empty">
              <MdOutlineNoFood />
              <h1>Basket is Empty</h1>
              <span>Add Recipes into your basket</span>
            </div>
          )}
        </div>
      )}

      <Toaster position="top-center" reverseOrder={false} />
    </>
  );
};

export default Favorites;

import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import useFetch from "../../hooks/fetch";
import Loader from "../../components/loader/Loader";
import "./style.scss";
import Tags from "../../components/tags/Tags";
import { CiImageOff } from "react-icons/ci";
import { GoHeart, GoHeartFill } from "react-icons/go";
import toast, { Toaster } from "react-hot-toast";
import { IoMdArrowDropright, IoMdArrowDropdown } from "react-icons/io";
import { BiLeaf } from "react-icons/bi";
import parse from "html-react-parser";
import { v4 } from "uuid";
import Cookies from "js-cookie";
import axios from "axios";
import ContentWrapper from "../../components/contentWrapper/ContentWrapper";

const RecipeDetails = () => {
  const { recipeId } = useParams();
  const [showDetails, setDetails] = useState({
    instructions: false,
    nutrients: false,
    ingredients: false,
  });
  const [liked, setLiked] = useState();

  const { data, loading, error } = useFetch(
    `/recipes/${recipeId}/information`,
    {
      includeNutrition: true,
    }
  );
  console.log(data);
  const recipeIngredients = [];
  data?.nutrition?.ingredients.forEach((i) => {
    const { id, name, amount, unit } = i;
    recipeIngredients.push({ id, name, amount, unit, available: false });
  });

  if (error) {
    return (
      <div className="loadingContainer">
        <h1>Something Went Wrong!</h1>
      </div>
    );
  }

  const toastOptions = {
    duration: 1000,
  };

  const handleUnlike = async (recipeId) => {
    setLiked(false);
    try {
      const host = `http://localhost:3000/api/favorites/unsaverecipe/${recipeId}`;
      const jwtToken = Cookies.get("recipeJwtToken");
      const { data } = await axios.delete(host, {
        headers: {
          "auth-token": jwtToken,
        },
      });

      if (data?.status) {
        toast.success(data.msg, toastOptions);
      } else {
        toast.error(data.msg, toastOptions);
      }
    } catch (error) {
      console.log(error);
    }
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
        data?.favRecipes.forEach((m) => {
          if (m.recipeId === recipeId) {
            setLiked(true);
          }
        });
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

  const handleLike = async (recipeId) => {
    try {
      const host = `http://localhost:3000/api/favorites/saverecipe`;
      const jwtToken = Cookies.get("recipeJwtToken");

      const recipeNutrients = [];

      let count = 0;
      data?.nutrition.nutrients.forEach((n) => {
        const { name, amount, unit } = n;
        const lower = name.toLowerCase();
        if (
          ["protein", "carbohydrates", "fat", "calories"].includes(lower) &&
          count < 4
        ) {
          recipeNutrients.push({ name, amount, unit });
          count += 1;
        }
      });
      const res = await axios.post(
        host,
        {
          recipeId,
          image: data?.image,
          recipe: data?.title,
          nutrition: recipeNutrients,
          dishTypes: data?.dishTypes || [],
          ingredients: recipeIngredients,
        },
        {
          headers: {
            "auth-token": jwtToken,
          },
        }
      );

      if (res?.data?.status) {
        toast.success(res?.data.msg, toastOptions);
      } else {
        toast.error(res?.data.msg, toastOptions);
      }
      setLiked(true);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <>
      {loading ? (
        <div className="loadingContainer">
          <Loader />
        </div>
      ) : (
        <>
          {error === "Something went wrong!" ? (
            <div className="loadingContainer">
              <h1>Something Went Wrong !</h1>
            </div>
          ) : (
            <div className="recipeDetailsContainer">
              <ContentWrapper>
                <div className="recipe">
                  <div className="left">
                    {data?.image ? (
                      <img src={data?.image} alt={data?.title} />
                    ) : (
                      <div className="noImage">
                        <CiImageOff />
                      </div>
                    )}
                  </div>
                  <div className="right">
                    <div
                      style={{ justifyContent: "space-between" }}
                      className="row"
                    >
                      <div className="row">
                        <span>Recipe:</span>
                        <p>{data?.title}</p>
                      </div>
                      <button className="like" type="button">
                        {liked ? (
                          <GoHeartFill onClick={() => handleUnlike(recipeId)} />
                        ) : (
                          <GoHeart onClick={() => handleLike(recipeId)} />
                        )}
                      </button>
                    </div>

                    <div className="row">
                      <span>Duration:</span>
                      <p>{data?.readyInMinutes} Min</p>
                    </div>

                    {data?.dishTypes.length > 0 && (
                      <div className="row">
                        <span>Dish Types:</span>
                        <Tags data={data?.dishTypes} />
                      </div>
                    )}

                    {data?.cuisines.length > 0 && (
                      <div className="row">
                        <span>Cuisines:</span>
                        <Tags data={data?.cuisines} />
                      </div>
                    )}

                    {data?.diets.length > 0 && (
                      <div className="row">
                        <span>Diets:</span>
                        <Tags diets={true} data={data?.diets} />
                      </div>
                    )}

                    {data?.vegetarian && (
                      <div className="vegan">
                        <p>Vegan</p>
                        <BiLeaf />
                      </div>
                    )}
                  </div>
                </div>

                <div className="details">
                  <div className="row">
                    <p>Instructions</p>
                    {!showDetails.instructions ? (
                      <IoMdArrowDropright
                        onClick={() =>
                          setDetails({ ...showDetails, instructions: true })
                        }
                      />
                    ) : (
                      <IoMdArrowDropdown
                        onClick={() =>
                          setDetails({ ...showDetails, instructions: false })
                        }
                      />
                    )}
                  </div>

                  {showDetails.instructions && (
                    <div className="instructionsContainer">
                      {parse(data?.instructions)}
                    </div>
                  )}

                  <div className="row">
                    <p>Nutrients</p>
                    {!showDetails.nutrients ? (
                      <IoMdArrowDropright
                        onClick={() =>
                          setDetails({ ...showDetails, nutrients: true })
                        }
                      />
                    ) : (
                      <IoMdArrowDropdown
                        onClick={() =>
                          setDetails({ ...showDetails, nutrients: false })
                        }
                      />
                    )}
                  </div>
                  {showDetails.nutrients && (
                    <div className="nutrients">
                      {data?.nutrition?.nutrients.map((n) => {
                        const { name, amount, unit } = n;
                        return (
                          <li key={name}>
                            <p>{name}</p>
                            <span>
                              {amount} {unit}
                            </span>
                          </li>
                        );
                      })}
                    </div>
                  )}

                  <div className="row">
                    <p>Ingredients</p>
                    {!showDetails.ingredients ? (
                      <IoMdArrowDropright
                        onClick={() =>
                          setDetails({ ...showDetails, ingredients: true })
                        }
                      />
                    ) : (
                      <IoMdArrowDropdown
                        onClick={() =>
                          setDetails({ ...showDetails, ingredients: false })
                        }
                      />
                    )}
                  </div>
                  {showDetails.ingredients && (
                    <div className="ingredientsContainer">
                      {data?.nutrition?.ingredients.map((i) => {
                        const { id, name, amount, unit } = i;
                        return (
                          <li key={v4()}>
                            <p>{name}</p>
                            <span>
                              {amount} {unit}
                            </span>
                          </li>
                        );
                      })}
                    </div>
                  )}
                </div>
              </ContentWrapper>
            </div>
          )}
        </>
      )}
      <Toaster position="top-center" reverseOrder={false} />
    </>
  );
};

export default RecipeDetails;

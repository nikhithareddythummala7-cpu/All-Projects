import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import useFetch from "../../hooks/fetch";
import Loader from "../../components/loader/Loader";
import { searchContext } from "../../context/searchContext";
import Tags from "../../components/tags/Tags";
import { BiLeaf } from "react-icons/bi";
import "./style.scss";
import { useNavigate } from "react-router-dom";
import { CiImageOff } from "react-icons/ci";

const Recipes = () => {
  const { query, setQuery } = useContext(searchContext);
  console.log(query);
  const { data, loading } = useFetch("/recipes/complexSearch", {
    query: query,
    addRecipeNutrition: true,
  });
  const navigate = useNavigate();

  return (
    <>
      {loading ? (
        <div className="loadingContainer">
          <Loader />
        </div>
      ) : (
        <div className="recipesContainer">
          <h1>Recipes</h1>
          {data?.number == 0 ? (
            <div className="empty">No Recipes Found</div>
          ) : (
            <ul className="recipes">
              {data?.results.map((r, i) => {
                const {
                  title,
                  vegetarian,
                  readyInMinutes,
                  id,
                  dishTypes,
                  cuisines,
                  image,
                  diets,
                } = r;

                return (
                  <li
                    onClick={() => navigate(`/recipe/${id}`)}
                    key={id}
                    className="recipe"
                  >
                    <div className={i % 2 === 0 ? "left second" : "left"}>
                      {image ? (
                        <img src={image} alt={title} />
                      ) : (
                        <div className="noImage">
                          <CiImageOff />
                        </div>
                      )}
                    </div>
                    <div className={i % 2 === 0 ? "right first" : "right"}>
                      <div className="row">
                        <span>Recipe:</span>
                        <p>{title}</p>
                      </div>
                      <div className="row">
                        <span>Duration:</span>
                        <p>{readyInMinutes} Min</p>
                      </div>

                      {dishTypes.length > 0 && (
                        <div className="row">
                          <span>Dish Types:</span>
                          <Tags data={dishTypes} />
                        </div>
                      )}
                      {cuisines.length > 0 && (
                        <div className="row">
                          <span>Cuisines:</span>
                          <Tags data={cuisines} />
                        </div>
                      )}
                      {diets.length > 0 && (
                        <div className="row">
                          <span>Diets:</span>
                          <Tags diets={true} data={diets} />
                        </div>
                      )}
                      {vegetarian && (
                        <div className="vegan">
                          <p>Vegan</p>
                          <BiLeaf />
                        </div>
                      )}
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
      )}
    </>
  );
};

export default Recipes;

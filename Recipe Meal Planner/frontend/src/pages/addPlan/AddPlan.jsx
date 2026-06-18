import React, { useEffect, useState } from "react";
import Loader from "../../components/loader/Loader";
import { FiChevronLeft } from "react-icons/fi";
import toast, { Toaster } from "react-hot-toast";
import { CgClose } from "react-icons/cg";
import { FiChevronRight } from "react-icons/fi";
import { IoAdd } from "react-icons/io5";
import dayjs from "dayjs";
import "./style.scss";
import Cookies from "js-cookie";
import axios from "axios";
import { CiCalendarDate } from "react-icons/ci";
import { useNavigate } from "react-router-dom";
import { CiImageOff } from "react-icons/ci";
import { MdOutlineNoFood } from "react-icons/md";
import Tags from "../../components/tags/Tags";

var days = [
  "Sunday",
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
];

const AddPlan = () => {
  const [mealPlan, setMealPlan] = useState({
    breakfast: {
      recipe: "",
      macroNutrients: [],
      recipeId: "",
      image: "",
      ingredients: [],
    },
    lunch: {
      recipe: "",
      macroNutrients: [],
      recipeId: "",
      image: "",
      ingredients: [],
    },
    dinner: {
      recipe: "",
      recipeId: "",
      macroNutrients: [],
      image: "",
      ingredients: [],
    },
  });

  const [loading, setLoading] = useState(true);
  const [date, setDate] = useState();
  const [weekDay, setWeekDay] = useState();
  const [macro, setMacro] = useState({
    protein: 0,
    carbohydrates: 0,
    fat: 0,
    calories: 0,
  });
  const [recipes, setRecipes] = useState([]);
  const [recipeModal, setRecipeModal] = useState();
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

  const getPlan = async () => {
    const present = new Date();
    setDate(
      `${present.getFullYear()}-${present.getMonth() + 1}-${present.getDate()}`
    );
    setWeekDay(days[present.getDay()]);
    setLoading(false);
  };
  useEffect(() => {
    getPlan();
  }, []);

  const handleLeftDay = () => {
    const leftDay = new Date(date);
    leftDay.setDate(leftDay.getDate() - 1);
    setWeekDay(days[leftDay.getDay()]);
    setDate(
      `${leftDay.getFullYear()}-${leftDay.getMonth() + 1}-${leftDay.getDate()}`
    );
  };

  const handleRightDay = () => {
    const rightDay = new Date(date);
    rightDay.setDate(rightDay.getDate() + 1);
    setWeekDay(days[rightDay.getDay()]);
    setDate(
      `${rightDay.getFullYear()}-${
        rightDay.getMonth() + 1
      }-${rightDay.getDate()}`
    );
  };

  const handleRecipe = async (r) => {
    const nutrients = r.nutrition;
    const recipeMacro = macro;

    const recipeNutrients = [];
    let count = 0;
    nutrients.forEach((n) => {
      const { name, amount, unit } = n;

      const lower = name.toLowerCase();
      if (recipeMacro.hasOwnProperty(lower) && count < 4) {
        recipeMacro[lower] += parseFloat(amount);
        const fixedAmount = amount.toFixed(2);
        recipeNutrients.push({ fixedAmount, name: lower, unit });
        count += 1;
      }
    });
    setMacro({ ...recipeMacro });

    const individualMealPlan = {
      macroNutrients: recipeNutrients,
      recipe: r.recipe,
      recipeId: r.recipeId,
      image: r.image,
      ingredients: r.ingredients,
    };
    setMealPlan({ ...mealPlan, [recipeModal]: individualMealPlan });
    setRecipeModal("");
  };

  const handleCancel = (t) => {
    const nutrients = mealPlan[t].macroNutrients;
    const recipesMacro = macro;
    nutrients.forEach((n) => {
      const { name, fixedAmount } = n;
      recipesMacro[name] -= parseFloat(fixedAmount);
    });

    setMacro(recipesMacro);
    setMealPlan({
      ...mealPlan,
      [t]: {
        recipe: "",
        macroNutrients: [],
        recipeId: "",
        image: "",
      },
    });
  };

  const validatePlan = () => {
    const { breakfast, dinner, lunch } = mealPlan;
    if (!breakfast && !dinner && !lunch) {
      return false;
    }
    return true;
  };

  const handleSave = async () => {
    if (validatePlan()) {
      try {
        const host = `http://localhost:3000/api/plans/addplan`;
        console.log(macro);
        const recipeData = {
          plan: mealPlan,
          macroNutrients: macro,
          date: date,
        };
        const jwtToken = Cookies.get("recipeJwtToken");

        const { data } = await axios.post(host, recipeData, {
          headers: {
            "auth-token": jwtToken,
          },
        });
        console.log(data);
        if (data.status) {
          toast.success(data.msg, { duration: 1000 });
        } else {
          toast.error(data.msg, { duration: 1000 });
        }
      } catch (error) {
        console.log(error);
        toast.error("Something went wrong!", { duration: 1000 });
      }
    }
  };

  const handleDateChange = (e) => {
    const day = new Date(e.target.value);
    setWeekDay(days[day.getDay()]);
    setDate(`${day.getFullYear()}-${day.getMonth() + 1}-${day.getDate()}`);
  };

  return (
    <>
      {loading ? (
        <div className="loadingContainer">
          <Loader />
        </div>
      ) : (
        <>
          <div className="planContainer">
            <div className="heading">
              <h1>Plan, Cook, Enjoy</h1>
            </div>
            <div className="packer">
              <div className="dateContainer">
                <FiChevronLeft onClick={handleLeftDay} />
                <div className="row">
                  <div className="date">{date}</div>
                  <p>{weekDay}</p>
                </div>
                <FiChevronRight onClick={handleRightDay} />
              </div>
              <input onChange={handleDateChange} type="date" />
              <h1>Macro Nutrients</h1>
              <div className="nutrition">
                <div className="row">
                  <span>Protein:</span>
                  <p>{macro.protein.toFixed(2)} g</p>
                </div>
                <div className="row">
                  <span>Carbohydrates:</span>
                  <p>{macro.carbohydrates.toFixed(2)} g</p>
                </div>
                <div className="row">
                  <span>Fat:</span>
                  <p>{macro.fat.toFixed(2)} g</p>
                </div>
                <div className="row">
                  <span>Calories:</span>
                  <p>{macro.calories.toFixed(2)} kcal</p>
                </div>
              </div>

              <h1>Dish Types</h1>
              <div className="plan">
                <div className="col">
                  <div className="row">
                    <h1>Breakfast Recipe</h1>

                    {mealPlan.breakfast.recipeId && (
                      <div
                        onClick={() => handleCancel("breakfast")}
                        className="close"
                      >
                        <CgClose />
                      </div>
                    )}
                  </div>

                  {mealPlan.breakfast?.image ? (
                    <div
                      onClick={() => setRecipeModal("breakfast")}
                      className="meal"
                    >
                      <div className="image">
                        {mealPlan.breakfast?.image ? (
                          <img
                            src={mealPlan.breakfast?.image}
                            alt={mealPlan.breakfast?.recipe}
                          />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>
                      <p>{mealPlan.breakfast?.recipe}</p>
                    </div>
                  ) : (
                    <div
                      className="add"
                      onClick={() => setRecipeModal("breakfast")}
                    >
                      <IoAdd />
                    </div>
                  )}
                </div>
                <div className="col">
                  <div className="row">
                    <h1>Lunch Recipe</h1>
                    {mealPlan.lunch.recipeId && (
                      <div
                        onClick={() => handleCancel("lunch")}
                        className="close"
                      >
                        <CgClose />
                      </div>
                    )}
                  </div>

                  {mealPlan.lunch?.image ? (
                    <div
                      onClick={() => setRecipeModal("lunch")}
                      className="meal"
                    >
                      <div className="image">
                        {mealPlan.lunch?.image ? (
                          <img
                            src={mealPlan.lunch?.image}
                            alt={mealPlan.lunch?.recipe}
                          />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>
                      <p>{mealPlan.lunch?.recipe}</p>
                    </div>
                  ) : (
                    <div
                      className="add"
                      onClick={() => setRecipeModal("lunch")}
                    >
                      <IoAdd />
                    </div>
                  )}
                </div>

                <div className="col">
                  <div className="row">
                    <h1>Dinner Recipe</h1>
                    {mealPlan.dinner.recipeId && (
                      <div
                        onClick={() => handleCancel("dinner")}
                        className="close"
                      >
                        <CgClose />
                      </div>
                    )}
                  </div>

                  {mealPlan.dinner?.image ? (
                    <div
                      onClick={() => setRecipeModal("dinner")}
                      className="meal"
                    >
                      <div className="image">
                        {mealPlan.dinner?.image ? (
                          <img
                            src={mealPlan.dinner?.image}
                            alt={mealPlan.dinner?.recipe}
                          />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>
                      <p>{mealPlan.dinner?.recipe}</p>
                    </div>
                  ) : (
                    <div
                      className="add"
                      onClick={() => setRecipeModal("dinner")}
                    >
                      <IoAdd />
                    </div>
                  )}
                </div>
              </div>
              <button
                style={
                  !mealPlan.lunch && !mealPlan.breakfast && !mealPlan.dinner
                    ? { opacity: "0.5", pointerEvents: "none" }
                    : {}
                }
                onClick={handleSave}
                type="button"
              >
                Save
              </button>
            </div>
          </div>

          {recipeModal?.length > 0 && (
            <div className="modal">
              <div className="recipes">
                <div className="modalRecipeContainer">
                  {recipes.length > 0 ? (
                    <>
                      <div className="row">
                        <h1>Favorite Recipes</h1>
                        <CgClose
                          onClick={() => setRecipeModal("")}
                          className="close"
                        />
                      </div>

                      <ul className="favorites">
                        {recipes.map((r) => {
                          const { recipeId, image, recipe, dishTypes } = r;
                          return (
                            <li
                              className="recipe"
                              onClick={(e) => {
                                handleRecipe(r);
                              }}
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
                              {dishTypes.length > 0 && (
                                <div className="row">
                                  <Tags data={dishTypes} />
                                </div>
                              )}

                              <div className="row"></div>
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
              </div>
            </div>
          )}
        </>
      )}
      <Toaster position="top-center" reverseOrder={false} />
    </>
  );
};

export default AddPlan;

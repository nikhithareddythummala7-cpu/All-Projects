import axios from "axios";
import React, { useEffect, useState } from "react";
import toast, { Toaster } from "react-hot-toast";
import useSWR from "swr";
import { CiImageOff } from "react-icons/ci";
import { MdNoFood } from "react-icons/md";
import Cookies from "js-cookie";
import host from "../../host";
import Loader from "../../components/loader/Loader";
import { useNavigate } from "react-router-dom";
import { FiChevronLeft, FiChevronRight } from "react-icons/fi";
var days = [
  "Sunday",
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
];
import "./style.scss";
import Ingredients from "../../components/ingredients/Ingredients";
import HeroSection from "../../components/hero/Hero";
import Features from "../../components/features/Features";

const Home = () => {
  const present = new Date();
  const [date, setDate] = useState(
    `${present.getFullYear()}-${present.getMonth() + 1}-${present.getDate()}`
  );
  const [weekDay, setWeekDay] = useState(days[present.getDay()]);
  const navigate = useNavigate();

  const fetcher = async (url) => {
    try {
      const jwtToken = Cookies.get("recipeJwtToken");
      const { data } = await axios.get(url, {
        headers: {
          "auth-token": jwtToken,
        },
      });
      if (data.status) {
        return data.recipe;
      } else {
        toast.error(data.msg, { duration: 1000 });
      }
    } catch (error) {
      console.log(error);
    }
  };

  const { data, loading, error, mutate } = useSWR(
    `${host}/api/plans/getplan/${date}`,
    fetcher
  );

  let macro;
  let mealPlan;
  macro = data?.macroNutrients;
  mealPlan = data?.plan;

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

  return (
    <>
      {loading ? (
        <div className="loadingContainer">
          <Loader />
        </div>
      ) : (
        <>
          <div className="dayPlanContainer">
            <HeroSection />
            <Features />
            <div className="heading">
              <h1>Meal Plan</h1>
            </div>
            <div id="meal" className="packer">
              <div className="dateContainer">
                <FiChevronLeft onClick={handleLeftDay} />
                <div className="row">
                  <div className="date">{date}</div>
                  <p>{weekDay}</p>
                </div>
                <FiChevronRight onClick={handleRightDay} />
              </div>
              <h1>Macro Nutrients</h1>
              <div className="nutrition">
                <div className="row">
                  <span>Protein:</span>
                  <p>{macro?.protein.toFixed(2)} g</p>
                </div>
                <div className="row">
                  <span>Carbohydrates:</span>
                  <p>{macro?.carbohydrates.toFixed(2)} g</p>
                </div>
                <div className="row">
                  <span>Fat:</span>
                  <p>{macro?.fat.toFixed(2)} g</p>
                </div>
                <div className="row">
                  <span>Calories:</span>
                  <p>{macro?.calories.toFixed(2)} kcal</p>
                </div>
              </div>

              <div className="plan">
                <div className="col">
                  <h1>Breakfast Recipe</h1>

                  {mealPlan?.breakfast?.image ? (
                    <div
                      onClick={() =>
                        navigate(`/recipe/${mealPlan?.breakfast?.recipeId}`)
                      }
                      className="meal"
                    >
                      <div className="image">
                        {mealPlan?.breakfast?.image ? (
                          <img
                            src={mealPlan?.breakfast?.image}
                            alt={mealPlan?.breakfast?.recipe}
                          />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>
                      <p>{mealPlan?.breakfast?.recipe}</p>
                      <div className="mealDetails">
                        <h2>Recipe Ingredients</h2>
                        {mealPlan?.breakfast?.ingredients?.length > 0 && (
                          <Ingredients
                            ingredients={mealPlan?.breakfast?.ingredients}
                            mealPlan={mealPlan}
                            name={"breakfast"}
                            date={date}
                          />
                        )}
                      </div>
                    </div>
                  ) : (
                    <div onClick={() => navigate("/addplan")} className="add">
                      <MdNoFood />
                      <p>Click to add recipe</p>
                    </div>
                  )}
                </div>
                <div className="col">
                  <h1>Lunch Recipe</h1>

                  {mealPlan?.lunch?.image ? (
                    <div
                      onClick={() =>
                        navigate(`/recipe/${mealPlan?.lunch?.recipeId}`)
                      }
                      className="meal"
                    >
                      <div className="image">
                        {mealPlan?.lunch?.image ? (
                          <img
                            src={mealPlan?.lunch?.image}
                            alt={mealPlan?.lunch?.recipe}
                          />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>
                      <p>{mealPlan?.lunch?.recipe}</p>
                      <div className="mealDetails">
                        <h2>Recipe Ingredients</h2>
                        {mealPlan?.lunch?.ingredients?.length > 0 && (
                          <Ingredients
                            ingredients={mealPlan?.lunch?.ingredients}
                            mealPlan={mealPlan}
                            name={"lunch"}
                            date={date}
                          />
                        )}
                      </div>
                    </div>
                  ) : (
                    <div onClick={() => navigate("/addplan")} className="add">
                      <MdNoFood />
                      <p>Click to add recipe</p>
                    </div>
                  )}
                </div>

                <div className="col">
                  <h1>Dinner Recipe</h1>

                  {mealPlan?.dinner?.image ? (
                    <div
                      onClick={() =>
                        navigate(`/recipe/${mealPlan?.dinner?.recipeId}`)
                      }
                      className="meal"
                    >
                      <div className="image">
                        {mealPlan?.dinner?.image ? (
                          <img
                            src={mealPlan?.dinner?.image}
                            alt={mealPlan?.dinner?.recipe}
                          />
                        ) : (
                          <div className="noImage">
                            <CiImageOff />
                          </div>
                        )}
                      </div>
                      <p>{mealPlan?.dinner?.recipe}</p>
                      <div className="mealDetails">
                        <h2>Recipe Ingredients</h2>
                        {mealPlan?.dinner?.ingredients?.length > 0 && (
                          <Ingredients
                            ingredients={mealPlan?.dinner?.ingredients}
                            mealPlan={mealPlan}
                            name={"dinner"}
                            date={date}
                          />
                        )}
                      </div>
                    </div>
                  ) : (
                    <div onClick={() => navigate("/addplan")} className="add">
                      <MdNoFood />
                      <p>Click to add recipe</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </>
      )}
      <Toaster position="top-center" reverseOrder={true} />
    </>
  );
};

export default Home;

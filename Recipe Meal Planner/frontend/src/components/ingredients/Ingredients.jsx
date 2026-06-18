import React, { useEffect, useState, useRef } from "react";
import host from "../../host";
import toast from "react-hot-toast";
import "./style.scss";
import Cookies from "js-cookie";
import { v4 } from "uuid";
import axios from "axios";

const Ingredients = (props) => {
  const [ingredients, setIngredients] = useState(props.ingredients);
  const jwtToken = Cookies.get("recipeJwtToken");

  // Using a ref to store the latest state value
  const ingredientsRef = useRef(ingredients);
  ingredientsRef.current = ingredients;

  const updateCheckList = async () => {
    try {
      const url = `${host}/api/plans/editplan`;
      const plan = props.mealPlan;
      plan[props.name].ingredients = ingredientsRef.current; // Accessing the current state via ref

      const body = {
        plan,
        date: props.date,
      };

      const { data } = await axios.put(url, body, {
        headers: {
          "auth-token": jwtToken,
        },
      });
      if (data.status) {
      } else {
        toast.error(data.msg, { duration: 1000 });
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    return () => {
      // Calling updateCheckList with the latest state when component unmounts
      updateCheckList();
    };
    updateCheckList();
  }, [ingredients]);

  const handleCheck = (e) => {
    const id = parseInt(e.target.value);
    setIngredients((prevIngredients) => {
      const updatedIngredients = prevIngredients.map((ingredient) => {
        if (ingredient.id === id) {
          return {
            ...ingredient,
            available: !ingredient.available,
          };
        }
        return ingredient;
      });
      return updatedIngredients;
    });
  };

  return (
    <ul className="ingredients">
      {ingredients?.map((i) => {
        const { id, amount, unit, available, name } = i;
        return (
          <li key={v4()}>
            <p style={available ? { textDecoration: "line-through" } : {}}>
              {name} : {amount} {unit}
            </p>
            <input
              checked={available ? true : false}
              value={id}
              onChange={handleCheck}
              style={
                available
                  ? {
                      opacity: "0.5",
                    }
                  : {}
              }
              type="checkbox"
            />
          </li>
        );
      })}
    </ul>
  );
};

export default Ingredients;

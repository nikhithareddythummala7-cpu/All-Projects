import React from "react";
import { useNavigate } from "react-router-dom";
import "./style.scss";

const HeroSection = () => {
  const navigate = useNavigate();
  return (
    <div className="hero-container">
      <div className="hero-content">
        <h1 className="hero-title">Simplify Your Meal Planning</h1>
        <p className="hero-subtitle">
          Discover, Plan, and Enjoy Delicious Meals
        </p>
        <div className="hero-buttons">
          <button className="btn btn-primary">
            <a style={{ textDecoration: "none", color: "white" }} href="#meal">
              Today's Meal
            </a>
          </button>
          <button
            onClick={() => navigate("/recipes")}
            className="btn btn-secondary"
          >
            Explore Recipes
          </button>
        </div>
      </div>
    </div>
  );
};

export default HeroSection;

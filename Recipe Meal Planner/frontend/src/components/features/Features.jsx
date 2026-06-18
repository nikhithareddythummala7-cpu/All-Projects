import React from "react";
import "./style.scss"; // Import your SCSS file for styling

const Features = () => {
  return (
    <div className="features-background-container">
      <div className="features-container">
        <h2 className="section-title">Key Features</h2>
        <div className="feature feature1">
          <i className="fas fa-book feature-icon"></i>
          <h3 className="feature-title">Recipe Database</h3>
          <p className="feature-description">
            Access a vast database of recipes covering various cuisines, dietary
            preferences, and cooking styles.
          </p>
        </div>
        <div className="feature feature2">
          <i className="far fa-calendar-alt feature-icon"></i>
          <h3 className="feature-title">Custom Meal Plans</h3>
          <p className="feature-description">
            Create personalized meal plans by selecting recipes from the
            database and organizing them into schedules.
          </p>
        </div>
        <div className="feature feature3">
          <i className="fas fa-info-circle feature-icon"></i>
          <h3 className="feature-title">Nutritional Information</h3>
          <p className="feature-description">
            View detailed nutritional information for each recipe, including
            calorie count, macronutrient breakdown, and allergen information.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Features;

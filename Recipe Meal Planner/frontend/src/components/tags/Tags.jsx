import React from "react";
import "./style.scss";

const Tags = ({ data, diets }) => {
  return (
    <div className="genres">
      {data?.map((g, i) => {
        const genre = g[0].toUpperCase() + g.slice(1);
        return (
          <div
            style={{ backgroundColor: diets ? "green" : "crimson" }}
            key={i}
            className="genre"
          >
            {genre}
          </div>
        );
      })}
    </div>
  );
};

export default Tags;

const mongoose = require("mongoose");

const favoriteSchema = new mongoose.Schema({
  userEmail: {
    type: String,
    required: true,
  },
  recipeId: {
    type: String,
    required: true,
  },

  recipe: {
    type: String,
    required: true,
  },
  image: {
    type: String,
    required: true,
  },
  nutrition: {
    type: Object,
    required: true,
  },

  dishTypes: {
    type: Array,
  },
  ingredients: {
    type: Array,
  },
});

module.exports = mongoose.model("Favorites", favoriteSchema);

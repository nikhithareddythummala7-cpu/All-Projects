const Favorites = require("../models/favoritesModel");

module.exports.saveRecipe = async (req, res) => {
  try {
    const { recipeId, image, recipe, nutrition, dishTypes, ingredients } =
      req.body;
    const { email } = req.user.userDetails;
    const find = await Favorites.findOne({ recipeId, userEmail: email });
    if (find) {
      return res.json({ status: false, msg: "Recipe is already saved!" });
    }
    await Favorites.create({
      recipeId,
      userEmail: email,
      recipe,
      image,
      nutrition,
      dishTypes,
      ingredients,
    });
    return res
      .status(201)
      .json({ status: true, msg: "Recipe saved successfully :)" });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, msg: "Server issue :(" });
  }
};

module.exports.unsaveRecipe = async (req, res) => {
  try {
    const { recipeId } = req.params;
    const { email } = req.user.userDetails;

    await Favorites.deleteOne({
      recipeId,
      userEmail: email,
    });
    return res
      .status(200)
      .json({ status: true, msg: "Recipe unsaved successfully :)" });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, msg: "Server issue :(" });
  }
};

module.exports.getSavedRecipes = async (req, res) => {
  try {
    const { email } = req.user.userDetails;
    const favRecipes = await Favorites.find({ userEmail: email });

    return res.status(200).json({ status: true, favRecipes });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, msg: "Server issue :(" });
  }
};

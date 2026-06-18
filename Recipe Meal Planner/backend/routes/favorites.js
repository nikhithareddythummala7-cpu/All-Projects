const router = require("express").Router();
const fetchUser = require("../middlewares/fetchUser");
const {
  saveRecipe,
  unsaveRecipe,
  getSavedRecipes,
} = require("../controllers/favoritesController");

router.post("/saverecipe", fetchUser, saveRecipe);
router.delete("/unsaverecipe/:recipeId", fetchUser, unsaveRecipe);
router.get("/getsavedrecipes", fetchUser, getSavedRecipes);

module.exports = router;

const router = require("express").Router();
const fetchUser = require("../middlewares/fetchUser");
const {
  register,
  login,
  editProfile,
  fetchUserDetails,
} = require("../controllers/authController");

router.post("/register", register);
router.post("/login", login);
router.put("/editprofile", editProfile);
router.get("/fetchuser", fetchUser, fetchUserDetails);
module.exports = router;

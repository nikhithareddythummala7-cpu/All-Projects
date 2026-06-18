const router = require("express").Router();

const fetchUser = require("../middlewares/fetchUser");
const { addPlan, editPlan, getPlan } = require("../controllers/planController");

router.post("/addplan", fetchUser, addPlan);
router.put("/editplan", fetchUser, editPlan);
router.get("/getplan/:date", fetchUser, getPlan);

module.exports = router;

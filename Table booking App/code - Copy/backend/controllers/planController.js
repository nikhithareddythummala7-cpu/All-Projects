const Plan = require("../models/planModel");

module.exports.addPlan = async (req, res) => {
  try {
    const { plan, macroNutrients, date } = req.body;

    const { email } = req.user.userDetails;

    const findPlan = await Plan.findOne({ date, userEmail: email });
    if (findPlan) {
      await Plan.updateOne(
        { userEmail: email, date },
        {
          $set: {
            plan,
            macroNutrients,
          },
        }
      );
    } else {
      await Plan.create({
        plan,
        macroNutrients,
        date,
        userEmail: email,
      });
    }

    return res.status(201).json({ status: true, msg: "Meal Plan Saved :)" });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, msg: "Server issue :(" });
  }
};

module.exports.editPlan = async (req, res) => {
  try {
    const { plan, date } = req.body;

    const { email } = req.user.userDetails;
    // const findPlan = await Plan.findOne({ date, userEmail: email });
    await Plan.updateOne(
      { userEmail: email, date },
      {
        $set: {
          plan,
        },
      }
    );
    return res.status(200).json({ status: true, msg: "Meal Plan Saved :)" });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, msg: "Server issue :(" });
  }
};

module.exports.getPlan = async (req, res) => {
  try {
    const { date } = req.params;
    const user = req.user.userDetails;
    const recipe = await Plan.findOne({ userEmail: user.email, date });

    return res.status(200).json({ status: true, recipe });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ status: false, msg: "Server issue :(" });
  }
};

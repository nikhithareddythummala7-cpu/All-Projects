const mongoose = require("mongoose");

const planSchema = new mongoose.Schema({
  userEmail: {
    type: String,
    required: true,
  },
  plan: {
    type: Object,
    required: true,
  },
  macroNutrients: {
    type: Object,
    required: true,
  },
  date: {
    type: String,
    required: true,
  },
});

module.exports = mongoose.model("Plan", planSchema);

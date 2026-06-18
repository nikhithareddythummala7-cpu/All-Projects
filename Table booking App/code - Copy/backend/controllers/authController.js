const User = require("../models/userModel");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");

module.exports.register = async (req, res, next) => {
  try {
    const { name: username, password, email } = req.body;
    console.log(username);
    //check that is there a same username exits
    const usernameCheck = await User.findOne({ username });
    if (usernameCheck) {
      return res.json({ msg: "Username already used", status: false });
    }

    //check that is there a same email exists
    const emailCheck = await User.findOne({ email });
    if (emailCheck) {
      return res.json({ msg: "Email is already registered!", status: false });
    }

    //create hashed pass
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    const user = await User.create({
      email,
      username,
      password: hashedPassword,
    });

    const userDetails = {
      name: user.username,
      email,
    };

    const secretKey = "SSC";
    const payload = {
      userDetails,
    };
    const jwtToken = await jwt.sign(payload, secretKey);

    return res.json({ status: true, jwtToken, userDetails });
  } catch (error) {
    return res.json({ msg: "Server issue :(", status: false });
  }
};

module.exports.login = async (req, res, next) => {
  try {
    const { email, password } = req.body;

    //authentication for user
    const user = await User.findOne({ email });
    if (!user)
      return res.json({ msg: "Email is not registered!", status: false });

    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid)
      return res.json({ msg: "Incorrect Password :(", status: false });

    const userDetails = {
      name: user.username,
      email,
    };

    const secretKey = "SSC";
    const payload = {
      userDetails,
    };
    const jwtToken = await jwt.sign(payload, secretKey);

    return res.json({ status: true, jwtToken, userDetails });
  } catch (error) {
    return res.json({ msg: "Server issue :(", status: false });
  }
};

module.exports.editProfile = async (req, res) => {
  try {
    const { name, password, email } = req.body;
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    await User.updateOne(
      { email },
      {
        $set: {
          username: name,
          password: hashedPassword,
          email: email,
        },
      }
    );
    const user = await User.findOne({ email });
    const userDetails = {
      name: user.username,
      email,
    };
    const secretKey = "SSC";
    const payload = {
      userDetails,
    };
    const jwtToken = await jwt.sign(payload, secretKey);
    res.status(200).json({ status: true, jwtToken });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ msg: "Server issue :(", status: false });
  }
};

module.exports.fetchUserDetails = async (req, res) => {
  try {
    const user = await User.findOne({ email: req.user.userDetails.email });
    const { username, email } = user;
    res.status(200).json({ status: true, details: { username, email } });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ msg: "Server issue :(", status: false });
  }
};

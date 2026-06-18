import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import Cookie from "js-cookie";
import host from "../../host";
import "./style.scss";
import axios from "axios";
import { RxCross2 } from "react-icons/rx";
import Cookies from "js-cookie";
import { CgProfile } from "react-icons/cg";

const toastOptions = {
  duration: 1000,
};

const Profile = () => {
  const [userData, setUserData] = useState({
    name: "",
    password: "",
    email: "",
    confirmPassword: "",
  });
  const [showPass, setShowPass] = useState(false);
  const [edit, setEdit] = useState(false);
  const onChange = (e) => {
    setUserData({ ...userData, [e.target.name]: e.target.value });
  };
  const navigate = useNavigate();

  const fetchUserDetails = async () => {
    try {
      const jwtToken = Cookies.get("recipeJwtToken");
      const url = `${host}/api/auth/fetchuser`;
      const { data } = await axios.get(url, {
        headers: {
          "auth-token": jwtToken,
        },
      });

      if (data.status) {
        const { username, email } = data.details;
        setUserData({
          name: username,
          email,
          password: "",
          confirmPassword: "",
        });
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    fetchUserDetails();
  }, []);

  const handleValidation = () => {
    const { password, confirmPassword, name, email } = userData;
    if (password !== confirmPassword) {
      toast.error(
        "Password and confirm password should be same.",
        toastOptions
      );
      return false;
    } else if (name.length < 3) {
      toast.error(
        "Username should be greater than 3 characters.",
        toastOptions
      );
      return false;
    } else if (password.length < 4) {
      toast.error(
        "Password should be equal or greater than 8 characters.",
        toastOptions
      );
      return false;
    } else if (email === "") {
      toast.error("Email is required.", toastOptions);
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { email, password, name } = userData;

    if (handleValidation()) {
      const host = `http://localhost:3000/api/auth/editprofile`;

      const response = await axios.put(host, {
        name,
        password,
        email,
      });
      const { data } = response;

      if (data.status) {
        localStorage.setItem("user", JSON.stringify(data.userData));
        Cookie.set("recipeJwtToken", data.jwtToken);
        setUserData({
          name: "",
          password: "",
          email: "",
          confirmPassword: "",
        });
        toast.success("Profile edit Successful", {
          duration: 1000,
        });
        setTimeout(() => {
          navigate("/");
        }, 1000);
      } else {
        toast.error(data.msg, toastOptions);
      }
    }
  };
  const onShowPass = () => {
    setShowPass((prev) => !prev);
  };
  return (
    <>
      <div className="profileContainer">
        <form onSubmit={handleSubmit} className="formContainer">
          <p className="loginTitle">
            <CgProfile style={{ fontSize: "60px" }} />
          </p>

          <div className="inputContainer">
            <label htmlFor="name">Username</label>
            <input
              name="name"
              id="name"
              type="text"
              placeholder="Enter Username"
              className="input"
              onChange={(e) => onChange(e)}
              value={userData.name}
            />
          </div>
          <div className="inputContainer">
            <label htmlFor="email">Email</label>
            <input
              name="email"
              id="email"
              type="email"
              placeholder="Enter your email"
              className="input"
              onChange={(e) => onChange(e)}
              value={userData.email}
            />
          </div>

          {edit && (
            <div className="inputContainer">
              <label htmlFor="pass">Password</label>
              <input
                name="password"
                id="pass"
                type={showPass ? "text" : "password"}
                placeholder="Enter your password"
                className="input"
                onChange={(e) => onChange(e)}
                value={userData.password}
              />
            </div>
          )}

          {edit && (
            <div className="inputContainer">
              <label htmlFor="confirm">Confirm Password</label>
              <input
                name="confirmPassword"
                id="confirm"
                type={showPass ? "text" : "password"}
                placeholder="Confirm your password"
                className="input"
                onChange={(e) => onChange(e)}
                value={userData.confirmPassword}
              />
            </div>
          )}

          {edit && (
            <div className="checkbox">
              <input onChange={onShowPass} id="check" type="checkbox" />
              <label htmlFor="check">Show Password</label>
            </div>
          )}

          {edit && <button type="submit">Save</button>}
          {!edit && (
            <button onClick={() => setEdit(true)} type="button">
              Edit
            </button>
          )}

          {edit && (
            <RxCross2 onClick={() => setEdit(false)} className="cancelIcon" />
          )}
        </form>
        <Toaster position="top-center" reverseOrder={false} />
      </div>
    </>
  );
};

export default Profile;

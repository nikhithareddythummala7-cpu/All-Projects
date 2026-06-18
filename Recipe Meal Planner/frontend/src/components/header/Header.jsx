import React, { useContext, useEffect, useState } from "react";
import { RxHamburgerMenu } from "react-icons/rx";
import { BiFoodMenu } from "react-icons/bi";
import { IoFastFoodOutline } from "react-icons/io5";
import { GoSearch } from "react-icons/go";
import { PiHandbagSimple } from "react-icons/pi";
import "./style.scss";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import Cookies from "js-cookie";
import logo from "../../assets/logo.png";
import { CgProfile } from "react-icons/cg";
import { CiLogin, CiLogout } from "react-icons/ci";
import { searchContext } from "../../context/searchContext";

const Header = () => {
  const [menu, setMenu] = useState(false);
  const [search, setSearch] = useState(false);
  const [searchInput, setSearchInput] = useState("");
  const { setQuery } = useContext(searchContext);
  const navigate = useNavigate();

  useEffect(() => {
    const jwtToken = Cookies.get("recipeJwtToken");
    if (!jwtToken) {
      navigate("/login", { replace: true });
    }
  }, []);

  const handleLogout = () => {
    Cookies.remove("jwtToken");
    toast.success("Signed out successfully", { duration: 1000 });
    setTimeout(() => {
      navigate("/login");
    }, 1000);
  };
  const handleLogin = () => {
    navigate("/login");
  };

  const jwtToken = Cookies.get("jwtToken");

  const handleSearch = () => {
    setQuery(searchInput.length === 0 ? "burger" : searchInput);
    navigate("/recipes");
  };

 const handleEnter = (e) => {
  if (e.key === "Enter") {
    setQuery(searchInput.length === 0 ? "burger" : searchInput);
    navigate("/recipes");
  }
};

  return (
    <>
      <nav className="navbar">
        <div
          onClick={() => {
            navigate("/");
            setMenu(false);
          }}
          className="logo"
        >
          <img src={logo} className="icon" />
          <p>TasteTrack</p>
        </div>

        <div
          className="menuButton"
          onClick={() => setMenu(!menu)}
          type="button"
        >
          <RxHamburgerMenu />
        </div>

        {menu && (
          <ul className="navbarMenu">
            <li>
              <GoSearch onClick={() => setSearch(!search)} />
            </li>
            <li>
              <IoFastFoodOutline
                onClick={() => {
                  navigate("/recipes");
                  setQuery("burger");
                  setMenu(!menu);
                }}
              />
            </li>
            <li>
              <PiHandbagSimple
                onClick={() => {
                  navigate("/favorites");
                  setMenu(!menu);
                }}
              />
            </li>
            <li>
              <BiFoodMenu
                onClick={() => {
                  navigate("/addplan");
                  setMenu(!menu);
                }}
              />
            </li>
            <li>
              <CgProfile
                onClick={() => {
                  navigate("/profile");
                  setMenu(!menu);
                }}
              />
            </li>
            <li>
              {jwtToken ? (
                <CiLogout onClick={handleLogout} />
              ) : (
                <CiLogin onClick={handleLogin} />
              )}
            </li>
          </ul>
        )}

        <ul className="menu">
          <li className="search">
            <div className="menuSearchContainer">
              <input
                onKeyUp={handleEnter}
                onChange={(e) => setSearchInput(e.target.value)}
                placeholder="e.g: burger"
              />
              <button onClick={handleSearch} className="searchButton">
                <GoSearch />
              </button>
            </div>
          </li>

          <li>
            <IoFastFoodOutline
              onClick={() => {
                navigate("/recipes");
                setQuery("burger");
              }}
            />
          </li>
          <li>
            <PiHandbagSimple onClick={() => navigate("/favorites")} />
          </li>
          <li>
            <BiFoodMenu onClick={() => navigate("/addplan")} />
          </li>
          <li>
            <CgProfile
              onClick={() => {
                navigate("/profile");
                setMenu(!menu);
              }}
            />
          </li>
          <li>
            {jwtToken ? (
              <CiLogout onClick={handleLogout} />
            ) : (
              <CiLogin onClick={handleLogin} />
            )}
          </li>
        </ul>

        <Toaster position="top-center" reverseOrder={false} />
      </nav>
      {search && (
        <div className="searchContainer">
          <input
            onKeyUp={handleEnter}
            onChange={(e) => setSearchInput(e.target.value)}
            placeholder="e.g: burger"
          />
          <button onClick={handleSearch}>
            <GoSearch />
          </button>
        </div>
      )}
    </>
  );
};

export default Header;

import React, { Suspense, lazy } from "react";
import { Route, Routes } from "react-router-dom";
import Header from "./components/header/Header";
import Footer from "./components/footer/Footer";
import { Toaster } from "react-hot-toast";
import Loader from "./components/loader/Loader";

// Lazy load the pages
const Home = lazy(() => import("./pages/home/Home"));
const Login = lazy(() => import("./pages/login/Login"));
const Register = lazy(() => import("./pages/register/Register"));
const RecipeDetails = lazy(() => import("./pages/recipeDetails/RecipeDetails"));
const Favorites = lazy(() => import("./pages/favorites/Favorites"));
const AddPlan = lazy(() => import("./pages/addPlan/AddPlan"));
const Recipes = lazy(() => import("./pages/recipes/Recipes"));
const Profile = lazy(() => import("./pages/profile/Profile"));

const ComponentWrapper = ({ children }) => (
  <>
    <Header />
    {children}
    <Footer />
  </>
);

const App = () => {
  return (
    <>
      <Suspense
        fallback={
          <div className="loadingContainer">
            <Loader />
          </div>
        }
      >
        <Routes>
          <Route
            path="/"
            element={
              <ComponentWrapper>
                <Home />
              </ComponentWrapper>
            }
          />
          <Route
            path="/recipe/:recipeId"
            element={
              <ComponentWrapper>
                <RecipeDetails />
              </ComponentWrapper>
            }
          />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/favorites"
            element={
              <ComponentWrapper>
                <Favorites />
              </ComponentWrapper>
            }
          />
          <Route
            path="/addplan"
            element={
              <ComponentWrapper>
                <AddPlan />
              </ComponentWrapper>
            }
          />
          <Route
            path="/recipes"
            element={
              <ComponentWrapper>
                <Recipes />
              </ComponentWrapper>
            }
          />
          <Route
            path="/profile"
            element={
              <ComponentWrapper>
                <Profile />
              </ComponentWrapper>
            }
          />
        </Routes>
      </Suspense>
      <Toaster position="top-center" reverseOrder={false} />
    </>
  );
};

export default App;

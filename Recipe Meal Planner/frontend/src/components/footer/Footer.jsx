import React from "react";
import { FaFacebook } from "react-icons/fa6";
import { FaInstagram } from "react-icons/fa6";
import { FaLinkedin } from "react-icons/fa";
import "./style.scss"; // Import your SCSS file for styling

const Footer = () => {
  return (
    <footer className="footer-container">
      <div className="footer-content">
        <h1 className="footer-title">TasteTrack: Plan, Cook, Enjoy</h1>
        <div className="footer-links">
          <a href="#home">Home</a>
          <a href="#recipes">Recipes</a>
          <a href="#meal-plans">Meal Plans</a>
          <a href="#shopping-list">Shopping List</a>
          <a href="#about">About Us</a>
          <a href="#contact">Contact</a>
        </div>
        <ul className="social-icons">
          <li>
            <FaFacebook />
          </li>
          <li>
            <FaLinkedin />
          </li>
          <li>
            <FaInstagram />
          </li>
          {/* Add more social media icons as needed */}
        </ul>
        <p className="copyright">
          © {new Date().getFullYear()} TasteTrack. All rights reserved.
        </p>
      </div>
    </footer>
  );
};

export default Footer;

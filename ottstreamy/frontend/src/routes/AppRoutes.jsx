import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Home from '../pages/Home';
import MovieDetails from '../pages/MovieDetails';
import WatchPage from '../pages/WatchPage';
import Watchlist from '../pages/Watchlist';
import Profile from '../pages/Profile';
import AdminDashboard from '../pages/AdminDashboard';
import AddMovie from '../pages/AddMovie';
import EditMovie from '../pages/EditMovie';

const AppRoutes = () => (
  <Routes>
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />
    <Route path="/" element={<Home />} />
    <Route path="/movie/:id" element={<MovieDetails />} />
    <Route path="/watch/:id" element={<ProtectedRoute><WatchPage /></ProtectedRoute>} />
    <Route path="/watchlist" element={<ProtectedRoute><Watchlist /></ProtectedRoute>} />
    <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
    <Route path="/admin" element={<ProtectedRoute roles={["ADMIN"]}><AdminDashboard /></ProtectedRoute>} />
    <Route path="/admin/add-movie" element={<ProtectedRoute roles={["ADMIN"]}><AddMovie /></ProtectedRoute>} />
    <Route path="/admin/edit-movie/:id" element={<ProtectedRoute roles={["ADMIN"]}><EditMovie /></ProtectedRoute>} />
  </Routes>
);

export default AppRoutes;

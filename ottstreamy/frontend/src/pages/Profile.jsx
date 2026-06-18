import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const Profile = () => {
  const { user, logout } = useContext(AuthContext);
  if (!user) return <div className="container">Loading...</div>;
  return (
    <div className="container">
      <h2>Profile</h2>
      <p>Name: {user.name}</p>
      <p>Email: {user.email}</p>
      <p>Role: {user.role}</p>
      <p>Subscription: {user.subscriptionType}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

export default Profile;

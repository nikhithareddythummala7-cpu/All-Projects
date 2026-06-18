import React, { useEffect, useState, useContext } from 'react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Watchlist = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState('');
  const { user } = useContext(AuthContext);

  useEffect(() => {
    if (!user) return;
    api.get(`/api/users/${user.id}/watchlist`)
      .then(res => setMovies(res.data))
      .catch(() => setError('Failed to load watchlist'));
  }, [user]);

  if (error) return <div className="container" style={{color:'red'}}>{error}</div>;

  return (
    <div className="container">
      <h2>My Watchlist</h2>
      <div style={{display:'flex',flexWrap:'wrap',gap:'1rem'}}>
        {movies.map(movie => (
          <div className="card" key={movie.id} style={{width:'200px'}}>
            <img src={movie.thumbnailUrl} alt={movie.title} style={{width:'100%'}} />
            <h3>{movie.title}</h3>
            <a href={`/movie/${movie.id}`}>Details</a>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Watchlist;

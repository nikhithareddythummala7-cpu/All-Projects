import React, { useEffect, useState } from 'react';
import api from '../services/api';

const Home = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/api/movies')
      .then(res => setMovies(res.data))
      .catch(() => setError('Failed to load movies'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="container">Loading...</div>;
  if (error) return <div className="container" style={{color:'red'}}>{error}</div>;

  return (
    <div className="container">
      <h2>Movies</h2>
      <div style={{display:'flex',flexWrap:'wrap',gap:'1rem'}}>
        {movies.map(movie => (
          <div className="card" key={movie.id} style={{width:'200px'}}>
            <img src={movie.thumbnailUrl} alt={movie.title} style={{width:'100%'}} />
            <h3>{movie.title} {movie.isPremium && <span style={{color:'#FFD700'}}>★</span>}</h3>
            <p>{movie.genre?.join(', ')}</p>
            <p>{movie.releaseYear}</p>
            <a href={`/movie/${movie.id}`}>Details</a>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Home;

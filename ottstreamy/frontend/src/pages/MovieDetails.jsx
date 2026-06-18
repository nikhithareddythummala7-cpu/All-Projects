import React, { useEffect, useState, useContext } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const MovieDetails = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState('');
  const { token } = useContext(AuthContext);

  useEffect(() => {
    api.get(`/api/movies/${id}`)
      .then(res => setMovie(res.data))
      .catch(() => setError('Movie not found'));
  }, [id]);

  if (error) return <div className="container" style={{color:'red'}}>{error}</div>;
  if (!movie) return <div className="container">Loading...</div>;

  return (
    <div className="container">
      <h2>{movie.title} {movie.isPremium && <span style={{color:'#FFD700'}}>★ Premium</span>}</h2>
      <img src={movie.thumbnailUrl} alt={movie.title} style={{width:'300px'}} />
      <p>{movie.description}</p>
      <p>Genre: {movie.genre?.join(', ')}</p>
      <p>Release Year: {movie.releaseYear}</p>
      <p>Rating: {movie.rating}</p>
      <a href={`/watch/${movie.id}`}>Watch Now</a>
    </div>
  );
};

export default MovieDetails;

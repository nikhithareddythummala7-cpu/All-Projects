import React from 'react';

const MovieCard = ({ movie }) => (
  <div className="card" style={{width:'200px'}}>
    <img src={movie.thumbnailUrl} alt={movie.title} style={{width:'100%'}} />
    <h3>{movie.title} {movie.isPremium && <span style={{color:'#FFD700'}}>★</span>}</h3>
    <p>{movie.genre?.join(', ')}</p>
    <p>{movie.releaseYear}</p>
    <a href={`/movie/${movie.id}`}>Details</a>
  </div>
);

export default MovieCard;

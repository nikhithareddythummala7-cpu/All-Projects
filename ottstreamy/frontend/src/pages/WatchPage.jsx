import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/api';

const WatchPage = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get(`/api/movies/${id}`)
      .then(res => setMovie(res.data))
      .catch(() => setError('Movie not found'));
  }, [id]);

  if (error) return <div className="container" style={{color:'red'}}>{error}</div>;
  if (!movie) return <div className="container">Loading...</div>;

  return (
    <div className="container">
      <h2>Watching: {movie.title}</h2>
      <video width="100%" height="400" controls>
        <source src={`${import.meta.env.VITE_API_URL}/api/stream/${movie.videoUrl}`} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
    </div>
  );
};

export default WatchPage;

import React from 'react';
import { useParams } from 'react-router-dom';

const EditMovie = () => {
  const { id } = useParams();
  return (
    <div className="container">
      <h2>Edit Movie</h2>
      <p>Edit movie with ID: {id} (to be implemented).</p>
    </div>
  );
};

export default EditMovie;

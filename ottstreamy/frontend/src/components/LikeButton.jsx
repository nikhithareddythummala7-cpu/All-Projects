import React from 'react';

const LikeButton = ({ liked, onClick }) => (
  <button onClick={onClick} style={{color: liked ? '#1db954' : '#fff'}}>
    {liked ? '♥' : '♡'} Like
  </button>
);

export default LikeButton;

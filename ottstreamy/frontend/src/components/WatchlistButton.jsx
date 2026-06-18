import React from 'react';

const WatchlistButton = ({ inWatchlist, onClick }) => (
  <button onClick={onClick} style={{color: inWatchlist ? '#1db954' : '#fff'}}>
    {inWatchlist ? '✓' : '+'} Watchlist
  </button>
);

export default WatchlistButton;

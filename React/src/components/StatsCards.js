import React from 'react';
import './StatsCards.css';

function StatsCards({ stats, loading }) {
  const cards = [
    {
      title: 'Total Patients',
      value: stats?.totalPatients || 0,
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
      ),
      color: 'blue',
    },
    {
      title: 'High-Risk Cases',
      value: stats?.highRiskCases || 0,
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
          <line x1="12" y1="9" x2="12" y2="13"/>
          <line x1="12" y1="17" x2="12.01" y2="17"/>
        </svg>
      ),
      color: 'red',
    },
    {
      title: 'Newly Reported',
      value: stats?.newlyReported || 0,
      subtitle: 'Last 24 hours',
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>
      ),
      color: 'green',
    },
    {
      title: 'Emergency Priority',
      value: stats?.emergencyPriority || 0,
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
        </svg>
      ),
      color: 'orange',
    },
    {
      title: 'Attended Cases',
      value: stats?.attendedCases || 0,
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
          <polyline points="22 4 12 14.01 9 11.01"/>
        </svg>
      ),
      color: 'teal',
    },
    {
      title: 'Pending Cases',
      value: stats?.pendingCases || 0,
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="8" x2="12" y2="12"/>
          <line x1="12" y1="16" x2="12.01" y2="16"/>
        </svg>
      ),
      color: 'purple',
    },
  ];

  if (loading) {
    return (
      <div className="stats-grid">
        {[1, 2, 3, 4, 5, 6].map((i) => (
          <div key={i} className="stats-card stats-card-loading">
            <div className="stats-skeleton stats-skeleton-icon"></div>
            <div className="stats-skeleton stats-skeleton-text"></div>
            <div className="stats-skeleton stats-skeleton-value"></div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="stats-grid">
      {cards.map((card, index) => (
        <div key={index} className={`stats-card stats-card-${card.color}`}>
          <div className="stats-card-header">
            <div className={`stats-icon stats-icon-${card.color}`}>
              {card.icon}
            </div>
          </div>
          <div className="stats-card-body">
            <p className="stats-title">{card.title}</p>
            <h3 className="stats-value">{card.value.toLocaleString()}</h3>
            {card.subtitle && <p className="stats-subtitle">{card.subtitle}</p>}
          </div>
        </div>
      ))}
    </div>
  );
}

export default StatsCards;

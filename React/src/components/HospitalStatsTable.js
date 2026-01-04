import React, { useState } from 'react';
import { statsAPI } from '../services/api';
import './HospitalStatsTable.css';

const ITEMS_PER_PAGE = 10;

function HospitalStatsTable({ stats, onRefresh }) {
  const [toggling, setToggling] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);

  if (!stats || stats.length === 0) {
    return <div className="no-stats">No hospital statistics available.</div>;
  }

  // Pagination calculations
  const totalPages = Math.ceil(stats.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const currentStats = stats.slice(startIndex, endIndex);

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const getPageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;
    
    if (totalPages <= maxVisiblePages) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        for (let i = 1; i <= 4; i++) pages.push(i);
        pages.push('...');
        pages.push(totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(1);
        pages.push('...');
        for (let i = totalPages - 3; i <= totalPages; i++) pages.push(i);
      } else {
        pages.push(1);
        pages.push('...');
        for (let i = currentPage - 1; i <= currentPage + 1; i++) pages.push(i);
        pages.push('...');
        pages.push(totalPages);
      }
    }
    return pages;
  };

  const handleToggleStatus = async (hospitalId) => {
    try {
      setToggling(hospitalId);
      await statsAPI.toggleHospitalStatus(hospitalId);
      if (onRefresh) onRefresh();
    } catch (err) {
      console.error('Error toggling hospital status:', err);
      alert('Failed to update hospital status');
    } finally {
      setToggling(null);
    }
  };

  return (
    <div className="hospital-stats-container">
      <h3>Hospital Performance Metrics</h3>
      <div className="table-responsive">
        <table className="hospital-stats-table">
          <thead>
            <tr>
              <th rowSpan="2">Hospital Name</th>
              <th rowSpan="2">Status</th>
              <th rowSpan="2">State</th>
              <th rowSpan="2">City</th>
              <th rowSpan="2">State Total</th>
              <th colSpan="5">Hospital Attended (State-wide)</th>
              <th colSpan="6">Region Specific (Pincodes)</th>
              <th rowSpan="2">Actions</th>
            </tr>
            <tr>
              <th>Total</th>
              <th>Low</th>
              <th>Med</th>
              <th>High</th>
              <th>Prio</th>
              <th>Total Cases</th>
              <th>Attended</th>
              <th>Low</th>
              <th>Med</th>
              <th>High</th>
              <th>Prio</th>
            </tr>
          </thead>
          <tbody>
            {currentStats.map((hospital) => (
              <tr key={hospital.hospitalId} className={!hospital.active ? 'row-blocked' : ''}>
                <td className="hospital-name">
                  {hospital.hospitalName}
                  {!hospital.active && <span className="blocked-badge">Service Blocked</span>}
                </td>
                <td>
                  <span className={`status-indicator ${hospital.active ? 'status-active' : 'status-inactive'}`}>
                    {hospital.active ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td>{hospital.state}</td>
                <td>{hospital.city}</td>
                <td className="count-cell">{hospital.stateTotalCases}</td>
                
                {/* Hospital Attended Stats */}
                <td className="count-cell highlight">{hospital.hospitalAttendedCases}</td>
                <td className="count-cell">{hospital.hospitalPriorityStats.Low}</td>
                <td className="count-cell">{hospital.hospitalPriorityStats.Medium}</td>
                <td className="count-cell">{hospital.hospitalPriorityStats.High}</td>
                <td className="count-cell">{hospital.hospitalPriorityStats.Priority}</td>
                
                {/* Region Stats */}
                <td className="count-cell region-cell">{hospital.regionTotalCases}</td>
                <td className="count-cell region-cell highlight">{hospital.regionAttendedCases}</td>
                <td className="count-cell region-cell">{hospital.regionPriorityStats.Low}</td>
                <td className="count-cell region-cell">{hospital.regionPriorityStats.Medium}</td>
                <td className="count-cell region-cell">{hospital.regionPriorityStats.High}</td>
                <td className="count-cell region-cell">{hospital.regionPriorityStats.Priority}</td>

                <td>
                  <button 
                    className={`btn-toggle ${hospital.active ? 'btn-deactivate' : 'btn-activate'}`}
                    onClick={() => handleToggleStatus(hospital.hospitalId)}
                    disabled={toggling === hospital.hospitalId}
                  >
                    {toggling === hospital.hospitalId ? '...' : (hospital.active ? 'Block' : 'Unblock')}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      <div className="stats-table-footer">
        <p>Showing {startIndex + 1}-{Math.min(endIndex, stats.length)} of {stats.length} hospital{stats.length !== 1 ? 's' : ''}</p>
        
        {totalPages > 1 && (
          <div className="pagination">
            <button 
              className="pagination-btn"
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15 18 9 12 15 6"/>
              </svg>
            </button>
            
            {getPageNumbers().map((page, index) => (
              page === '...' ? (
                <span key={`ellipsis-${index}`} className="pagination-ellipsis">...</span>
              ) : (
                <button
                  key={page}
                  className={`pagination-btn ${currentPage === page ? 'active' : ''}`}
                  onClick={() => handlePageChange(page)}
                >
                  {page}
                </button>
              )
            ))}
            
            <button 
              className="pagination-btn"
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default HospitalStatsTable;

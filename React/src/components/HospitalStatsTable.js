import React, { useState } from 'react';
import { statsAPI } from '../services/api';
import './HospitalStatsTable.css';

function HospitalStatsTable({ stats, onRefresh }) {
  const [toggling, setToggling] = useState(null);

  if (!stats || stats.length === 0) {
    return <div className="no-stats">No hospital statistics available.</div>;
  }

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
            {stats.map((hospital) => (
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
    </div>
  );
}

export default HospitalStatsTable;

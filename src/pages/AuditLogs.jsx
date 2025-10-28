import React, { useState, useEffect } from 'react';
import { auditAPI } from '../services/api';
import { toast } from 'react-toastify';

const AuditLogs = () => {
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      const response = await auditAPI.getAll();
      setLogs(response.data);
    } catch (error) {
      toast.error('Failed to fetch audit logs');
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Audit Logs</h2>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Action</th>
              <th>Entity</th>
              <th>Performed By</th>
              <th>IP Address</th>
              <th>Details</th>
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id}>
                <td>{new Date(log.timestamp).toLocaleString()}</td>
                <td><span className={`badge badge-${log.action.toLowerCase()}`}>{log.action}</span></td>
                <td>{log.entityName}</td>
                <td>{log.performedBy}</td>
                <td>{log.ipAddress}</td>
                <td>{log.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AuditLogs;

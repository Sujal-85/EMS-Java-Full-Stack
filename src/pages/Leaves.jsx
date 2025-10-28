import React, { useState, useEffect } from 'react';
import { leaveAPI, employeeAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import { FaPlus, FaCheck, FaTimes } from 'react-icons/fa';
import '../styles/Common.css';

const Leaves = () => {
  const { user, hasRole, loading } = useAuth();
  const [leaves, setLeaves] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    employee: { id: '' },
    leaveType: 'SICK_LEAVE',
    startDate: '',
    endDate: '',
    reason: ''
  });
  const [filterStatus, setFilterStatus] = useState('');

  useEffect(() => {
    if (loading) return;
    if (hasRole(['ADMIN', 'HR'])) {
      fetchLeaves();
      fetchEmployees();
    } else {
      // EMPLOYEE: fetch using /leaves/my (no need for employeeId)
      fetchLeaves();
    }
  }, [loading]);

  const fetchLeaves = async () => {
    try {
      const response = hasRole(['ADMIN', 'HR']) 
        ? await leaveAPI.getAll()
        : await leaveAPI.getMy();
      setLeaves(response.data);
    } catch (error) {
      toast.error('Failed to fetch leaves');
    }
  };

  const fetchEmployees = async () => {
    try {
      const response = await employeeAPI.getAll();
      const list = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setEmployees(list);
    } catch (error) {
      console.error('Failed to fetch employees');
      setEmployees([]);
    }
  };

  const handleApplyLeave = async (e) => {
    e.preventDefault();
    try {
      await leaveAPI.apply(formData);
      toast.success('Leave applied successfully');
      setShowModal(false);
      fetchLeaves();
    } catch (error) {
      toast.error('Failed to apply leave');
    }
  };

  const handleApprove = async (id) => {
    const comments = prompt('Enter approval comments (optional):');
    try {
      await leaveAPI.approve(id, comments);
      toast.success('Leave approved');
      fetchLeaves();
    } catch (error) {
      toast.error('Failed to approve leave');
    }
  };

  const handleReject = async (id) => {
    const comments = prompt('Enter rejection reason:');
    if (!comments) return;
    
    try {
      await leaveAPI.reject(id, comments);
      toast.success('Leave rejected');
      fetchLeaves();
    } catch (error) {
      toast.error('Failed to reject leave');
    }
  };

  const filteredLeaves = leaves.filter(leave => 
    !filterStatus || leave.status === filterStatus
  );

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Leave Management</h2>
        {/* Only EMPLOYEE can apply for leave */}
        {!hasRole(['ADMIN', 'HR']) && (
          <button
            className="btn btn-primary"
            onClick={() => {
              setFormData((fd) => ({ ...fd, employee: { id: user?.employeeId || '' } }));
              setShowModal(true);
            }}
          >
            <FaPlus /> Apply Leave
          </button>
        )}
      </div>

      <div className="filters-section">
        <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
          <option value="">All Status</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
        </select>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee</th>
              <th>Leave Type</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Days</th>
              <th>Status</th>
              <th>Reason</th>
              {hasRole(['ADMIN', 'HR']) && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {filteredLeaves.map(leave => (
              <tr key={leave.id}>
                <td>{leave.employee?.firstName} {leave.employee?.lastName}</td>
                <td>{leave.leaveType.replace('_', ' ')}</td>
                <td>{new Date(leave.startDate).toLocaleDateString()}</td>
                <td>{new Date(leave.endDate).toLocaleDateString()}</td>
                <td>{leave.numberOfDays}</td>
                <td><span className={`badge badge-${leave.status.toLowerCase()}`}>{leave.status}</span></td>
                <td>{leave.reason}</td>
                {hasRole(['ADMIN', 'HR']) && leave.status === 'PENDING' && (
                  <td className="actions">
                    <button onClick={() => handleApprove(leave.id)} className="btn-success" title="Approve">
                      <FaCheck />
                    </button>
                    <button onClick={() => handleReject(leave.id)} className="btn-danger" title="Reject">
                      <FaTimes />
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Apply for Leave</h3>
              <button onClick={() => setShowModal(false)}>&times;</button>
            </div>
            <form onSubmit={handleApplyLeave}>
              {hasRole(['ADMIN', 'HR']) && (
                <div className="form-group">
                  <label>Employee *</label>
                  <select required value={formData.employee.id}
                    onChange={(e) => setFormData({...formData, employee: { id: e.target.value }})}>
                    <option value="">Select Employee</option>
                    {employees.map(emp => (
                      <option key={emp.id} value={emp.id}>
                        {emp.firstName} {emp.lastName}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="form-group">
                <label>Leave Type *</label>
                <select required value={formData.leaveType}
                  onChange={(e) => setFormData({...formData, leaveType: e.target.value})}>
                  <option value="SICK_LEAVE">Sick Leave</option>
                  <option value="CASUAL_LEAVE">Casual Leave</option>
                  <option value="ANNUAL_LEAVE">Annual Leave</option>
                  <option value="MATERNITY_LEAVE">Maternity Leave</option>
                  <option value="PATERNITY_LEAVE">Paternity Leave</option>
                  <option value="UNPAID_LEAVE">Unpaid Leave</option>
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Start Date *</label>
                  <input type="date" required value={formData.startDate}
                    onChange={(e) => setFormData({...formData, startDate: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>End Date *</label>
                  <input type="date" required value={formData.endDate}
                    onChange={(e) => setFormData({...formData, endDate: e.target.value})} />
                </div>
              </div>

              <div className="form-group">
                <label>Reason *</label>
                <textarea rows="4" required value={formData.reason}
                  onChange={(e) => setFormData({...formData, reason: e.target.value})} />
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">Submit</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Leaves;

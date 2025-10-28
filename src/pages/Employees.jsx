import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { employeeAPI, departmentAPI } from '../services/api';
import { toast } from 'react-toastify';
import { FaPlus, FaEdit, FaTrash, FaSearch, FaEye } from 'react-icons/fa';
import '../styles/Common.css';

const Employees = () => {
  const { hasRole } = useAuth();
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterDept, setFilterDept] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', phoneNumber: '',
    dateOfBirth: '', dateOfJoining: '', designation: '', address: '',
    status: 'ACTIVE', department: { id: '' }
  });
  const [photoFile, setPhotoFile] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchEmployees();
    fetchDepartments();
  }, []);

  const fetchEmployees = async () => {
    try {
      const response = await employeeAPI.getAll();
      // Debug log to verify what the backend returns
      console.log('GET /employees response:', response.status, response.data);
      const data = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setEmployees(data);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to fetch employees');
      setEmployees([]);
    }
  };

  const fetchDepartments = async () => {
    try {
      const response = await departmentAPI.getAll();
      const list = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setDepartments(list);
    } catch (error) {
      console.error('Failed to fetch departments');
      setDepartments([]);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchEmployees();
      return;
    }
    try {
      const response = await employeeAPI.search(searchTerm);
      const data = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setEmployees(data);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Search failed');
      setEmployees([]);
    }
  };

  const handleCreate = () => {
    setEditingEmployee(null);
    setFormData({
      firstName: '', lastName: '', email: '', phoneNumber: '',
      dateOfBirth: '', dateOfJoining: '', designation: '', address: '',
      status: 'ACTIVE', department: { id: '' }
    });
    setPhotoFile(null);
    setShowModal(true);
  };

  const handleEdit = (employee) => {
    setEditingEmployee(employee);
    setFormData({
      ...employee,
      department: { id: employee.department?.id || '' }
    });
    setPhotoFile(null);
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // sanitize payload for backend (LocalDate expects ISO date or null; id should be number)
    const payload = {
      ...formData,
      dateOfBirth: formData.dateOfBirth ? formData.dateOfBirth : null,
      dateOfJoining: formData.dateOfJoining ? formData.dateOfJoining : null,
      department: formData.department?.id
        ? { id: Number(formData.department.id) }
        : null,
    };

    const formDataToSend = new FormData();
    formDataToSend.append('employee', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
    if (photoFile) {
      formDataToSend.append('photo', photoFile);
    }

    try {
      if (editingEmployee) {
        await employeeAPI.update(editingEmployee.id, formDataToSend);
        toast.success('Employee updated successfully');
      } else {
        await employeeAPI.create(formDataToSend);
        toast.success('Employee created successfully');
      }
      setShowModal(false);
      fetchEmployees();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Operation failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this employee?')) return;
    
    try {
      await employeeAPI.delete(id);
      toast.success('Employee deleted successfully');
      fetchEmployees();
    } catch (error) {
      toast.error('Failed to delete employee');
    }
  };

  const employeesArray = Array.isArray(employees) ? employees : [];
  const filteredEmployees = employeesArray.filter(emp => {
    const matchesDept = !filterDept || emp.department?.id === parseInt(filterDept);
    const matchesStatus = !filterStatus || emp.status === filterStatus;
    return matchesDept && matchesStatus;
  });

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Employee Management</h2>
        <div style={{ opacity: 0.7, fontSize: 14 }}>
          Showing {filteredEmployees.length} of {Array.isArray(employees) ? employees.length : 0}
        </div>
        {hasRole(['ADMIN', 'HR']) && (
          <button className="btn btn-primary" onClick={handleCreate}>
            <FaPlus /> Add Employee
          </button>
        )}
      </div>

      <div className="filters-section">
        <div className="search-bar">
          <input
            type="text"
            placeholder="Search by name or email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button onClick={handleSearch}><FaSearch /></button>
        </div>

        <div className="filters">
          <select value={filterDept} onChange={(e) => setFilterDept(e.target.value)}>
            <option value="">All Departments</option>
            {(Array.isArray(departments) ? departments : []).map(dept => (
              <option key={dept.id} value={dept.id}>{dept.name}</option>
            ))}
          </select>

          <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
            <option value="">All Status</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="ON_LEAVE">On Leave</option>
            <option value="TERMINATED">Terminated</option>
          </select>
        </div>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Code</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Department</th>
              <th>Designation</th>
              <th>Status</th>
              {hasRole(['ADMIN', 'HR']) && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {filteredEmployees.length === 0 && (
              <tr>
                <td colSpan={hasRole(['ADMIN', 'HR']) ? 8 : 7} style={{ textAlign: 'center', color: '#777' }}>
                  No employees found
                </td>
              </tr>
            )}
            {filteredEmployees.map(emp => (
              <tr key={emp.id}>
                <td>{emp.employeeCode}</td>
                <td>{emp.firstName} {emp.lastName}</td>
                <td>{emp.email}</td>
                <td>{emp.phoneNumber || 'N/A'}</td>
                <td>{emp.department?.name || 'N/A'}</td>
                <td>{emp.designation || 'N/A'}</td>
                <td>
                  {(() => {
                    const statusText = emp.status || 'UNKNOWN';
                    const statusClass = `badge badge-${
                      typeof statusText === 'string' ? statusText.toLowerCase() : 'unknown'
                    }`;
                    return <span className={statusClass}>{statusText}</span>;
                  })()}
                </td>
                {hasRole(['ADMIN', 'HR']) && (
                  <td className="actions">
                    <button onClick={() => handleEdit(emp)} title="Edit"><FaEdit /></button>
                    <button onClick={() => handleDelete(emp.id)} className="btn-danger" title="Delete"><FaTrash /></button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && hasRole(['ADMIN', 'HR']) && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{editingEmployee ? 'Edit Employee' : 'Add Employee'}</h3>
              <button onClick={() => setShowModal(false)}>&times;</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label>First Name *</label>
                  <input type="text" required value={formData.firstName}
                    onChange={(e) => setFormData({...formData, firstName: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Last Name *</label>
                  <input type="text" required value={formData.lastName}
                    onChange={(e) => setFormData({...formData, lastName: e.target.value})} />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Email *</label>
                  <input type="email" required value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Phone</label>
                  <input type="tel" value={formData.phoneNumber}
                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})} />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Date of Birth</label>
                  <input type="date" value={formData.dateOfBirth}
                    onChange={(e) => setFormData({...formData, dateOfBirth: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Date of Joining</label>
                  <input type="date" value={formData.dateOfJoining}
                    onChange={(e) => setFormData({...formData, dateOfJoining: e.target.value})} />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Department</label>
                  <select value={formData.department.id}
                    onChange={(e) => setFormData({...formData, department: { id: e.target.value }})}>
                    <option value="">Select Department</option>
                    {departments.map(dept => (
                      <option key={dept.id} value={dept.id}>{dept.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Designation</label>
                  <input type="text" value={formData.designation}
                    onChange={(e) => setFormData({...formData, designation: e.target.value})} />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Status</label>
                  <select value={formData.status}
                    onChange={(e) => setFormData({...formData, status: e.target.value})}>
                    <option value="ACTIVE">Active</option>
                    <option value="INACTIVE">Inactive</option>
                    <option value="ON_LEAVE">On Leave</option>
                    <option value="TERMINATED">Terminated</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Photo</label>
                  <input type="file" accept="image/*"
                    onChange={(e) => setPhotoFile(e.target.files[0])} />
                </div>
              </div>

              <div className="form-group">
                <label>Address</label>
                <textarea rows="3" value={formData.address}
                  onChange={(e) => setFormData({...formData, address: e.target.value})} />
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? 'Saving...' : 'Save'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Employees;

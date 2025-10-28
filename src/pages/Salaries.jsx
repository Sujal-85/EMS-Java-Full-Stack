import React, { useState, useEffect } from 'react';
import { salaryAPI, employeeAPI } from '../services/api';
import { toast } from 'react-toastify';
import { FaPlus, FaDownload } from 'react-icons/fa';
import '../styles/Common.css';
import { useAuth } from '../context/AuthContext';

const Salaries = () => {
  const [salaries, setSalaries] = useState([]);
  const [employees, setEmployees] = useState([]);
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    employeeId: '',
    month: new Date().getMonth() + 1,
    year: new Date().getFullYear(),
    basicSalary: '',
    hra: 0,
    transportAllowance: 0,
    medicalAllowance: 0,
    deductions: 0,
    paymentStatus: 'PENDING',
  });

  useEffect(() => {
    fetchEmployees();
    fetchCurrentMonthSalaries();
  }, []);

  const fetchEmployees = async () => {
    try {
      const response = await employeeAPI.getAll();
      console.log('GET /employees (for salaries) response:', response.status, response.data);
      const list = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setEmployees(list.filter(emp => emp.status === 'ACTIVE'));
    } catch (error) {
      console.error('Employees fetch error:', error);
      toast.error(error.response?.data?.message || 'Failed to fetch employees');
    }
  };

  const netSalary = () => {
    const basic = parseFloat(formData.basicSalary || 0);
    const hra = parseFloat(formData.hra || 0);
    const ta = parseFloat(formData.transportAllowance || 0);
    const ma = parseFloat(formData.medicalAllowance || 0);
    const ded = parseFloat(formData.deductions || 0);
    return (basic + hra + ta + ma - ded).toFixed(2);
  };

  const openModal = () => {
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
  };

  const handleCreateSalary = async (e) => {
    e.preventDefault();
    try {
      if (!formData.employeeId) {
        toast.warning('Please select an employee');
        return;
      }
      if (!formData.basicSalary || isNaN(Number(formData.basicSalary))) {
        toast.warning('Please enter a valid basic salary');
        return;
      }

      const payload = {
        employee: { id: Number(formData.employeeId) },
        month: Number(formData.month),
        year: Number(formData.year),
        basicSalary: Number(formData.basicSalary),
        hra: Number(formData.hra || 0),
        transportAllowance: Number(formData.transportAllowance || 0),
        medicalAllowance: Number(formData.medicalAllowance || 0),
        deductions: Number(formData.deductions || 0),
        netSalary: Number(netSalary()),
        paymentStatus: formData.paymentStatus,
      };

      await salaryAPI.create(payload);
      toast.success('Salary created successfully');
      closeModal();
      fetchCurrentMonthSalaries();
    } catch (error) {
      console.error('Create salary error:', error);
      toast.error(error.response?.data?.message || 'Failed to create salary');
    }
  };

  const fetchCurrentMonthSalaries = async () => {
    try {
      const currentMonth = new Date().getMonth() + 1;
      const currentYear = new Date().getFullYear();
      const response = await salaryAPI.getByMonth(currentMonth, currentYear);
      console.log(`GET /salaries/month/${currentMonth}/year/${currentYear} response:`, response.status, response.data);
      const list = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setSalaries(list);
    } catch (error) {
      console.error('Salaries fetch error:', error);
      const msg = error.response?.data?.message
        || (error.response?.status ? `Failed to fetch salaries (status ${error.response.status})` : 'Failed to fetch salaries');
      toast.error(msg);
    }
  };

  const downloadPayslip = async (id) => {
    try {
      const response = await salaryAPI.downloadPayslip(id);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `payslip_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      toast.success('Payslip downloaded');
    } catch (error) {
      console.error('Payslip download error:', error);
      toast.error(error.response?.data?.message || 'Failed to download payslip');
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Salary Management</h2>
        {isAdmin && (
          <button className="btn btn-primary" onClick={openModal}>
            <FaPlus /> Add Salary
          </button>
        )}
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee</th>
              <th>Basic</th>
              <th>Allowances</th>
              <th>Deductions</th>
              <th>Net Salary</th>
              <th>Month/Year</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {salaries.map(salary => (
              <tr key={salary.id}>
                <td>{salary.employee?.firstName} {salary.employee?.lastName}</td>
                <td>${salary.basicSalary}</td>
                <td>${((salary.hra || 0) + (salary.transportAllowance || 0) + (salary.medicalAllowance || 0)).toFixed(2)}</td>
                <td>${salary.deductions || 0}</td>
                <td className="font-weight-bold">${salary.netSalary}</td>
                <td>{salary.month}/{salary.year}</td>
                <td>
                  {(() => {
                    const statusText = salary.paymentStatus || 'PENDING';
                    const statusClass = `badge badge-${
                      typeof statusText === 'string' ? statusText.toLowerCase() : 'pending'
                    }`;
                    return <span className={statusClass}>{statusText}</span>;
                  })()}
                </td>
                <td className="actions">
                  <button onClick={() => downloadPayslip(salary.id)} title="Download Payslip">
                    <FaDownload />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {isAdmin && showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Add Salary</h3>
              <button onClick={closeModal}>&times;</button>
            </div>
            <form onSubmit={handleCreateSalary}>
              <div className="form-group">
                <label>Employee *</label>
                <select
                  value={formData.employeeId}
                  onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
                  required
                >
                  <option value="">Select Employee</option>
                  {employees.map((emp) => (
                    <option key={emp.id} value={emp.id}>
                      {emp.employeeCode} - {emp.firstName} {emp.lastName}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Month *</label>
                  <input
                    type="number"
                    min="1"
                    max="12"
                    value={formData.month}
                    onChange={(e) => setFormData({ ...formData, month: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Year *</label>
                  <input
                    type="number"
                    min="2000"
                    max="2100"
                    value={formData.year}
                    onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Basic Salary *</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.basicSalary}
                  onChange={(e) => setFormData({ ...formData, basicSalary: e.target.value })}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>HRA</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.hra}
                    onChange={(e) => setFormData({ ...formData, hra: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Transport Allowance</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.transportAllowance}
                    onChange={(e) => setFormData({ ...formData, transportAllowance: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Medical Allowance</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.medicalAllowance}
                    onChange={(e) => setFormData({ ...formData, medicalAllowance: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Deductions</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.deductions}
                  onChange={(e) => setFormData({ ...formData, deductions: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Payment Status</label>
                <select
                  value={formData.paymentStatus}
                  onChange={(e) => setFormData({ ...formData, paymentStatus: e.target.value })}
                >
                  <option value="PENDING">PENDING</option>
                  <option value="PAID">PAID</option>
                </select>
              </div>

              <div className="form-group">
                <label>Net Salary</label>
                <input type="text" value={`$${netSalary()}`} disabled />
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Salaries;

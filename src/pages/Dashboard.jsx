import React, { useState, useEffect } from 'react';
import { dashboardAPI } from '../services/api';
import { BarChart, Bar, PieChart, Pie, Cell, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { FaUsers, FaUserCheck, FaUserTimes, FaUserClock, FaCalendarCheck, FaHourglassHalf, FaBuilding, FaMoneyBillWave } from 'react-icons/fa';
import '../styles/Dashboard.css';

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      const response = await dashboardAPI.getStats();
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: '70vh' }}>
        <div className="spinner-border text-primary" role="status" />
      </div>
    );
  }

  const employeeData = [
    { name: 'Active', value: stats?.activeEmployees || 0, color: '#28a745' },
    { name: 'Inactive', value: stats?.inactiveEmployees || 0, color: '#dc3545' },
    { name: 'On Leave', value: stats?.onLeaveEmployees || 0, color: '#ffc107' },
  ];

  const attendanceData = [
    { name: 'Present Today', value: stats?.presentToday || 0 },
    { name: 'Total Employees', value: stats?.totalEmployees || 0 },
  ];

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Dashboard Overview</h2>
        <p className="text-muted">Welcome to Employee Management System</p>
      </div>

      <div className="row g-4 mb-4">
        <div className="col-md-3">
          <div className="stat-card stat-primary">
            <div className="stat-icon">
              <FaUsers />
            </div>
            <div className="stat-content">
              <h3>{stats?.totalEmployees || 0}</h3>
              <p>Total Employees</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="stat-card stat-success">
            <div className="stat-icon">
              <FaUserCheck />
            </div>
            <div className="stat-content">
              <h3>{stats?.activeEmployees || 0}</h3>
              <p>Active Employees</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="stat-card stat-warning">
            <div className="stat-icon">
              <FaCalendarCheck />
            </div>
            <div className="stat-content">
              <h3>{stats?.presentToday || 0}</h3>
              <p>Present Today</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="stat-card stat-info">
            <div className="stat-icon">
              <FaHourglassHalf />
            </div>
            <div className="stat-content">
              <h3>{stats?.pendingLeaves || 0}</h3>
              <p>Pending Leaves</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4 mb-4">
        <div className="col-md-3">
          <div className="stat-card stat-secondary">
            <div className="stat-icon">
              <FaBuilding />
            </div>
            <div className="stat-content">
              <h3>{stats?.totalDepartments || 0}</h3>
              <p>Departments</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="stat-card stat-danger">
            <div className="stat-icon">
              <FaUserTimes />
            </div>
            <div className="stat-content">
              <h3>{stats?.inactiveEmployees || 0}</h3>
              <p>Inactive Employees</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="stat-card stat-warning">
            <div className="stat-icon">
              <FaUserClock />
            </div>
            <div className="stat-content">
              <h3>{stats?.onLeaveEmployees || 0}</h3>
              <p>On Leave</p>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="stat-card stat-success">
            <div className="stat-icon">
              <FaMoneyBillWave />
            </div>
            <div className="stat-content">
              <h3>${stats?.monthlySalaryExpense?.toLocaleString() || 0}</h3>
              <p>Monthly Salary Expense</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        <div className="col-md-6">
          <div className="chart-card">
            <h4>Employee Status Distribution</h4>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={employeeData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, value }) => `${name}: ${value}`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {employeeData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="col-md-6">
          <div className="chart-card">
            <h4>Attendance Overview</h4>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={attendanceData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="value" fill="#667eea" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;

import React, { useState, useEffect } from 'react';
import { attendanceAPI, employeeAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import { FaCalendarAlt, FaCheckCircle, FaTimesCircle } from 'react-icons/fa';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../styles/Common.css';

const Attendance = () => {
  const { user, hasRole } = useAuth();
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState('');
  const [attendanceRecords, setAttendanceRecords] = useState([]);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [todayPresent, setTodayPresent] = useState(0);

  useEffect(() => {
    // Initialize defaults based on role
    if (!hasRole(['ADMIN', 'HR'])) {
      // EMPLOYEE role: lock to own employeeId
      setSelectedEmployee(user?.employeeId || '');
    } else {
      fetchEmployees();
    }
    fetchTodayPresent();
  }, []);

  useEffect(() => {
    if (selectedEmployee) {
      fetchAttendanceByEmployee();
    }
  }, [selectedEmployee]);

  const fetchEmployees = async () => {
    try {
      const response = await employeeAPI.getAll();
      const list = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setEmployees(list.filter(emp => emp.status === 'ACTIVE'));
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to fetch employees');
      setEmployees([]);
    }
  };

  const fetchTodayPresent = async () => {
    try {
      const response = await attendanceAPI.getTodayPresent();
      setTodayPresent(response.data);
    } catch (error) {
      console.error('Failed to fetch today present count');
    }
  };

  const fetchAttendanceByEmployee = async () => {
    try {
      const response = await attendanceAPI.getByEmployee(selectedEmployee);
      setAttendanceRecords(response.data);
    } catch (error) {
      toast.error('Failed to fetch attendance records');
    }
  };

  const handleCheckIn = async () => {
    if (!selectedEmployee) {
      toast.warning('Please select an employee');
      return;
    }

    try {
      await attendanceAPI.checkIn(selectedEmployee);
      toast.success('Check-in recorded successfully');
      fetchAttendanceByEmployee();
      fetchTodayPresent();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Check-in failed');
    }
  };

  const handleCheckOut = async () => {
    if (!selectedEmployee) {
      toast.warning('Please select an employee');
      return;
    }

    try {
      await attendanceAPI.checkOut(selectedEmployee);
      toast.success('Check-out recorded successfully');
      fetchAttendanceByEmployee();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Check-out failed');
    }
  };

  const getAttendanceForDate = (date) => {
    return attendanceRecords.find(record => 
      new Date(record.date).toDateString() === date.toDateString()
    );
  };

  const tileContent = ({ date, view }) => {
    if (view === 'month' && selectedEmployee) {
      const attendance = getAttendanceForDate(date);
      if (attendance) {
        return (
          <div className={`calendar-marker ${attendance.status.toLowerCase()}`}>
            {attendance.status === 'PRESENT' ? '✓' : '✗'}
          </div>
        );
      }
    }
    return null;
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Attendance Management</h2>
        <div className="stat-badge">
          <FaCheckCircle /> Today Present: {todayPresent}
        </div>
      </div>

      <div className="attendance-section">
        <div className="attendance-controls">
          {hasRole(['ADMIN', 'HR']) ? (
            <div className="form-group">
              <label>Select Employee</label>
              <select 
                value={selectedEmployee} 
                onChange={(e) => setSelectedEmployee(e.target.value)}
                className="form-control"
              >
                <option value="">Choose an employee...</option>
                {employees.map(emp => (
                  <option key={emp.id} value={emp.id}>
                    {emp.employeeCode} - {emp.firstName} {emp.lastName}
                  </option>
                ))}
              </select>
            </div>
          ) : (
            <div className="form-group">
              <label>Employee</label>
              <input type="text" className="form-control" value={user?.fullName} disabled />
            </div>
          )}

          <div className="button-group">
            <button 
              className="btn btn-success" 
              onClick={handleCheckIn}
              disabled={!selectedEmployee}
            >
              <FaCheckCircle /> Check In
            </button>
            <button 
              className="btn btn-danger" 
              onClick={handleCheckOut}
              disabled={!selectedEmployee}
            >
              <FaTimesCircle /> Check Out
            </button>
          </div>
        </div>

        {selectedEmployee && (
          <div className="calendar-section">
            <h4><FaCalendarAlt /> Attendance Calendar</h4>
            <Calendar
              onChange={setSelectedDate}
              value={selectedDate}
              tileContent={tileContent}
            />
          </div>
        )}

        {selectedEmployee && attendanceRecords.length > 0 && (
          <div className="attendance-table">
            <h4>Attendance History</h4>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Check In</th>
                  <th>Check Out</th>
                  <th>Working Hours</th>
                  <th>Status</th>
                  <th>Remarks</th>
                </tr>
              </thead>
              <tbody>
                {attendanceRecords.slice(0, 10).map(record => (
                  <tr key={record.id}>
                    <td>{new Date(record.date).toLocaleDateString()}</td>
                    <td>{record.checkInTime ? new Date(record.checkInTime).toLocaleTimeString() : 'N/A'}</td>
                    <td>{record.checkOutTime ? new Date(record.checkOutTime).toLocaleTimeString() : 'N/A'}</td>
                    <td>{record.workingHours ? `${record.workingHours.toFixed(2)} hrs` : 'N/A'}</td>
                    <td><span className={`badge badge-${record.status.toLowerCase()}`}>{record.status}</span></td>
                    <td>{record.remarks || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Attendance;

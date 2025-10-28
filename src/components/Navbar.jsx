import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { FaSun, FaMoon, FaSignOutAlt, FaUser, FaBars, FaTimes } from 'react-icons/fa';
import '../styles/Navbar.css';

const Navbar = () => {
  const { user, logout } = useAuth();
  const { darkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
    setIsMenuOpen(false);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  return (
    <nav className="navbar-custom">
      <div className="navbar-brand">
        <Link to="/dashboard" onClick={closeMenu}>EMS</Link>
      </div>

      <button className="hamburger" onClick={() => setIsMenuOpen(!isMenuOpen)}>
        {isMenuOpen ? <FaTimes /> : <FaBars />}
      </button>

      <div className={`navbar-menu ${isMenuOpen ? 'active' : ''}`}>
        <Link to="/dashboard" className="nav-link" onClick={closeMenu}>Dashboard</Link>
        {(user?.role === 'ADMIN' || user?.role === 'HR') && (
          <Link to="/employees" className="nav-link" onClick={closeMenu}>Employees</Link>
        )}
        <Link to="/attendance" className="nav-link" onClick={closeMenu}>Attendance</Link>
        <Link to="/leaves" className="nav-link" onClick={closeMenu}>Leaves</Link>
        {(user?.role === 'ADMIN' || user?.role === 'HR') && (
          <>
            <Link to="/salaries" className="nav-link" onClick={closeMenu}>Salaries</Link>
            <Link to="/departments" className="nav-link" onClick={closeMenu}>Departments</Link>
          </>
        )}
        {user?.role === 'ADMIN' && (
          <Link to="/audit-logs" className="nav-link" onClick={closeMenu}>Audit Logs</Link>
        )}
      </div>

      <div className={`navbar-actions ${isMenuOpen ? 'active' : ''}`}>
        <button onClick={toggleTheme} className="theme-toggle" title="Toggle Theme">
          {darkMode ? <FaSun /> : <FaMoon />}
        </button>
        
        <div className="user-info">
          <FaUser />
          <span className="user-name">{user?.fullName}</span>
          <span className="user-role">{user?.role}</span>
        </div>

        <button onClick={handleLogout} className="logout-btn" title="Logout">
          <FaSignOutAlt /> <span>Logout</span>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;

import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaEnvelope, FaKey } from 'react-icons/fa';
import '../styles/Auth.css';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);
  const { forgotPassword } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const success = await forgotPassword(email);
    setLoading(false);
    
    if (success) {
      setSent(true);
    }
  };

  if (sent) {
    return (
      <div className="auth-container">
        <div className="auth-card text-center">
          <FaKey className="auth-icon text-success" size={60} />
          <h2>Check Your Email</h2>
          <p>We've sent a password reset token to your email address.</p>
          <Link to="/login" className="btn btn-primary">Back to Login</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <FaKey className="auth-icon" />
          <h2>Forgot Password</h2>
          <p>Enter your email to receive a reset token</p>
        </div>
        
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label><FaEnvelope /> Email Address</label>
            <input
              type="email"
              className="form-control"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          
          <button type="submit" className="btn btn-primary w-100" disabled={loading}>
            {loading ? 'Sending...' : 'Send Reset Token'}
          </button>
        </form>
        
        <div className="auth-footer">
          <Link to="/login">Back to Login</Link>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;

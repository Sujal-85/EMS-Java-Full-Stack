import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle unauthorized responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  signup: () => Promise.reject({ response: { data: { message: 'Signup is disabled. Use predefined admin/hr accounts.' } } }),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (data) => api.post('/auth/reset-password', data),
};

// Employee APIs
export const employeeAPI = {
  getAll: () => api.get('/employees'),
  getById: (id) => api.get(`/employees/${id}`),
  create: (formData) => api.post('/employees', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  update: (id, formData) => api.put(`/employees/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  delete: (id) => api.delete(`/employees/${id}`),
  search: (query) => api.get(`/employees/search?query=${query}`),
  getByDepartment: (deptId) => api.get(`/employees/department/${deptId}`),
  getByStatus: (status) => api.get(`/employees/status/${status}`),
};

// Department APIs
export const departmentAPI = {
  getAll: () => api.get('/departments'),
  getById: (id) => api.get(`/departments/${id}`),
  create: (data) => api.post('/departments', data),
  update: (id, data) => api.put(`/departments/${id}`, data),
  delete: (id) => api.delete(`/departments/${id}`),
};

// Leave APIs
export const leaveAPI = {
  getAll: () => api.get('/leaves'),
  getById: (id) => api.get(`/leaves/${id}`),
  apply: (data) => api.post('/leaves', data),
  approve: (id, comments) => api.put(`/leaves/${id}/approve`, null, { params: { comments } }),
  reject: (id, comments) => api.put(`/leaves/${id}/reject`, null, { params: { comments } }),
  getByEmployee: (empId) => api.get(`/leaves/employee/${empId}`),
  getMy: () => api.get('/leaves/my'),
  getPending: () => api.get('/leaves/pending'),
};

// Attendance APIs
export const attendanceAPI = {
  checkIn: (empId) => api.post(`/attendance/checkin/${empId}`),
  checkOut: (empId) => api.post(`/attendance/checkout/${empId}`),
  getByEmployee: (empId) => api.get(`/attendance/employee/${empId}`),
  getByDateRange: (startDate, endDate) => 
    api.get(`/attendance/range?startDate=${startDate}&endDate=${endDate}`),
  getTodayPresent: () => api.get('/attendance/today-present'),
};

// Salary APIs
export const salaryAPI = {
  create: (data) => api.post('/salaries', data),
  update: (id, data) => api.put(`/salaries/${id}`, data),
  getByEmployee: (empId) => api.get(`/salaries/employee/${empId}`),
  getByMonth: (month, year) => api.get(`/salaries/month/${month}/year/${year}`),
  downloadPayslip: (id) => api.get(`/salaries/${id}/payslip`, { responseType: 'blob' }),
  getMonthlyExpense: (month, year) => api.get(`/salaries/expense/month/${month}/year/${year}`),
};

// Dashboard APIs
export const dashboardAPI = {
  getStats: () => api.get('/dashboard/stats'),
};

// Audit Log APIs
export const auditAPI = {
  getAll: () => api.get('/audit-logs'),
  getByUser: (email) => api.get(`/audit-logs/user/${email}`),
  getByEntity: (entityName, entityId) => api.get(`/audit-logs/entity/${entityName}/${entityId}`),
};

export default api;

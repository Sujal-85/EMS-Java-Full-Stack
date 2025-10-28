# Production-Grade Employee Management System (EMS)

A complete, enterprise-ready Employee Management System built with **Spring Boot 3.3.2** (Java 17) backend and **React + Vite** frontend.

## 🚀 Features Implemented

### Authentication & Authorization ✅
- JWT-based authentication with refresh tokens
- Role-based access control (ADMIN, HR, EMPLOYEE)
- Login / Signup / Logout
- Forgot password with email OTP
- Secure password encryption (BCrypt)

### Employee Management ✅
- Complete CRUD operations
- Photo upload with multipart form data
- Department and designation assignment
- Employee search and filtering
- Status management (Active, Inactive, On Leave, Terminated)

### Attendance Management ✅
- Daily check-in/check-out with timestamps
- Calendar-based attendance view
- Working hours calculation
- Attendance history tracking

### Leave Management ✅
- Apply for various leave types (Sick, Casual, Annual, Maternity, Paternity, Unpaid)
- Approve/Reject leave requests
- Leave balance tracking
- Email notifications on status changes

### Salary Management ✅
- Salary structure (Basic + Allowances - Deductions)
- Auto-calculate net salary
- Generate and download payslips (PDF)
- Monthly salary reports

### Admin Dashboard ✅
- Real-time statistics
- Interactive charts using Recharts
- Employee status distribution (Pie Chart)
- Attendance overview (Bar Chart)
- Monthly salary expenses

### Additional Features ✅
- Department management
- Audit logging (track all CRUD operations)
- Email notifications (Welcome, Password Reset, Leave Status, Employee Creation)
- Dark mode / Light mode toggle
- Responsive design with Bootstrap
- Search, filters, and sorting
- Toast notifications

## 📁 Project Structure

```
Employee-Management-System/
├── Backend (Spring Boot)
│   ├── entity/          # User, Employee, Department, Leave, Attendance, Salary, AuditLog
│   ├── repository/      # JPA Repositories
│   ├── service/         # Business logic
│   ├── controller/      # REST API endpoints
│   ├── security/        # JWT, Spring Security
│   ├── dto/             # Data Transfer Objects
│   └── config/          # Security & CORS configuration
│
└── Frontend (React + Vite)
    ├── components/      # Navbar, ProtectedRoute
    ├── pages/           # Login, Dashboard, Employees, Attendance, Leaves, etc.
    ├── context/         # AuthContext, ThemeContext
    ├── services/        # API calls with Axios
    └── styles/          # CSS files
```

## 🛠️ Tech Stack

### Backend
- Spring Boot 3.3.2
- Spring Security + JWT
- Spring Data JPA
- MySQL 8
- Maven
- Lombok
- iText PDF
- JavaMail

### Frontend
- React 18
- Vite
- React Router DOM
- Axios
- Recharts (Charts)
- React Icons
- React Calendar
- React Toastify
- Bootstrap 5
- JWT Decode

## ⚙️ Setup Instructions

### Prerequisites
- Java 17 or later
- Node.js 18+ and npm
- MySQL 8+
- Maven (optional, if not using mvnw)

### Database Setup

1. Start MySQL and create database:
```sql
CREATE DATABASE employee_management_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

2. Update credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Email Configuration (Optional)

Update SMTP settings in `application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

For Gmail, enable 2FA and generate an App Password.

### Backend Setup

```bash
# Navigate to project root
cd Employee-Management-System-Full-Stack-Project

# Install dependencies and run
mvn clean install
mvn spring-boot:run
```

Backend will start on **http://localhost:8081**

### Frontend Setup

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend will start on **http://localhost:5173**

## 📝 API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/signup` - Register
- `POST /api/auth/forgot-password` - Forgot password
- `POST /api/auth/reset-password` - Reset password

### Employees
- `GET /api/employees` - Get all employees
- `POST /api/employees` - Create employee (ADMIN/HR)
- `PUT /api/employees/{id}` - Update employee (ADMIN/HR)
- `DELETE /api/employees/{id}` - Delete employee (ADMIN)
- `GET /api/employees/search?query={term}` - Search employees

### Attendance
- `POST /api/attendance/checkin/{employeeId}` - Check in
- `POST /api/attendance/checkout/{employeeId}` - Check out
- `GET /api/attendance/employee/{employeeId}` - Get attendance by employee

### Leaves
- `POST /api/leaves` - Apply leave
- `PUT /api/leaves/{id}/approve` - Approve leave (ADMIN/HR)
- `PUT /api/leaves/{id}/reject` - Reject leave (ADMIN/HR)
- `GET /api/leaves/pending` - Get pending leaves (ADMIN/HR)

### Salaries
- `POST /api/salaries` - Create salary (ADMIN/HR)
- `GET /api/salaries/{id}/payslip` - Download payslip PDF
- `GET /api/salaries/month/{month}/year/{year}` - Get monthly salaries

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics (ADMIN/HR)

### Departments
- `GET /api/departments` - Get all departments
- `POST /api/departments` - Create department (ADMIN)

### Audit Logs
- `GET /api/audit-logs` - Get all audit logs (ADMIN)

## 🎨 Features Showcase

### Dashboard
- Total employees, active/inactive counts
- Today's attendance
- Pending leaves
- Monthly salary expenses
- Interactive pie and bar charts

### Employee Management
- Photo upload
- Comprehensive employee details
- Search and filter by department/status
- Export capabilities

### Attendance Calendar
- Visual calendar view
- Color-coded attendance status
- Daily check-in/check-out tracking

### Leave Management
- Multiple leave types
- Workflow: Apply → Pending → Approved/Rejected
- Email notifications

### Salary & Payslips
- Professional PDF payslips
- Detailed salary breakdown
- Monthly reports

## 🔐 Default Users

After first signup, you can create users with different roles:
- **ADMIN**: Full access to all features
- **HR**: Manage employees, leaves, salaries
- **EMPLOYEE**: View own data, apply leaves, mark attendance

## 🌓 Dark Mode

Toggle between light and dark themes using the sun/moon icon in the navbar.

## 📧 Email Notifications

The system sends emails for:
- Welcome message on signup
- Password reset tokens
- Leave approval/rejection
- New employee creation

## 🔒 Security Features

- JWT token-based authentication
- Password hashing with BCrypt
- CORS configuration
- Role-based access control
- Audit logging for all operations
- Token expiration handling

## 📊 Audit Logs

Track all system activities:
- User actions (CREATE, UPDATE, DELETE, LOGIN, LOGOUT)
- Timestamp and IP address
- Entity details
- Performed by user tracking

## 🚨 Error Handling

- Global exception handling
- Toast notifications for user feedback
- Proper HTTP status codes
- Validation messages

## 🎯 Production Considerations

1. **Environment Variables**: Move sensitive data to environment variables
2. **Database**: Use connection pooling
3. **File Storage**: Consider cloud storage (AWS S3) for production
4. **Email**: Configure production SMTP server
5. **Logging**: Add proper logging framework (Logback/SLF4J)
6. **Monitoring**: Add health checks and metrics
7. **Docker**: Containerize the application

## 📦 Build for Production

### Backend
```bash
mvn clean package
java -jar target/springboot-backend-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
npm run build
# Serve the 'dist' folder with nginx or any static server
```

## 🤝 Contributing

This is a production-ready template. Feel free to customize according to your needs.

## 📄 License

MIT License

## 👨‍💻 Developer Notes

- Backend runs on port **8081** (to avoid conflict with Jenkins)
- Frontend runs on port **5173** (Vite default)
- Database auto-creates tables via JPA (ddl-auto=update)
- JWT tokens expire in 24 hours
- File uploads stored in `uploads/employee-photos/`

---

**Happy Coding! 🚀**

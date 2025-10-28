import api from './api';

const EMPLOYEE_API_BASE_URL = '/employees';

class EmployeeService {

    getEmployees(){
        return api.get(EMPLOYEE_API_BASE_URL);
    }

    createEmployee(employee){
        return api.post(EMPLOYEE_API_BASE_URL, employee);
    }

    getEmployeeById(employeeId){
        return api.get(`${EMPLOYEE_API_BASE_URL}/${employeeId}`);
    }

    updateEmployee(employee, employeeId){
        return api.put(`${EMPLOYEE_API_BASE_URL}/${employeeId}`, employee);
    }

    deleteEmployee(employeeId){
        return api.delete(`${EMPLOYEE_API_BASE_URL}/${employeeId}`);
    }
}

export default new EmployeeService()
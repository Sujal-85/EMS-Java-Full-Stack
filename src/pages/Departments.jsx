import React, { useState, useEffect } from 'react';
import { departmentAPI } from '../services/api';
import { toast } from 'react-toastify';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';

const Departments = () => {
  const [departments, setDepartments] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingDept, setEditingDept] = useState(null);
  const [formData, setFormData] = useState({ name: '', description: '' });

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const response = await departmentAPI.getAll();
      const list = Array.isArray(response.data)
        ? response.data
        : (Array.isArray(response.data?.content) ? response.data.content : []);
      setDepartments(list);
    } catch (error) {
      toast.error('Failed to fetch departments');
      setDepartments([]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingDept) {
        await departmentAPI.update(editingDept.id, formData);
        toast.success('Department updated');
      } else {
        await departmentAPI.create(formData);
        toast.success('Department created');
      }
      setShowModal(false);
      fetchDepartments();
    } catch (error) {
      toast.error('Operation failed');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this department?')) return;
    try {
      await departmentAPI.delete(id);
      toast.success('Department deleted');
      fetchDepartments();
    } catch (error) {
      toast.error('Failed to delete');
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Departments</h2>
        <button className="btn btn-primary" onClick={() => { setEditingDept(null); setFormData({ name: '', description: '' }); setShowModal(true); }}>
          <FaPlus /> Add Department
        </button>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {(Array.isArray(departments) ? departments : []).map(dept => (
              <tr key={dept.id}>
                <td>{dept.name}</td>
                <td>{dept.description || 'N/A'}</td>
                <td className="actions">
                  <button onClick={() => { setEditingDept(dept); setFormData(dept); setShowModal(true); }}><FaEdit /></button>
                  <button onClick={() => handleDelete(dept.id)} className="btn-danger"><FaTrash /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{editingDept ? 'Edit' : 'Add'} Department</h3>
              <button onClick={() => setShowModal(false)}>&times;</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Name *</label>
                <input type="text" required value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea rows="3" value={formData.description} onChange={(e) => setFormData({...formData, description: e.target.value})} />
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Departments;

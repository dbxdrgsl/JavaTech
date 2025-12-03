// Global state
let authToken = null;
let currentUser = null;
let currentRole = null;
let allStudents = [];
let allCourses = [];
let allPacks = [];
let allPreferences = [];
let allGrades = [];

// API Base URL
const API_BASE = '/api';

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    console.log('PrefSchedule Frontend initialized');
    
    // Check if user is already logged in
    const savedToken = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');
    const savedRole = localStorage.getItem('currentRole');
    
    if (savedToken && savedUser) {
        authToken = savedToken;
        currentUser = savedUser;
        currentRole = savedRole;
        showLoggedInState();
        showSection('dashboard');
        loadDashboard();
    } else {
        showSection('login');
    }
    
    // Setup form handlers
    setupFormHandlers();
});

// Form handlers setup
function setupFormHandlers() {
    // Login form
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        await handleLogin();
    });
    
    // Register form
    document.getElementById('registerForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        await handleRegister();
    });
    
    // Student form
    document.getElementById('studentForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        await handleAddStudent();
    });
    
    // Preference form
    document.getElementById('preferenceForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        await handleAddPreference();
    });
}

// Navigation
function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Show selected section
    const section = document.getElementById(sectionId);
    if (section) {
        section.classList.add('active');
        
        // Load data for specific sections
        if (authToken) {
            switch(sectionId) {
                case 'dashboard':
                    loadDashboard();
                    break;
                case 'students':
                    loadStudents();
                    break;
                case 'courses':
                    loadCourses();
                    break;
                case 'packs':
                    loadPacks();
                    break;
                case 'preferences':
                    loadPreferences();
                    loadStudentsForDropdown();
                    loadCoursesForDropdown();
                    break;
                case 'grades':
                    loadGrades();
                    break;
                case 'actuator':
                    loadActuatorHealth();
                    break;
            }
        }
    }
}

function showLoggedInState() {
    const nav = document.getElementById('mainNav');
    nav.innerHTML = `
        <button class="nav-btn" onclick="showSection('dashboard')">Dashboard</button>
        <button class="nav-btn" onclick="showSection('students')">Students</button>
        <button class="nav-btn" onclick="showSection('courses')">Courses</button>
        <button class="nav-btn" onclick="showSection('packs')">Packs</button>
        <button class="nav-btn" onclick="showSection('preferences')">Preferences</button>
        <button class="nav-btn" onclick="showSection('grades')">Grades</button>
        <button class="nav-btn" onclick="showSection('microservices')">Services</button>
        <button class="nav-btn" onclick="showSection('actuator')">Monitor</button>
        <button class="nav-btn logout" onclick="handleLogout()">Logout</button>
    `;
}

// Authentication
async function handleLogin() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data.username;
            currentRole = data.role;
            
            // Save to localStorage
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', currentUser);
            localStorage.setItem('currentRole', currentRole);
            
            showMessage('loginMessage', 'Login successful!', 'success');
            showLoggedInState();
            
            setTimeout(() => {
                showSection('dashboard');
                loadDashboard();
            }, 500);
        } else {
            const error = await response.json();
            showMessage('loginMessage', error.message || 'Login failed', 'error');
        }
    } catch (error) {
        showMessage('loginMessage', 'Error: ' + error.message, 'error');
    }
}

async function handleRegister() {
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const role = document.getElementById('regRole').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, name, email, role })
        });
        
        if (response.ok) {
            showMessage('registerMessage', 'Registration successful! Please login.', 'success');
            setTimeout(() => showSection('login'), 2000);
        } else {
            const error = await response.json();
            showMessage('registerMessage', error.error || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('registerMessage', 'Error: ' + error.message, 'error');
    }
}

function handleLogout() {
    authToken = null;
    currentUser = null;
    currentRole = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('currentRole');
    
    document.getElementById('mainNav').innerHTML = `
        <button class="nav-btn" onclick="showSection('login')">Login</button>
    `;
    
    showSection('login');
}

// Dashboard
async function loadDashboard() {
    const userInfo = document.getElementById('userInfo');
    userInfo.innerHTML = `
        <h3>Welcome, ${currentUser}! 👋</h3>
        <p><strong>Role:</strong> <span class="badge badge-primary">${currentRole}</span></p>
    `;
    
    // Load counts
    try {
        const studentCount = await fetchWithAuth(`${API_BASE}/students/count`);
        document.getElementById('studentCount').textContent = await studentCount.text();
        
        const preferenceCount = await fetchWithAuth(`${API_BASE}/preferences/count`);
        document.getElementById('preferenceCount').textContent = await preferenceCount.text();
        
        // Load courses and packs to count them
        const coursesRes = await fetch(`${API_BASE}/courses`);
        if (coursesRes.ok) {
            const courses = await coursesRes.json();
            document.getElementById('courseCount').textContent = courses.length || 0;
        }
        
        const packsRes = await fetch(`${API_BASE}/packs`);
        if (packsRes.ok) {
            const packs = await packsRes.json();
            document.getElementById('packCount').textContent = packs.length || 0;
        }
        
        const gradesRes = await fetchWithAuth(`${API_BASE}/grades`);
        if (gradesRes.ok) {
            const grades = await gradesRes.json();
            document.getElementById('gradeCount').textContent = grades.length || 0;
        }
        
        // Check service status
        const healthRes = await fetchWithAuth(`${API_BASE}/microservices/quickgrade/health`);
        if (healthRes.ok) {
            const health = await healthRes.json();
            document.getElementById('serviceStatus').textContent = health.status || 'Unknown';
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

// Students
async function loadStudents() {
    try {
        const response = await fetch(`${API_BASE}/students`);
        if (response.ok) {
            allStudents = await response.json();
            displayStudents(allStudents);
        } else {
            showMessage('studentsMessage', 'Failed to load students', 'error');
        }
    } catch (error) {
        showMessage('studentsMessage', 'Error: ' + error.message, 'error');
    }
}

function displayStudents(students) {
    const container = document.getElementById('studentsList');
    
    if (students.length === 0) {
        container.innerHTML = '<p class="text-center">No students found</p>';
        return;
    }
    
    container.innerHTML = students.map(student => `
        <div class="data-item">
            <div class="data-item-header">
                <div class="data-item-title">${student.name}</div>
                <div class="data-item-actions">
                    ${canModifyStudents() ? `
                        <button class="btn btn-danger btn-small" onclick="deleteStudent(${student.id})">Delete</button>
                    ` : ''}
                </div>
            </div>
            <div class="data-item-content">
                <div class="data-item-field"><strong>Code:</strong> ${student.code}</div>
                <div class="data-item-field"><strong>Email:</strong> ${student.email}</div>
                <div class="data-item-field"><strong>Year:</strong> ${student.year}</div>
                <div class="data-item-field"><strong>ID:</strong> ${student.id}</div>
            </div>
        </div>
    `).join('');
}

function filterStudents() {
    const searchTerm = document.getElementById('studentSearchInput').value.toLowerCase();
    const filtered = allStudents.filter(student => 
        student.code.toLowerCase().includes(searchTerm) ||
        student.name.toLowerCase().includes(searchTerm) ||
        student.email.toLowerCase().includes(searchTerm)
    );
    displayStudents(filtered);
}

function showAddStudentForm() {
    document.getElementById('addStudentForm').style.display = 'block';
}

function cancelAddStudent() {
    document.getElementById('addStudentForm').style.display = 'none';
    document.getElementById('studentForm').reset();
}

async function handleAddStudent() {
    const student = {
        code: document.getElementById('studentCode').value,
        name: document.getElementById('studentName').value,
        email: document.getElementById('studentEmail').value,
        year: parseInt(document.getElementById('studentYear').value)
    };
    
    try {
        const response = await fetchWithAuth(`${API_BASE}/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(student)
        });
        
        if (response.ok) {
            showMessage('studentsMessage', 'Student added successfully!', 'success');
            cancelAddStudent();
            loadStudents();
        } else {
            const error = await response.json();
            showMessage('studentsMessage', error.message || 'Failed to add student', 'error');
        }
    } catch (error) {
        showMessage('studentsMessage', 'Error: ' + error.message, 'error');
    }
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    
    try {
        const response = await fetchWithAuth(`${API_BASE}/students/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok || response.status === 204) {
            showMessage('studentsMessage', 'Student deleted successfully!', 'success');
            loadStudents();
        } else {
            showMessage('studentsMessage', 'Failed to delete student', 'error');
        }
    } catch (error) {
        showMessage('studentsMessage', 'Error: ' + error.message, 'error');
    }
}

// Courses
async function loadCourses() {
    try {
        const response = await fetch(`${API_BASE}/courses`);
        if (response.ok) {
            allCourses = await response.json();
            displayCourses(allCourses);
        } else {
            showMessage('coursesMessage', 'Failed to load courses', 'error');
        }
    } catch (error) {
        showMessage('coursesMessage', 'Error: ' + error.message, 'error');
    }
}

function displayCourses(courses) {
    const container = document.getElementById('coursesList');
    
    if (courses.length === 0) {
        container.innerHTML = '<p class="text-center">No courses found</p>';
        return;
    }
    
    container.innerHTML = courses.map(course => `
        <div class="data-item">
            <div class="data-item-header">
                <div class="data-item-title">${course.name}</div>
                <span class="badge ${course.type === 'COMPULSORY' ? 'badge-primary' : 'badge-warning'}">
                    ${course.type}
                </span>
            </div>
            <div class="data-item-content">
                <div class="data-item-field"><strong>Code:</strong> ${course.code}</div>
                <div class="data-item-field"><strong>Abbr:</strong> ${course.abbr || 'N/A'}</div>
                <div class="data-item-field"><strong>Groups:</strong> ${course.groupCount || 'N/A'}</div>
                <div class="data-item-field"><strong>ID:</strong> ${course.id}</div>
            </div>
            ${course.description ? `<div class="mt-1"><small>${course.description}</small></div>` : ''}
        </div>
    `).join('');
}

function filterCourses() {
    const typeFilter = document.getElementById('courseTypeFilter').value;
    const filtered = typeFilter 
        ? allCourses.filter(c => c.type === typeFilter)
        : allCourses;
    displayCourses(filtered);
}

// Packs
async function loadPacks() {
    try {
        const response = await fetch(`${API_BASE}/packs`);
        if (response.ok) {
            allPacks = await response.json();
            displayPacks(allPacks);
        } else {
            showMessage('packsMessage', 'Failed to load packs', 'error');
        }
    } catch (error) {
        showMessage('packsMessage', 'Error: ' + error.message, 'error');
    }
}

function displayPacks(packs) {
    const container = document.getElementById('packsList');
    
    if (packs.length === 0) {
        container.innerHTML = '<p class="text-center">No packs found</p>';
        return;
    }
    
    container.innerHTML = packs.map(pack => `
        <div class="data-item">
            <div class="data-item-header">
                <div class="data-item-title">${pack.name}</div>
                <span class="badge badge-primary">Year ${pack.year}</span>
            </div>
            <div class="data-item-content">
                <div class="data-item-field"><strong>Semester:</strong> ${pack.semester}</div>
                <div class="data-item-field"><strong>Year:</strong> ${pack.year}</div>
                <div class="data-item-field"><strong>ID:</strong> ${pack.id}</div>
            </div>
        </div>
    `).join('');
}

function filterPacks() {
    const yearFilter = document.getElementById('packYearFilter').value;
    const filtered = yearFilter 
        ? allPacks.filter(p => p.year == yearFilter)
        : allPacks;
    displayPacks(filtered);
}

// Preferences
async function loadPreferences() {
    try {
        const response = await fetchWithAuth(`${API_BASE}/preferences`);
        if (response.ok) {
            allPreferences = await response.json();
            displayPreferences(allPreferences);
            populateStudentFilter(allPreferences);
        } else {
            showMessage('preferencesMessage', 'Failed to load preferences', 'error');
        }
    } catch (error) {
        showMessage('preferencesMessage', 'Error: ' + error.message, 'error');
    }
}

function displayPreferences(preferences) {
    const container = document.getElementById('preferencesList');
    
    if (preferences.length === 0) {
        container.innerHTML = '<p class="text-center">No preferences found</p>';
        return;
    }
    
    container.innerHTML = preferences.map(pref => `
        <div class="data-item">
            <div class="data-item-header">
                <div class="data-item-title">${pref.studentName} → ${pref.courseName}</div>
                <div class="data-item-actions">
                    <span class="badge badge-primary">Rank ${pref.rankOrder}</span>
                    ${canModifyPreferences() ? `
                        <button class="btn btn-danger btn-small" onclick="deletePreference(${pref.id})">Delete</button>
                    ` : ''}
                </div>
            </div>
            <div class="data-item-content">
                <div class="data-item-field"><strong>Student:</strong> ${pref.studentCode}</div>
                <div class="data-item-field"><strong>Course:</strong> ${pref.courseCode}</div>
                <div class="data-item-field"><strong>Pack:</strong> ${pref.packName || 'N/A'}</div>
                <div class="data-item-field"><strong>Rank:</strong> ${pref.rankOrder}</div>
            </div>
        </div>
    `).join('');
}

function filterPreferences() {
    const studentFilter = document.getElementById('preferenceStudentFilter').value;
    const filtered = studentFilter 
        ? allPreferences.filter(p => p.studentId == studentFilter)
        : allPreferences;
    displayPreferences(filtered);
}

function populateStudentFilter(preferences) {
    const select = document.getElementById('preferenceStudentFilter');
    const uniqueStudents = [...new Set(preferences.map(p => JSON.stringify({id: p.studentId, name: p.studentName})))];
    
    select.innerHTML = '<option value="">All Students</option>' + 
        uniqueStudents.map(s => {
            const student = JSON.parse(s);
            return `<option value="${student.id}">${student.name}</option>`;
        }).join('');
}

function showAddPreferenceForm() {
    document.getElementById('addPreferenceForm').style.display = 'block';
}

function cancelAddPreference() {
    document.getElementById('addPreferenceForm').style.display = 'none';
    document.getElementById('preferenceForm').reset();
}

async function loadStudentsForDropdown() {
    try {
        const response = await fetch(`${API_BASE}/students`);
        if (response.ok) {
            const students = await response.json();
            const select = document.getElementById('prefStudentId');
            select.innerHTML = '<option value="">Select Student</option>' +
                students.map(s => `<option value="${s.id}">${s.name} (${s.code})</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading students:', error);
    }
}

async function loadCoursesForDropdown() {
    try {
        const response = await fetch(`${API_BASE}/courses`);
        if (response.ok) {
            const courses = await response.json();
            const select = document.getElementById('prefCourseId');
            select.innerHTML = '<option value="">Select Course</option>' +
                courses.map(c => `<option value="${c.id}">${c.name} (${c.code})</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading courses:', error);
    }
}

async function handleAddPreference() {
    const preference = {
        studentId: parseInt(document.getElementById('prefStudentId').value),
        courseId: parseInt(document.getElementById('prefCourseId').value),
        rankOrder: parseInt(document.getElementById('prefRankOrder').value)
    };
    
    try {
        const response = await fetchWithAuth(`${API_BASE}/preferences`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(preference)
        });
        
        if (response.ok) {
            showMessage('preferencesMessage', 'Preference added successfully!', 'success');
            cancelAddPreference();
            loadPreferences();
        } else {
            const error = await response.json();
            showMessage('preferencesMessage', error.message || 'Failed to add preference', 'error');
        }
    } catch (error) {
        showMessage('preferencesMessage', 'Error: ' + error.message, 'error');
    }
}

async function deletePreference(id) {
    if (!confirm('Are you sure you want to delete this preference?')) return;
    
    try {
        const response = await fetchWithAuth(`${API_BASE}/preferences/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok || response.status === 204) {
            showMessage('preferencesMessage', 'Preference deleted successfully!', 'success');
            loadPreferences();
        } else {
            showMessage('preferencesMessage', 'Failed to delete preference', 'error');
        }
    } catch (error) {
        showMessage('preferencesMessage', 'Error: ' + error.message, 'error');
    }
}

// Grades
async function loadGrades() {
    try {
        const response = await fetchWithAuth(`${API_BASE}/grades`);
        if (response.ok) {
            allGrades = await response.json();
            displayGrades(allGrades);
            populateGradeStudentFilter(allGrades);
        } else {
            showMessage('gradesMessage', 'Failed to load grades', 'error');
        }
    } catch (error) {
        showMessage('gradesMessage', 'Error: ' + error.message, 'error');
    }
}

function displayGrades(grades) {
    const container = document.getElementById('gradesList');
    
    if (grades.length === 0) {
        container.innerHTML = '<p class="text-center">No grades found. Grades are received from QuickGrade messaging service.</p>';
        return;
    }
    
    container.innerHTML = grades.map(grade => {
        const gradeClass = grade.grade >= 5 ? 'badge-success' : 'badge-danger';
        return `
            <div class="data-item">
                <div class="data-item-header">
                    <div class="data-item-title">${grade.studentCode} - ${grade.courseCode}</div>
                    <span class="badge ${gradeClass}">Grade: ${grade.grade}</span>
                </div>
                <div class="data-item-content">
                    <div class="data-item-field"><strong>Student:</strong> ${grade.studentCode}</div>
                    <div class="data-item-field"><strong>Course:</strong> ${grade.courseCode}</div>
                    <div class="data-item-field"><strong>Grade:</strong> ${grade.grade}/10</div>
                    <div class="data-item-field"><strong>Received:</strong> ${new Date(grade.receivedAt).toLocaleString()}</div>
                </div>
            </div>
        `;
    }).join('');
}

function filterGrades() {
    const studentFilter = document.getElementById('gradeStudentFilter').value;
    const filtered = studentFilter 
        ? allGrades.filter(g => g.studentCode === studentFilter)
        : allGrades;
    displayGrades(filtered);
}

function populateGradeStudentFilter(grades) {
    const select = document.getElementById('gradeStudentFilter');
    const uniqueStudents = [...new Set(grades.map(g => g.studentCode))];
    
    select.innerHTML = '<option value="">All Students</option>' + 
        uniqueStudents.map(code => `<option value="${code}">${code}</option>`).join('');
}

// Microservices
async function checkQuickGradeHealth() {
    try {
        const response = await fetchWithAuth(`${API_BASE}/microservices/quickgrade/health`);
        if (response.ok) {
            const health = await response.json();
            const statusClass = health.available ? 'up' : 'down';
            document.getElementById('quickGradeHealth').innerHTML = `
                <div class="data-item">
                    <div class="data-item-content">
                        <div class="data-item-field">
                            <strong>Service:</strong> ${health.service}
                        </div>
                        <div class="data-item-field">
                            <strong>Status:</strong> 
                            <span class="status-indicator ${statusClass}"></span>
                            ${health.status}
                        </div>
                        <div class="data-item-field">
                            <strong>Available:</strong> ${health.available ? 'Yes' : 'No'}
                        </div>
                    </div>
                </div>
            `;
        } else {
            showMessage('microservicesMessage', 'Failed to check service health', 'error');
        }
    } catch (error) {
        showMessage('microservicesMessage', 'Error: ' + error.message, 'error');
    }
}

async function loadQuickGradeStatistics() {
    try {
        const response = await fetchWithAuth(`${API_BASE}/microservices/quickgrade/statistics`);
        if (response.ok) {
            const stats = await response.json();
            document.getElementById('quickGradeStats').innerHTML = `
                <div class="stats-grid">
                    <div class="stat-item">
                        <div class="stat-value">${stats.totalGrades || 0}</div>
                        <div class="stat-label">Total Grades</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">${stats.averageGrade ? stats.averageGrade.toFixed(2) : 'N/A'}</div>
                        <div class="stat-label">Average Grade</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">${stats.passRate ? (stats.passRate * 100).toFixed(1) : 'N/A'}%</div>
                        <div class="stat-label">Pass Rate</div>
                    </div>
                </div>
            `;
        } else {
            const error = await response.json();
            showMessage('microservicesMessage', error.message || 'Service unavailable', 'error');
        }
    } catch (error) {
        showMessage('microservicesMessage', 'Error: ' + error.message, 'error');
    }
}

// Actuator
async function loadActuatorHealth() {
    try {
        const response = await fetch('/actuator/health');
        if (response.ok) {
            const health = await response.json();
            document.getElementById('actuatorHealth').innerHTML = `
                <pre>${JSON.stringify(health, null, 2)}</pre>
            `;
        } else {
            showMessage('actuatorMessage', 'Failed to load health info', 'error');
        }
    } catch (error) {
        showMessage('actuatorMessage', 'Error: ' + error.message, 'error');
    }
}

async function loadActuatorInfo() {
    try {
        const response = await fetch('/actuator/info');
        if (response.ok) {
            const info = await response.json();
            document.getElementById('actuatorInfo').innerHTML = `
                <pre>${JSON.stringify(info, null, 2)}</pre>
            `;
        } else {
            document.getElementById('actuatorInfo').innerHTML = '<p>No info available</p>';
        }
    } catch (error) {
        showMessage('actuatorMessage', 'Error: ' + error.message, 'error');
    }
}

// Utility Functions
async function fetchWithAuth(url, options = {}) {
    if (authToken) {
        options.headers = options.headers || {};
        options.headers['Authorization'] = `Bearer ${authToken}`;
    }
    return fetch(url, options);
}

function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.className = `message ${type} show`;
    
    setTimeout(() => {
        element.classList.remove('show');
    }, 5000);
}

function canModifyStudents() {
    return currentRole === 'ADMIN' || currentRole === 'INSTRUCTOR';
}

function canModifyPreferences() {
    return currentRole === 'ADMIN' || currentRole === 'STUDENT';
}

// Add courses and packs endpoints (assuming they exist)
// Note: These would need to be added to the backend if they don't exist
async function loadCoursesPublic() {
    try {
        const response = await fetch(`${API_BASE}/courses`);
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('Error loading courses:', error);
    }
    return [];
}

async function loadPacksPublic() {
    try {
        const response = await fetch(`${API_BASE}/packs`);
        if (response.ok) {
            return await response.json();
        }
    } catch (error) {
        console.error('Error loading packs:', error);
    }
    return [];
}

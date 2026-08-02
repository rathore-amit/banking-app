import axios from 'axios'


const API_BASE = '/api'


const api = axios.create({
    baseURL: API_BASE,
    headers: { 'Content-Type': 'application/json' }
})


api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})


api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)


export const authService = {
    login: (username, password) => api.post('/auth/login', { username, password }),
}


export const accountService = {
    getAll: () => api.get('/accounts'),
    getById: (id) => api.get(`/accounts/${id}`),
    create: (data) => api.post('/accounts', data),
    credit: (id, amount) => axios.post(`http://localhost:38081/api/accounts/${id}/credit`, { amount }),
}


export const customerService = {
    create: (data) => axios.post('http://localhost:38081/api/customers', data, {
        headers: { 'Content-Type': 'application/json' }
    }),
    getAll: () => axios.get('http://localhost:38081/api/customers'),
}


export const transferService = {
    execute: (data) => api.post('/transfers', data),
}


export const notificationService = {
    getByAccount: (accountId) => axios.get(`http://localhost:38083/api/notifications/account/${accountId}`),
}


export const healthService = {
    gateway: () => axios.get('http://localhost:38080/actuator/health'),
    account: () => axios.get('http://localhost:38081/actuator/health'),
    transaction: () => axios.get('http://localhost:38082/actuator/health'),
    notification: () => axios.get('http://localhost:38083/actuator/health'),
    eureka: () => axios.get('http://localhost:38761/actuator/health'),
    config: () => axios.get('http://localhost:38888/actuator/health'),
}


export default api

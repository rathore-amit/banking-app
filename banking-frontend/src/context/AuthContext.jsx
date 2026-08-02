import { createContext, useContext, useState, useEffect } from 'react'


const AuthContext = createContext(null)


export function AuthProvider({ children }) {
 const [token, setToken] = useState(localStorage.getItem('token'))
 const [user, setUser] = useState(localStorage.getItem('user'))


 const login = (tokenValue, username) => {
   localStorage.setItem('token', tokenValue)
   localStorage.setItem('user', username)
   setToken(tokenValue)
   setUser(username)
 }


 const logout = () => {
   localStorage.removeItem('token')
   localStorage.removeItem('user')
   setToken(null)
   setUser(null)
 }


 return (
   <AuthContext.Provider value={{ token, user, login, logout, isAuthenticated: !!token }}>
     {children}
   </AuthContext.Provider>
 )
}


export const useAuth = () => useContext(AuthContext)






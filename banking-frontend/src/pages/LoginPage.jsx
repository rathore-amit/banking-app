import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/api'
import { Lock, User, AlertCircle, Info } from 'lucide-react'


export default function LoginPage() {
 const [username, setUsername] = useState('')
 const [password, setPassword] = useState('')
 const [error, setError] = useState('')
 const [loading, setLoading] = useState(false)
 const { login } = useAuth()
 const navigate = useNavigate()


 const handleSubmit = async (e) => {
   e.preventDefault()
   setError('')
   setLoading(true)
   try {
     const res = await authService.login(username, password)
     login(res.data.token, username)
     navigate('/dashboard')
   } catch (err) {
     setError(err.response?.data?.message || 'Login failed. Check credentials.')
   } finally {
     setLoading(false)
   }
 }


 return (
   <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-900 via-primary-800 to-primary-700">
     <div className="w-full max-w-md">
       <div className="bg-white rounded-2xl shadow-2xl p-8">
         <div className="text-center mb-8">
           <div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-full mb-4">
             <span className="text-3xl">🏦</span>
           </div>
           <h1 className="text-2xl font-bold text-gray-900">Banking Portal</h1>
           <p className="text-gray-500 mt-1">Microservices Demo</p>
         </div>


         {error && (
           <div className="flex items-center gap-2 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">
             <AlertCircle size={18} />
             <span className="text-sm">{error}</span>
           </div>
         )}


         <form onSubmit={handleSubmit} className="space-y-4">
           <div>
             <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
             <div className="relative">
               <User size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
               <input
                 type="text"
                 value={username}
                 onChange={(e) => setUsername(e.target.value)}
                 className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none transition"
                 placeholder="alice"
                 required
               />
             </div>
           </div>


           <div>
             <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
             <div className="relative">
               <Lock size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
               <input
                 type="password"
                 value={password}
                 onChange={(e) => setPassword(e.target.value)}
                 className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none transition"
                 placeholder="password123"
                 required
               />
             </div>
           </div>


           <button
             type="submit"
             disabled={loading}
             className="w-full bg-primary-600 hover:bg-primary-700 text-white font-medium py-2.5 rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
           >
             {loading ? 'Logging in...' : 'Sign In'}
           </button>
         </form>


         <div className="mt-6 p-3 bg-gray-50 rounded-lg">
           <p className="text-xs text-gray-500 text-center">
             Demo credentials: <code className="bg-gray-200 px-1 rounded">alice</code> / <code className="bg-gray-200 px-1 rounded">password123</code>
           </p>
         </div>
       </div>


       {/* Technical Info Panel */}
       <div className="mt-4 bg-white/10 backdrop-blur rounded-xl p-5 border border-white/20">
         <div className="flex items-center gap-2 mb-3">
           <Info size={16} className="text-blue-300" />
           <h3 className="text-sm font-semibold text-white">How This Works (Technical)</h3>
         </div>
         <div className="text-xs text-blue-100 space-y-2">
           <div className="flex items-start gap-2">
             <span className="bg-blue-500/30 px-1.5 py-0.5 rounded text-[10px] font-mono shrink-0">POST</span>
             <span>/api/auth/login → <strong>API Gateway (8080)</strong></span>
           </div>
           <div className="bg-white/5 rounded-lg p-3 font-mono text-[11px] space-y-1">
             <p>1. Request hits <strong>API Gateway</strong> → AuthController.java</p>
             <p>2. Credentials verify (hardcoded demo users)</p>
             <p>3. <strong>JwtUtil.java</strong> generates JWT token (HS256, secret from application.yml)</p>
             <p>4. Token returned → stored in localStorage</p>
             <p>5. All future requests include <strong>Authorization: Bearer &lt;token&gt;</strong></p>
             <p>6. <strong>JwtAuthFilter.java</strong> validates token on every request</p>
           </div>
           <div className="mt-2 p-2 bg-yellow-500/10 border border-yellow-500/20 rounded">
             <p className="text-yellow-200">📁 Files: <code>api-gateway/filter/AuthController.java</code>, <code>JwtAuthFilter.java</code>, <code>config/JwtUtil.java</code></p>
           </div>
         </div>
       </div>
     </div>
   </div>
 )
}






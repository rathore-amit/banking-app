import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { LayoutDashboard, ArrowLeftRight, Bell, Activity, LogOut } from 'lucide-react'


const navItems = [
 { to: '/dashboard', label: 'Accounts', icon: LayoutDashboard },
 { to: '/transfer', label: 'Transfer', icon: ArrowLeftRight },
 { to: '/notifications', label: 'Notifications', icon: Bell },
 { to: '/services', label: 'Services', icon: Activity },
]


export default function Layout() {
 const { user, logout } = useAuth()
 const navigate = useNavigate()


 const handleLogout = () => {
   logout()
   navigate('/login')
 }


 return (
   <div className="min-h-screen bg-gray-50">
     <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
       <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
         <div className="flex items-center justify-between h-16">
           <div className="flex items-center gap-8">
             <div className="flex items-center gap-2">
               <span className="text-2xl">🏦</span>
               <span className="font-bold text-gray-900">Banking</span>
             </div>
             <div className="hidden md:flex items-center gap-1">
               {navItems.map(({ to, label, icon: Icon }) => (
                 <NavLink
                   key={to}
                   to={to}
                   className={({ isActive }) =>
                     `flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition ${
                       isActive ? 'bg-primary-50 text-primary-700' : 'text-gray-600 hover:bg-gray-100'
                     }`
                   }
                 >
                   <Icon size={16} />
                   {label}
                 </NavLink>
               ))}
             </div>
           </div>
           <div className="flex items-center gap-4">
             <span className="text-sm text-gray-600">👤 {user}</span>
             <button onClick={handleLogout} className="flex items-center gap-1 text-sm text-gray-500 hover:text-red-600 transition">
               <LogOut size={16} /> Logout
             </button>
           </div>
         </div>
       </div>
     </nav>


     <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
       <Outlet />
     </main>


     <footer className="border-t border-gray-200 bg-white mt-12">
       <div className="max-w-7xl mx-auto px-4 py-4 text-center text-xs text-gray-400">
         Banking Microservices Demo — Spring Boot + React + Kafka + Saga Pattern
       </div>
     </footer>
   </div>
 )
}






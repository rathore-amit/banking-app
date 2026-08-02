import { useState, useEffect } from 'react'
import { accountService, customerService } from '../services/api'
import { Wallet, Plus, RefreshCw, Users, Info, Database, Zap } from 'lucide-react'


export default function DashboardPage() {
 const [accounts, setAccounts] = useState([])
 const [loading, setLoading] = useState(true)
 const [showCreate, setShowCreate] = useState(false)
 const [creating, setCreating] = useState(false)
 const [newAccount, setNewAccount] = useState({ customerId: 1, accountType: 'SAVINGS' })
 const [error, setError] = useState('')
 const [showTech, setShowTech] = useState(true)


 const fetchAccounts = async () => {
   setLoading(true)
   try {
     const res = await accountService.getAll()
     setAccounts(res.data || [])
   } catch (err) {
     setError('Could not fetch accounts. Is account-service running?')
   } finally {
     setLoading(false)
   }
 }


 useEffect(() => { fetchAccounts() }, [])


 const handleCreate = async (e) => {
   e.preventDefault()
   setCreating(true)
   try {
     await accountService.create(newAccount)
     setShowCreate(false)
     fetchAccounts()
   } catch (err) {
     setError(err.response?.data?.message || 'Failed to create account')
   } finally {
     setCreating(false)
   }
 }


 const handleCredit = async (accountId) => {
   const amount = prompt('Enter amount to credit:')
   if (!amount || isNaN(amount)) return
   try {
     await accountService.credit(accountId, parseFloat(amount))
     fetchAccounts()
   } catch (err) {
     alert('Credit failed: ' + (err.response?.data?.message || err.message))
   }
 }


 const getStatusColor = (status) => {
   switch (status) {
     case 'ACTIVE': return 'bg-green-100 text-green-800'
     case 'FROZEN': return 'bg-red-100 text-red-800'
     default: return 'bg-gray-100 text-gray-800'
   }
 }


 return (
   <div className="space-y-6">
     <div className="flex items-center justify-between">
       <div>
         <h1 className="text-2xl font-bold text-gray-900">Accounts</h1>
         <p className="text-gray-500">Manage your bank accounts</p>
       </div>
       <div className="flex gap-2">
         <button onClick={() => setShowTech(!showTech)} className={`flex items-center gap-2 px-3 py-2 border rounded-lg text-sm transition ${showTech ? 'border-blue-300 bg-blue-50 text-blue-700' : 'border-gray-300 text-gray-600 hover:bg-gray-50'}`}>
           <Info size={16} /> Tech Info
         </button>
         <button onClick={fetchAccounts} className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition">
           <RefreshCw size={16} /> Refresh
         </button>
         <button onClick={() => setShowCreate(!showCreate)} className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition">
           <Plus size={16} /> New Account
         </button>
       </div>
     </div>


     {/* Technical Info Panel */}
     {showTech && (
       <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl p-5">
         <div className="flex items-center gap-2 mb-3">
           <Info size={18} className="text-blue-600" />
           <h3 className="font-semibold text-blue-900">Technical Flow — Account Service</h3>
         </div>
         <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
           <div className="space-y-2">
             <div className="flex items-center gap-2 text-blue-800 font-medium">
               <Database size={14} /> Request Flow
             </div>
             <div className="bg-white rounded-lg p-3 text-xs font-mono text-gray-700 space-y-1">
               <p>GET /api/accounts</p>
               <p className="text-gray-400">↓</p>
               <p><strong>API Gateway</strong> → JWT verify → route to account-service</p>
               <p className="text-gray-400">↓</p>
               <p><strong>AccountController.java</strong> → @GetMapping</p>
               <p className="text-gray-400">↓</p>
               <p><strong>AccountService.java</strong> → @Cacheable (Redis check)</p>
               <p className="text-gray-400">↓</p>
               <p><strong>AccountRepository.java</strong> → JPA → Postgres (account_db)</p>
             </div>
           </div>
           <div className="space-y-2">
             <div className="flex items-center gap-2 text-blue-800 font-medium">
               <Zap size={14} /> Key Concepts Used
             </div>
             <div className="bg-white rounded-lg p-3 text-xs text-gray-700 space-y-2">
               <p>🔹 <strong>@Transactional + Pessimistic Lock</strong> — debit/credit ACID guarantee</p>
               <p>🔹 <strong>@Cacheable (Redis)</strong> — account data cache, evict on update</p>
               <p>🔹 <strong>Eureka Discovery</strong> — API Gateway finds account-service via Eureka</p>
               <p>🔹 <strong>DTO Pattern</strong> — Entity ≠ API response (AccountResponse.java)</p>
               <p>🔹 <strong>Validation</strong> — @NotNull, @DecimalMin on CreateAccountRequest</p>
               <p>🔹 <strong>Custom Exception</strong> — AccountNotFoundException → 404</p>
             </div>
           </div>
         </div>
         <div className="mt-3 p-2 bg-blue-100/50 rounded-lg text-xs text-blue-700">
           📁 <strong>Files:</strong> account-service/controller/AccountController.java, service/AccountService.java, config/CacheConfig.java, repository/AccountRepository.java
         </div>
       </div>
     )}


     {error && (
       <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>
     )}


     {showCreate && (
       <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
         <h3 className="font-semibold text-gray-900 mb-4">Create New Account</h3>
         <form onSubmit={handleCreate} className="flex gap-4 items-end">
           <div>
             <label className="block text-sm text-gray-600 mb-1">Customer ID</label>
             <input
               type="number"
               value={newAccount.customerId}
               onChange={(e) => setNewAccount({ ...newAccount, customerId: parseInt(e.target.value) })}
               className="px-3 py-2 border border-gray-300 rounded-lg w-32"
               min="1"
             />
           </div>
           <div>
             <label className="block text-sm text-gray-600 mb-1">Account Type</label>
             <select
               value={newAccount.accountType}
               onChange={(e) => setNewAccount({ ...newAccount, accountType: e.target.value })}
               className="px-3 py-2 border border-gray-300 rounded-lg"
             >
               <option value="SAVINGS">Savings</option>
               <option value="CURRENT">Current</option>
             </select>
           </div>
           <button type="submit" disabled={creating} className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50">
             {creating ? 'Creating...' : 'Create'}
           </button>
         </form>
       </div>
     )}


     {loading ? (
       <div className="flex justify-center py-12">
         <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
       </div>
     ) : accounts.length === 0 ? (
       <div className="text-center py-12 bg-white rounded-xl border border-gray-200">
         <Wallet size={48} className="mx-auto text-gray-300 mb-4" />
         <p className="text-gray-500">No accounts found. Create one to get started!</p>
       </div>
     ) : (
       <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
         {accounts.map((acc) => (
           <div key={acc.id} className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm hover:shadow-md transition">
             <div className="flex items-center justify-between mb-3">
               <span className="text-sm font-medium text-gray-500">Account #{acc.id}</span>
               <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${getStatusColor(acc.status)}`}>
                 {acc.status}
               </span>
             </div>
             <div className="mb-3">
               <p className="text-2xl font-bold text-gray-900">₹{acc.balance?.toLocaleString('en-IN') || '0.00'}</p>
               <p className="text-sm text-gray-500">{acc.accountType} Account</p>
             </div>
             {acc.customerName && (
               <p className="text-sm text-gray-600 flex items-center gap-1 mb-3">
                 <Users size={14} /> {acc.customerName}
               </p>
             )}
             <button
               onClick={() => handleCredit(acc.id)}
               className="w-full text-sm px-3 py-2 border border-primary-200 text-primary-700 rounded-lg hover:bg-primary-50 transition"
             >
               + Credit Money
             </button>
           </div>
         ))}
       </div>
     )}
   </div>
 )
}






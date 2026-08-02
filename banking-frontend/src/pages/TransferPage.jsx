import { useState } from 'react'
import { transferService } from '../services/api'
import { ArrowRight, Send, CheckCircle, XCircle, Info, GitBranch } from 'lucide-react'


export default function TransferPage() {
 const [form, setForm] = useState({ fromAccountId: '', toAccountId: '', amount: '' })
 const [loading, setLoading] = useState(false)
 const [result, setResult] = useState(null)
 const [error, setError] = useState('')
 const [showTech, setShowTech] = useState(true)


 const handleSubmit = async (e) => {
   e.preventDefault()
   setError('')
   setResult(null)
   setLoading(true)
   try {
     const res = await transferService.execute({
       fromAccountId: parseInt(form.fromAccountId),
       toAccountId: parseInt(form.toAccountId),
       amount: parseFloat(form.amount)
     })
     setResult(res.data)
   } catch (err) {
     setError(err.response?.data?.message || err.response?.data?.error || 'Transfer failed')
   } finally {
     setLoading(false)
   }
 }


 return (
   <div className="max-w-3xl mx-auto space-y-6">
     <div className="flex items-center justify-between">
       <div>
         <h1 className="text-2xl font-bold text-gray-900">Transfer Money</h1>
         <p className="text-gray-500">Triggers the Saga Orchestrator pattern</p>
       </div>
       <button onClick={() => setShowTech(!showTech)} className={`flex items-center gap-2 px-3 py-2 border rounded-lg text-sm transition ${showTech ? 'border-blue-300 bg-blue-50 text-blue-700' : 'border-gray-300 text-gray-600 hover:bg-gray-50'}`}>
         <Info size={16} /> Tech Info
       </button>
     </div>


     {/* Technical Info Panel */}
     {showTech && (
       <div className="bg-gradient-to-r from-purple-50 to-pink-50 border border-purple-200 rounded-xl p-5">
         <div className="flex items-center gap-2 mb-3">
           <GitBranch size={18} className="text-purple-600" />
           <h3 className="font-semibold text-purple-900">Saga Orchestrator Pattern — Complete Flow</h3>
         </div>
         <div className="bg-white rounded-lg p-4 text-xs font-mono text-gray-700 space-y-1">
           <p className="text-purple-600 font-bold">POST /api/transfers → API Gateway → Transaction Service</p>
           <p className="text-gray-400 ml-4">↓</p>
           <p className="ml-4"><strong>TransferSagaOrchestrator.java</strong></p>
           <p className="text-gray-400 ml-4">↓</p>
           <p className="ml-4 text-green-700">Step 1: FraudCheckService.validateTransfer()</p>
           <p className="ml-8 text-gray-500">├─ CompletableFuture.supplyAsync() → isFraudulent() [parallel]</p>
           <p className="ml-8 text-gray-500">└─ CompletableFuture.supplyAsync() → isWithinDailyLimit() [parallel]</p>
           <p className="ml-8 text-gray-500">   └─ .thenCombine() → dono results merge</p>
           <p className="text-gray-400 ml-4">↓</p>
           <p className="ml-4 text-green-700">Step 2: AccountServiceClient.debit(from, amount)</p>
           <p className="ml-8 text-gray-500">├─ @CircuitBreaker(name="accountService") — Resilience4j</p>
           <p className="ml-8 text-gray-500">├─ @Retry(name="accountService") — exponential backoff</p>
           <p className="ml-8 text-gray-500">└─ RestTemplate → http://account-service/api/accounts/{'{id}'}/debit</p>
           <p className="ml-8 text-gray-500">   └─ Eureka resolves "account-service" → actual IP:port</p>
           <p className="text-gray-400 ml-4">↓</p>
           <p className="ml-4 text-green-700">Step 3: AccountServiceClient.credit(to, amount)</p>
           <p className="text-gray-400 ml-4">↓</p>
           <p className="ml-4 text-red-600">If Step 3 FAILS → COMPENSATING TRANSACTION:</p>
           <p className="ml-8 text-red-500">└─ AccountServiceClient.compensateDebit(from, amount) — refund!</p>
           <p className="text-gray-400 ml-4">↓</p>
           <p className="ml-4 text-blue-700">Step 4: TransactionEventProducer.publish(event)</p>
           <p className="ml-8 text-gray-500">└─ KafkaTemplate.send("transaction-events", key, event)</p>
           <p className="ml-8 text-gray-500">   └─ Notification Service ka TransactionEventConsumer consume karega</p>
         </div>
         <div className="mt-3 grid grid-cols-1 md:grid-cols-3 gap-2 text-xs">
           <div className="bg-green-50 border border-green-200 rounded-lg p-2">
             <p className="font-bold text-green-800">Multithreading</p>
             <p className="text-green-700">CompletableFuture + thenCombine()</p>
             <p className="text-green-600">📁 FraudCheckService.java</p>
           </div>
           <div className="bg-orange-50 border border-orange-200 rounded-lg p-2">
             <p className="font-bold text-orange-800">Circuit Breaker</p>
             <p className="text-orange-700">Resilience4j — CLOSED/OPEN/HALF_OPEN</p>
             <p className="text-orange-600">📁 AccountServiceClient.java</p>
           </div>
           <div className="bg-blue-50 border border-blue-200 rounded-lg p-2">
             <p className="font-bold text-blue-800">Event-Driven</p>
             <p className="text-blue-700">Kafka → async notification</p>
             <p className="text-blue-600">📁 TransactionEventProducer.java</p>
           </div>
         </div>
         <div className="mt-3 p-2 bg-purple-100/50 rounded-lg text-xs text-purple-700">
           📁 <strong>Main File:</strong> transaction-service/service/TransferSagaOrchestrator.java — ye ek button click pe poora orchestration karta hai
         </div>
       </div>
     )}


     <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
       <form onSubmit={handleSubmit} className="space-y-5">
         <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
           <div>
             <label className="block text-sm font-medium text-gray-700 mb-1">From Account ID</label>
             <input
               type="number"
               value={form.fromAccountId}
               onChange={(e) => setForm({ ...form, fromAccountId: e.target.value })}
               className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 outline-none"
               placeholder="1"
               required
               min="1"
             />
           </div>
           <div className="flex justify-center">
             <ArrowRight size={24} className="text-gray-400" />
           </div>
           <div>
             <label className="block text-sm font-medium text-gray-700 mb-1">To Account ID</label>
             <input
               type="number"
               value={form.toAccountId}
               onChange={(e) => setForm({ ...form, toAccountId: e.target.value })}
               className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 outline-none"
               placeholder="2"
               required
               min="1"
             />
           </div>
         </div>


         <div>
           <label className="block text-sm font-medium text-gray-700 mb-1">Amount (₹)</label>
           <input
             type="number"
             step="0.01"
             value={form.amount}
             onChange={(e) => setForm({ ...form, amount: e.target.value })}
             className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 outline-none"
             placeholder="500.00"
             required
             min="0.01"
           />
         </div>


         <button
           type="submit"
           disabled={loading}
           className="w-full flex items-center justify-center gap-2 bg-primary-600 hover:bg-primary-700 text-white font-medium py-3 rounded-lg transition disabled:opacity-50"
         >
           <Send size={18} />
           {loading ? 'Processing Saga...' : 'Execute Transfer'}
         </button>
       </form>
     </div>


     {result && (
       <div className="bg-green-50 border border-green-200 rounded-xl p-5">
         <div className="flex items-center gap-2 mb-2">
           <CheckCircle size={20} className="text-green-600" />
           <h3 className="font-semibold text-green-800">Transfer Successful!</h3>
         </div>
         <p className="text-sm text-green-700">Transaction ID: <code className="bg-green-100 px-2 py-0.5 rounded">{result.transactionId}</code></p>
         <div className="mt-3 p-3 bg-white/60 rounded-lg text-xs text-green-800 space-y-1">
           <p>✅ Step 1: Fraud check passed (CompletableFuture parallel execution)</p>
           <p>✅ Step 2: Source account debited (Circuit Breaker = CLOSED, healthy)</p>
           <p>✅ Step 3: Destination account credited (via Eureka-resolved service)</p>
           <p>✅ Step 4: Kafka event published → Notification Service will consume</p>
           <p>✅ Transaction status: COMPLETED (saved in Postgres transaction_db)</p>
         </div>
       </div>
     )}


     {error && (
       <div className="bg-red-50 border border-red-200 rounded-xl p-5">
         <div className="flex items-center gap-2 mb-2">
           <XCircle size={20} className="text-red-600" />
           <h3 className="font-semibold text-red-800">Transfer Failed</h3>
         </div>
         <p className="text-sm text-red-700">{error}</p>
         <div className="mt-3 p-3 bg-white/60 rounded-lg text-xs text-red-800 space-y-1">
           <p>❌ Saga detected failure — checking if compensation needed...</p>
           <p>🔄 If debit was done → compensateDebit() called (money refunded)</p>
           <p>📝 Transaction status: FAILED or COMPENSATED (saved in DB)</p>
           <p>📨 Failure event published to Kafka for audit trail</p>
         </div>
       </div>
     )}
   </div>
 )
}






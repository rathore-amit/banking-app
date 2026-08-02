import { useState } from 'react'
import { notificationService } from '../services/api'
import { Bell, Search, Info, Radio } from 'lucide-react'


export default function NotificationsPage() {
 const [accountId, setAccountId] = useState('')
 const [notifications, setNotifications] = useState([])
 const [loading, setLoading] = useState(false)
 const [searched, setSearched] = useState(false)
 const [showTech, setShowTech] = useState(true)


 const fetchNotifications = async (e) => {
   e.preventDefault()
   if (!accountId) return
   setLoading(true)
   setSearched(true)
   try {
     const res = await notificationService.getByAccount(accountId)
     setNotifications(res.data || [])
   } catch (err) {
     setNotifications([])
   } finally {
     setLoading(false)
   }
 }


 const getStatusBadge = (status) => {
   switch (status) {
     case 'COMPLETED': return 'bg-green-100 text-green-800'
     case 'FAILED': return 'bg-red-100 text-red-800'
     case 'COMPENSATED': return 'bg-yellow-100 text-yellow-800'
     default: return 'bg-gray-100 text-gray-800'
   }
 }


 return (
   <div className="max-w-3xl mx-auto space-y-6">
     <div className="flex items-center justify-between">
       <div>
         <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
         <p className="text-gray-500">Events consumed from Kafka by Notification Service</p>
       </div>
       <button onClick={() => setShowTech(!showTech)} className={`flex items-center gap-2 px-3 py-2 border rounded-lg text-sm transition ${showTech ? 'border-blue-300 bg-blue-50 text-blue-700' : 'border-gray-300 text-gray-600 hover:bg-gray-50'}`}>
         <Info size={16} /> Tech Info
       </button>
     </div>


     {/* Technical Info Panel */}
     {showTech && (
       <div className="bg-gradient-to-r from-green-50 to-emerald-50 border border-green-200 rounded-xl p-5">
         <div className="flex items-center gap-2 mb-3">
           <Radio size={18} className="text-green-600" />
           <h3 className="font-semibold text-green-900">Kafka Event-Driven Architecture</h3>
         </div>
         <div className="bg-white rounded-lg p-4 text-xs font-mono text-gray-700 space-y-1">
           <p className="text-green-700 font-bold">Event Flow: Producer → Kafka Topic → Consumer</p>
           <p className="text-gray-400 mt-2">─── PRODUCER (Transaction Service) ───</p>
           <p className="ml-4">TransactionEventProducer.java:</p>
           <p className="ml-8">kafkaTemplate.send("transaction-events", key, event)</p>
           <p className="ml-8 text-gray-500">key = transactionId → same partition → order guarantee</p>
           <p className="text-gray-400 mt-2">─── KAFKA BROKER ───</p>
           <p className="ml-4">Topic: "transaction-events"</p>
           <p className="ml-4 text-gray-500">Messages persist until consumed (unlike RabbitMQ)</p>
           <p className="text-gray-400 mt-2">─── CONSUMER (Notification Service) ───</p>
           <p className="ml-4">TransactionEventConsumer.java:</p>
           <p className="ml-8">@KafkaListener(topics = "transaction-events")</p>
           <p className="ml-8">consume(TransactionEvent event) {'{'}</p>
           <p className="ml-12">notification = new Notification(event)</p>
           <p className="ml-12">notificationRepository.save(notification) → H2 DB</p>
           <p className="ml-8">{'}'}</p>
         </div>
         <div className="mt-3 grid grid-cols-1 md:grid-cols-2 gap-2 text-xs">
           <div className="bg-green-100/50 border border-green-200 rounded-lg p-2">
             <p className="font-bold text-green-800">Why Kafka?</p>
             <p className="text-green-700">Decoupling — Transaction Service ko Notification Service ke up/down hone ki chinta nahi</p>
             <p className="text-green-700 mt-1">Resilience — message Kafka mein safe rehta hai until consumed</p>
           </div>
           <div className="bg-green-100/50 border border-green-200 rounded-lg p-2">
             <p className="font-bold text-green-800">Concepts</p>
             <p className="text-green-700">• Partition Key → ordering per transaction</p>
             <p className="text-green-700">• Consumer Group → scalable consumers</p>
             <p className="text-green-700">• At-least-once delivery</p>
           </div>
         </div>
         <div className="mt-3 p-2 bg-green-100/50 rounded-lg text-xs text-green-700">
           📁 <strong>Files:</strong> transaction-service/messaging/TransactionEventProducer.java, notification-service/messaging/TransactionEventConsumer.java, KafkaConsumerConfig.java
         </div>
       </div>
     )}


     <div className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm">
       <form onSubmit={fetchNotifications} className="flex gap-3">
         <div className="flex-1">
           <input
             type="number"
             value={accountId}
             onChange={(e) => setAccountId(e.target.value)}
             className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 outline-none"
             placeholder="Enter Account ID (e.g. 1)"
             min="1"
           />
         </div>
         <button type="submit" disabled={loading} className="flex items-center gap-2 px-5 py-2.5 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50">
           <Search size={16} /> {loading ? 'Loading...' : 'Search'}
         </button>
       </form>
     </div>


     {searched && (
       notifications.length === 0 ? (
         <div className="text-center py-12 bg-white rounded-xl border border-gray-200">
           <Bell size={48} className="mx-auto text-gray-300 mb-4" />
           <p className="text-gray-500">No notifications for this account yet.</p>
           <p className="text-sm text-gray-400 mt-1">Make a transfer first — Kafka event will appear here!</p>
         </div>
       ) : (
         <div className="space-y-3">
           {notifications.map((n, i) => (
             <div key={n.id || i} className="bg-white border border-gray-200 rounded-xl p-4 shadow-sm">
               <div className="flex items-center justify-between mb-2">
                 <span className="text-sm font-medium text-gray-600">Transaction #{n.transactionId}</span>
                 <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${getStatusBadge(n.status)}`}>
                   {n.status}
                 </span>
               </div>
               <p className="text-sm text-gray-700">{n.message || `Transfer of ₹${n.amount} from Account #${n.fromAccountId} to Account #${n.toAccountId}`}</p>
               {n.createdAt && <p className="text-xs text-gray-400 mt-2">{new Date(n.createdAt).toLocaleString()}</p>}
               <p className="text-[10px] text-gray-400 mt-1 font-mono">via Kafka topic: transaction-events → NotificationService consumer</p>
             </div>
           ))}
         </div>
       )
     )}
   </div>
 )
}






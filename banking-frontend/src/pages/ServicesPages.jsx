import { useState, useEffect } from 'react'
import { healthService } from '../services/api'
import { Activity, CheckCircle, XCircle, RefreshCw, Info, Server } from 'lucide-react'


const SERVICES = [
 { name: 'API Gateway', port: 38080, check: healthService.gateway, tech: 'Spring Cloud Gateway + JWT Filter + Redis Rate Limiter' },
 { name: 'Account Service', port: 38081, check: healthService.account, tech: 'Spring Boot + JPA + Postgres + Redis Cache' },
 { name: 'Transaction Service', port: 38082, check: healthService.transaction, tech: 'Saga Orchestrator + Circuit Breaker + Kafka Producer' },
 { name: 'Notification Service', port: 38083, check: healthService.notification, tech: 'Kafka Consumer + H2 in-memory DB' },
 { name: 'Eureka Server', port: 38761, check: healthService.eureka, tech: 'Netflix Eureka — Service Registry & Discovery' },
 { name: 'Config Server', port: 38888, check: healthService.config, tech: 'Spring Cloud Config — centralized config from config-repo/' },
]


export default function ServicesPage() {
 const [statuses, setStatuses] = useState({})
 const [loading, setLoading] = useState(true)
 const [showTech, setShowTech] = useState(true)


 const checkAll = async () => {
   setLoading(true)
   const results = {}
   await Promise.all(
     SERVICES.map(async (svc) => {
       try {
         const res = await svc.check()
         results[svc.name] = { status: 'UP', details: res.data }
       } catch (err) {
         results[svc.name] = { status: 'DOWN', error: err.message }
       }
     })
   )
   setStatuses(results)
   setLoading(false)
 }


 useEffect(() => { checkAll() }, [])


 const upCount = Object.values(statuses).filter(s => s.status === 'UP').length
 const totalCount = SERVICES.length


 return (
   <div className="space-y-6">
     <div className="flex items-center justify-between">
       <div>
         <h1 className="text-2xl font-bold text-gray-900">Services Health</h1>
         <p className="text-gray-500">Real-time microservices status via Spring Actuator</p>
       </div>
       <div className="flex gap-2">
         <button onClick={() => setShowTech(!showTech)} className={`flex items-center gap-2 px-3 py-2 border rounded-lg text-sm transition ${showTech ? 'border-blue-300 bg-blue-50 text-blue-700' : 'border-gray-300 text-gray-600 hover:bg-gray-50'}`}>
           <Info size={16} /> Tech Info
         </button>
         <button onClick={checkAll} disabled={loading} className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition disabled:opacity-50">
           <RefreshCw size={16} className={loading ? 'animate-spin' : ''} /> Refresh
         </button>
       </div>
     </div>


     {/* Technical Info Panel */}
     {showTech && (
       <div className="bg-gradient-to-r from-indigo-50 to-violet-50 border border-indigo-200 rounded-xl p-5">
         <div className="flex items-center gap-2 mb-3">
           <Server size={18} className="text-indigo-600" />
           <h3 className="font-semibold text-indigo-900">Microservices Architecture — How Services Communicate</h3>
         </div>
         <div className="bg-white rounded-lg p-4 text-xs font-mono text-gray-700">
           <pre className="whitespace-pre-wrap">{`┌─────────────────────────────────────────────────────────┐
│  Client (React Frontend :3000)                          │
└──────────────────────┬──────────────────────────────────┘
                      │ HTTP (JWT in header)
                      ▼
┌──────────────────────────────────────────────────────────┐
│  API Gateway (:8080)                                     │
│  • JwtAuthFilter → validates token                       │
│  • Route to service via Eureka lookup                    │
│  • Redis rate limiter (RequestRateLimiter filter)        │
└──────┬────────────────────────┬──────────────────────────┘
      │                        │
      ▼                        ▼
┌──────────────┐     ┌─────────────────────┐
│ Account Svc  │◄────│ Transaction Svc     │
│ (:8081)      │     │ (:8082)             │
│              │     │                     │
│ • Postgres   │     │ • Saga Orchestrator │
│ • Redis      │     │ • Circuit Breaker   │
│ • @Cacheable │     │ • @LoadBalanced     │
│ • Pessimist. │     │ • Kafka Producer    │
│   Lock       │     │ • CompletableFuture │
└──────────────┘     └──────────┬──────────┘
                               │ Kafka
                               ▼
                    ┌─────────────────────┐
                    │ Notification Svc    │
                    │ (:8083)             │
                    │ • @KafkaListener    │
                    │ • H2 in-memory DB   │
                    └─────────────────────┘


┌─────────────────┐  ┌──────────────────┐
│ Eureka (:8761)  │  │ Config (:8888)   │
│ Service Registry│  │ config-repo/*.yml│
└─────────────────┘  └──────────────────┘`}</pre>
         </div>
         <div className="mt-3 grid grid-cols-1 md:grid-cols-3 gap-2 text-xs">
           <div className="bg-indigo-100/50 border border-indigo-200 rounded-lg p-2">
             <p className="font-bold text-indigo-800">Service Discovery</p>
             <p className="text-indigo-700">Eureka — har service startup pe register hoti hai. Gateway Eureka se poochta hai "account-service kidhar hai?"</p>
           </div>
           <div className="bg-indigo-100/50 border border-indigo-200 rounded-lg p-2">
             <p className="font-bold text-indigo-800">Config Server</p>
             <p className="text-indigo-700">Centralized config — services apni properties Config Server se lete hain (config-repo/ folder)</p>
           </div>
           <div className="bg-indigo-100/50 border border-indigo-200 rounded-lg p-2">
             <p className="font-bold text-indigo-800">Health Checks</p>
             <p className="text-indigo-700">Spring Actuator — /actuator/health endpoint + custom DatabaseHealthIndicator</p>
           </div>
         </div>
       </div>
     )}


     <div className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm">
       <div className="flex items-center gap-3 mb-2">
         <Activity size={20} className="text-primary-600" />
         <span className="font-semibold text-gray-900">System Overview</span>
       </div>
       <div className="flex items-center gap-4">
         <div className="text-3xl font-bold text-gray-900">{upCount}/{totalCount}</div>
         <div className="text-sm text-gray-500">services running</div>
         <div className={`ml-auto px-3 py-1 rounded-full text-sm font-medium ${upCount === totalCount ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>
           {upCount === totalCount ? 'All Healthy' : 'Degraded'}
         </div>
       </div>
     </div>


     <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
       {SERVICES.map((svc) => {
         const info = statuses[svc.name]
         const isUp = info?.status === 'UP'
         return (
           <div key={svc.name} className={`bg-white border rounded-xl p-5 shadow-sm ${isUp ? 'border-green-200' : 'border-red-200'}`}>
             <div className="flex items-center justify-between">
               <div>
                 <h3 className="font-semibold text-gray-900">{svc.name}</h3>
                 <p className="text-sm text-gray-500">Port: {svc.port}</p>
               </div>
               {loading ? (
                 <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-primary-600"></div>
               ) : isUp ? (
                 <CheckCircle size={24} className="text-green-500" />
               ) : (
                 <XCircle size={24} className="text-red-500" />
               )}
             </div>
             <p className="text-xs text-gray-500 mt-2 italic">{svc.tech}</p>
             {info && !loading && (
               <div className={`mt-3 text-xs px-3 py-2 rounded-lg ${isUp ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
                 {isUp ? '● Status: UP — /actuator/health returned 200' : `● Error: ${info.error || 'Service unreachable'}`}
               </div>
             )}
           </div>
         )
       })}
     </div>
   </div>
 )
}






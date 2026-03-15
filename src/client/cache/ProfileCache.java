package client.cache;

import shared.models.Employee;
import shared.models.FamilyMember;
import shared.models.LeaveBalance;
import shared.models.LeaveApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
PURPOSE:Store frequently accessed data on client side to reduce network calls
CONCEPT: Client-Side Caching
- When data is fetched from server, store it locally
- Next time same data is needed, return from cache instead of calling server
- Cache expires after timeout to prevent stale data
- Reduces network latency (faster response times)
- Reduces server load (fewer RMI calls)
- Improves user experience (instant loading)
- Trade-off: Freshness vs Performance (cached data might be slightly outdated) 
CACHE INVALIDATION STRATEGIES:
1. Time-based expiration (implemented )
2. Manual invalidation on update operations
 */
public class ProfileCache {
    
    // Singleton instance - one cache for entire application
    private static ProfileCache instance;
    
    // Cache storage
    private Map<String, CachedEmployee> employeeCache;
    private Map<String, CachedFamilyList> familyCache;
    private Map<String, CachedLeaveBalance> leaveBalanceCache;
    private Map<String, CachedLeaveApplications> leaveApplicationsCache;
    
    // Cache timeout in milliseconds (5 minutes)
    private static final long CACHE_TIMEOUT = 5 * 60 * 1000;
    
    // Statistics for monitoring cache performance
    private int cacheHits = 0;
    private int cacheMisses = 0;
    
    /*
     Constructor:
     Initialize all cache maps
     */
    private ProfileCache() {
        employeeCache = new HashMap<>();
        familyCache = new HashMap<>();
        leaveBalanceCache = new HashMap<>();
        leaveApplicationsCache = new HashMap<>();
    }
    
    /*
     getInstance():
     Singleton pattern - ensure only one cache instance exists
     */
    public static synchronized ProfileCache getInstance() {
        if (instance == null) {
            instance = new ProfileCache();
        }
        return instance;
    }
    
    // EMPLOYEE PROFILE CACHING    
    /*
     getEmployee():
     FUNCTION getEmployee(employeeId)
     1. CHECK if employee exists in cache
     2. IF exists AND not expired THEN
     INCREMENT cache hits
     LOG "Cache hit"
     RETURN cached employee
     3. ELSE
     INCREMENT cache misses
     LOG "Cache miss"
     RETURN null (caller must fetch from server)
     */

    public Employee getEmployee(String employeeId) {
        CachedEmployee cached = employeeCache.get(employeeId);
        
        if (cached != null && !cached.isExpired()) {
            cacheHits++;
            System.out.println("CACHE HIT: Employee profile for " + employeeId);
            return cached.employee;
        }
        
        cacheMisses++;
        System.out.println("CACHE MISS: Employee profile for " + employeeId + " (fetching from server)");
        return null;
    }
    
    /*putEmployee():
     FUNCTION putEmployee(employeeId, employee)
        1. CREATE cached entry with current timestamp
        2. STORE in cache
        3. LOG cache update*/

    public void putEmployee(String employeeId, Employee employee) {
        employeeCache.put(employeeId, new CachedEmployee(employee));
        System.out.println("CACHED: Employee profile for " + employeeId);
    }
    
    /*invalidateEmployee():
    PURPOSE: Remove employee from cache when data is updated
    FUNCTION invalidateEmployee(employeeId)
        1. REMOVE from cache
        2. LOG invalidation
     WHEN TO USE:
     After updating employee profile
     After any modification to employee data
     Forces fresh fetch from server on next access*/

    public void invalidateEmployee(String employeeId) {
        employeeCache.remove(employeeId);
        System.out.println("INVALIDATED: Employee cache for " + employeeId);
    }
    
    // FAMILY MEMBERS CACHING
    public List<FamilyMember> getFamilyMembers(String employeeId) {
        CachedFamilyList cached = familyCache.get(employeeId);
        
        if (cached != null && !cached.isExpired()) {
            cacheHits++;
            System.out.println("CACHE HIT: Family members for " + employeeId);
            return cached.familyMembers;
        }
        
        cacheMisses++;
        System.out.println("CACHE MISS: Family members for " + employeeId + " (fetching from server)");
        return null;
    }
    
    public void putFamilyMembers(String employeeId, List<FamilyMember> familyMembers) {
        familyCache.put(employeeId, new CachedFamilyList(familyMembers));
        System.out.println("CACHED: Family members for " + employeeId);
    }
    
    public void invalidateFamilyMembers(String employeeId) {
        familyCache.remove(employeeId);
        System.out.println("INVALIDATED: Family cache for " + employeeId);
    }
    

    // LEAVE BALANCE CACHING
    public List<LeaveBalance> getLeaveBalance(String employeeId, int year) {
        String key = employeeId + "_" + year;
        CachedLeaveBalance cached = leaveBalanceCache.get(key);
        
        if (cached != null && !cached.isExpired()) {
            cacheHits++;
            System.out.println("CACHE HIT: Leave balance for " + employeeId + " (" + year + ")");
            return cached.balances;
        }
        
        cacheMisses++;
        System.out.println("CACHE MISS: Leave balance for " + employeeId + " (fetching from server)");
        return null;
    }
    
    public void putLeaveBalance(String employeeId, int year, List<LeaveBalance> balances) {
        String key = employeeId + "_" + year;
        leaveBalanceCache.put(key, new CachedLeaveBalance(balances));
        System.out.println("CACHED: Leave balance for " + employeeId + " (" + year + ")");
    }
    
    public void invalidateLeaveBalance(String employeeId, int year) {
        String key = employeeId + "_" + year;
        leaveBalanceCache.remove(key);
        System.out.println("INVALIDATED: Leave balance cache for " + employeeId);
    }
    

    // LEAVE APPLICATIONS CACHING
    public List<LeaveApplication> getLeaveApplications(String employeeId) {
        CachedLeaveApplications cached = leaveApplicationsCache.get(employeeId);
        
        if (cached != null && !cached.isExpired()) {
            cacheHits++;
            System.out.println("CACHE HIT: Leave applications for " + employeeId);
            return cached.applications;
        }
        
        cacheMisses++;
        System.out.println("CACHE MISS: Leave applications for " + employeeId + " (fetching from server)");
        return null;
    }
    
    public void putLeaveApplications(String employeeId, List<LeaveApplication> applications) {
        leaveApplicationsCache.put(employeeId, new CachedLeaveApplications(applications));
        System.out.println("CACHED: Leave applications for " + employeeId);
    }
    
    public void invalidateLeaveApplications(String employeeId) {
        leaveApplicationsCache.remove(employeeId);
        System.out.println("INVALIDATED: Leave applications cache for " + employeeId);
    }
      
    /*CACHE MANAGEMENT  
     clearAll():
     PURPOSE: Clear entire cache (e.g., on logout)*/

    public void clearAll() {
        employeeCache.clear();
        familyCache.clear();
        leaveBalanceCache.clear();
        leaveApplicationsCache.clear();
        System.out.println("⟳ CACHE CLEARED: All caches invalidated");
    }
    
    /*getStatistics():
    PURPOSE: Display cache performance metrics*/

    public void printStatistics() {
        int total = cacheHits + cacheMisses;
        double hitRate = total > 0 ? (cacheHits * 100.0 / total) : 0;
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CACHE STATISTICS");
        System.out.println("=".repeat(50));
        System.out.println("Cache Hits:   " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate:     " + String.format("%.2f", hitRate) + "%");
        System.out.println("=".repeat(50) + "\n");
    }
    
    /*INNER CLASSES - CACHED DATA WRAPPERS
     CachedEmployee:
     PURPOSE: Wrapper for Employee with timestamp
     STRUCTURE:
     employee: The actual data
     timestamp: When it was cached
     isExpired(): Check if cache is still valid */
     
    private static class CachedEmployee {
        Employee employee;
        long timestamp;
        
        CachedEmployee(Employee employee) {
            this.employee = employee;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT;
        }
    }
    
    private static class CachedFamilyList {
        List<FamilyMember> familyMembers;
        long timestamp;
        
        CachedFamilyList(List<FamilyMember> familyMembers) {
            this.familyMembers = familyMembers;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT;
        }
    }
    
    private static class CachedLeaveBalance {
        List<LeaveBalance> balances;
        long timestamp;
        
        CachedLeaveBalance(List<LeaveBalance> balances) {
            this.balances = balances;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT;
        }
    }
    
    private static class CachedLeaveApplications {
        List<LeaveApplication> applications;
        long timestamp;
        
        CachedLeaveApplications(List<LeaveApplication> applications) {
            this.applications = applications;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TIMEOUT;
        }
    }
}
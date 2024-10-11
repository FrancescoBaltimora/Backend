package com.triptravel.backend.users.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.triptravel.backend.users.models.IpAddress;
import com.triptravel.backend.users.repository.IpAddressesRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimiterFilter implements Filter {
	
	@Autowired
	private IpAddressesRepository ipRepo;
	
	private static final Logger errLog = (Logger) LoggerFactory.getLogger("errLog");
	private static final Logger infoLog = (Logger) LoggerFactory.getLogger("infLog");
	
    private final Map<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();
    private final long REQUEST_LIMIT = 20;
    private final long TIME_WINDOW_MS = 5 * 60 * 1000; // 5 minuti
    private final long BLOCK_TIME_MS = 1 * 60 * 1000; // Blocco di 5 minuti

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr();
        String path = httpRequest.getRequestURI();  // Ottieni il path della richiesta

        // Se la richiesta riguarda "/auth/**", bypassa il rate limiting
        if (path.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;  // Esci dal metodo
        }

        infoLog.info("Incoming request from ip: " + ip);
        long currentTime = System.currentTimeMillis();

        List<Long> timestamps = requestTimestamps.getOrDefault(ip, new ArrayList<>());

        // Rimuovi le richieste più vecchie della finestra temporale
        timestamps.removeIf(timestamp -> (currentTime - timestamp) > TIME_WINDOW_MS);
        infoLog.info("Removed old requests from ip: " + ip + " requests: " + timestamps.size());
        timestamps.add(currentTime);
        
        IpAddress ipAddress = ipRepo.findByIpAddress(ip);
        Long lastRequestTime = timestamps.getLast();
        
        if (ipAddress != null && ipAddress.isBlocked() == true) {
            lastRequestTime = ipAddress.getLastRequest();
            if ((currentTime - lastRequestTime) <= BLOCK_TIME_MS) {
                errLog.error("ip: " + ip + " is blocked!");
                // Blocca la richiesta se il limite è stato superato
                httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            } else {
                timestamps.clear(); // Rimuovi il blocco se scaduto
                ipAddress.setLastRequest(null); 
                ipAddress.setBlocked(false);
                ipRepo.save(ipAddress);
                infoLog.error("ip: " + ip + " unlocked!");
            }
        } 
        
        if (timestamps.size() >= REQUEST_LIMIT) {
            if (ipAddress != null) {
                ipAddress.setBlocked(true);
            } else {
                ipAddress = new IpAddress(ip, true, lastRequestTime);
            }
            ipRepo.save(ipAddress);
            errLog.error("too many requests from ip: " + ip + " requests: " + timestamps.size());
            // Blocca la richiesta se il limite è stato superato
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        requestTimestamps.put(ip, timestamps);
        chain.doFilter(request, response);
    }
}

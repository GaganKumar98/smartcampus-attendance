package smartcampus.attendance.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Reads the {@code X-Tenant-Id} HTTP header from every incoming request,
 * stores it in {@link TenantContextHolder}, and clears it after the request
 * completes to prevent ThreadLocal leaks.
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String tenantHeader = request.getHeader(TENANT_HEADER);
        try {
            if (tenantHeader != null && !tenantHeader.isBlank()) {
                TenantContextHolder.setTenantId(UUID.fromString(tenantHeader));
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}

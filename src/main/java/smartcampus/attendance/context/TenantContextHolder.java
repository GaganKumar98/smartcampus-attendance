package smartcampus.attendance.context;

import java.util.UUID;

/**
 * Holds the current tenant ID in a ThreadLocal for the duration of a request.
 * Must be cleared after request processing to prevent memory leaks.
 */
public final class TenantContextHolder {

    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();

    private TenantContextHolder() {
        // utility class
    }

    public static void setTenantId(UUID tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static UUID getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}

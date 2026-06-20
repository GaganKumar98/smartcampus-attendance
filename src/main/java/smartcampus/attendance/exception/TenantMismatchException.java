package smartcampus.attendance.exception;

public class TenantMismatchException extends RuntimeException {

    public TenantMismatchException(String message) {
        super(message);
    }

    public TenantMismatchException() {
        super("JWT tenant claim does not match the X-Tenant-Id header");
    }
}

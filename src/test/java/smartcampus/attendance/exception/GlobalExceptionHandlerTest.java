package smartcampus.attendance.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for {@link GlobalExceptionHandler}.
 * Instantiates the handler directly — no Spring context, no MockMvc.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/test/path");
    }

    @Test
    void handleEntityNotFound_returns404WithMessage() {
        EntityNotFoundException ex = new EntityNotFoundException("Record not found with id: 1");
        ResponseEntity<Map<String, Object>> response = handler.handleEntityNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertErrorBody(response.getBody(), 404, "Record not found with id: 1", "/test/path");
    }

    @Test
    void handleEntityNotFound_twoArgConstructor_formatsMessage() {
        EntityNotFoundException ex = new EntityNotFoundException("AttendanceRecord", 5L);
        ResponseEntity<Map<String, Object>> response = handler.handleEntityNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat((String) response.getBody().get("message")).contains("AttendanceRecord").contains("5");
    }

    @Test
    void handleDuplicateResource_returns409WithMessage() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate attendance record");
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResource(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertErrorBody(response.getBody(), 409, "Duplicate attendance record", "/test/path");
    }

    @Test
    void handleDuplicateResource_threeArgConstructor_formatsMessage() {
        DuplicateResourceException ex = new DuplicateResourceException("AttendanceRecord", "date", "2024-01-01");
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResource(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat((String) response.getBody().get("message"))
                .contains("AttendanceRecord").contains("date").contains("2024-01-01");
    }

    @Test
    void handleInvalidStateTransition_returns422WithMessage() {
        InvalidStateTransitionException ex = new InvalidStateTransitionException("Invalid transition");
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidStateTransition(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertErrorBody(response.getBody(), 422, "Invalid transition", "/test/path");
    }

    @Test
    void handleInvalidStateTransition_threeArgConstructor_formatsMessage() {
        InvalidStateTransitionException ex = new InvalidStateTransitionException("Exam", "DRAFT", "PUBLISHED");
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidStateTransition(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat((String) response.getBody().get("message"))
                .contains("Exam").contains("DRAFT").contains("PUBLISHED");
    }

    @Test
    void handleValidation_returns400WithMessage() {
        ValidationException ex = new ValidationException("Marks cannot exceed max marks");
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertErrorBody(response.getBody(), 400, "Marks cannot exceed max marks", "/test/path");
    }

    @Test
    void handleAccessDenied_returns403WithMessage() {
        AccessDeniedException ex = new AccessDeniedException("Access is denied");
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertErrorBody(response.getBody(), 403, "Access is denied", "/test/path");
    }

    @Test
    void handleTenantMismatch_returns403() {
        TenantMismatchException ex = new TenantMismatchException();
        ResponseEntity<Map<String, Object>> response = handler.handleTenantMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat((int) response.getBody().get("status")).isEqualTo(403);
        assertThat(response.getBody()).containsKey("message");
    }

    @Test
    void handleTenantMismatch_customMessage_returns403() {
        TenantMismatchException ex = new TenantMismatchException("Tenant mismatch detected");
        ResponseEntity<Map<String, Object>> response = handler.handleTenantMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat((String) response.getBody().get("message")).isEqualTo("Tenant mismatch detected");
    }

    @Test
    void handleAll_returns500WithGenericMessage() {
        RuntimeException ex = new RuntimeException("Unexpected failure");
        ResponseEntity<Map<String, Object>> response = handler.handleAll(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat((int) response.getBody().get("status")).isEqualTo(500);
        assertThat((String) response.getBody().get("message")).isEqualTo("An unexpected error occurred");
    }

    @Test
    void responseBody_containsTimestampAndErrorFields() {
        EntityNotFoundException ex = new EntityNotFoundException("not found");
        Map<String, Object> body = handler.handleEntityNotFound(ex, request).getBody();
        assertThat(body).containsKey("timestamp");
        assertThat(body).containsKey("error");
        assertThat((String) body.get("timestamp")).isNotBlank();
        assertThat((String) body.get("error")).isNotBlank();
    }

    @Test
    void responseBody_pathReflectsRequestUri() {
        request.setRequestURI("/api/attendance/records/99");
        ValidationException ex = new ValidationException("invalid");
        Map<String, Object> body = handler.handleValidation(ex, request).getBody();
        assertThat(body.get("path")).isEqualTo("/api/attendance/records/99");
    }

    private void assertErrorBody(Map<String, Object> body, int expectedStatus,
                                  String expectedMessage, String expectedPath) {
        assertThat(body).isNotNull();
        assertThat((int) body.get("status")).isEqualTo(expectedStatus);
        assertThat((String) body.get("message")).isEqualTo(expectedMessage);
        assertThat(body.get("path")).isEqualTo(expectedPath);
        assertThat(body).containsKey("timestamp");
        assertThat(body).containsKey("error");
    }
}

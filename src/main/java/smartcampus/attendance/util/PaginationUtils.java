package smartcampus.attendance.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building {@link Pageable} instances and standardised
 * page-response wrappers.
 */
public final class PaginationUtils {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 500;

    private PaginationUtils() {
        // utility class
    }

    public static Pageable toPageable(Integer page, Integer size, String sortBy, String direction) {
        int p = (page != null && page >= 0) ? page : DEFAULT_PAGE;
        int s = (size != null && size > 0) ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;

        if (sortBy != null && !sortBy.isBlank()) {
            Sort.Direction dir = "desc".equalsIgnoreCase(direction)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return PageRequest.of(p, s, Sort.by(dir, sortBy));
        }
        return PageRequest.of(p, s);
    }

    public static Pageable toPageable(Integer page, Integer size) {
        return toPageable(page, size, null, null);
    }

    public static <T> Map<String, Object> toPageResponse(Page<T> pageResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("page", pageResult.getNumber());
        response.put("size", pageResult.getSize());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("last", pageResult.isLast());
        return response;
    }

    public static <T> Map<String, Object> toPageResponse(
            List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("last", (page + 1) >= totalPages);
        return response;
    }
}

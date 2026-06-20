package smartcampus.attendance.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for {@link TenantContextHolder}.
 * No Spring context required — tests the ThreadLocal lifecycle directly.
 */
class TenantContextHolderTest {

    @AfterEach
    void cleanup() {
        TenantContextHolder.clear();
    }

    @Test
    void set_thenGet_returnsSameTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContextHolder.setTenantId(tenantId);
        assertThat(TenantContextHolder.getTenantId()).isEqualTo(tenantId);
    }

    @Test
    void get_withoutSet_returnsNull() {
        assertThat(TenantContextHolder.getTenantId()).isNull();
    }

    @Test
    void clear_afterSet_makesGetReturnNull() {
        TenantContextHolder.setTenantId(UUID.randomUUID());
        TenantContextHolder.clear();
        assertThat(TenantContextHolder.getTenantId()).isNull();
    }

    @Test
    void threadIsolation_eachThreadSeesOwnValue() throws InterruptedException {
        UUID tenantA = UUID.randomUUID();
        UUID tenantB = UUID.randomUUID();

        CountDownLatch bothSet = new CountDownLatch(2);
        CountDownLatch bothRead = new CountDownLatch(2);

        AtomicReference<UUID> seenByA = new AtomicReference<>();
        AtomicReference<UUID> seenByB = new AtomicReference<>();

        Thread threadA = new Thread(() -> {
            TenantContextHolder.setTenantId(tenantA);
            bothSet.countDown();
            try { bothSet.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            seenByA.set(TenantContextHolder.getTenantId());
            bothRead.countDown();
            TenantContextHolder.clear();
        });

        Thread threadB = new Thread(() -> {
            TenantContextHolder.setTenantId(tenantB);
            bothSet.countDown();
            try { bothSet.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            seenByB.set(TenantContextHolder.getTenantId());
            bothRead.countDown();
            TenantContextHolder.clear();
        });

        threadA.start();
        threadB.start();
        bothRead.await();

        assertThat(seenByA.get()).isEqualTo(tenantA);
        assertThat(seenByB.get()).isEqualTo(tenantB);
    }
}

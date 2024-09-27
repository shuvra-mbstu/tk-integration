package com.tk.integration.common.processor;

import com.tk.integration.common.exception.TkIntegrationServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ResultProcessorTest {

    private ResultProcessor resultProcessor;

    @BeforeEach
    public void setup() {
        resultProcessor = new ResultProcessor();
    }

    @Test
    public void testStoreAndRetrieveResult() {
        // Store a result and retrieve it
        String processId = "process123";
        String result = "Completed";

        resultProcessor.storeResult(processId, result);
        String retrievedResult = resultProcessor.retrieveResult(processId);

        assertNotNull(retrievedResult);
        assertEquals(result, retrievedResult);
    }

    @Test
    public void testRetrieveNonExistingResult() {
        // Try to retrieve a non-existing result
        String processId = "nonExistentProcess";

        assertNull(resultProcessor.retrieveResult(processId));
    }

    @Test
    public void testStoreResultWithNullProcessId() {
        // Expect IllegalArgumentException for null process ID
        assertThrows(TkIntegrationServerException.class, () -> resultProcessor.storeResult(null, "Some result"));
    }

    @Test
    public void testStoreResultWithEmptyProcessId() {
        // Expect IllegalArgumentException for empty process ID
        assertThrows(TkIntegrationServerException.class, () -> resultProcessor.storeResult("", "Some result"));
    }

    @Test
    public void testRetrieveResultWithNullProcessId() {
        // Expect IllegalArgumentException for null process ID during retrieval
        assertThrows(TkIntegrationServerException.class, () -> resultProcessor.retrieveResult(null));
    }
}

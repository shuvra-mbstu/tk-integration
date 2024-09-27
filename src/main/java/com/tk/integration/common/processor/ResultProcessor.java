package com.tk.integration.common.processor;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.exception.TkIntegrationServerException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResultProcessor {

    // Thread-safe map to store results by process ID
    private final Map<String, String> resultStore = new ConcurrentHashMap<>();

    /**
     * Stores the result of a process using its unique process ID.
     *
     * @param processId The unique identifier of the process
     * @param result    The result or status of the process
     */
    public void storeResult(String processId, String result) {
        if (processId == null || processId.isEmpty()) {
            throw TkIntegrationServerException.dataSaveException(ApplicationConstant.PROCESS_ID_NOT_NULL);
        }
        if (result == null) {
            throw TkIntegrationServerException.dataSaveException(ApplicationConstant.RESULT_NOT_NULL);
        }
        resultStore.put(processId, result); // Safely store the result in the concurrent map
    }

    /**
     * Retrieves the result of a process using its unique process ID.
     *
     * @param processId The unique identifier of the process
     * @return The result of the process, or null if no result exists for the given process ID
     */
    public String retrieveResult(String processId) {
        if (processId == null || processId.isEmpty()) {
            throw TkIntegrationServerException.dataSaveException(ApplicationConstant.PROCESS_ID_NOT_NULL);
        }
        return resultStore.get(processId); // Safely retrieve the result
    }
}

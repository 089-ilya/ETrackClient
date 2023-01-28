package org.example.job;

import lombok.extern.slf4j.Slf4j;
import org.example.IsLaptopOnChargeExecutor;
import org.example.RequestExecutor;
import org.example.dto.IntervalCheckDTO;
import org.example.exception.ExecutionException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import java.util.ArrayList;
import java.util.List;

import static org.example.util.Url.buildUrl;

@Slf4j
public class SendCurrentDataJob implements Job {

    public static List<IntervalCheckDTO> INTERVAL_CHECK_BUFFER = new ArrayList<>();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        boolean internetAvailable = isInternetAvailable();
        if (!INTERVAL_CHECK_BUFFER.isEmpty() && internetAvailable) {
            try {
                RequestExecutor.executeWithTimeout(() -> RequestExecutor.executePost(buildUrl("interval/data/list"), INTERVAL_CHECK_BUFFER), 5, 2);
                INTERVAL_CHECK_BUFFER.clear();
            } catch (ExecutionException e) {
                log.error("Can not send from buffer", e);
                checkChargingAndAddToBuffer();
                return;
            }
        }
        if (internetAvailable) {
            try {
                RequestExecutor.executeWithTimeout(() -> RequestExecutor.executePost(buildUrl("interval/data"), new IntervalCheckDTO(true)), 5, 2);
            } catch (ExecutionException e) {
                log.error("Can not save data to server.", e);
                checkChargingAndAddToBuffer();
            }
            return;
        }
        checkChargingAndAddToBuffer();
    }

    private void checkChargingAndAddToBuffer() {
        if (IsLaptopOnChargeExecutor.isCharging()) {
            INTERVAL_CHECK_BUFFER.add(new IntervalCheckDTO(true));
        } else {
            INTERVAL_CHECK_BUFFER.add(new IntervalCheckDTO(false));
        }
    }

    private boolean isInternetAvailable() {
        try {
            RequestExecutor.executeWithTimeout(
                    () -> RequestExecutor.executeGet("https://www.google.com"), 2, 1);
        } catch (ExecutionException e) {
            log.warn("Can not ping google.", e);
            return false;
        }
        return true;
    }
}

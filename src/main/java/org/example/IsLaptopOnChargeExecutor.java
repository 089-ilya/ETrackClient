package org.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.CmdException;

import java.nio.charset.StandardCharsets;

@Slf4j
public class IsLaptopOnChargeExecutor {

    @SneakyThrows
    public static boolean isCharging() {
        Process exec = Runtime.getRuntime().exec("WMIC /NameSpace:\\\\root\\WMI Path BatteryStatus Get PowerOnline");
        String result = new String(exec.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        if (result.contains("TRUE")) {
            return true;
        }
        if (result.contains("FALSE")) {
            return false;
        }
        log.error("Can not find if laptop is on charge is charging result: {}", result);
        throw new CmdException();
    }

}

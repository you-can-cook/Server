package org.youcancook.gobong.global.util.clock;

import java.time.LocalDateTime;
import java.util.Date;

public interface ClockService {
    LocalDateTime getCurrentDateTime();

    Date getCurrentDate();
}

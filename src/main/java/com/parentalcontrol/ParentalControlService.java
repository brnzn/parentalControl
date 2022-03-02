package com.parentalcontrol;

import com.parentalcontrol.exception.UnknownTitleException;

public interface ParentalControlService {
    boolean isAuthorised(ParentalControlLevel level, String titleId) throws UnknownTitleException;
}

package com.drexelsp.blunote.blunote;

import com.drexelsp.blunote.BlunoteMessages.*;

/**
 * Created by scantwell on 2/16/2016.
 */
public interface MessageHandler {
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message);
}

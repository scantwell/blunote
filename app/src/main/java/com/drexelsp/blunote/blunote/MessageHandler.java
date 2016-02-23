package com.drexelsp.blunote.blunote;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.WrapperMessage;

/**
 * Created by scantwell on 2/16/2016.
 * Used to handle messages from the network.
 */
public interface MessageHandler {
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message);
}

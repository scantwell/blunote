package com.drexelsp.blunote.blunote;

import android.util.Log;

import com.drexelsp.blunote.BlunoteMessages.*;

/**
 * Created by scantwell on 2/16/2016.
 */
public class VoteEngine implements MessageHandler {
    private final String TAG = "VoteEngine";
    @Override
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message) {
        if (WrapperMessage.Type.MULTI_ANSWER.equals(message.getType()))
        {
        }
        else if (WrapperMessage.Type.RECOMMEND.equals(message.getType()))
        {
        }
        else if (WrapperMessage.Type.SINGLE_ANSWER.equals(message.getType()))
        {
        }
        else if (WrapperMessage.Type.VOTE.equals(message.getType()))
        {
        }
        else
        {
            Log.v(TAG, "Undefined message.");
        }
        return false;
    }

    private void processMessage(DeliveryInfo dinfo, Vote vote)
    {
    }

    private void processMessage(DeliveryInfo dinfo, SingleAnswer answer)
    {
    }

    private void processMessage(DeliveryInfo dinfo, MultiAnswer answer)
    {
    }

    private void processMessage(DeliveryInfo dinfo, Recommendation recommendation)
    {
    }
}

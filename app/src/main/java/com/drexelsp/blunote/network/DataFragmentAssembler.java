package com.drexelsp.blunote.network;

import com.drexelsp.blunote.network.NetworkMessages.DeliveryInfo;
import com.drexelsp.blunote.network.NetworkMessages.DataFragment;
import com.drexelsp.blunote.network.ClientService.Direction;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stephencantwell on 5/2/16.
 */
public class DataFragmentAssembler {

    private class Fragment
    {
        public String address;
        public byte[] data;
        public HashMap<Long, DataFragment> fragments;
        public long id;
        public long target;


        public Fragment()
        {
            fragments = new HashMap<>();
            this.target = 1;
            this.data = ;
        }

        public boolean addFragment(DeliveryInfo dinfo, DataFragment frag)
        {
            if (fragments.size() == 0)
            {
                this.address = dinfo.getAddress();
                this.id = frag.getId();
            }
            if (dinfo.getAddress() == address && frag.getId() == id)
            {
                fragments.put(frag.getId(), frag);
                while(fragments.containsKey(target))
                {
                    byte
                }
                if (frag.getFragmentId() == target;)
                while
                return true;
            }
            return false;
        }

    }
    private ClientService service;
    private ArrayList<Fragment> fragments;

    public DataFragmentAssembler(ClientService service)
    {
        this.fragments = new ArrayList<>();
        this.service = service;
    }

    public void onReceive(Direction dir, DeliveryInfo dinfo, DataFragment frag)
    {
        if(frag.getTotalFragments() == 1)
        {
            service.onReceive(dir, dinfo, frag.getData().toByteArray());
        }
        else
        {
            for (Fragment f : fragments)
            {
                if (f.addFragment(dinfo, frag))
                {
                    return;
                }
            }
            Fragment f = new Fragment();
            f.addFragment(dinfo, frag);
            fragments.add(f);
        }
    }
}

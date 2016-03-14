package com.drexelsp.blunote.blunote;


import android.net.Uri;

import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Created by scantwell on 3/12/2016.
 */
public class SongAssembler {
    private long target;
    private int total;
    private FileOutputStream fos;
    private HashMap<Long, SongFragment> frags;
    private Set<Long> blackList;
    private Uri uri;

    public SongAssembler(File file) {
        try {
            this.fos = new FileOutputStream(file);

        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
        this.uri = Uri.fromFile(file);
        this.target = 1;
        this.frags = new HashMap<>();
        this.blackList = new HashSet<>();
    }

    public Uri getURI()
    {
        return this.uri;
    }

    public void addFragment(SongFragment frag) {
        long id = frag.getFragmentId();
        if (this.blackList.contains(id)) {
            return;
        }
        this.blackList.add(id);
        total = frag.getTotalFragments();
        if (id == target) {
            writeFragment(frag.getFragment());
            target++;
        } else {
            frags.put(id, frag);
        }

        while (frags.containsKey(target)) {
            frag = frags.remove(target);
            writeFragment(frag.getFragment());
            target++;
        }
        if (isCompleted()) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCompleted() {
        return target > total;
    }

    public void writeFragment(ByteString frag) {
        try {
            byte[] data = new byte[frag.size()];
            frag.copyTo(data, 0);
            fos.write(data);
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }
}

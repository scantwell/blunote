package com.drexelsp.blunote.blunote;


import android.net.Uri;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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

    public synchronized void addFragment(SongFragment frag) {
        long id = frag.getFragmentId();
        Log.v("Song Assembler", String.format("Writing Fragment: %d", id));
        Log.v("Song Assembler", String.format("Frag Size: %d", frag.toByteArray().length));
        if (this.blackList.contains(id)) {
            return;
        }
        this.blackList.add(id);
        frags.put(id, frag);
        total = frag.getTotalFragments();
        while (frags.containsKey(target)) {
            frag = frags.remove(target);
            writeFragment(frag.getFragment());
            target++;
        }
        if (isCompleted()) {
            try {
                Log.v("Song Assembler", String.format("%d", fos.getChannel().size()));
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

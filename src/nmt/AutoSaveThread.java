package nmt;

import java.io.*;

public class AutoSaveThread extends Thread {

    NMTFrame _parent;
    final int WAIT_SECONDS = 120;
    boolean autoSaveOn = false;

    public AutoSaveThread(NMTFrame parent) {
        _parent = parent;
    }

    @Override
    public void run() {
        autoSaveOn = true;
        while (true) {
            try {
                if (autoSaveOn && _parent.currentFile != null && !_parent.currentFile.equals("")) {
                    System.out.println("Auto saver saved! (" + _parent.currentFile + ")");
                    NMTParser.saveList(new FileOutputStream(new File(_parent.currentFile)), _parent.studentDB);
                }
                Thread.sleep(WAIT_SECONDS * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //end while
    }

}

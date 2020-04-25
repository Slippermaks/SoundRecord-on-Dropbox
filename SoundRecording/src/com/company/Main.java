package com.company;

// Importing official Dropbox sdk libraries (taken from github)
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class Main {
    public static void main(String[] args) {

        // Access_token from Dropbox.
        String ACCESS_TOKEN = "*** TAKE IT ON DROPBOX.COM ***";

        // Configuration and setting your client on Dropbox.
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // JSR instance.
        JavaSoundRecorder recorder = new JavaSoundRecorder(client);

        // Recording 3 sound files lasting 1 min.
        for (int i = 0; i < 3; i++) {
            recorder.recordSound(60000);
            try {
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

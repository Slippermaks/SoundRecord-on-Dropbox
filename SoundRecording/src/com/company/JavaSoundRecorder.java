package com.company;

import com.dropbox.core.v2.DbxClientV2;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaSoundRecorder
{
    private AudioFileFormat.Type fileType;
    private TargetDataLine line;
    private AudioFormat audioFormat;
    private DbxClientV2 client;

    public JavaSoundRecorder(DbxClientV2 dropboxClient)
    {
        client = dropboxClient;

        fileType = AudioFileFormat.Type.WAVE;

        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        try
        {
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    // Record sound method with name yyyyMMdd_HHmmss.wav (for ex. 20200426_120000).
    public void recordSound(long milliseconds)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = dateFormat.format(new Date()) + ".wav";

        File file = new File(fileName);
        start(file);
        delayFinish(milliseconds, file);
    }

    // Start recording in file .wav format.
    private void start(File file)
    {
        new Thread(() ->
        {
            try
            {
                line.open(audioFormat);
                line.start();
                AudioInputStream ais = new AudioInputStream(line);
                AudioSystem.write(ais, fileType, file);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();

    }

    // Delayed finish so that the sound has time to record.
    private void delayFinish(long delayTime, File file)
    {
        new Thread(() ->
        {
            try
            {
                Thread.sleep(delayTime);
                line.stop();
                line.close();

                // Uploading file to DropBox
                InputStream in = new FileInputStream(file.getName());
                client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
                in.close();

                // Deleting file from device
                file.delete();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }

}
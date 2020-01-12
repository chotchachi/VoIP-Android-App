package com.example.voip_app;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioCall {
    private static final int SAMPLE_RATE = 8000;
    private static final int SAMPLE_INTERVAL = 20;
    private static final int SAMPLE_SIZE = 2;
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2;
    private InetAddress address;
    private int port;
    private boolean mic = false;
    private boolean speakers = false;

    public AudioCall(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void startCall() {
        startMic();
        startSpeakers();
    }

    public void endCall() {
        muteMic();
        muteSpeakers();
    }

    private void muteMic() {
        mic = false;
    }

    private void muteSpeakers() {
        speakers = false;
    }

    private void startMic() {
        mic = true;
        Thread thread = new Thread(() -> {
            AudioRecord audioRecorder = new AudioRecord (MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*10);
            int bytes_read = 0;
            int bytes_sent = 0;
            byte[] buf = new byte[BUF_SIZE];
            try {
                DatagramSocket socket = new DatagramSocket();
                audioRecorder.startRecording();
                while(mic) {
                    bytes_read = audioRecorder.read(buf, 0, BUF_SIZE);
                    DatagramPacket packet = new DatagramPacket(buf, bytes_read, address, port);
                    socket.send(packet);
                    bytes_sent += bytes_read;
                    Log.i("xxx", "Send: " + bytes_sent);
                    Thread.sleep(SAMPLE_INTERVAL, 0);
                }
                audioRecorder.stop();
                audioRecorder.release();
                socket.disconnect();
                socket.close();
                mic = false;
            }
            catch(InterruptedException e) {
                Log.e("xxx", "InterruptedException: " + e.toString());
                mic = false;
            }
            catch(SocketException e) {
                Log.e("xxx", "SocketException: " + e.toString());
                mic = false;
            }
            catch(UnknownHostException e) {
                Log.e("xxx", "UnknownHostException: " + e.toString());
                mic = false;
            }
            catch(IOException e) {
                Log.e("xxx", "IOException: " + e.toString());
                mic = false;
            }
        });
        thread.start();
    }

    private void startSpeakers() {
        if(!speakers) {
            speakers = true;
            Thread receiveThread = new Thread(() -> {
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, AudioTrack.MODE_STREAM);
                track.play();
                try {
                    DatagramSocket socket = new DatagramSocket(port);
                    byte[] buf = new byte[BUF_SIZE];
                    while(speakers) {
                        DatagramPacket packet = new DatagramPacket(buf, BUF_SIZE);
                        socket.receive(packet);
                        Log.i("xxx", "Received: " + packet.getLength());
                        track.write(packet.getData(), 0, BUF_SIZE);
                    }
                    socket.disconnect();
                    socket.close();
                    track.stop();
                    track.flush();
                    track.release();
                    speakers = false;
                }
                catch(SocketException e) {
                    Log.e("xxx", "SocketException: " + e.toString());
                    speakers = false;
                }
                catch(IOException e) {
                    Log.e("xxx", "IOException: " + e.toString());
                    speakers = false;
                }
            });
            receiveThread.start();
        }
    }
}
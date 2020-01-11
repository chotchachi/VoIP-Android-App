package com.example.voip_app;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.voip_app.util.CommonConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioCall {
    private static final int SAMPLE_RATE = 8000; // Hertz
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
    private InetAddress receiveIP; // Address to call
    private boolean mic = false; // Enable mic?
    private boolean speakers = false; // Enable speakers?
    private int receivePort;

    AudioCall(InetAddress address, int port) {
        this.receiveIP = address;
        this.receivePort = port;
    }

    void startCall() {
        startMic();
        startSpeakers();
    }

    void endCall() {
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
            byte[] buf = new byte[512];
            try {
                DatagramSocket socket = new DatagramSocket();
                audioRecorder.startRecording();
                while(mic) {
                    bytes_read = audioRecorder.read(buf, 0, buf.length);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, receiveIP, receivePort);
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
                    DatagramSocket socket = new DatagramSocket(CommonConstants.MY_PORT);
                    byte[] buf = new byte[512];
                    while(speakers) {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        Log.i("xxx", "Receive: " + packet.getLength());
                        track.write(packet.getData(), 0, packet.getData().length);
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

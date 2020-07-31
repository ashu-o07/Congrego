package com.example.hppc.agoravideocalling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

public class call extends AppCompatActivity {
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler mRtcEventHandler;
    private int user=1;
    private boolean mCallEnd;
    private boolean mMuted;
    FloatingActionButton cal,mute,camera;
    SurfaceView surfaceView1;
    FrameLayout container1;
    SurfaceView surfaceView2;
    FrameLayout container2;
    int uid2;int uid3;int uid4;int uid5;int uid6;
    SurfaceView surfaceView3;
    FrameLayout container3;
    SurfaceView surfaceView4;
    FrameLayout container4;
    SurfaceView surfaceView5;
    FrameLayout container5;
    SurfaceView surfaceView6;
    FrameLayout container6;
    String room;

    private RtmChannel mRtmChannel;
    private RtmChannelListener mRtmChannelListener;

    private RtmClient rtmClient;
    String uid;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall);
        uid=(int)(Math.random()*100000)+"";

        room=(String)getIntent().getSerializableExtra("room");

        mRtmChannelListener = new RtmChannelListener() {
            @Override
            public void onAttributesUpdated(	List<RtmChannelAttribute> attributeList){
                //  do nothing
            }
            @Override
            public void onMemberCountUpdated(int count){
                final String s=Integer.toString(count);


            }
            @Override
            public void onMessageReceived(RtmMessage message, RtmChannelMember fromMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember member) {

            }

            @Override
            public void onMemberLeft(RtmChannelMember member) {

            }
        };


        try {
            rtmClient = RtmClient.createInstance(this, getString(R.string.agora_app_id),
                    new RtmClientListener() {
                        @Override
                        public void onConnectionStateChanged(int state, int reason) {

                        }

                        @Override
                        public void onMessageReceived(RtmMessage rtmMessage, String s) {

                        }

                        @Override
                        public void onTokenExpired() {
                            rtmClient.renewToken(null, new ResultCallback<Void>(){
                                @Override
                                public void onSuccess(Void responseInfo) {


                                }
                                @Override
                                public void onFailure(ErrorInfo errorInfo) {


                                }

                            });

                        }


                    });

        } catch (Exception e) {


            throw new RuntimeException("You need to check the RTM initialization process.");
        }



        mRtcEventHandler = new IRtcEngineEventHandler() {


            @Override
            public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
                Log.i("uid video",uid+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(uid);
                    }
                });
            }
            @Override
            public void onUserOffline(final int uid, int reason) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                        onRemoteUserLeft(uid);
                    }
                });
            }


        };
        initializeAgoraEngine();
        cal=(FloatingActionButton)findViewById(R.id.leave);
        mute=(FloatingActionButton)findViewById(R.id.mute);
        camera=(FloatingActionButton)findViewById(R.id.camera);
        cal.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (mCallEnd) {

                    mCallEnd = false;

                } else {
                    endCall();
                    mCallEnd = true;

                }

                showButtons(!mCallEnd);


            }
        });
        mute.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mMuted = !mMuted;
                mRtcEngine.muteLocalAudioStream(mMuted);


            }
        });
        camera.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mRtcEngine.switchCamera();


            }
        });

    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            joinChannel();



            mRtmChannel.getMembers(new ResultCallback<List<RtmChannelMember>>() {
                @Override
                public void onSuccess(List<RtmChannelMember> rtmChannelMembers) {
                    if(rtmChannelMembers.size()>6){
                        Toast.makeText(call.this,"Sorry Lobby full",Toast.LENGTH_SHORT).show();
                        finish();
                    }else setupLocalVideo();

                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
            setupVideoProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    private void setupLocalVideo() {
        mRtcEngine.enableVideo();
        container1 = (FrameLayout) findViewById(R.id.frameLayout11);
         surfaceView1 = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView1.setZOrderMediaOverlay(true);
        container1.addView(surfaceView1);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView1, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void joinChannel() {

        rtmClient.login(null, uid, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {


            }
            @Override
            public void onFailure(ErrorInfo errorInfo) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });



            }
        });


        try {
            mRtmChannel = rtmClient.createChannel(room, mRtmChannelListener);

        } catch (RuntimeException e) {

        }

        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {



            }
        });


        mRtcEngine.joinChannel(null, room, "Extra Optional Data", 0); // if you do not specify the uid, Agora will assign one.





    }

    private void setupRemoteVideo(int uid) {

        //FrameLayout container;
        switch(user){
            case 1:  uid2=uid;container2 = (FrameLayout) findViewById(R.id.frameLayout12);
             surfaceView2 = RtcEngine.CreateRendererView(getBaseContext());
                container2.addView(surfaceView2);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView2, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                surfaceView2.setTag(uid);
                user++;break;
            case 2:  uid3=uid;container3 = (FrameLayout) findViewById(R.id.frameLayout21);
            surfaceView3 = RtcEngine.CreateRendererView(getBaseContext());
                container3.addView(surfaceView3);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView3, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                surfaceView3.setTag(uid);
                user++;break;
            case 3:  uid4=uid;container4 = (FrameLayout) findViewById(R.id.frameLayout22);
            surfaceView4 = RtcEngine.CreateRendererView(getBaseContext());
                container4.addView(surfaceView4);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView4, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                surfaceView4.setTag(uid);
                user++;break;
            case 4: uid5=uid; container5 = (FrameLayout) findViewById(R.id.frameLayout31);
            surfaceView5 = RtcEngine.CreateRendererView(getBaseContext());
                container5.addView(surfaceView5);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView5, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                surfaceView5.setTag(uid);
                user++;break;
            case 5:  uid6=uid;container6 = (FrameLayout) findViewById(R.id.frameLayout32);
            surfaceView6 = RtcEngine.CreateRendererView(getBaseContext());
                container6.addView(surfaceView6);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView6, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
                surfaceView6.setTag(uid);
                user++;break;
            default:return;
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        rtmClient.logout(null);
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();

        mRtmChannel.leave(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });






    }
    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
        finish();
    }
    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mute.setVisibility(visibility);
        camera.setVisibility(visibility);
    }
    private void removeLocalVideo() {
        if ( surfaceView1 != null) {
             container1.removeView(surfaceView1);
        }
        surfaceView1 = null;
    }
    private void onRemoteUserLeft(final int uid){
        if(uid==uid2){ container2.removeView(surfaceView2);surfaceView2= null;}
        if(uid==uid3){ container3.removeView(surfaceView3);surfaceView3= null;}
        if(uid==uid4){ container4.removeView(surfaceView4);surfaceView4= null;}
        if(uid==uid5){ container5.removeView(surfaceView5);surfaceView5= null;}
        if(uid==uid6){ container6.removeView(surfaceView6);surfaceView6= null;}
        user--;

    }
    private void removeRemoteVideo() {
        if (surfaceView2 != null) {
            container2.removeView(surfaceView2);
        }
        surfaceView2= null;
        if (surfaceView3 != null) {
            container3.removeView(surfaceView3);
        }
        surfaceView3= null;
        if (surfaceView4 != null) {
            container4.removeView(surfaceView4);
        }
        surfaceView4= null;
        if (surfaceView5 != null) {
            container5.removeView(surfaceView5);
        }
        surfaceView5= null;
        if (surfaceView6 != null) {
            container6.removeView(surfaceView6);
        }
        surfaceView6= null;
    }

}

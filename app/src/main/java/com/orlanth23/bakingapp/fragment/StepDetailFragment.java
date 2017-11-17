package com.orlanth23.bakingapp.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.orlanth23.bakingapp.Constants;
import com.orlanth23.bakingapp.R;
import com.orlanth23.bakingapp.activity.RecipeListActivity;
import com.orlanth23.bakingapp.broadcast.NetworkReceiver;
import com.orlanth23.bakingapp.domain.Recipe;
import com.orlanth23.bakingapp.domain.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.NOTIFICATION_SERVICE;


public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener, NetworkReceiver.NetworkChangeListener {

    public static final String TAG = StepDetailFragment.class.getName();
    public static final String ARG_STEP_INDEX = "step_index";
    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_PLAYER_POSITION = "player_position";

    private Step mStep;
    private int mStepIndex;
    private Recipe mRecipe;
    private Long mPositionPlayer;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat sMediaSession;
    private boolean mIsConnected;
    private NotificationManager mNotificationManager;
    private NetworkReceiver mNetworkReceiver;
    private PlaybackStateCompat.Builder mStateBuilder;

    @BindView(R.id.player_view)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.step_description)
    TextView mStepDescription;
    @Nullable
    @BindView(R.id.scroll_step_description_land_phone)
    ScrollView mScrollStepDescription;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_previous:
                    if (mStepIndex > 0) {
                        mStepIndex--;
                        mStep = mRecipe.getSteps().get(mStepIndex);
                        mPositionPlayer = C.TIME_UNSET;
                        initializeViews();
                    }
                    return true;
                case R.id.navigation_next:
                    if (mStepIndex < mRecipe.getSteps().size() - 1) {
                        mStepIndex++;
                        mStep = mRecipe.getSteps().get(mStepIndex);
                        mPositionPlayer = C.TIME_UNSET;
                        initializeViews();
                    }
                    return true;
            }
            return false;
        }
    };

    public StepDetailFragment() {
    }

    public static StepDetailFragment newInstance(int stepIndex, Recipe recipe) {

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_STEP_INDEX, stepIndex);
        bundle.putParcelable(ARG_RECIPE, recipe);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(bundle);

        return stepDetailFragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(ARG_STEP_INDEX)) {
                mStepIndex = bundle.getInt(ARG_STEP_INDEX);
            }

            if (bundle.containsKey(ARG_RECIPE)) {
                mRecipe = bundle.getParcelable(ARG_RECIPE);
            }
            mPositionPlayer = C.TIME_UNSET;
            if (bundle.containsKey(ARG_PLAYER_POSITION)) {
                mPositionPlayer = bundle.getLong(ARG_PLAYER_POSITION);
            }

            if (mRecipe != null) {
                mStep = mRecipe.getSteps().get(mStepIndex);
            }
        }
    }

    @Override
    public void OnNetworkEnable() {
        mIsConnected = true;
        if (!TextUtils.isEmpty(mStep.getVideoURL()) && mPlayerView.getVisibility() == View.GONE) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer();

            // Set the video to load
            loadMedia(Uri.parse(mStep.getVideoURL()));

            // Change the current position
            if (mPositionPlayer != C.TIME_UNSET) {
                mExoPlayer.seekTo(mPositionPlayer);
            }
        }
    }

    @Override
    public void OnNetworkDisable() {
        mIsConnected = false;
        if (mPlayerView.getVisibility() == View.VISIBLE) {
            mPlayerView.setVisibility(View.GONE);
            releaseNotification();
            releasePlayer();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mNetworkReceiver = new NetworkReceiver(this);
        context.registerReceiver(mNetworkReceiver, NetworkReceiver.CONNECTIVITY_CHANGE_INTENT_FILTER);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readBundle(getArguments());
        initializeMediaSession();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get back the previous data
        if (savedInstanceState != null) {
            readBundle(savedInstanceState);
        }

        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        ButterKnife.bind(this, rootView);

        // Check the internet connection
        mIsConnected = mNetworkReceiver.checkConnection(getActivity());

        initializeViews();

        // BottomNavigation enable navigation between steps
        BottomNavigationView navigation = rootView.findViewById(R.id.navigation);
        if (navigation != null) {
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }

        return rootView;
    }

    public void initializeViews() {
        // Initialize the player with the video URL
        mPlayerView.setVisibility(View.GONE);
        if (mIsConnected) {
            if (!TextUtils.isEmpty(mStep.getVideoURL())) {
                mPlayerView.setVisibility(View.VISIBLE);
                initializePlayer();

                // Set the video to load
                loadMedia(Uri.parse(mStep.getVideoURL()));

                // Change the current position
                if (mPositionPlayer != C.TIME_UNSET) {
                    mExoPlayer.seekTo(mPositionPlayer);
                }
            } else {
                releaseNotificationPlayerMediaSession();
            }
        } else {
            releaseNotificationPlayerMediaSession();
        }

        // We always put the step description
        mStepDescription.setText(mStep.getDescription());

        // In Phone screen, exoplayer is fullscreen, so there is no step description
        if (mScrollStepDescription != null && !TextUtils.isEmpty(mStep.getVideoURL())) {
            mScrollStepDescription.setVisibility(View.GONE);
        }
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);
        }
    }

    private void loadMedia(Uri mediaUri) {
        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(getActivity(), getString(R.string.app_name));
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), Constants.BAKING_APP_CHANNEL_ID);

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = "pause";
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = "play";
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        // Pending Intent that return to the RecipeListActivity
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getActivity(), 0, new Intent(getActivity(), RecipeListActivity.class), 0);

        builder.setContentTitle(mRecipe.getName())
                .setContentText(mStep.getShortDescription())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.mipmap.ic_baking_app)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(playPauseAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(sMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0));


        mNotificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    private void releaseNotification(){
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    private void releasePlayer(){
        if (mExoPlayer != null) {
            mPositionPlayer = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        } else {
            mPositionPlayer = C.TIME_UNSET;
        }
    }

    private void releaseMediaSession(){
        if (sMediaSession != null) {
            sMediaSession.setActive(false);
            sMediaSession.release();
        }
    }

    private void releaseNotificationPlayerMediaSession() {
        releaseNotification();
        releasePlayer();
        releaseMediaSession();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseNotificationPlayerMediaSession();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        sMediaSession.setPlaybackState(mStateBuilder.build());
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (isLoading) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, error.getMessage(), error);
        Snackbar.make(mPlayerView, R.string.error_on_exoplayer, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_RECIPE, mRecipe);
        outState.putInt(ARG_STEP_INDEX, mStepIndex);
        if (mPositionPlayer != C.TIME_UNSET) {
            outState.putLong(ARG_PLAYER_POSITION, mPositionPlayer);
        }
    }

    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        sMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        sMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        sMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        sMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        sMediaSession.setCallback(new StepDetailFragment.MySessionCallback());

        // Start the Media Session since the activity is active.
        sMediaSession.setActive(true);
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(sMediaSession, intent);
        }
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
    }
}

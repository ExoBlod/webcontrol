package com.webcontrol.android.ui.videocall

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.twilio.video.*
import com.webcontrol.android.R
import com.webcontrol.android.data.network.ApiResponseAnglo
import com.webcontrol.android.util.RestClient
import com.webcontrol.android.util.SharedUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_videocall.*
import kotlinx.android.synthetic.main.content_video.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class VideoCallActivity : AppCompatActivity() {
    companion object {
        const val PREF_AUDIO_CODEC = "audio_codec"
        const val PREF_AUDIO_CODEC_DEFAULT = OpusCodec.NAME
        const val PREF_VIDEO_CODEC = "video_codec"
        const val PREF_VIDEO_CODEC_DEFAULT = Vp8Codec.NAME
        const val PREF_SENDER_MAX_AUDIO_BITRATE = "sender_max_audio_bitrate"
        const val PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT = "0"
        const val PREF_SENDER_MAX_VIDEO_BITRATE = "sender_max_video_bitrate"
        const val PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT = "0"
        const val PREF_VP8_SIMULCAST = "vp8_simulcast"
        const val PREF_VP8_SIMULCAST_DEFAULT = false
        const val PREF_ENABLE_AUTOMATIC_SUBSCRIPTION = "enable_automatic_subscription"
        const val PREF_ENABLE_AUTOMATIC_SUBCRIPTION_DEFAULT = true

        /*
                 * You must provide a Twilio Access Token to connect to the Video service
                 */
        const val TWILIO_ACCESS_TOKEN = "TWILIO_ACCESS_TOKEN"

    }

    private var USE_TOKEN_SERVER = true

    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
    private val TAG = "VideoCallActivity"

    //private var ACCESS_TOKEN_SERVER = ""

    /*
     * Access token used to connect. This field will be set either from the console generated token
     * or the request to the token server.
     */
    private lateinit var accessToken: String

    /*
     * A Room represents communication between a local participant and one or more participants.
     */
    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null

    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
    private val audioCodec: AudioCodec
        get() {
            val audioCodecName = sharedPreferences.getString(PREF_AUDIO_CODEC, PREF_AUDIO_CODEC_DEFAULT)

            return when (audioCodecName) {
                IsacCodec.NAME -> IsacCodec()
                OpusCodec.NAME -> OpusCodec()
                PcmaCodec.NAME -> PcmaCodec()
                PcmuCodec.NAME -> PcmuCodec()
                G722Codec.NAME -> G722Codec()
                else -> OpusCodec()
            }
        }
    private val videoCodec: VideoCodec
        get() {
            val videoCodecName = sharedPreferences.getString(PREF_VIDEO_CODEC, PREF_VIDEO_CODEC_DEFAULT)

            return when (videoCodecName) {
                Vp8Codec.NAME -> {
                    val simulcast = sharedPreferences.getBoolean(
                            PREF_VP8_SIMULCAST,
                            PREF_VP8_SIMULCAST_DEFAULT)
                    Vp8Codec(simulcast)
                }
                H264Codec.NAME -> H264Codec()
                Vp9Codec.NAME -> Vp9Codec()
                else -> Vp8Codec()
            }
        }

    private val enableAutomaticSubscription: Boolean
        get() {
            return sharedPreferences.getBoolean(PREF_ENABLE_AUTOMATIC_SUBSCRIPTION, PREF_ENABLE_AUTOMATIC_SUBCRIPTION_DEFAULT)
        }

    /*
     * Encoding parameters represent the sender side bandwidth constraints.
     */
    private val encodingParameters: EncodingParameters
        get() {
            val defaultMaxAudioBitrate = PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT
            val defaultMaxVideoBitrate = PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT
            val maxAudioBitrate = Integer.parseInt(
                    sharedPreferences.getString(PREF_SENDER_MAX_AUDIO_BITRATE,
                            defaultMaxAudioBitrate) ?: defaultMaxAudioBitrate
            )
            val maxVideoBitrate = Integer.parseInt(
                    sharedPreferences.getString(PREF_SENDER_MAX_VIDEO_BITRATE,
                            defaultMaxVideoBitrate) ?: defaultMaxVideoBitrate
            )

            return EncodingParameters(maxAudioBitrate, maxVideoBitrate)
        }

    /*
     * Room events listener
     */
    private val roomListener = object : Room.Listener {
        override fun onConnected(room: Room) {
            localParticipant = room.localParticipant
            videoStatusTextView.text = "Conectado a ${room.name}"
            title = room.name
            Log.e(TAG, "localParticipant is null? $localParticipant")
            // Only one participant is supported
            room.remoteParticipants?.firstOrNull()?.let { addRemoteParticipant(it) }
        }

        override fun onReconnected(room: Room) {
            videoStatusTextView.text = "Conectado a ${room.name}"
            reconnectingProgressBar.visibility = View.GONE;
            initializeUI()
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            videoStatusTextView.text = "Reconectando a ${room.name}"
            reconnectingProgressBar.visibility = View.VISIBLE;
        }

        override fun onConnectFailure(room: Room, e: TwilioException) {
            videoStatusTextView.text = "Fallo de conexión"
            configureAudio(false)
            initializeUI()
            reconnectUI()
        }

        override fun onDisconnected(room: Room, e: TwilioException?) {
            localParticipant = null
            videoStatusTextView.text = "Desconectado de ${room.name}"
            reconnectingProgressBar.visibility = View.GONE;
            this@VideoCallActivity.room = null
            // Only reinitialize the UI if disconnect was not called from onDestroy()
            if (!disconnectedFromOnDestroy) {
                configureAudio(false)
                initializeUI()
                reconnectUI()
                moveLocalVideoToPrimaryView()
            }
        }

        override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
            addRemoteParticipant(participant)
        }

        override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
            removeRemoteParticipant(participant)
        }

        override fun onRecordingStarted(room: Room) {
            /*
             * Indicates when media shared to a Room is being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
            Log.d(TAG, "onRecordingStarted")
        }

        override fun onRecordingStopped(room: Room) {
            /*
             * Indicates when media shared to a Room is no longer being recorded. Note that
             * recording is only available in our Group Rooms developer preview.
             */
            Log.d(TAG, "onRecordingStopped")
        }
    }

    /*
     * RemoteParticipant events listener
     */
    private val participantListener = object : RemoteParticipant.Listener {
        override fun onAudioTrackPublished(remoteParticipant: RemoteParticipant,
                                           remoteAudioTrackPublication: RemoteAudioTrackPublication) {
            Log.i(TAG, "onAudioTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteAudioTrackPublication.trackName}]")
            videoStatusTextView.text = "Canal de audio conectado"
        }

        override fun onAudioTrackUnpublished(remoteParticipant: RemoteParticipant,
                                             remoteAudioTrackPublication: RemoteAudioTrackPublication) {
            Log.i(TAG, "onAudioTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteAudioTrackPublication.trackName}]")
            videoStatusTextView.text = "Canal de audio desconectado"
        }

        override fun onDataTrackPublished(remoteParticipant: RemoteParticipant,
                                          remoteDataTrackPublication: RemoteDataTrackPublication) {
            Log.i(TAG, "onDataTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteDataTrackPublication.trackName}]")
            videoStatusTextView.text = "Canal de datos establecido"
        }

        override fun onDataTrackUnpublished(remoteParticipant: RemoteParticipant,
                                            remoteDataTrackPublication: RemoteDataTrackPublication) {
            Log.i(TAG, "onDataTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteDataTrackPublication.trackName}]")
            videoStatusTextView.text = "Canal de datos cerrado"
        }

        override fun onVideoTrackPublished(remoteParticipant: RemoteParticipant,
                                           remoteVideoTrackPublication: RemoteVideoTrackPublication) {
            Log.i(TAG, "onVideoTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteVideoTrackPublication.trackName}]")
            videoStatusTextView.text = "Canal de video establecido"
        }

        override fun onVideoTrackUnpublished(remoteParticipant: RemoteParticipant,
                                             remoteVideoTrackPublication: RemoteVideoTrackPublication) {
            Log.i(TAG, "onVideoTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteVideoTrackPublication.trackName}]")
            videoStatusTextView.text = "Canal de video cerrado"
        }

        override fun onAudioTrackSubscribed(remoteParticipant: RemoteParticipant,
                                            remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                            remoteAudioTrack: RemoteAudioTrack) {
            Log.i(TAG, "onAudioTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                    "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                    "name=${remoteAudioTrack.name}]")
            videoStatusTextView.text = "Canal de audio establecido"
        }

        override fun onAudioTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                              remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                              remoteAudioTrack: RemoteAudioTrack) {
            Log.i(TAG, "onAudioTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                    "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                    "name=${remoteAudioTrack.name}]")
            videoStatusTextView.text = "Canal de audio cerrado"
        }

        override fun onAudioTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                    remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                                    twilioException: TwilioException) {
            Log.i(TAG, "onAudioTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "name=${remoteAudioTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]")
            videoStatusTextView.text = "Fallo al establecer canal de conexión"
        }

        override fun onDataTrackSubscribed(remoteParticipant: RemoteParticipant,
                                           remoteDataTrackPublication: RemoteDataTrackPublication,
                                           remoteDataTrack: RemoteDataTrack) {
            Log.i(TAG, "onDataTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                    "name=${remoteDataTrack.name}]")
            videoStatusTextView.text = "Conectado a canal de datos"
        }

        override fun onDataTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                             remoteDataTrackPublication: RemoteDataTrackPublication,
                                             remoteDataTrack: RemoteDataTrack) {
            Log.i(TAG, "onDataTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                    "name=${remoteDataTrack.name}]")
            videoStatusTextView.text = "Desconectado de canal de datos"
        }

        override fun onDataTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                   remoteDataTrackPublication: RemoteDataTrackPublication,
                                                   twilioException: TwilioException) {
            Log.i(TAG, "onDataTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "name=${remoteDataTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]")
            videoStatusTextView.text = "Fallo en la conexión a canal de datos"
        }

        override fun onVideoTrackSubscribed(remoteParticipant: RemoteParticipant,
                                            remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                            remoteVideoTrack: RemoteVideoTrack) {
            Log.i(TAG, "onVideoTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]")
            videoStatusTextView.text = "Conectado a canal de video"
            addRemoteParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                              remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                              remoteVideoTrack: RemoteVideoTrack) {
            Log.i(TAG, "onVideoTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]")
            videoStatusTextView.text = "Desconectado de canal de video"
            removeParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                    remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                                    twilioException: TwilioException) {
            Log.i(TAG, "onVideoTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "name=${remoteVideoTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]")
            videoStatusTextView.text = "Fallo en la conexión a canal de video"
            Snackbar.make(connectActionFab,
                    "Failed to subscribe to ${remoteParticipant.identity}",
                    Snackbar.LENGTH_LONG)
                    .show()
        }

        override fun onAudioTrackEnabled(remoteParticipant: RemoteParticipant,
                                         remoteAudioTrackPublication: RemoteAudioTrackPublication) {
        }

        override fun onVideoTrackEnabled(remoteParticipant: RemoteParticipant,
                                         remoteVideoTrackPublication: RemoteVideoTrackPublication) {
        }

        override fun onVideoTrackDisabled(remoteParticipant: RemoteParticipant,
                                          remoteVideoTrackPublication: RemoteVideoTrackPublication) {
        }

        override fun onAudioTrackDisabled(remoteParticipant: RemoteParticipant,
                                          remoteAudioTrackPublication: RemoteAudioTrackPublication) {
        }
    }

    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private var alertDialog: MaterialDialog? = null
    private val cameraCapturerCompat by lazy {
        CameraCapturerCompat(this, getAvailableCameraSource())
    }
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this@VideoCallActivity)
    }
    private val audioManager by lazy {
        this@VideoCallActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private var participantIdentity: String? = null

    private var previousAudioMode = 0
    private var previousMicrophoneMute = false
    private lateinit var localVideoView: VideoRenderer
    private var disconnectedFromOnDestroy = false
    private var isSpeakerPhoneEnabled = true
    private lateinit var turnSpeakerOnMenuItem: MenuItem
    private lateinit var turnSpeakerOffMenuItem: MenuItem
    private lateinit var workerRut: String
    private lateinit var roomName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)
        /*
         * Set local worker id from shared
         */
        workerRut = SharedUtils.getUsuarioId(applicationContext)
        roomName = SharedUtils.getRoomTwilio(applicationContext)
        /*
         * Set local video view to primary view
         */
        localVideoView = primaryVideoView

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager.isSpeakerphoneOn = true

        /*
         * Set the initial state of the UI
         */
        initializeUI()

        /*
         * Set access token
         */
        setAccessToken()

        /*
         * Request permissions.
         */
        //requestPermissionForCameraAndMicrophone()

        //connectToRoom(this.accessToken)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            var cameraAndMicPermissionGranted = true

            for (grantResult in grantResults) {
                cameraAndMicPermissionGranted = cameraAndMicPermissionGranted and
                        (grantResult == PackageManager.PERMISSION_GRANTED)
            }
            // Una vez obtenido los permisos correspondientes procedemos a inicializar audio y video
            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks()
                // Con ambos canales establecidos, conectamos a Room
                connectToRoom(roomName)
            } else {
                Toast.makeText(this,
                        R.string.permissions_needed,
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*
         * If the local video track was released when the app was put in the background, recreate.
         */
        localVideoTrack = if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            LocalVideoTrack.create(this,
                    true,
                    cameraCapturerCompat.videoCapturer)
        } else {
            localVideoTrack
        }
        localVideoTrack?.addRenderer(localVideoView)

        /*
         * If connected to a Room then share the local video track.
         */
        localVideoTrack?.let {

            localParticipant?.publishTrack(it)
        }

        /*
         * Update encoding parameters if they have changed.
         */
        localParticipant?.setEncodingParameters(encodingParameters)

        /*
         * Route audio through cached value.
         */
        audioManager.isSpeakerphoneOn = isSpeakerPhoneEnabled

        /*
         * Update reconnecting UI
         */
        room?.let {
            reconnectingProgressBar.visibility = if (it.state != Room.State.RECONNECTING)
                View.GONE else
                View.VISIBLE
            videoStatusTextView.text = "Connected to ${it.name}"
        }
    }

    override fun onPause() {
        /*
         * If this local video track is being shared in a Room, remove from local
         * participant before releasing the video track. Participants will be notified that
         * the track has been removed.
         */
        localVideoTrack?.let { localParticipant?.unpublishTrack(it) }


        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        localVideoTrack?.release()
        localVideoTrack = null
        super.onPause()
    }

    override fun onDestroy() {
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        room?.disconnect()
        disconnectedFromOnDestroy = true

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        localAudioTrack?.release()
        localVideoTrack?.release()

        /*
         * Antes de cerrar la actividad limpiamos el token de preferences
         */
        clearLocalToken()

        super.onDestroy()
    }

    private fun clearLocalToken() {
        SharedUtils.setRoomTwilio(this@VideoCallActivity, "");
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    R.string.permissions_needed,
                    Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    CAMERA_MIC_PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED
    }

    private fun createAudioAndVideoTracks() {
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(this, true)
        // Share your camera
        localVideoTrack = LocalVideoTrack.create(this,
                true,
                cameraCapturerCompat.videoCapturer)
    }

    private fun getAvailableCameraSource(): CameraCapturer.CameraSource {
        return if (CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA))
            CameraCapturer.CameraSource.FRONT_CAMERA
        else
            CameraCapturer.CameraSource.BACK_CAMERA
    }

    private fun setAccessToken() {
        if (!USE_TOKEN_SERVER) {
            if (!intent.getStringExtra(TWILIO_ACCESS_TOKEN).isNullOrEmpty())
                this.accessToken = intent.getStringExtra(TWILIO_ACCESS_TOKEN)!!
            else {
                Toast.makeText(this, "Nombre de Room no recibido", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            if (!workerRut.isNullOrEmpty() && !roomName.isNullOrEmpty())
                retrieveAccessTokenfromServer()
            else {
                Toast.makeText(this, "Nombre de Room y/o usuario no recibido", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun connectToRoom(roomName: String) {
        configureAudio(true)
        val connectOptionsBuilder = ConnectOptions.Builder(accessToken)
                .roomName(roomName)

        /*
         * Add local audio track to connect options to share with participants.
         */
        localAudioTrack?.let { connectOptionsBuilder.audioTracks(listOf(it)) }

        /*
         * Add local video track to connect options to share with participants.
         */
        localVideoTrack?.let { connectOptionsBuilder.videoTracks(listOf(it)) }

        /*
         * Set the preferred audio and video codec for media.
         */
        connectOptionsBuilder.preferAudioCodecs(listOf(audioCodec))
        connectOptionsBuilder.preferVideoCodecs(listOf(videoCodec))

        /*
         * Set the sender side encoding parameters.
         */
        connectOptionsBuilder.encodingParameters(encodingParameters)

        /*
         * Toggles automatic track subscription. If set to false, the LocalParticipant will receive
         * notifications of track publish events, but will not automatically subscribe to them. If
         * set to true, the LocalParticipant will automatically subscribe to tracks as they are
         * published. If unset, the default is true. Note: This feature is only available for Group
         * Rooms. Toggling the flag in a P2P room does not modify subscription behavior.
         */
        connectOptionsBuilder.enableAutomaticSubscription(enableAutomaticSubscription)

        room = Video.connect(this, connectOptionsBuilder.build(), roomListener)
        setDisconnectAction()
    }

    /*
     * The initial state when there is no active room.
     */
    private fun initializeUI() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_call_end_white_24px))
        connectActionFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@VideoCallActivity, R.color.holo_red_dark))
        connectActionFab.show()
        connectActionFab.setOnClickListener(disconnectClickListener())
        //connectActionFab.setOnClickListener(connectActionClickListener())
        switchCameraActionFab.show()
        switchCameraActionFab.setOnClickListener(switchCameraClickListener())
        localVideoActionFab.show()
        localVideoActionFab.setOnClickListener(localVideoClickListener())
        muteActionFab.show()
        muteActionFab.setOnClickListener(muteClickListener())
    }

    private fun reconnectUI() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_video_call_white_24dp))
        connectActionFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@VideoCallActivity, R.color.green))
        connectActionFab.setOnClickListener(connectActionClickListener())
    }

    /*
     * The actions performed during disconnect.
     */
    private fun setDisconnectAction() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_call_end_white_24px))
        connectActionFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@VideoCallActivity, R.color.holo_red_dark))
        connectActionFab.show()
        connectActionFab.setOnClickListener(disconnectClickListener())
    }

    /*
     * Creates an connect UI dialog
     */
    private fun showConnectDialog() {
        val roomEditText = EditText(this)
        alertDialog = createConnectDialog(roomEditText,
                connectClickListener(roomEditText), cancelConnectDialogClickListener(), this)
        alertDialog!!.show()
    }

    /*
     * Called when participant joins the room
     */
    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        if (thumbnailVideoView.visibility == View.VISIBLE) {
            Snackbar.make(connectActionFab,
                    "Multiple participants are not currently support in this UI",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            return
        }
        participantIdentity = remoteParticipant.identity
        videoStatusTextView.text = "Participant $participantIdentity joined"

        /*
         * Add participant renderer
         */
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { addRemoteParticipantVideo(it) }
                Log.e(TAG, "remoteVideoTrackPublication is ok")
            }
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(participantListener)
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private fun addRemoteParticipantVideo(videoTrack: VideoTrack) {
        moveLocalVideoToThumbnailView()
        primaryVideoView.mirror = false
        videoTrack.addRenderer(primaryVideoView)
    }

    private fun moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.visibility == View.GONE) {
            thumbnailVideoView.visibility = View.VISIBLE
            with(localVideoTrack) {
                this?.removeRenderer(primaryVideoView)
                this?.addRenderer(thumbnailVideoView)
            }
            localVideoView = thumbnailVideoView
            thumbnailVideoView.mirror = cameraCapturerCompat.cameraSource ==
                    CameraCapturer.CameraSource.FRONT_CAMERA
        }
    }

    /*
     * Called when participant leaves the room
     */
    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {
        videoStatusTextView.text = "Participant $remoteParticipant.identity left."
        if (remoteParticipant.identity != participantIdentity) {
            return
        }

        /*
         * Remove participant renderer
         */
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { removeParticipantVideo(it) }
            }
        }
        moveLocalVideoToPrimaryView()
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeRenderer(primaryVideoView)
    }

    private fun moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.visibility == View.VISIBLE) {
            thumbnailVideoView.visibility = View.GONE
            with(localVideoTrack) {
                this?.removeRenderer(thumbnailVideoView)
                this?.addRenderer(primaryVideoView)
            }
            localVideoView = primaryVideoView
            primaryVideoView.mirror = cameraCapturerCompat.cameraSource ==
                    CameraCapturer.CameraSource.FRONT_CAMERA
        }
    }

    private fun connectClickListener(roomEditText: EditText): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog,
                                                     which ->
            connectToRoom(roomName)
        }
    }

    private fun disconnectClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Disconnect from room
             */
            room?.disconnect()
            //initializeUI()
            finish()
        }
    }

    private fun connectActionClickListener(): View.OnClickListener {
        return View.OnClickListener { connectToRoom(roomName) }
    }

    private fun cancelConnectDialogClickListener(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog,
                                                     which ->
            run {
                initializeUI()
                alertDialog!!.dismiss()
            }
        }
    }

    private fun switchCameraClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val cameraSource = cameraCapturerCompat.cameraSource
            cameraCapturerCompat.switchCamera()
            if (thumbnailVideoView.visibility == View.VISIBLE) {
                thumbnailVideoView.mirror = cameraSource == CameraCapturer.CameraSource.BACK_CAMERA
            } else {
                primaryVideoView.mirror = cameraSource == CameraCapturer.CameraSource.BACK_CAMERA
            }
        }
    }

    private fun localVideoClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Enable/disable the local video track
             */
            localVideoTrack?.let {
                val enable = !it.isEnabled
                it.enable(enable)
                val icon: Int
                if (enable) {
                    icon = R.drawable.ic_videocam_white_24dp
                    switchCameraActionFab.show()
                } else {
                    icon = R.drawable.ic_videocam_off_black_24dp
                    switchCameraActionFab.hide()
                }
                localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoCallActivity, icon))
            }
        }
    }

    private fun muteClickListener(): View.OnClickListener {
        return View.OnClickListener {
            /*
             * Enable/disable the local audio track. The results of this operation are
             * signaled to other Participants in the same Room. When an audio track is
             * disabled, the audio is muted.
             */
            localAudioTrack?.let {
                val enable = !it.isEnabled
                it.enable(enable)
                val icon = if (enable)
                    R.drawable.ic_mic_white_24dp
                else
                    R.drawable.ic_mic_off_black_24dp
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                        this@VideoCallActivity, icon))
            }
        }
    }

    private fun retrieveAccessTokenfromServer() {
        try {
            val api = RestClient.buildAnglo()
            val call = api.getTwilioToken(workerRut, roomName)
            call.enqueue(object : Callback<ApiResponseAnglo<Any>> {
                override fun onResponse(call: Call<ApiResponseAnglo<Any>>, response: Response<ApiResponseAnglo<Any>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.isSuccess) {
                            if (result.data != null && result.data.toString() !== "") {
                                accessToken = result.data.toString()
                                Log.i(TAG, "ACCESS_TOKEN: $accessToken")
                                requestPermissionForCameraAndMicrophone()
                            }
                        }
                    } else {
                        Log.e(TAG, "retrieveAccessTokenfromServer bad response: " + response.message())
                        SharedUtils.showToast(applicationContext, "No se pudo obtener el nombre de la sala")
                        //finish()
                    }
                }

                override fun onFailure(call: Call<ApiResponseAnglo<Any>>, t: Throwable) {
                    videoStatusTextView.text = "Error al conectar"
                    Log.e(TAG, "retrieveAccessTokenfromServer: ${t.message}", t)
                    SharedUtils.showToast(applicationContext, "No se pudo obtener el nombre de la sala")
                    //finish()
                }
            })
        } catch (ex: Exception) {
            Log.e(TAG, "retrieveAccessTokenfromServer: ${ex.message}")
            SharedUtils.showToast(applicationContext, "No se pudo obtener el nombre de la sala")
            //finish()
        }
    }

    private fun configureAudio(enable: Boolean) {
        with(audioManager) {
            if (enable) {
                previousAudioMode = audioManager.mode
                // Request audio focus before making any device switch
                requestAudioFocus()
                /*
                 * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
                 * to be in this mode when playout and/or recording starts for the best
                 * possible VoIP performance. Some devices have difficulties with
                 * speaker mode if this is not set.
                 */
                mode = AudioManager.MODE_IN_COMMUNICATION
                /*
                 * Always disable microphone mute during a WebRTC call.
                 */
                previousMicrophoneMute = isMicrophoneMute
                isMicrophoneMute = false
            } else {
                mode = previousAudioMode
                abandonAudioFocus(null)
                isMicrophoneMute = previousMicrophoneMute
            }
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { }
                    .build()
            audioManager.requestAudioFocus(focusRequest)
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        }
    }

    private fun createConnectDialog(participantEditText: EditText,
                                    callParticipantsClickListener: MaterialDialog.SingleButtonCallback,
                                    cancelClickListener: MaterialDialog.SingleButtonCallback,
                                    context: Context): MaterialDialog {
        val alertDialogBuilder = MaterialDialog.Builder(context).apply {
            icon(ContextCompat.getDrawable(this@VideoCallActivity, R.drawable.ic_video_call_white_24dp)!!)
            title = "Conectar a una room?"
            positiveText("Conectar")
            onPositive(callParticipantsClickListener)
            negativeText("Cancelar")
            onNegative(cancelClickListener)
            cancelable(false)
        }

        setRoomNameFieldInDialog(participantEditText, alertDialogBuilder, context)

        return alertDialogBuilder.build()
    }

    @SuppressLint("RestrictedApi")
    private fun setRoomNameFieldInDialog(roomNameEditText: EditText,
                                         alertDialogBuilder: MaterialDialog.Builder,
                                         context: Context) {
        roomNameEditText.hint = "room name"
        val horizontalPadding = context.resources.getDimensionPixelOffset(R.dimen.activity_horizontal_margin)
        val verticalPadding = context.resources.getDimensionPixelOffset(R.dimen.activity_vertical_margin)
        alertDialogBuilder.customView(roomNameEditText, false)
    }
}
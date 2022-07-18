package com.example.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.toBitmap

class MainActivity : AppCompatActivity() {
    lateinit var notificationManger: NotificationManager

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManger = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        resgisteNewChannel(CHANNEL_NAME, CHANNEL_ID, CHANNEL_PRI)

        //view Initialization
        val normalNotification = findViewById<Button>(R.id.push_notification)
        val progressNotifcation = findViewById<Button>(R.id.progress_not)
        val silentCheckbox = findViewById<CheckBox>(R.id.silent_progress)
        val indeterminateCheckBox = findViewById<CheckBox>(R.id.is_indeterminate)
        val messageNotification = findViewById<Button>(R.id.messaging_notification)
        val bigPictureNotification = findViewById<Button>(R.id.big_picture_notif)
        val bigTextNotification = findViewById<Button>(R.id.big_text_notif)
        val actionNotification = findViewById<Button>(R.id.action_buttons)
        val actionsNumberPicker = findViewById<NumberPicker>(R.id.action_number_picker)
        val addReplyCheckbox = findViewById<CheckBox>(R.id.add_reply_action)
        actionsNumberPicker.maxValue = 5
        actionsNumberPicker.minValue = 1
        val groupNotification = findViewById<Button>(R.id.group_notification)
        val mediaNotification = findViewById<Button>(R.id.media_notification)
        val inboxStyleNotification = findViewById<Button>(R.id.inbox_style_notif)
        val chronometerNotification = findViewById<Button>(R.id.chrono_notif)

        //Click listeners
        normalNotification.setOnClickListener{
            pushNotification("TITLE", " this is content", CHANNEL_ID)
        }
        progressNotifcation.setOnClickListener{
            pushNotificationWithProgress(silentCheckbox.isChecked, indeterminateCheckBox.isChecked)
        }
        messageNotification.setOnClickListener{
            pushMessagingNotifications()
        }
        bigPictureNotification.setOnClickListener{
            pushLargeImageNotification()
        }
        bigTextNotification.setOnClickListener{
            pushBigTextStyleNotification()
        }
        actionNotification.setOnClickListener{
            pushNotifWithActionButtons(actionsNumberPicker.value, addReplyCheckbox.isChecked)
        }
        groupNotification.setOnClickListener{
            pushGroupNotifications()
        }
        mediaNotification.setOnClickListener{
            pushMediaNotification()
        }
        inboxStyleNotification.setOnClickListener{
            pushInboxStyleNotification()
        }
        chronometerNotification.setOnClickListener{
            pushNotificationWithChronometer()
        }
    }
    val CHANNEL_NAME = "CHANNEL_1"
    val CHANNEL_ID = "ID_1"
    val CHANNEL_PRI = NotificationManager.IMPORTANCE_MAX
    val notificationId = 5
    val messagingNotId = 1
    val bigPictureNotId = 2

    private val KEY_TEXT_REPLY = "key_text_reply"
    fun pushNotification(title: String, content: String, channelId: String): Notification{

        Toast.makeText(this,("channel prio: " + notificationManger.getNotificationChannel(channelId).importance), Toast.LENGTH_SHORT).show()

        var largeIcon = resources.getDrawable(R.drawable.user_icon)
        var builder = NotificationCompat.Builder(this,channelId)
            .setContentInfo("Hello I am contetnt Info")
            .setContentTitle("SYSTEM not")
            .setSubText("Hello i am subtext")
            .setContentText("Hello i am content text...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setNumber(5)
            .setLargeIcon(largeIcon.toBitmap())
            .setTicker("Hii i am ticker text..")
            .setWhen(1000)

        var notification = builder.build()
        notificationManger.notify(notificationId, notification)
        return notification
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun resgisteNewChannel(name: String, id: String, pri: Int){
        var channel = NotificationChannel(id,name,pri)
//        channel.setBypassDnd(true)
        notificationManger.createNotificationChannel(channel)
    }
    fun pushNotificationWithProgress(silent : Boolean, indeterminate: Boolean){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Progress notification")
            .setContentText("Process in progress")
            .setSilent(silent)
            .setProgress(5, 0, indeterminate)
        notificationManger.notify(0, builder.build())
        Thread(Runnable {
                for (i in 1..5){
                    builder.setProgress(5, i, indeterminate)
                    var per = (i/5.0f) * 100
                    builder.setContentText("$per %")
                    try {
                        Thread.sleep(2000)
                    } catch (e: InterruptedException){
                        Log.d("MAIN","Failed to sleep(WOW)")
                    }
                    notificationManger.notify(0, builder.build())
                }
                builder.setContentText("Complete")
                    // When indeterminate is true it will ignore the first 2 params and will keep
                    // showing progress even if it is not complete...
                    // so when download is complete pass indeterminate value to false..
                    .setProgress(0,0, false)
                notificationManger.notify(0, builder.build())
            }
        ).start()
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun pushMessagingNotifications(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Messaging Style Notification")
            .setContentText("Expand me to see chat..")
        val taki = Person.Builder()
            .setName("Taki")
        val mitsua = Person.Builder()
            .setName("Mitsua")
        var style = NotificationCompat.MessagingStyle("Taki")
            .setConversationTitle("Magic Hour")
            .setGroupConversation(true)
            .addMessage("Magic hour started",0,  taki.build())
            .addMessage("Now we can meet",1, mitsua.build())
            .addMessage("Write down my name", 2, taki.build())
            .addMessage("So that we can remember", 3, taki.build())
            .addMessage("Hmmmmmmmm", 5, mitsua.build())
            .addHistoricMessage(NotificationCompat.MessagingStyle.Message("this is historic message", 4, mitsua.build()))

        builder.setStyle(style)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
        notificationManger.notify(messagingNotId, builder.build())
    }
    fun pushLargeImageNotification(){
        var image = resources.getDrawable(R.drawable.ic_card)
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Big Picture Notification")
            .setContentText("Expand me to see image")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(image.toBitmap()))
        notificationManger.notify(bigPictureNotId, builder.build())
    }
    fun pushBigTextStyleNotification(){
        var text = "Taki, Miki, and their friend Tsukasa travel to Gifu by train on a trip to Hida " +
                "in search of Mitsuha, though Taki does not know the name of Itomori, relying on his" +
                " sketches of the surrounding landscape from memory. A restaurant owner in Hida " +
                "recognizes the town in the sketch, being originally from there. He takes Taki and " +
                "his friends to the ruins of Itomori, which has been destroyed and where 500 " +
                "residents were killed when Tiamat unexpectedly fragmented as it passed by Earth " +
                "three years earlier. Taki observes Mitsuha's messages disappear from his phone and " +
                "his memories of her begin to gradually fade, realizing the two were also separated " +
                "by time, as he is in 2016. Taki finds Mitsuha's name in the record of fatalities. " +
                "While Miki and Tsukasa return to Tokyo, Taki journeys to the shrine, hoping to r" +
                "econnect with Mitsuha and warn her about Tiamat. There, Taki drinks Mitsuha's " +
                "kuchikamizake then lapses into a vision, where he glimpses Mitsuha's past. He also " +
                "recalls that he encountered Mitsuha on a train when she came to Tokyo the day " +
                "before the event to find him, though Taki did not recognize her as the " +
                "body-switching was yet to occur in his timeframe. Before leaving the train in " +
                "embarrassment, Mitsuha had handed him her hair ribbon, which he has since worn on " +
                "his wrist as a good-luck charm. "
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Big Text Notification")
            .setStyle(NotificationCompat.BigTextStyle().bigText(text)
                .setBigContentTitle("Big Text Notification Title")
                .setSummaryText("Big Text Notification summary"))
        notificationManger.notify(bigPictureNotId, builder.build())
    }
    val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    val ACTION_NONE = "ACTION.NO_ACTION"
    val REQUEST_CODE = 100
    @SuppressLint("LaunchActivityFromNotification")
    fun pushNotifWithActionButtons(actions: Int, replyAction: Boolean){

        val myIntent = Intent(this, MyBroadcaseReceiver::class.java).apply {
            action = ACTION_NONE
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, myIntent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Adding Actions..")
            .setContentText("Max 3 actions can be added")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        if(replyAction){
            var replyLabel: String = "Reply"
            var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
                setLabel(replyLabel)
                build()
            }

            val replyIntent = Intent(this, MyBroadcaseReceiver::class.java).apply {
                action = ACTION_NONE
                putExtra(EXTRA_NOTIFICATION_ID, 0)
            }
            var replyPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, replyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            var action: NotificationCompat.Action =
                NotificationCompat.Action.Builder(R.drawable.ic_facebook,
                    "reply", replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .build()
            builder.addAction(action)
        }

        val thisActivityIntent = Intent(this, MainActivity::class.java)
        val pendingIntentForThisActivity = PendingIntent.getActivity(this,0, thisActivityIntent, 0)
        val settingsIntent = Intent("android.settings.ADD_NETWORK")
        settingsIntent.putExtra("isWifiDropDownAddNetwork", true)
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        val pendingIntentForSettings = PendingIntent.getActivity(this, 0, settingsIntent, 0)
        builder.addAction(R.drawable.ic_launcher_foreground, "launch settings", pendingIntentForSettings)

        builder.addAction(R.drawable.ic_launcher_background, "launch activity", pendingIntentForThisActivity)

        for(i in 1..actions){
            builder.addAction(R.drawable.ic_launcher_foreground, "action $i", pendingIntent )
        }
        notificationManger.notify(bigPictureNotId, builder.build())
    }
    class MyBroadcaseReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            //Do nothing for now
            Log.d("AMAN", "recieved")
        }

    }
    val SUMMARY_ID = 0
    val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
    fun pushGroupNotifications() {
        val newMessageNotification1 = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Message 1")
            .setContentText("You will not believe...")
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .build()
        val newMessageNotification2 = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Message 2")
            .setContentText("Please join us to celebrate the...")
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .build()
        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("summaryNotification")
            //set content text to support devices running API level < 24
            .setContentText("Two new messages")
            .setSmallIcon(R.drawable.ic_facebook)
            //build summary info into InboxStyle template
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("Alex Faarborg Check this out")
                    .addLine("Jeff Chang Launch Party")
                    .setBigContentTitle("2 new messages")
                    .setSummaryText("janedoe@example.com")
            )
            //specify which group this notification belongs to
            .setGroup(GROUP_KEY_WORK_EMAIL)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .build()
        notificationManger.apply {
            notify(10, newMessageNotification1)
            notify(10, newMessageNotification2)
            notify(10, summaryNotification)
        }
        Thread(Runnable {
            for(i in 1..5){
                Thread.sleep(2000)
                val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("summaryNotification")
                    //set content text to support devices running API level < 24
                    .setContentText( "$i new messages")
                    .setSmallIcon(R.drawable.ic_facebook)
                    //build summary info into InboxStyle template
                    .setStyle(
                        NotificationCompat.InboxStyle()
                            .addLine("Alex Faarborg Check this out")
                            .addLine("Jeff Chang Launch Party")
                            .setBigContentTitle("$i new messages")
                            .setSummaryText("janedoe@example.com")
                    )
                    //specify which group this notification belongs to
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    //set this notification as the summary for the group
                    .setGroupSummary(true)
                    .build()
                notificationManger.notify(10, newMessageNotification2)
                notificationManger.notify(10, summaryNotification)
            }
        }).start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pushMediaNotification(){
        val myIntent = Intent(this, MyBroadcaseReceiver::class.java).apply {
            action = ACTION_NONE
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, myIntent, 0)

        var builder = Notification.Builder(this, CHANNEL_ID)
            .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntent)
            .addAction(android.R.drawable.ic_media_play, "Pause", pendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", pendingIntent)
        var style = Notification.MediaStyle().setShowActionsInCompactView(1 /* #1: pause button \*/)
        builder.setSmallIcon(R.drawable.ic_facebook)
            .setContentTitle("Media Notification")
            .setContentText("Song is being played")
            .setStyle(style)
        notificationManger.notify(11, builder.build())
    }

    fun pushInboxStyleNotification(){
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("Inbox Style")
            .setContentText("not a group chat")
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("Heloo its monday again")
                .addLine("I sppent all my salary in 1st week"))
        notificationManger.notify(12, builder.build())
    }

    fun pushNotificationWithChronometer(){
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("Chronometer Notification")
            .setContentText("Stopwatch")
            .setUsesChronometer(true)
        notificationManger.notify(12, builder.build())
    }
}
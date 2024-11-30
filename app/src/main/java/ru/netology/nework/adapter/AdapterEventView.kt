package ru.netology.nework.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.netology.nework.R
import ru.netology.nework.databinding.EventViewBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.UserPreview
import ru.netology.nework.enumeration.AttachmentType
import ru.netology.nework.enumeration.MeetingType
import ru.netology.nework.util.AndroidUtils
import javax.inject.Inject

interface OnEventListener{
    fun onLike(event: Event)
    fun openSpacePhoto(event: Event)
    fun playVideo(url: String)
    fun playAudio(url: String)
    fun showUsers(users: List<Long>?)
}

class AdapterEventView @Inject constructor(
    private val binding: EventViewBinding,
    private val yakit: YaKit,
    @ApplicationContext
    private val context: Context,
    private val onEventListener: OnEventListener
) {
    init {
        yakit.initMapView(binding.map)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bind(event: Event){
        with(binding){

//                //layMaps.visibility = View.VISIBLE
//                val startLocation = Point(59.9402, 30.315)
//                yakit.moveToStartLocation(startLocation)
//                yakit.setMarkerInStartLocation(startLocation)

            author.text = event.author
            published.text = AndroidUtils.getTimePublish(event.published)
            infoDate.text= event.typeMeeting.toString()
            if (event.typeMeeting == MeetingType.ONLINE) infoDate.setTextColor(Color.parseColor("#00ff00"))
            content.text = event.content
            workPlace.text = event.authorJob

            icLike.isChecked = event.likedByMe?:false
            icLike.text = event.likeOwnerIds?.count().toString()
            icLike.setOnClickListener {
                onEventListener.onLike(event)
            }

            icParticipants.isChecked = event.participatedByMe?:false
            icParticipants.text = event.participantsIds?.count().toString()
            "Speakers ${event.speakerIds?.count().toString()}".also { speakers.text = it }

            Glide.with(avatar)
                .load(event.authorAvatar)
//                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.icon_person_24)
                .timeout(45_000)
                .circleCrop()
                .into(avatar)
            event.coordinates?.let {
                layMaps.visibility = View.VISIBLE
                val startLocation = Point(event.coordinates.lat!!, event.coordinates.longCr!!)
                yakit.moveToStartLocation(startLocation)
                yakit.setMarkerInStartLocation(startLocation)
            }

            imageView.visibility = View.GONE
            layAudio.visibility = View.GONE
            layMaps.visibility = View.GONE
            play.visibility = View.GONE

            imageView.setOnClickListener {
                if(event.attachment?.type == AttachmentType.IMAGE) onEventListener.openSpacePhoto(event)
                if(event.attachment?.type == AttachmentType.VIDEO) {
                    videoView.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    onEventListener.playVideo(event.attachment.url)
                    play.visibility = View.GONE
                }
            }

            event.attachment?.let {
                when (it.type) {
                    AttachmentType.IMAGE, null -> {
                        imageView.visibility = View.VISIBLE
                        Glide.with(imageView)
                            .load(event.attachment?.url)
                            .placeholder(R.drawable.ic_loading_100dp)
                            //.error(R.drawable.ic_error_100dp)
                            .timeout(45_000)
                            .into(imageView)
                    }

                    AttachmentType.VIDEO -> {
                        play.visibility = View.VISIBLE
                        imageView.visibility = View.VISIBLE
                        Glide.with(imageView)
                            .load(event.attachment.url)
                            .placeholder(R.drawable.ic_loading_100dp)
                            //.error(R.drawable.ic_error_100dp)
                            .timeout(180_000)
                            .into(imageView)

                        play.setOnClickListener {
                            videoView.visibility = View.VISIBLE
                            imageView.visibility = View.GONE
                            onEventListener.playVideo(event.attachment.url)
                            play.visibility = View.GONE
                        }
                    }

                    AttachmentType.AUDIO -> {
                        layAudio.visibility = View.VISIBLE
                        playAudio.setOnClickListener { onEventListener.playAudio(event.attachment.url) }
                    }
                }

            }

            event.likeOwnerIds?.let {
                val listUsersLike = mutableListOf<UserPreview>()
                event.users?.forEach { (t, u) ->
                    if (event.likeOwnerIds.contains(t.toLong()) && u.avatar != null) listUsersLike.add(
                        u
                    )
                }

                val dimViewAvatarLikes = listOf(
                    avatarUserLike1,
                    avatarUserLike2,
                    avatarUserLike3,
                    avatarUserLike4,
                    avatarUserLike5
                )
                if (listUsersLike.size < 6) {
                    for (i in 0..<listUsersLike.size) {
                        Glide.with(dimViewAvatarLikes[i])
                            .load(listUsersLike[i].avatar)
                            .timeout(45_000)
                            .circleCrop()
                            .into(dimViewAvatarLikes[i])
                    }
                } else {
                    for (i in 0..<5) {
                        Glide.with(dimViewAvatarLikes[i])
                            .load(listUsersLike[i].avatar)
                            .timeout(45_000)
                            .circleCrop()
                            .into(dimViewAvatarLikes[i])
                    }
                    Glide.with(avatarUserLike6)
                        .load(R.drawable.but_plus)
                        .timeout(45_000)
                        .circleCrop()
                        .into(avatarUserLike6)

                }
            }

            event.participantsIds?.let {
                val listUsersParticipants = mutableListOf<UserPreview>()
                event.users?.forEach { (t, u) ->
                    if (event.participantsIds.contains(t.toLong()) && u.avatar != null) listUsersParticipants.add(
                        u
                    )
                }

                val dimViewAvatarParticipants = listOf(
                    avatarUser1,
                    avatarUser2,
                    avatarUser3,
                    avatarUser4,
                    avatarUser5
                )
                if (listUsersParticipants.size < 6) {
                    for (i in 0..<listUsersParticipants.size) {
                        Glide.with(dimViewAvatarParticipants[i])
                            .load(listUsersParticipants[i].avatar)
                            .timeout(45_000)
                            .circleCrop()
                            .into(dimViewAvatarParticipants[i])
                    }
                } else {
                    for (i in 0..<5) {
                        Glide.with(dimViewAvatarParticipants[i])
                            .load(listUsersParticipants[i].avatar)
                            .timeout(45_000)
                            .circleCrop()
                            .into(dimViewAvatarParticipants[i])
                    }
                    Glide.with(avatarUser6)
                        .load(R.drawable.but_plus)
                        .timeout(45_000)
                        .circleCrop()
                        .into(avatarUser6)

                }
            }

            event.speakerIds?.let {
                val listUsersSpeakers = mutableListOf<UserPreview>()
                event.users?.forEach { (t, u) ->
                    if (event.speakerIds.contains(t.toLong()) && u.avatar != null) listUsersSpeakers.add(
                        u
                    )
                }

                val dimViewAvatarSpeakers = listOf(
                    avatarSpeakers1,
                    avatarSpeakers2,
                    avatarSpeakers3,
                    avatarSpeakers4,
                    avatarSpeakers5
                )
                if (listUsersSpeakers.size < 6) {
                    for (i in 0..<listUsersSpeakers.size) {
                        Glide.with(dimViewAvatarSpeakers[i])
                            .load(listUsersSpeakers[i].avatar)
                            .timeout(45_000)
                            .circleCrop()
                            .into(dimViewAvatarSpeakers[i])
                    }
                } else {
                    for (i in 0..<5) {
                        Glide.with(dimViewAvatarSpeakers[i])
                            .load(listUsersSpeakers[i].avatar)
                            .timeout(45_000)
                            .circleCrop()
                            .into(dimViewAvatarSpeakers[i])
                    }
                    Glide.with(avatarUser6)
                        .load(R.drawable.but_plus)
                        .timeout(45_000)
                        .circleCrop()
                        .into(avatarUser6)

                }
            }

            avatarUser6.setOnClickListener {
                onEventListener.showUsers(event.participantsIds)
            }
            avatarUserLike6.setOnClickListener {
                onEventListener.showUsers(event.likeOwnerIds)
            }

            avatarSpeakers6.setOnClickListener {
                onEventListener.showUsers(event.speakerIds)
            }

            transparentImage.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        scroll.requestDisallowInterceptTouchEvent(true)
                        false
                    }

                    MotionEvent.ACTION_UP -> {
                        scroll.requestDisallowInterceptTouchEvent(false)
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        scroll.requestDisallowInterceptTouchEvent(true)
                        false
                    }

                    else -> {
                        true
                    }
                }

            }
        }
    }
}
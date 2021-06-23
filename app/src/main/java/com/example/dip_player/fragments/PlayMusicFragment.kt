package com.example.dip_player.fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.dip_player.R
import com.example.dip_player.databinding.FragmentPlayMusicBinding
import com.example.dip_player.helper.Constants
import com.example.dip_player.model.Song


class PlayMusicFragment : Fragment() {

    private var _binding: FragmentPlayMusicBinding? = null
    private val binding get() = _binding!!
    private val args: PlayMusicFragmentArgs by navArgs()
    private lateinit var song: Song
    private var mMediaPlayer: MediaPlayer? = null

    private var seekLength: Int = 0
    private val seekForwardTime = 5 * 1000
    private val seekBackwardTime = 5 * 1000




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayMusicBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        song = args.song!!
        mMediaPlayer = MediaPlayer()

        binding.tvTitle.text = song.songTitle
        binding.tvDuration.text = song.songDuration
        binding.tvAuthor.text = song.songArtist

        binding.ibPlay.setOnClickListener {
            playSong()
        }
        binding.ibForwardSong.setOnClickListener {
            forwardSong()
        }

        binding.ibBackwardSong.setOnClickListener {
            rewindSong()
        }

        binding.ibRepeat.setOnClickListener {
            repeatSong()
        }
        displaySongArt()




    }

    private fun displaySongArt() {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(song.songUri)
        val data = mmr.embeddedPicture

        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.ibCover.setImageBitmap(bitmap)
        }

    }

    private fun repeatSong() {
        if (!mMediaPlayer!!.isLooping) {
            mMediaPlayer!!.isLooping = true
            binding.ibRepeat.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_repeat_white
                )
            )
        } else {
            mMediaPlayer!!.isLooping = false
            binding.ibRepeat.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_repeat
                )
            )
        }

    }

    private fun rewindSong() {

        if (mMediaPlayer != null) {
            val currentPosition: Int = mMediaPlayer!!.currentPosition
            if (currentPosition - seekBackwardTime >= 0) {
                mMediaPlayer!!.seekTo(currentPosition - seekBackwardTime)
            } else {
                mMediaPlayer!!.seekTo(0)
            }
        }

    }

    private fun forwardSong() {
        if (mMediaPlayer != null) {
            val currentPosition: Int = mMediaPlayer!!.currentPosition
            if (currentPosition + seekForwardTime <= mMediaPlayer!!.duration) {
                mMediaPlayer!!.seekTo(currentPosition + seekForwardTime)
            } else {
                mMediaPlayer!!.seekTo(mMediaPlayer!!.duration)
            }
        }

    }


    private fun updateSeekBar() {
        if (mMediaPlayer != null) {
            binding.tvCurrentTime.text =
                Constants.durationConverter(mMediaPlayer!!.currentPosition.toLong())
        }
        seekBarSetUp()
        Handler().postDelayed(runnable, 50)
    }
    var runnable = Runnable { updateSeekBar() }

    private fun seekBarSetUp() {

        if (mMediaPlayer != null) {
            binding.seekBar.progress = mMediaPlayer!!.currentPosition
            binding.seekBar.max = mMediaPlayer!!.duration
        }
        binding.seekBar.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mMediaPlayer!!.seekTo(progress)
                    binding.tvCurrentTime.text = Constants.durationConverter(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                    if (seekBar != null) {
                        mMediaPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }
        })
    }


    private fun playSong() {
        if(!mMediaPlayer!!.isPlaying){
            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(song.songUri)
            mMediaPlayer!!.prepare()
          //  mMediaPlayer!!.seekTo(seek)
            mMediaPlayer!!.start()

            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_pause
                )
            )
            updateSeekBar()
        }else{
            mMediaPlayer!!.pause()
            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_play
                )
            )

        }

    }

    private fun clearMediaPlayer(){
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        playSong()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }


}
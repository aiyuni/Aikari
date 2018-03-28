package bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.core.audio.AudioSendHandler;

/**
 * Wrapper that allows JDA to be used with Lavaplayer
 * @author Perry
 *
 */
public class AudioPlayerSendHandler implements AudioSendHandler {
    
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;
    
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }
    
    @Override
    public boolean canProvide() {
      lastFrame = audioPlayer.provide();
      return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio() {
      return lastFrame.data;
    }

    @Override
    public boolean isOpus() {
      return true;
    }

}

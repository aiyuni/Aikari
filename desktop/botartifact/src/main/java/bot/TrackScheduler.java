package bot;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

/**
 * This class provides the event handlers for the AudioPlayer.
 * @author Perry
 *
 */
public class TrackScheduler extends AudioEventAdapter  {
    
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    TrackEndEvent trackEnd;
    
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
      }

    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
          queue.offer(track);
          System.out.println("adding to queue");
          
        }
        else {
            System.out.println("inside else1");
        }
        System.out.println("inside queue method");
    }
    
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
      // A track started playing
        System.out.println("A track has started playing.");
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        System.out.println("Inside track end.");
      if (endReason.mayStartNext) {
        // Start next track
          System.out.println("Inside track end.");
          nextTrack();
      }
      else {
          System.out.println("No more tracks to play. ");
      }

      // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
      // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
      // endReason == STOPPED: The player was stopped.
      // endReason == REPLACED: Another track started playing while this had not finished
      // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
      //                       clone of this back to your queue
    }
    
}

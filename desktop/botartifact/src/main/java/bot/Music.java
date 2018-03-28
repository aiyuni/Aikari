package bot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * Work in progress.
 * Implements the music bot feature of Aikari using Lavaplayer.
 * TODO: proper pause/stop feature. Join the user's voice channel instead of a default one.
 * @author Perry
 *
 */
public class Music {
    
    //ONLY USE 1 INSTANCE OF THIS IF POSSIBLE 
    static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    
    //create as many audioPlayer objects as needed, it doesnt drain resources unless music is being played!
    private AudioPlayer player;
    
    protected TrackScheduler trackScheduler;
    private Guild guild;
    
    public Music() {
        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }
    
    public void loadMusic(Guild guild, String url, MessageChannel channel) {
        
        this.guild = guild;
        String id = url;
        
        playerManager.loadItem(url, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                
                  connectToFirstVoiceChannel(guild.getAudioManager());
                  trackScheduler.queue(track);
                  channel.sendMessage("Playing " + track.getInfo().title).queue();
                  System.out.println("Playing music");
                  //trackScheduler.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
              System.out.println("Inside playlistloaded");
              for (AudioTrack track : playlist.getTracks()) {
                connectToFirstVoiceChannel(guild.getAudioManager());
                trackScheduler.queue(track);
                channel.sendMessage("Playing " + track.getInfo().title).queue();
                //trackScheduler.onTrackStart(player, track);
                System.out.println("does this get printed");
              }
            }

            @Override
            public void noMatches() {
                System.out.println("inside no matches");
              // Notify the user that we've got nothing
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("everything exploded");
              // Notify the user that everything exploded
            }
            
        });
    }
    
    private void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
          for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
            audioManager.openAudioConnection(voiceChannel);
            audioManager.setSendingHandler(this.getSendHandler());
            break;
          }
        }
      }
    
    public void disconnect(AudioManager audioManager) {
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
        }
    }
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
      }

}

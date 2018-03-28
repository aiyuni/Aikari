package bot;

import java.io.IOException;

import com.michaelwflaherty.cleverbotapi.CleverBotQuery;

/**
 * Simple cleverbot implementation using a wrapper.
 * @author Perry
 *
 */
public class Cleverbot {

    CleverBotQuery cleverBot;
    
    public Cleverbot(String input) {
        
    }
    
    public String getReply(String string) {
        
        cleverBot = new CleverBotQuery("CC4lqvHLiexebtTLrPV4olH3CkQ", string);
        
        String response;

        try {
            cleverBot.sendRequest();
            response = cleverBot.getResponse();
        }
        catch (IOException e) {
            response = e.getMessage();
            System.out.println("caught an IOexception in cleverbot...");
        }
        
        return response;
    }
}

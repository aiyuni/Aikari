package bot;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * A class that contains methods related to CST, such as Comp 1510 labs, and methods used for 
 * Carly's presentation. Also contains the fun random-selector! 
 * @author Perry
 *
 */
public class Comp1510 {
    
    MessageChannel channel;
    
    public void flipCoin (MessageChannel channel) {
        this.channel = channel;
       final int max = 10; //set to 100 later
            
            int heads = 0;
            int maxheads = 0;
            Coin coin = new Coin();
            channel.sendMessage("flipping a coin 10 times...").queue();
            for (int i = 0; i < max; i++) {  
                coin.flip();
                channel.sendMessage(coin.toString()).queue();
                if (coin.isHeads()) {
                    heads++;
                }
                if (coin.isHeads()) {
                    if (maxheads < heads) {
                        maxheads = heads;
                    }
                }
                else {
                    heads = 0;
                }
            }
            
            System.out.println("Max heads is: " + maxheads);
            channel.sendMessage("The maximum number of heads you have flipped in a row is " + maxheads).queue();
    }
    
   public String randomPicker(String ... values) {
       
       List<String> options = Arrays.asList(values);
       System.out.println(options.size());
       
       Random rand = new Random();
       int random = rand.nextInt(options.size());
       
       String randomString = options.get(random);
       System.out.println(randomString);
       return randomString;
    } 
   
   public void sendFiles(MessageChannel channel) {
       File jdaDependency = new File(".\\JDA-3.5.0_327-withDependencies.jar");
       File jdaSources = new File(".\\JDA-3.5.0_327-sources.jar");
       File jdaJavadocs = new File(".\\JDA-3.5.0_327-javadoc.jar");
       System.out.println(jdaDependency);
       channel.sendFile(jdaDependency).queue();
       channel.sendFile(jdaSources).queue();
       channel.sendFile(jdaJavadocs).queue();
   }
   
   public void sendBotFile (MessageChannel channel) {
       File botFile = new File(".\\DiscordBot.zip");
       channel.sendFile(botFile).queue();
   }
   
   public void sendPart1 (MessageChannel channel) {
       File botFile = new File(".\\DiscordBotPart1.zip");
       channel.sendFile(botFile).queue();
   }
   
   public void sendSolution (MessageChannel channel) {
       String solution = "```public void onMessageReceived(MessageReceivedEvent event) {\r\n" + 
               "        \r\n" + 
               "        MessageChannel channel = event.getChannel(); //this returns an object representing the channel that the event was fired.\r\n" + 
               "        String msg = event.getMessage().getContentDisplay(); //this returns a String representation of the event.\r\n" + 
               "        User user = event.getAuthor();  //this returns the User that fired the event.\r\n" + 
               "        Boolean bot = user.isBot(); //this returns if the User that fired the event was a bot or not (prevents bots replying to bots!)\r\n" + 
               "        \r\n" + 
               "        if (!bot) {\r\n" + 
               "            channel.sendMessage(msg).queue(); //If user is not a bot, sends the same message they sent to the channel!\r\n" + 
               "        }      \r\n" + 
               "    }```";
       channel.sendMessage(solution).queue();
   }
   
   public void sendMainMethod(MessageChannel channel) {
       String solution = "```package bot;\r\n" + 
               "\r\n" + 
               "import net.dv8tion.jda.core.AccountType;\r\n" + 
               "import net.dv8tion.jda.core.JDA;\r\n" + 
               "import net.dv8tion.jda.core.JDABuilder;\r\n" + 
               "import net.dv8tion.jda.core.entities.Message;\r\n" + 
               "import net.dv8tion.jda.core.entities.MessageChannel;\r\n" + 
               "import net.dv8tion.jda.core.entities.User;\r\n" + 
               "import net.dv8tion.jda.core.events.message.MessageReceivedEvent;\r\n" + 
               "import net.dv8tion.jda.core.hooks.ListenerAdapter;\r\n" + 
               "\r\n" + 
               "public class DiscordBot extends ListenerAdapter {\r\n" + 
               "\r\n" + 
               "    public static void main(String[] args) {\r\n" + 
               "        \r\n" + 
               "        try {\r\n" + 
               "            JDA jda = new JDABuilder(AccountType.BOT).setToken(\"NDI3NTcwMjI0NzEyMTIyMzk4.DZnHtA.RYQPqh5J7iWLQsguZY9ujxl6E00\").addEventListener(new DiscordBot()).buildBlocking();\r\n" + 
               "            \r\n" + 
               "        } catch (Exception e) {\r\n" + 
               "            e.printStackTrace();\r\n" + 
               "        }\r\n" + 
               "    }\r\n" + 
               "}\r\n" + 
               "    ```";
       channel.sendMessage(solution).queue();
   }
   
   public void sendSourceCode(MessageChannel channel) {
       String solution = "```package bot;\r\n" + 
               "\r\n" + 
               "import net.dv8tion.jda.core.AccountType;\r\n" + 
               "import net.dv8tion.jda.core.JDA;\r\n" + 
               "import net.dv8tion.jda.core.JDABuilder;\r\n" + 
               "import net.dv8tion.jda.core.entities.Message;\r\n" + 
               "import net.dv8tion.jda.core.entities.MessageChannel;\r\n" + 
               "import net.dv8tion.jda.core.entities.User;\r\n" + 
               "import net.dv8tion.jda.core.events.message.MessageReceivedEvent;\r\n" + 
               "import net.dv8tion.jda.core.hooks.ListenerAdapter;\r\n" + 
               "\r\n" + 
               "public class DiscordBot extends ListenerAdapter {\r\n" + 
               "\r\n" + 
               "    public static void main(String[] args) {\r\n" + 
               "        \r\n" + 
               "        try {\r\n" + 
               "            JDA jda = new JDABuilder(AccountType.BOT).setToken(\"NDI3NTcwMjI0NzEyMTIyMzk4.DZnHtA.RYQPqh5J7iWLQsguZY9ujxl6E00\").addEventListener(new DiscordBot()).buildBlocking();\r\n" + 
               "            \r\n" + 
               "        } catch (Exception e) {\r\n" + 
               "            e.printStackTrace();\r\n" + 
               "        }\r\n" + 
               "    }\r\n" + 
               "    \r\n" + 
               "    @Override\r\n" + 
               "    public void onMessageReceived(MessageReceivedEvent event) {\r\n" + 
               "        \r\n" + 
               "        MessageChannel channel = event.getChannel(); //this returns an object representing the channel that the event was fired.\r\n" + 
               "        String msg = event.getMessage().getContentDisplay(); //this returns a String representation of the event.\r\n" + 
               "        //User user = event.getAuthor();  //this returns the User that fired the event.\r\n" + 
               "        //Boolean bot = user.isBot(); //this returns if the User that fired the event was a bot or not (prevents bots replying to bots!)\r\n" + 
               "        \r\n" + 
               "        if (msg.equalsIgnoreCase(\"hi\")) {\r\n" + 
               "            channel.sendMessage(\"Hello!\").queue(); //Sends the message to the channel.\r\n" + 
               "        }      \r\n" + 
               "    }\r\n" + 
               "}\r\n" + 
               "```";
       channel.sendMessage(solution).queue();
   }
}

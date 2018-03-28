package bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.RoleManager;
import net.dv8tion.jda.core.requests.Route;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This is the core of the bot. Logins the bot to discord. Tells the bot what to do in response to user messages.
 * Creates and calls the Reddit, Music, Cleverbot, and Hangman classes as needed.
 * 
 * TODO: a lot of bugs. 
 * @author Perry
 *
 */
public class Aikari extends ListenerAdapter{
    
    Comp1510 comp1510 = new Comp1510();
    
    private static User userBound; //the variable that holds user of guessing number game
    private static User rpsUser; //the variable that holds user of rps game
    private static int randomNumber;
    private static int randomrps;
    private int attempts = 0; //guessing number attempts
    private static boolean spamBot = false;
    private static boolean guessNumber = false;
    private static boolean rps = false;
    private static boolean hangmanSolve = false;
    
    List<Role> roles = new ArrayList<Role>();
    
    Guild guild;
    
    Random rand = new Random();
    Hangman hangmanGame;
    
    Reddit redditBot = new Reddit();
    
    Music musicBot = new Music();
    
    Cleverbot cleverBot = new Cleverbot("hellothere");
    
    OkHttpClient client = new OkHttpClient();
    public static ArrayList <Long> messageList = new ArrayList <Long>(); 
    
    public static void generateRandom() {
        Random rand = new Random();
        randomNumber = rand.nextInt(10) + 1;
        randomrps = rand.nextInt(3) + 1;
    }
    
    public static void main(String[] arguments) throws Exception
    {
      
        generateRandom();
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken("NDA0OTAyOTA0Mjk0NTM5Mjc2.DWOGFA.lzvwgEQy8MB9ARUVFMnAVGdX6c0")           //The token of the account that is logging in.
                    .addEventListener(new Aikari())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
    
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        
        //These are provided with every event in JDA
        JDA jda = event.getJDA();  //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
                                                        //  This could be a TextChannel, PrivateChannel, or Group!
        
        Guild guild = event.getGuild();    
        
        String msg = message.getContentDisplay();              //This returns a human readable version of the Message. Similar to
                                                        // what you would see in the client.
        long msgID = message.getIdLong();
        messageList.add(msgID);
        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that
                                                        // sent the Message is a BOT or not!
        
        
        
        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!
            
            List<Role> roles = new ArrayList<Role>();
            roles = member.getRoles();
            
            String name;
            if (message.isWebhookMessage())
            {
                name = author.getName();                //If this is a Webhook message, then there is no Member associated
            }                                           // with the User, thus we default to the author for name.
            else
            {
                name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
            }                                           // otherwise it will default to their username. (User#getName())

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }
        else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
        {
            //The message was sent in a PrivateChannel.
            //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }
        else if (event.isFromType(ChannelType.GROUP))   //If this message was sent to a Group. This is CLIENT only!
        {
            //The message was sent in a Group. It should be noted that Groups are CLIENT only.
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.

            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
        }
        
        if (msg.equals("!commands")) {
             author.openPrivateChannel().queue(new Consumer<PrivateChannel>() {

                @Override
                public void accept(PrivateChannel privateChannel) {
                    privateChannel.sendMessage("```Commands list (probably not updated): \n\n !commands \n pls help \n pls clear \n pls delete"
                            + "\n pls purge (use with caution, you have been warned) \n !guessnumber \n !rps \n !hangman \n play youtubelink \n skip \n stop "
                            + "\n\n reddit subreddit (i.e reddit memes) \n reddit top post subreddit index timeSpan (i.e reddit top nba 1 month - "
                            + "gets the #1 top post in /r nba in the past month) \n reddit hot post subreddit index  \n reddit random subreddit (gets a random post in the subreddit) \n\n hungry \n @Platypus message (activate Cleverbot) \n pls spam \n pls nospam \n if spam is toggled on, "
                            + "get ready for a lot of spam lol ```").queue();
                }
             });
        }
        
        if (hangmanSolve) {
            hangmanGame.checkGuess(msg, channel);
        }
        
        if (msg.equals("!solve")) {
            hangmanSolve = true;
            channel.sendMessage("Please enter your guess: ").queue();
        }
        
        else {
            hangmanSolve = false;
        }
        
        if (msg.equals("pls help") || msg.equals("pls halp") || msg.equals("halp") || msg.equals("help")) {
            channel.sendMessage("Help is on the way, " + author.getAsMention()).queue();
            channel.sendMessage("Opps help can't help you since you don't have Platypus insurance, " + author.getAsMention()).queueAfter(10, TimeUnit.SECONDS);;
        }
        if (msg.equals("pls nospam") && spamBot) {
            spamBot = false;
            event.getChannel().sendMessage("Okay, I will try to not spam").queue();
        }
        
        if (msg.equals("pls spam") && !spamBot) {
            spamBot = true;
            event.getChannel().sendMessage("Thanks, now I can spam again.").queue();
        }
        
        if (msg.equals("\uD83D\uDC40")) {
            channel.sendMessage(":eyes:   I see you, " + author.getAsMention()).queue();
        }
        
        if (msg.contains("\uD83D\uDDE1")) {
            channel.sendMessage("Violence is not the solution, " + author.getAsMention()).queue();
        }
        
        if (event.getMessage().getContentRaw().equals("!guessnumber")) {
           userBound = event.getAuthor();
           generateRandom();
            //number = rand.nextInt(10) + 1;
            event.getChannel().sendMessage("Hello, " + event.getAuthor().getAsMention() + ", guess a number between 1-10")
            .queue(messsageSent -> {userBound = event.getAuthor();});
            guessNumber = true;
        } 
        
        if (attempts < 5 && guessNumber) {
            if ((Integer.parseInt(event.getMessage().getContentRaw()) != 0 && event.getAuthor() == userBound)) {
                int numberChoosen = Integer.parseInt(event.getMessage().getContentRaw());
                attempts++;
                if (numberChoosen == randomNumber) {
                    event.getChannel().sendMessage("You guessed correctly! It took you " + attempts + " attempts!").queue();;
                    userBound = null;
                    guessNumber = false;
                }
                else {
                    if (numberChoosen > randomNumber) {
                        event.getChannel().sendMessage("Your guess is too high, try again!").queue();
                    }
                    else {
                        event.getChannel().sendMessage("Your guess is too low, try again!").queue();
                    }
                }
             }
        }
        
        if (attempts == 5 && guessNumber) {
            event.getChannel().sendMessage("You have ran out of attempts. The correct number is " + randomNumber + ".").queue();
            userBound = null;
            guessNumber = false;
            attempts = 0;
        }
        
        if (msg.equals("!rps")) {
            generateRandom();
            rpsUser = author;
            channel.sendMessage(author.getAsMention() + ", I have picked Rock, Scissors, or Paper. What do you pick? ").queue();
        }
        
        if (msg.equalsIgnoreCase("rock") && author == rpsUser) {
            if (randomrps == 1) {
                channel.sendMessage("It's a tie since I also selected rock, " + author.getAsMention()).queue();
            }
            if (randomrps == 2) {
                channel.sendMessage("I chose Paper, so you lost, rip " + author.getAsMention()).queue();
            }
            if (randomrps == 3) {
                channel.sendMessage("Opps I chose Scissors, but you lost anyways cuz I said so, " + author.getAsMention()).queue();
            }
            rpsUser = null;
        }
        
        if (msg.equalsIgnoreCase("paper") && author == rpsUser) {
            if (randomrps == 1) {
                channel.sendMessage("Opps I chose Rock, but you lost anyways cuz I said so, " + author.getAsMention()).queue();
            }
            if (randomrps == 2) {
                channel.sendMessage("It's a tie since I also selected paper, " + author.getAsMention()).queue();
            }
            if (randomrps == 3) {
                channel.sendMessage("I chose Scissors, so you lost, rip " + author.getAsMention()).queue();
            }
            rpsUser = null;
        }
        
        if (msg.equalsIgnoreCase("scissors") && author == rpsUser) {
            if (randomrps == 1) {
                channel.sendMessage("I chose Rock, so you lost, rip, " + author.getAsMention()).queue();
            }
            if (randomrps == 2) {
                channel.sendMessage("Opps I chose Paper, but you lost anyways cuz I said so, " + author.getAsMention()).queue();
            }
            if (randomrps == 3) {
                channel.sendMessage("It's a tie since I also selected scissors, " + author.getAsMention()).queue();
            }
            rpsUser = null;
        }
        
        if (msg.equals("pls purge")) {
            channel.sendMessage("Purging will delete the last 100 messages in this channel. Are you sure, " + author.getAsMention() + "?").queue();
            TextChannel textChannel = event.getTextChannel();
            ArrayList <Message> msglist = new ArrayList <Message>();
            channel.sendMessage("Opps I don't have a terminate option for this command, "
                    + "deleting the last 100 messages...").queueAfter(10, TimeUnit.SECONDS);
            for (Message oldmsg : textChannel.getIterableHistory().complete()) {
                msglist.add(oldmsg);
            }
            textChannel.deleteMessages(msglist).queueAfter(15, TimeUnit.SECONDS);
            
        }
                      
        if (msg.equals("pls clear")) {
            for (int i = 0; i<messageList.size(); i++) {
                channel.deleteMessageById(messageList.get(i).longValue()).queue();
            }
            channel.sendMessage("Deleting all messages since I came online...").queue();;
            messageList.clear();
        }
        
        if (msg.equals("pls delete")) { //deletes last 2 messages (user's pls delete and one other, and removes them.
            channel.deleteMessageById(messageList.get(messageList.size()-2));
            channel.deleteMessageById(messageList.get(messageList.size()-1));
            messageList.remove(messageList.size()-1);
            messageList.remove(messageList.size()-1);
        }
        
        if (msg.equals("!hangman")) {
            hangmanGame = new Hangman(channel);
            hangmanGame.drawHangman(channel, null);
        }
        
        if (msg.length() == 1 && hangmanGame.getStart()) {
            hangmanGame.drawHangman(channel, msg);
        }
        
        if (msg.equals("!roll"))
        {
            //In this case, we have an example showing how to use the Success consumer for a RestAction. The Success consumer
            // will provide you with the object that results after you execute your RestAction. As a note, not all RestActions
            // have object returns and will instead have Void returns. You can still use the success consumer to determine when
            // the action has been completed!

            Random random = new Random();
            int roll = random.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
            channel.sendMessage("Your roll: " + roll).queue(sentMessage ->  //This is called a lambda statement. If you don't know
            {                                                               // what they are or how they work, try google!
                if (roll < 3)
                {
                    channel.sendMessage("The roll for messageId: " + sentMessage.getId() + " wasn't very good... Must be bad luck!\n").queue();
                }
            });
        }
        
        if (msg.contains("vermintide")) {
            try {
                redditBot.getSubredditPictures(channel, author, "vermintide");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("something went wrong with trying vermintides, so im catching rats.");
                e.printStackTrace();
            }
        }
        
        if (msg.equals("pls reddit")) {
            String str = "hi";
            try {
               str = run("https://www.reddit.com/r/nba/");
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            channel.sendMessage(str).queue();
        }

        else if (msg.startsWith("reddit random post")) {
            String subreddit = msg.substring(19);
            try {
                redditBot.getSubredditRandomPost(channel, author, subreddit);
            } catch (ArrayIndexOutOfBoundsException e) {
                // TODO Auto-generated catch block
                System.out.println("Sorry, either the subreddit is not a real subreddit or something exploded."
                        + " Cannot access " + subreddit);
            }
        }
        
        else if (msg.startsWith("reddit top post")) {

            int index;
            String time;
            String subreddit;
            
            String[] splits = msg.split(" ");
            
            subreddit = splits[3];
            
            if (splits.length < 5) {
                index = 1; //if user doesnt give an index, default it to 1 (the top post)
                time = "day";
            }
            
            else if (splits.length < 6) {
                index = Integer.valueOf(splits[4]);
                time = "day";
            }
            else {
                index = Integer.valueOf(splits[4]);
                time = splits[5];
            }
            
            System.out.println("The index is: " + index);
            System.out.println("the subreddit is: " + subreddit);
            System.out.println("the time is: " + time);
            
            try {
                redditBot.getSubredditTopPost(channel, author, subreddit, index, time);
            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                System.out.println("Sorry, either the subreddit is not a real subreddit or something exploded."
                        + " Cannot access " + subreddit);
            }
        }
        
        else if (msg.startsWith("reddit hot post")) {
            int index;
            String subreddit;
            
            String[] splits = msg.split(" ");
            
            subreddit = splits[3];
            
            if (splits.length < 5) {
                index = 1; //if user doesnt give an index, default it to 1 (the top post)
            }
            else {
                index = Integer.valueOf(splits[4]);
            }
            //System.out.println("The index is: " + index);
            //System.out.println("the subreddit is: " + subreddit);
            try {
                redditBot.getSubredditHotPost(channel, author, subreddit, index);
            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                System.out.println("Sorry, either the subreddit is not a real subreddit or something exploded."
                        + " Cannot access " + subreddit);
            }
        }
        else if (msg.startsWith("reddit")) {
            String subreddit = msg.substring(7);
            System.out.println("subreddit name: " + subreddit);
            try {
                redditBot.getSubredditPictures(channel, author, subreddit);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                channel.sendMessage("Sorry, either the subreddit is not a real subreddit or something exploded."
                        + " Cannot pull images from /r " + subreddit).queue();
                e.printStackTrace();
            }
        }
       
        else if (msg.equals("reddit food") || msg.contains("hungry")){
            if (!bot) {
                redditBot.getFood(channel, author);
            }
        }
        
        if (!bot && msg.contains("play") && msg.contains("youtube")) {
            String id;
            id = msg.substring(msg.lastIndexOf("=") + 1);
            
            musicBot.loadMusic(guild, id, channel);
        }
        
        if (msg.equals("skip")) { //skips to the next music in queue
            musicBot.trackScheduler.nextTrack();
        }
        
        if (msg.equals("stop")) { //this just disconnects the bot, but doesnt clear its queue
            System.out.println("disconnecting...");
            channel.sendMessage("Disconnecting from voice channel... ");
            musicBot.disconnect(guild.getAudioManager());
        }
        
        if (msg.contains("@Platypus")) {
            System.out.println("calling cleverbot");
            String string = msg.substring(9);
            System.out.println("String is: " + string);
            
            String reply;
            reply = cleverBot.getReply(string);
            channel.sendMessage(reply).queue();
        }
        
        if (msg.equals("!coinflip")) {
            comp1510.flipCoin(channel);
        }
        
        if (msg.startsWith("Choose")) {
            
            String newMessage = msg.substring(7);
            List<String> stringList = Arrays.asList(newMessage.split(","));
            System.out.println("the stringList is: " + stringList);
            String random = comp1510.randomPicker(stringList.toArray(new String[stringList.size()]));
            channel.sendMessage("I have chosen " + random + " for you, " + author.getAsMention()).queue();
        }
        
        if (msg.equalsIgnoreCase("!botJDAFiles")) {
            comp1510.sendFiles(channel);
        }
        
        if (msg.equalsIgnoreCase("!mimicBotSolution")) {
            comp1510.sendSolution(channel);
        }
                
        if (msg.equalsIgnoreCase("!botSourceCode")){
            comp1510.sendBotFile(channel);
        }
        
        if (msg.equalsIgnoreCase("!botCodePart1")) {
            comp1510.sendPart1(channel);
        }
        
        if (msg.equalsIgnoreCase("!printBotMainMethod")) {
            comp1510.sendMainMethod(channel);
        }
        
        if (msg.equalsIgnoreCase("!printBotFullCode")) {
            comp1510.sendSourceCode(channel);
        }
        
        /*RoleManager roleManager = new RoleManager(roles.get(0));
        roleManager.revokePermissions(perms) */
    }

    @Override
    public void onUserTyping(UserTypingEvent event) {
        
        JDA jda = event.getJDA();      
        
        Emote emote = jda.getEmoteById(422163585343422476L);
        Emote emotePeek = jda.getEmoteById(404801550993719296L);
        Emote emoteDagger = jda.getEmoteById(405151476428701696L);
        
        User author = event.getUser();
        MessageChannel channel = event.getChannel();    
        //188521975809966080>
        if ((spamBot && author.getIdLong() != 323734050147794944L) || spamBot && (author != userBound || author != rpsUser)) {
            if (author.getIdLong() != 323734050147794944L) {
                Random rand = new Random();
                int random = rand.nextInt(3);
                if (random == 0) {
                    channel.sendMessage("Stop typing, " + author.getAsMention() + "!" + emoteDagger.getAsMention() + emoteDagger.getAsMention() + emoteDagger.getAsMention()).queue();
                }
                else if (random == 1) {
                    channel.sendMessage(emotePeek.getAsMention() + "I see you typing, " + author.getAsMention() + "!").queue();
                }
                else if (random ==2) {
                    channel.sendMessage("Please type faster, " + author.getAsMention()).queue();
                }
            }
        }
    }
   
    
    
    @Override
    public void onUserGameUpdate(UserGameUpdateEvent event) {
        JDA jda = event.getJDA();      
        User author = event.getUser();
        Game game = event.getCurrentGame();
        Emote emote = jda.getEmoteById(422163585343422476L);
        Emote emotePeek = jda.getEmoteById(404801550993719296L);
        Emote emoteDagger = jda.getEmoteById(405151476428701696L);
        
        List <Guild> server = author.getMutualGuilds();
        
        MessageChannel channel;
        for (int i = 0; i<server.size(); i++) {
            channel = server.get(i).getDefaultChannel();
            if (spamBot) {
                channel.sendMessage(author.getAsMention() + " is now playing " + game + " instead of studying.").queue();
                channel.sendMessage("Why does this print twice.").queue();
            }
        }
    }
    
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event ) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        User user = event.getUser();
        
        user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {

            @Override
            public void accept(PrivateChannel privateChannel) {
                privateChannel.sendMessage("Here is the source code for the Discord Bot that you will be coding! "
                        + "You can either copy the text inside these files into your Class, or type them yourself.").queue();
                File botFile1 = new File(".\\DiscordBotPart1.zip");
                privateChannel.sendFile(botFile1, "MainMethodOnlySourceCode.zip").queue();
                File botFile2 = new File(".\\DiscordBot.zip");
                privateChannel.sendFile(botFile2, "DiscordBotFullSourceCode.zip").queue();
                
                privateChannel.sendMessage("Also, if you have not yet downloaded the JDA file, please download this now: ").queue();
                File jdaDependency = new File(".\\JDA-3.5.0_327-withDependencies.jar");
                privateChannel.sendFile(jdaDependency).queue();
            }
         });
    }
    
    String run(String url) throws IOException {
        System.out.println("inside run");
        Request request = new Request.Builder()
            .url("https://raw.githubusercontent.com/square/okhttp/master/samples/guide/src/main/java/okhttp3/guide/GetExample.java")
            .build();

        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());
        return response.body().string();
      }
}

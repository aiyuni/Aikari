/**
 * 
 */
package bot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.tree.RootCommentNode;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * TODO: still a few bugs.
 * Provides the reddit API portion of Aikari using JRAW 1.0.0.  Allows fetching of posts/images from subreddits.
 * @author Perry
 *
 */
public class Reddit {
    ArrayList<String> gifsList = new ArrayList<String>();
    List<String> images = new ArrayList<String>();
    List<String> text = new ArrayList<String>();
    List<String> posts = new ArrayList<String>();
    Credentials oauthCreds;
    UserAgent userAgent;
    RedditClient reddit;
    
    MessageChannel msgChannel;
    TextChannel textChannel;
    User user;
    
    Random rand = new Random();
    
    public Reddit(MessageChannel channel, User user) {
        oauthCreds = Credentials.script("deadbysunset", "perry26845", "ytsd_aYqtkrPjw", "Dui-Ls_Q6rAPJJ_HBSD0ffhYKIQ");
        userAgent = new UserAgent("bot", "platypusScript", "1.0.0", "deadbysunset");
        reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);
        Account me = reddit.me().about();
        
        msgChannel = channel;
        this.user = user;
    }
    
    public Reddit() {
        oauthCreds = Credentials.script("deadbysunset", "perry26845", "ytsd_aYqtkrPjw", "Dui-Ls_Q6rAPJJ_HBSD0ffhYKIQ");
        userAgent = new UserAgent("bot", "platypusScript", "1.0.0", "deadbysunset");
        reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);
        Account me = reddit.me().about();
    }
 
    public void getSubredditPictures(MessageChannel channel, User user, String subreddit) throws Exception {
        images.clear();
        msgChannel = channel;
        this.user = user;
        
        DefaultPaginator<Submission> pictures = reddit.subreddit(subreddit).posts().build();
        for (Submission s : pictures.next()) {
            if (!s.isSelfPost() && s.getUrl().contains("i.imgur.com") || !s.isSelfPost() && s.getUrl().contains("i.redd.it") 
                    || !s.isSelfPost() && s.getUrl().contains(".png")) {
                images.add(s.getUrl());
            }
        }

        URL url = null;
        System.out.println("The size of the images list is: " + images.size());
        try {
            url = new URL(images.get(rand.nextInt(images.size())));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream link = null;
        try {
            link = url.openConnection().getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        msgChannel.sendMessage("Here's a random image from /r " + subreddit + ", " + user.getAsMention()).queue();
        msgChannel.sendFile(link, "hi.jpg").queue();

    }
    
    /**
     * Randomly gets a post from a specific subreddit
     * @param channel
     * @param user
     * @param subreddit
     * @throws ArrayIndexOutOfBoundsException
     */
    public void getSubredditRandomPost(MessageChannel channel, User user, String subreddit) throws ArrayIndexOutOfBoundsException {
        images.clear();
        msgChannel = channel;
        this.user = user;
        
        msgChannel.sendMessage("Here's a random post from /r " + subreddit + "```" + reddit.subreddit(subreddit).randomSubmission().getSubject().getTitle()+
                "\n" + reddit.subreddit(subreddit).randomSubmission().getSubject().getSelfText() + "```").queue();

        /*msgChannel.sendMessage("Here's the Top 3 posts today in /r " + subreddit + ", " + user.getAsMention()).queue();
        msgChannel.sendMessage(posts.get(0)).queue(); */

    }
    
    /**
     * Gets a `top` post in a specific reddit
     * @param channel channel to send the post to
     * @param user user that requested the post 
     * @param subreddit subreddit in question
     * @param index the # that represents the top post (1 for the most top post, 2 for the 2nd top post, etc...)
     * @param time the time to search for (day, week, month, year, all)
     */
    public void getSubredditTopPost(MessageChannel channel, User user, String subreddit, int index, String time) {
        images.clear();
        msgChannel = channel;
        this.user = user;
        
        TimePeriod duration = null;
        
        if (time.equalsIgnoreCase("day")) {
            duration = TimePeriod.DAY;
        }
        
        else if (time.equalsIgnoreCase("week")) {
            duration = TimePeriod.WEEK;
        }
        
        else if (time.equalsIgnoreCase("month")) {
            duration = TimePeriod.MONTH;
        }
        
        else if (time.equalsIgnoreCase("year")) {
            duration = TimePeriod.YEAR;
        }
        
        else if (time.equalsIgnoreCase("all") || time.equalsIgnoreCase("alltime")) {
            duration = TimePeriod.ALL;
        }
        
        DefaultPaginator<Submission> postings = 
                reddit.subreddit(subreddit).posts().sorting(SubredditSort.TOP).timePeriod(duration).limit(index).build();
        
        Submission submission; 
        
        do {
        submission = postings.iterator().next().get(index-1);
        System.out.println("loops");
        } while (submission.isStickied());
        
        String title = submission.getTitle();
        String body;
        InputStream link;
        
        msgChannel.sendMessage("The number " + index + " top post in /r " + subreddit + " for the past " + time + " is: " ).queue();
        
        if (!submission.isSelfPost()) {
            body = submission.getUrl();
            msgChannel.sendMessage("```" + title + "```").queue();
            msgChannel.sendMessage("" + body ).queue();
        }
        else {
            body = postings.getCurrent().get(index-1).getSelfText();
            msgChannel.sendMessage("```" + title + "\n" + body + "```").queue();
        }
    }
    
    public void getSubredditHotPost(MessageChannel channel, User user, String subreddit, int index) {
        images.clear();
        msgChannel = channel;
        this.user = user;

        DefaultPaginator<Submission> postings = 
                reddit.subreddit(subreddit).posts().sorting(SubredditSort.HOT).limit(index).build();
        
        Submission submission; 
        
        do {
        submission = postings.iterator().next().get(index-1);
        System.out.println("loops");
        } while (submission.isStickied());
        
        String title = submission.getTitle();
        String body;
        InputStream link;
        
        msgChannel.sendMessage("The number " + index + " hot post in /r " + subreddit + " is: " ).queue();
        
        if (!submission.isSelfPost()) {
            body = submission.getUrl();
            msgChannel.sendMessage("```" + title + "```").queue();
            msgChannel.sendMessage("" + body ).queue();
        }
        else {
            body = postings.getCurrent().get(index-1).getSelfText();
            msgChannel.sendMessage("```" + title + "\n" + body + "```").queue();
        }
    }
   
    public void getMemes(MessageChannel channel, User user) {
        images.clear();
        msgChannel = channel;
        this.user = user;
        
        DefaultPaginator<Submission> memes = reddit.subreddits("Memes", "dankmemes", "wholesomememes").build();

        for (Submission s : memes.next()) {
            if (!s.isSelfPost() && s.getUrl().contains("i.imgur.com") || !s.isSelfPost() && s.getUrl().contains("i.redd.it") ) {
                images.add(s.getUrl());
            }
        }
        
        URL url = null;
        try {
            url = new URL(images.get(rand.nextInt(images.size())));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream link = null;
        try {
            link = url.openConnection().getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        msgChannel.sendMessage("Here's your random meme, " + user.getAsMention()).queue();
        msgChannel.sendFile(link, "hi.jpg").queue();
    }
    
    public void getFood(MessageChannel channel, User user) {
        images.clear();
        msgChannel = channel;
        this.user = user;
        
        DefaultPaginator<Submission> foods = reddit.subreddits("food", "foodporn").build();

        for (Submission s : foods.next()) {
            if (!s.isSelfPost() && s.getUrl().contains("i.imgur.com") || !s.isSelfPost() && s.getUrl().contains("i.redd.it") ) {
                images.add(s.getUrl());
            }
        }
        
        URL url = null;
        try {
            url = new URL(images.get(rand.nextInt(images.size())));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream link = null;
        try {
            link = url.openConnection().getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        msgChannel.sendMessage("Are you hungry, " + user.getAsMention() + "?").queue();
        msgChannel.sendFile(link, "gethungry.jpg").queue();
    }
}
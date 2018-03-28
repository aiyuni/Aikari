package bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import net.dv8tion.jda.core.entities.MessageChannel;

/**
 * Simple class that provides the hangman game upon user request.  
 * Words are stored locally in hangmanlist. 
 * No categories are provided (maybe implement this somehow?)
 * @author Perry
 *
 */
public class Hangman {
    
    private static boolean hangman = false; //when this is true, it signals that a hangman game is ongoing
    
    private Set <String> guesses = new TreeSet<String>();
    private String hangmanWord;
    private String hiddenWord;
    private int hangmanAttempts = 0;
      
    public Hangman(MessageChannel channel) {
        System.out.println("This constructor will set up the random word.");
        
        hangman = true;
        channel.sendMessage("Starting a hangman game...").queue();
        File wordFile = new File(".\\hangmanlist");
        List <String> wordList = new ArrayList <String>();
        Scanner scan;
        try {
            scan = new Scanner(wordFile);
            while (scan.hasNext()) {
                wordList.add(scan.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        Random rand = new Random();
        
        hangmanWord = wordList.get(rand.nextInt(wordList.size()));    
        hiddenWord = hangmanWord;
        for (int i =0; i<hangmanWord.length(); i++) {
            hiddenWord = hiddenWord.replace(hiddenWord.charAt(i), '_');
        }
        System.out.println(hangmanWord);
        System.out.println(hiddenWord);
    }
    
    public void drawHangman(MessageChannel channel, String guess) {
        if (guess!= null) {
            guesses.add(guess);
        }
        //If guess is not in word, increase attempts (reduce lives)
        if (guess != null && !hangmanWord.contains(guess)){
            hangmanAttempts++;
        }
        
        if (guess != null && hangmanWord.contains(guess)) {
            for (int i = 0; i < hangmanWord.length(); i++) {
                if (hangmanWord.charAt(i) == guess.charAt(0)) {
                    char[] hiddenArray = hiddenWord.toCharArray();
                    hiddenArray[i] = guess.charAt(0);
                    hiddenWord = String.valueOf(hiddenArray);
                }
            }
            
            if (hangmanWord.equals(hiddenWord)) {
                channel.sendMessage("You have won! ").queue();
                hangman = false;
                hangmanAttempts = 0;
            }
        }
        
        if (hangmanAttempts == 0 && hangman) {
                    
            channel.sendMessage("```...............  \n┃.......... ┋\n┃.......... ┋ \n┃..........   \n┃..........  \n┃.......... \n┃..........   ```").queue(); 
       }
            
            if (hangmanAttempts == 1) {
                channel.sendMessage("```...............  \n┃.......... ┋\n┃.......... ┋\n┃........  \uD83D\uDE04 \n┃..........  \n┃.......... \n┃.......... ```").queue();
         
            }
            
            if (hangmanAttempts == 2) {
                channel.sendMessage("```............... \n┃.......... ┋\n┃.......... ┋\n┃........  \uD83D\uDE04\n┃.......... |\n┃.......... \n┃..........  ```").queue();
            }
            
            if (hangmanAttempts == 3) {
                channel.sendMessage("```...............  \n┃.......... ┋\n┃.......... ┋\n┃........  \uD83D\uDE04\n┃........../|\n┃..........\n┃..........   ```").queue();
            }
            
            if (hangmanAttempts == 4) {
                channel.sendMessage("```...............  \n┃.......... ┋\n┃.......... ┋\n┃........  \uD83D\uDE04\n┃........../|\\ \n┃..........\n┃..........  ```").queue();
            }
            
            if (hangmanAttempts == 5) {
                channel.sendMessage("```...............  \n┃.......... ┋\n┃.......... ┋\n┃........  \uD83D\uDE04\n┃........../|\\ \n┃........../\n┃..........```").queue();
            }
            
            else if(hangmanAttempts == 6) {
                channel.sendMessage("```You lost. Please appreciate this beauty of art. \n ...............  \n┃.......... ┋\n┃.......... ┋\n┃........  \uD83D\uDE26\n┃........../|\\ \n┃........../\\\n┃..........```").queue();
                hangman = false;
                hangmanAttempts = 0;
                channel.sendMessage("The actual word was: " + hangmanWord).queue();
            }
            
            if (hangmanAttempts != 6) {
                channel.sendMessage("```The word is: " + hiddenWord + " \nYour guesses thus far are: " + guesses.toString() + "```  " ).queue();
            }
    }
    
    public void checkGuess(String msg, MessageChannel channel) {
        if (msg.equals(hangmanWord)) {
            channel.sendMessage("You have won! ").queue();
            channel.sendMessage("```The word is: " + hangmanWord + " \nYour guesses were: " + guesses.toString() + "```  " ).queue();
            hangman = false;
            hangmanAttempts = 0;
        }
    }
    
    //Returns true if there is a hangman game ongoing, false otherwise.
    public boolean getStart() {
        return hangman;
    }
}

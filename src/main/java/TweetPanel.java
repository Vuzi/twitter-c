import twitter4j.TwitterException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel containing the text area and button to post tweets.
 *
 * Created by Vuzi on 20/09/2015.
 */
public class TweetPanel extends JPanel {

    private JTextArea tweet;
    private JButton tweetButton;

    /**
     * Default constructor.
     */
    public TweetPanel() {
        init();
    }

    /**
     * Initialize the panel's content.
     */
    private void init() {
        GridBagConstraints gcb;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("Tweet"));

        tweet = new JTextArea();
        tweet.setPreferredSize(new Dimension(200, 50));
        tweet.setMinimumSize(new Dimension(200, 50));
        tweetButton = new JButton("Tweet this !");
        tweetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendTweet();
            }
        });

        gcb = new GridBagConstraints();
        gcb.gridx = 0;
        gcb.gridy = 1;
        gcb.weightx = 1.0;
        gcb.fill = GridBagConstraints.BOTH;
        this.add(tweet, gcb);

        gcb = new GridBagConstraints();
        gcb.gridx = 0;
        gcb.gridy = 2;
        gcb.weightx = 1.0;
        gcb.weighty = 1.0;
        gcb.fill = GridBagConstraints.BOTH;
        this.add(tweetButton, gcb);
    }

    /**
     * Send the content of the tweet to twitter.
     */
    private void sendTweet() {
        String content = tweet.getText();
        try {
            App.TWITTER.updateStatus(content);
            JOptionPane.showMessageDialog(this, "Tweet posted!");
            tweet.setText("");
        } catch (TwitterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while posting tweet : " + e.getErrorMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

import twitter4j.AccountSettings;
import twitter4j.TwitterException;

import javax.swing.*;
import java.awt.*;

/**
 * Panel containing the user information.
 *
 * Created by Vuzi on 20/09/2015.
 */
public class UserPanel extends JPanel {

    private JLabel icon;
    private JLabel login;

    /**
     * Default constructor.
     */
    public UserPanel() {
        init();
    }

    /**
     * Initialize the panel's content.
     */
    private void init() {
        GridBagConstraints gcb;
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(100, 70));
        this.setMaximumSize(new Dimension(100, 70));
        this.setBorder(BorderFactory.createTitledBorder("User"));

        icon = new JLabel();
        login = new JLabel();

        gcb = new GridBagConstraints();
        gcb.gridx = 0;
        gcb.gridy = 0;
        gcb.weightx = 1.0;
        gcb.fill = GridBagConstraints.HORIZONTAL;
        this.add(login, gcb);

        // TODO image

        updateInfo();
    }

    /**
     * Update the user information.
     */
    public void updateInfo() {
        try {
            AccountSettings account = App.TWITTER.getAccountSettings();
            login.setText(account.getScreenName());
            // TODO image
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }
}

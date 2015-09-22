import javax.swing.*;
import java.awt.*;

/**
 * Main panel.
 *
 * Created by Vuzi on 20/09/2015.
 */
public class MainPanel extends JPanel {

    // User info
    private UserPanel userPanel;

    // User tweet zone
    private TweetPanel tweetPanel;

    // User timeline
    private TimelinePanel timelinePanel;

    /**
     * Default constructor.
     */
    public MainPanel() {
        init();
    }

    /**
     * Initialize the panel's content.
     */
    private void init() {
        GridBagConstraints gcb;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userPanel = new UserPanel();
        tweetPanel = new TweetPanel();
        timelinePanel = new TimelinePanel();

        // User info
        gcb = new GridBagConstraints();
        gcb.gridx = 0;
        gcb.gridy = 0;
        gcb.fill = GridBagConstraints.BOTH;
        this.add(userPanel, gcb);

        // Tweet zone
        gcb = new GridBagConstraints();
        gcb.gridx = 1;
        gcb.gridy = 0;
        gcb.weightx = 1.0;
        gcb.fill = GridBagConstraints.BOTH;
        this.add(tweetPanel, gcb);

        // Timeline
        gcb = new GridBagConstraints();
        gcb.gridx = 1;
        gcb.gridy = 1;
        gcb.weightx = 1.0;
        gcb.weighty = 1.0;
        gcb.fill = GridBagConstraints.BOTH;
        this.add(timelinePanel, gcb);
    }
}

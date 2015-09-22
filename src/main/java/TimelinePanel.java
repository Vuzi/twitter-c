import twitter4j.Status;
import twitter4j.TwitterException;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Timer;

/**
 * Timeline panel.
 *
 * Created by Vuzi on 20/09/2015.
 */
public class TimelinePanel extends JPanel {

    private JScrollPane timelineScrollPane;
    private JList<Status> timeline;

    /**
     * Default constructor.
     */
    public TimelinePanel() {
        init();
    }

    /**
     * Initialize the panel's content.
     */
    private void init() {
        GridBagConstraints gcb;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("Timeline"));

        timeline = new JList<Status>();
        timeline.setCellRenderer(new StatusCellRender());

        timelineScrollPane = new JScrollPane(timeline);

        gcb = new GridBagConstraints();
        gcb.gridx = 0;
        gcb.gridy = 0;
        gcb.weightx = 1.0;
        gcb.weighty = 1.0;
        gcb.fill = GridBagConstraints.BOTH;
        this.add(timelineScrollPane, gcb);

        updateInfo();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateInfo();
            }
        }, 0, 60000); // Every minute
    }

    /**
     * Update the timeline.
     */
    private void updateInfo() {
        try {
            timeline.removeAll();
            timeline.setListData(new Vector<Status>(App.TWITTER.getHomeTimeline()));
            timeline.updateUI();
        } catch (TwitterException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }
}

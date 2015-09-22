import twitter4j.Status;

import javax.swing.*;
import java.awt.*;

/**
 * Status cell renderer.
 *
 * Created by Vuzi on 20/09/2015.
 */
public class StatusCellRender implements ListCellRenderer<Status> {

    /**
     * Status cell.
     */
    private class StatusCell extends JPanel {

        public StatusCell(Status status, boolean selected) {

            GridBagConstraints gcb;

            this.setLayout(new GridBagLayout());
            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

            // Date
            gcb = new GridBagConstraints();
            gcb.gridx = 0;
            gcb.gridy = 0;
            gcb.weightx = 1.0;
            gcb.fill = GridBagConstraints.HORIZONTAL;
            this.add(new JLabel("Date : " + status.getCreatedAt().toString()), gcb);

            // Author
            gcb = new GridBagConstraints();
            gcb.gridx = 0;
            gcb.gridy = 1;
            gcb.weightx = 1.0;
            gcb.fill = GridBagConstraints.HORIZONTAL;
            this.add(new JLabel("<html>By   : <b>" + status.getUser().getScreenName() + "</b> (" + status.getUser().getName() + ")</html>"), gcb);

            // Content
            gcb = new GridBagConstraints();
            gcb.gridx = 0;
            gcb.gridy = 2;
            gcb.weightx = 1.0;
            gcb.weighty = 1.0;
            gcb.fill = GridBagConstraints.BOTH;
            this.add(new JLabel(status.getText()), gcb);

            if(selected)
                this.setBackground(new Color(255, 254, 237));
        }

    }

    public Component getListCellRendererComponent(JList<? extends Status> list, Status value, int index, boolean isSelected, boolean cellHasFocus) {
        return new StatusCell(value, isSelected);
    }
}

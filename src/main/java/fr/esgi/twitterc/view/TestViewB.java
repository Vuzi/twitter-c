package fr.esgi.twitterc.view;

import fr.esgi.twitterc.view.controller.ViewController;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

/**
 * Example panel B.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class TestViewB extends ViewController {

    /**
     * Constructor for a view controller.
     */
    public TestViewB() {
        super();
    }

    @Override
    public void onCreation() {
        System.out.println("Creation");
    }

    @Override
    public void onShow() {
        System.out.println("Showed");
    }

    @Override
    public void onHide() {
        System.out.println("Hidden");
    }

    @Override
    public void onDeletion() {
        System.out.println("Deleted");
    }

    @Override
    public String getID() {
        return null;
    }

    public void back(ActionEvent actionEvent) {
        // Go back
        getWindowController().showBack();
    }

    public void showA(ActionEvent actionEvent) {
        // Create or re-use a view
        getWindowController().showOrReuseView("TestViewA.fxml", TestViewA.ID);
    }
    public void showB(ActionEvent actionEvent) {
        // Force creation
        getWindowController().showView("TestViewB.fxml");
    }
}

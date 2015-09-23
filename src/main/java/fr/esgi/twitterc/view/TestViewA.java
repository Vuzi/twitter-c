package fr.esgi.twitterc.view;

import fr.esgi.twitterc.view.controller.ViewController;
import javafx.event.ActionEvent;

/**
 * Example panel A.
 *
 * Created by Vuzi on 23/09/2015.
 */
public class TestViewA extends ViewController {

    public static String ID = "A";

    /**
     * Constructor for a view controller.
     */
    public TestViewA() {
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
        return ID;
    }

    public void action(ActionEvent actionEvent) {

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

    public void window(ActionEvent actionEvent) {
        // Create a new window
        getAppController().createWindow("New window", "TestViewA.fxml");
    }
}

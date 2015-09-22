package fr.esgi.twitterc.view.controller;

import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Abstract view controller. Any view must extends this class.
 *
 * Created by Vuzi on 22/09/2015.
 */
public abstract class ViewController implements Initializable {

    private WindowController windowController;
    private AppController appController;
    private Node view;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Constructor for a view controller.
     */
    public ViewController() {
        super();
    }

    /**
     * Return the windows controller.
     *
     * @return The window controller.
     */
    public WindowController getWindowController() {
        return windowController;
    }

    /**
     * Set the windows controller.
     *
     * @param windowController The window controller.
     */
    public void setWindowController(WindowController windowController) {
        this.windowController = windowController;
        this.appController = windowController.getAppController();
    }

    /**
     * Return the application controller.
     *
     * @return The appliocation controller.
     */
    public AppController getAppController() {
        return appController;
    }

    public Node getView() {
        return view;
    }

    public void setView(Node view) {
        this.view = view;
    }

    /**
     * Abstract method called at the creation of the view. Note that the view is created as needed, and this will
     * likely be fires only one time.
     */
    protected abstract void onCreation();

    /**
     * Abstract method called when the view is showed.
     */
    protected abstract void onShow();

    /**
     * Abstract method called when the view id hid.
     */
    protected abstract void onHide();

    /**
     * Abstract method called at the deletion of the view. Note that will be called when the view's window is disposed,
     * not at the real object destruction.
     */
    protected abstract void onDeletion();

    /**
     * Return the identifier of the view. To be reusable, a view should provided a non-null ID. If null is provided,
     * the view will be indicated as non re-usable.
     *
     * @return The view identifier.
     */
    protected abstract String getID();

    /**
     * True if re usable, false otherwise.
     *
     * @return True or false.
     */
    public boolean isReusable() {
        return getID() != null;
    }
}

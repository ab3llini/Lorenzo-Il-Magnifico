package client.view.gui;/*
 * Created by albob on 22/06/2017.
 */

public enum View {

    Connect("connect.fxml", "Connect", 400, 650),
    Lobby("Lobby.fxml", "Lobby", 400, 650),
    Gui("GUI.fxml", "Lorenzo il Magnifico", 1400, 1000);

    private String filename;
    private String title;
    private int w,h;

    View(String filename, String title, int w, int h) {

        this.filename = filename;
        this.title = title;
        this.w = w;
        this.h = h;

    }

    @Override
    public String toString() {
        return this.filename;
    }

    public String getTitle() {
        return title;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}

package client.view.gui;/*
 * Created by albob on 22/06/2017.
 */

public enum View {

    Connect("Connect.fxml", "Connect", 400, 650),
    Lobby("Lobby.fxml", "Lobby", 400, 650),
    Gui("GUI.fxml", "Lorenzo il Magnifico", 1400, 1000),
    StandardPlacement("StandardPlacement.fxml", "Select placement options", 300, 350),
    ImmediatePlacement("ImmediatePlacement.fxml", "Select immediate action options", 300, 200),
    CouncilPrivilegeSelection("CouncilPrivilegeSelection.fxml", "Select a council privilege", 300, 200),
    SelectFamilyMember("SelectFamilyMember.fxml", "Select a family member", 300, 200),
    SelectConversion("SelectConversion.fxml", "Select a conversion option", 300, 200),
    SelectCost("SelectCost.fxml", "Select a cost option", 300, 200),
    SelectDiscount("DecideDiscountOption.fxml", "Select a discount option", 300, 200),
    DraftLeaderCards("LeaderCardDraft.fxml", "Select a leader card to draft!", 1020, 450),
    SelectBanOption("SelectBanOption.fxml", "Select a leader card to draft!", 1020, 450);



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

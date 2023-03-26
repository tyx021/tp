package tfifteenfour.clipboard.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import tfifteenfour.clipboard.logic.CurrentSelection;
import tfifteenfour.clipboard.logic.Logic;
import tfifteenfour.clipboard.logic.PageType;

/**
 * A UI for navigation bar.
 */
public class NavigationBar extends UiPart<Region> {

    private static final String FXML = "NavigationBar.fxml";

    @FXML
    private Label status;

    /**
     * Constructs a navigation bar based on current page selection.
     */
    public NavigationBar(Logic logic) {
        super(FXML);
        CurrentSelection currentSelection = logic.getCurrentSelection();

        if (currentSelection.getCurrentPage() == PageType.GROUP_PAGE) {
            status.setText(currentSelection.getSelectedCourse().getCourseCode());

        } else if (currentSelection.getCurrentPage() == PageType.STUDENT_PAGE) {
            String courseCode = currentSelection.getSelectedCourse().getCourseCode();
            String groupName = currentSelection.getSelectedGroup().getGroupName();

            status.setText(courseCode + " > " + groupName);
        }
    }
}

package tfifteenfour.clipboard.testutil;

import java.io.InputStream;

import tfifteenfour.clipboard.logic.CurrentSelection;
import tfifteenfour.clipboard.logic.PageType;
import tfifteenfour.clipboard.model.Model;
import tfifteenfour.clipboard.model.ModelManager;
import tfifteenfour.clipboard.model.Roster;
import tfifteenfour.clipboard.model.UserPrefs;
import tfifteenfour.clipboard.model.util.SampleDataUtil;

/**
 * A utility class containing a list of {@code Student} objects to be used in tests.
 */
public class TypicalModel {
//        private static Path sampleFilePath = Paths.get("data", "sampleRoster.json");
        private static InputStream sampleResourceStream = TypicalModel.class.getResourceAsStream("/assets/sampleRoster.json");
        private Model typicalModel;


        public TypicalModel() {
                Roster typicalRoster = getTypicalRoster();
                CurrentSelection typicalCurrentSelection = new CurrentSelection();

                typicalCurrentSelection.selectCourse(typicalRoster.getUnmodifiableCourseList().get(0));
                typicalCurrentSelection.selectGroup(typicalCurrentSelection.getSelectedCourse().getUnmodifiableGroupList().get(0));
                typicalCurrentSelection.selectStudent(typicalCurrentSelection.getSelectedGroup().getUnmodifiableFilteredStudentList().get(0));
                typicalCurrentSelection.selectSession(typicalCurrentSelection.getSelectedGroup().getUnmodifiableFilteredSessionList().get(0));
                typicalCurrentSelection.selectTask(typicalCurrentSelection.getSelectedGroup().getUnmodifiableTaskList().get(0));

                typicalCurrentSelection.setCurrentPage(PageType.STUDENT_PAGE);
                this.typicalModel = new ModelManager(typicalRoster, new UserPrefs());
        }

        private static Roster getTypicalRoster() {
                Roster tmp = new Roster(SampleDataUtil.getTypicalRoster(sampleResourceStream));
                return tmp;
//                return new Roster(SampleDataUtil.getSampleRoster(sampleFilePath, sampleResourceStream));
        }

        public Model getTypicalModel() {
                return this.typicalModel.copy();
        }



}

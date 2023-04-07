package tfifteenfour.clipboard.logic.commands.editcommand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.DESC_AMY;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.DESC_BOB;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.assertCommandFailure;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tfifteenfour.clipboard.logic.commands.editcommand.EditStudentCommand.MESSAGE_EDIT_PERSON_SUCCESS;
import static tfifteenfour.clipboard.logic.commands.editcommand.EditStudentCommand.createEditedStudent;
import static tfifteenfour.clipboard.testutil.Assert.assertThrows;
import static tfifteenfour.clipboard.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tfifteenfour.clipboard.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;
import tfifteenfour.clipboard.commons.core.index.Index;
import tfifteenfour.clipboard.logic.CurrentSelection;
import tfifteenfour.clipboard.logic.commands.ClearCommand;
import tfifteenfour.clipboard.logic.commands.CommandResult;
import tfifteenfour.clipboard.logic.commands.exceptions.CommandException;
import tfifteenfour.clipboard.logic.parser.EditCommandParser.EditStudentDescriptor;
import tfifteenfour.clipboard.model.ModelManager;
import tfifteenfour.clipboard.model.course.Course;
import tfifteenfour.clipboard.model.course.Group;
import tfifteenfour.clipboard.model.course.Session;
import tfifteenfour.clipboard.model.student.Email;
import tfifteenfour.clipboard.model.student.Name;
import tfifteenfour.clipboard.model.student.Phone;
import tfifteenfour.clipboard.model.student.Remark;
import tfifteenfour.clipboard.model.student.Student;
import tfifteenfour.clipboard.model.student.StudentId;
import tfifteenfour.clipboard.model.task.Task;
import tfifteenfour.clipboard.testutil.EditStudentDescriptorBuilder;

public class EditStudentCommandTestNew {

    private Student JOHN = new Student(
            new Name("John Doe"), new Phone("98765432"), new Email("johndoe@example.com"),
            new StudentId("A1234567M"), new Remark("Likes to play video games"));
    private Student JANE = new Student(
            new Name("Jane Smith"), new Phone("91234567"), new Email("janesmith@example.com"),
            new StudentId("A2345678M"), new Remark("Enjoys reading books"));
    private Student BOB = new Student(
            new Name("Bob Lee"), new Phone("94567890"), new Email("boblee@example.com"),
            new StudentId("A3456789M"), new Remark("Plays basketball on weekends"));

    // Test case where all the student details are edited successfully
    @Test
    public void execute_editStudentDetails_success() {
        ModelStubWithSelectedGroup modelStub = new ModelStubWithSelectedGroup();
        List<Student> lastShownList = modelStub.getCurrentSelection().getSelectedGroup()
                .getUnmodifiableFilteredStudentList();
        Index index = Index.fromOneBased(1);

        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptorBuilder()
                .withName("New Name")
                .withEmail("new.email@example.com")
                .withPhone("91234567")
                .build();

        EditCommand editCommand = new EditStudentCommand(index, editStudentDescriptor);

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        Student editedStudent = createEditedStudent(studentToEdit, editStudentDescriptor);

        String expectedMessage = String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedStudent);
        CommandResult expectedResult = new CommandResult(editCommand, expectedMessage, true);

        ModelStubWithSelectedGroup expectedModel = new ModelStubWithSelectedGroup();
        expectedModel.getCurrentSelection().selectGroup(modelStub.getCurrentSelection().getSelectedGroup());
        expectedModel.getCurrentSelection().getSelectedGroup().setStudent(studentToEdit, editedStudent);

        assertCommandSuccess(editCommand, modelStub, expectedResult, expectedModel);
    }

    @Test
    public void execute_duplicateStudentUnfilteredList_failure() {
        ModelStubWithSelectedGroup modelStub = new ModelStubWithSelectedGroup();
        List<Student> lastShownList = modelStub.getCurrentSelection().getSelectedGroup()
                .getUnmodifiableFilteredStudentList();
        Index index = Index.fromOneBased(1);

        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptorBuilder()
                .withName("New Name")
                .withEmail("new.email@example.com")
                .withPhone("91234567")
                .build();

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(studentToEdit).build();

        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_SECOND_PERSON, descriptor);
        assertCommandFailure(editStudentCommand, modelStub, EditStudentCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_nonExistentStudent_throwsCommandException() {
        Index index = Index.fromOneBased(99);

        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptorBuilder()
                .withStudentId("A1111111M")
                .build();

        EditStudentCommand editStudentCommand = new EditStudentCommand(index, new EditStudentDescriptor());
        ModelStubWithSelectedGroup modelStub = new ModelStubWithSelectedGroup();

        assertThrows(CommandException.class, () -> editStudentCommand.execute(modelStub));
    }

    @Test
    public void execute_onWrongPage_throwsCommandException() {
        ModelStubWithSelectedGroup modelStub = new ModelStubWithSelectedGroup();
        modelStub.getCurrentSelection().navigateBackFromStudentPage();
        Index index = Index.fromOneBased(1);

        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptorBuilder()
                .withName("New Name")
                .withEmail("new.email@example.com")
                .withPhone("91234567")
                .build();


        EditStudentCommand editStudentCommand = new EditStudentCommand(index, new EditStudentDescriptor());
        assertThrows(CommandException.class, () -> editStudentCommand.execute(modelStub));
    }


    @Test
    public void equals() {
        final EditStudentCommand standardCommand = new EditStudentCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditStudentDescriptor copyDescriptor = new EditStudentDescriptor(DESC_AMY);
        EditStudentCommand commandWithSameValues = new EditStudentCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditStudentCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditStudentCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }



    class ModelStubWithSelectedGroup extends ModelManager {
        private Course selectedCourse;
        private Group selectedGroup;
        private Session selectedSession;
        private Task selectedTask;

        public ModelStubWithSelectedGroup() {
            selectedCourse = new Course("CS2105");
            selectedGroup = new Group("Group 1");
            selectedSession = new Session("Tutorial1");
            selectedTask = new Task("Assignment 1");


            selectedCourse.addGroup(selectedGroup);
            selectedGroup.addSession(selectedSession);
            selectedGroup.addTask(selectedTask);

            selectedGroup.addStudent(JOHN);
            selectedGroup.addStudent(JANE);
            selectedGroup.addStudent(BOB);


            CurrentSelection currentSelection = this.getCurrentSelection();
            currentSelection.selectCourse(selectedCourse);
            currentSelection.selectGroup(selectedGroup);
//            currentSelection.selectStudent(JOHN);
//            selectedGroup.addMember(new Person("Jane"));
        }
    }
}

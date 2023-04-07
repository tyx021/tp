package tfifteenfour.clipboard.logic.commands.editcommand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.DESC_AMY;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.DESC_BOB;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.assertCommandFailure;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tfifteenfour.clipboard.logic.commands.CommandTestUtil.showStudentAtIndex;
import static tfifteenfour.clipboard.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tfifteenfour.clipboard.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tfifteenfour.clipboard.commons.core.Messages;
import tfifteenfour.clipboard.commons.core.index.Index;
import tfifteenfour.clipboard.logic.commands.ClearCommand;
import tfifteenfour.clipboard.logic.parser.EditCommandParser.EditStudentDescriptor;
import tfifteenfour.clipboard.model.Model;
import tfifteenfour.clipboard.model.course.Group;
import tfifteenfour.clipboard.model.student.Student;
import tfifteenfour.clipboard.testutil.EditStudentDescriptorBuilder;
import tfifteenfour.clipboard.testutil.StudentBuilder;
import tfifteenfour.clipboard.testutil.TypicalModel;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditStudentCommand.
 */
public class EditStudentCommandTest {

    private Model model = new TypicalModel().getTypicalModel();
    private Group actualSelectedGroup = model.getCurrentSelection().getSelectedGroup();
//    private Model model;
//    private Group actualSelectedGroup;
    private Student typicalStudent = model.getCurrentSelection().getSelectedStudent();


    @BeforeEach
    void setUp() {
        model = new TypicalModel().getTypicalModel();
        actualSelectedGroup = model.getCurrentSelection().getSelectedGroup();
        typicalStudent = model.getCurrentSelection().getSelectedStudent();
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
//        Student editedStudent = model.getCurrentSelection().getSelectedStudent();
//        model = new TypicalModel().getTypicalModel();

        Student editedStudent = new StudentBuilder(typicalStudent).build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(editedStudent).build();
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedStudent);

        Model expectedModel = model.copy();

        Group selectedGroup = expectedModel.getCurrentSelection().getSelectedGroup();
        selectedGroup.setStudent(selectedGroup.getUnmodifiableFilteredStudentList().get(0), editedStudent);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        model = new TypicalModel().getTypicalModel();

        Index indexLastStudent = Index.fromOneBased(actualSelectedGroup.getUnmodifiableStudentList().size());
        Student lastStudent = actualSelectedGroup.getUnmodifiableStudentList().get(indexLastStudent.getZeroBased());

        StudentBuilder studentInList = new StudentBuilder(lastStudent);
        Student editedStudent = studentInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB).build();

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();
        EditStudentCommand editStudentCommand = new EditStudentCommand(indexLastStudent, descriptor);

        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedStudent);

        Model expectedModel = model.copy();

        Group expectedSelectedGroup = expectedModel.getCurrentSelection().getSelectedGroup();
        expectedSelectedGroup.setStudent(expectedSelectedGroup.getUnmodifiableFilteredStudentList().get(indexLastStudent.getZeroBased()), editedStudent);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_FIRST_PERSON, new EditStudentDescriptor());
        Student editedStudent = actualSelectedGroup.getUnmodifiableFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedStudent);

        Model expectedModel = model.copy();

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }


    // This tests if the command is executed correctly if the displayed list is filtered
    @Test
    public void execute_filteredList_success() {
        showStudentAtIndex(model, INDEX_FIRST_PERSON);

        Student studentInFilteredList = actualSelectedGroup.getUnmodifiableFilteredStudentList()
                .get(INDEX_FIRST_PERSON.getZeroBased());
        Student editedStudent = new StudentBuilder(studentInFilteredList).withName(VALID_NAME_BOB).build();
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_FIRST_PERSON,
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedStudent);

        Model expectedModel = model.copy();
        showStudentAtIndex(expectedModel, INDEX_FIRST_PERSON);

        Group expectedSelectedGroup = expectedModel.getCurrentSelection().getSelectedGroup();
        expectedSelectedGroup.setStudent(expectedSelectedGroup.getUnmodifiableFilteredStudentList().get(0), editedStudent);

        assertCommandSuccess(editStudentCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateStudentUnfilteredList_failure() {
        Student firstStudent = actualSelectedGroup.getUnmodifiableFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(firstStudent).build();
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editStudentCommand, model, EditStudentCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateStudentFilteredList_failure() {
        showStudentAtIndex(model, INDEX_FIRST_PERSON);

        // edit student in filtered list into a duplicate in address book
        Student studentInList = actualSelectedGroup.getUnmodifiableStudentList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditStudentCommand editStudentCommand = new EditStudentCommand(INDEX_FIRST_PERSON,
                new EditStudentDescriptorBuilder(studentInList).build());

        assertCommandFailure(editStudentCommand, model, EditStudentCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidStudentIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(actualSelectedGroup.getUnmodifiableFilteredStudentList().size() + 1);
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditStudentCommand editStudentCommand = new EditStudentCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editStudentCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidStudentIndexFilteredList_failure() {
        showStudentAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < actualSelectedGroup.getUnmodifiableStudentList().size());

        EditStudentCommand editStudentCommand = new EditStudentCommand(outOfBoundIndex,
                new EditStudentDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editStudentCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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

}

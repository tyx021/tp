package tfifteenfour.clipboard.logic.commands.deletecommand;

import static java.util.Objects.requireNonNull;

import java.util.List;

import tfifteenfour.clipboard.commons.core.Messages;
import tfifteenfour.clipboard.commons.core.index.Index;
import tfifteenfour.clipboard.logic.CurrentSelection;
import tfifteenfour.clipboard.logic.PageType;
import tfifteenfour.clipboard.logic.commands.CommandResult;
import tfifteenfour.clipboard.logic.commands.exceptions.CommandException;
import tfifteenfour.clipboard.model.Model;
import tfifteenfour.clipboard.model.course.Course;


public class DeleteCourseCommand extends DeleteCommand {
    public static final String COMMAND_TYPE_WORD = "course";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " " + COMMAND_TYPE_WORD
            + ": Deletes a course and ALL its students."
            + "Parameters: "
            + "INDEX\n"
            + "Example: " + COMMAND_WORD
            + " " + COMMAND_TYPE_WORD
            + " " + "1";

    public static final String MESSAGE_SUCCESS = "Course deleted: %1$s";

    private final Index index;

    public DeleteCourseCommand(Index index) {
        this.index = index;
    }

    public CommandResult execute(Model model, CurrentSelection currentSelection) throws CommandException {
        requireNonNull(model);

        if (currentSelection.getCurrentPage() != PageType.COURSE_PAGE) {
            throw new CommandException("Wrong page. Navigate to course page to delete course");
        }

        List<Course> lastShownList = model.getUnmodifiableFilteredCourseList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Course courseToDelete = lastShownList.get(index.getZeroBased());
        model.deleteCourse(courseToDelete);
        return new CommandResult(this, String.format(MESSAGE_SUCCESS, courseToDelete), willModifyState);
    }

    @Override
    public boolean equals(Object other) {
        return other == this;
    }
}
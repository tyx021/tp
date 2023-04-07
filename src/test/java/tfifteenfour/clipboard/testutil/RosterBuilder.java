package tfifteenfour.clipboard.testutil;

import tfifteenfour.clipboard.model.Roster;

/**
 * A utility class to help with building Roster objects.
 * Example usage: <br>
 *     {@code Roster ab = new RosterBuilder().withStudent("John", "Doe").build();}
 */
public class RosterBuilder {

    private Roster roster;

    public RosterBuilder() {
        roster = new Roster();
    }

    public RosterBuilder(Roster roster) {
        this.roster = roster;
    }

//    /**
//     * Adds a new {@code Student} to the {@code Roster} that we are building.
//     */
//    public RosterBuilder withStudent(Student student) {
//        roster.addStudent(student);
//        return this;
//    }

    public Roster build() {
        return roster;
    }
}

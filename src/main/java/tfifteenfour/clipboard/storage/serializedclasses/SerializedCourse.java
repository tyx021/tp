package tfifteenfour.clipboard.storage.serializedclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import tfifteenfour.clipboard.model.course.Course;

public class SerializedCourse {
    private String courseCode;
    private List<SerializedGroup> groups = new ArrayList<>();

    /**
     * Serializes a course into json format
     * @param course
     */
    public SerializedCourse(Course course) {
        this.courseCode = course.getCourseCode();
        this.groups = course.getUnmodifiableGroupList().stream()
                .map(group -> new SerializedGroup(group))
                .collect(Collectors.toList());
    }

    public SerializedCourse() {}


    // json property acts like a field name
    @JsonProperty("courseCode")
    public String getCourseCode() {
        return courseCode;
    }

    // Jackson (the library that manages json) automatically converts lists into array format in json
    @JsonProperty("groups")
    public List<SerializedGroup> getStudents() {
        return groups;
    }

    public Course toModelType() {
        Course newCourse = new Course(courseCode);
        this.groups.stream().forEach(group -> newCourse.addGroup(group.toModelType()));
        return newCourse;
    }
}

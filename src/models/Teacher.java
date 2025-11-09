package models;

import java.util.List;

public class Teacher extends User {
private List<String> assignedSubjects;

public Teacher(String id, String name, String email, String className, List<String> assignedSubjects) {
super(id, name, email, className);
this.assignedSubjects = assignedSubjects;
}

public List<String> getAssignedSubjects() {
return assignedSubjects;
}
}

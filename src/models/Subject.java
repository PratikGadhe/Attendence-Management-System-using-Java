package models;

public class Subject {
private String subjectId;
private String name;

public Subject(String subjectId, String name) {
this.subjectId = subjectId;
this.name = name;
}

public String getSubjectId() { return subjectId; }
public String getName() { return name; }
}
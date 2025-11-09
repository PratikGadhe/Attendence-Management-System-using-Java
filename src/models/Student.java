package models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Student maintains subject-wise date records: Map<subject, Map<LocalDate, Boolean>>
public class Student extends User {
// subject -> (date -> present)
private Map<String, Map<LocalDate, Boolean>> attendanceMap;

public Student(String id, String name, String email, String className) {
super(id, name, email, className);
attendanceMap = new HashMap<>();
}

public void ensureSubject(String subject) {
attendanceMap.putIfAbsent(subject, new HashMap<>());
}

public void markAttendance(String subject, LocalDate date, boolean present) {
ensureSubject(subject);
attendanceMap.get(subject).put(date, present);
}

public Map<String, Map<LocalDate, Boolean>> getAttendanceMap() {
return attendanceMap;
}
}

package services;

import models.Student;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class AttendanceManager {
    private Map<String, Student> students;
    private String[] subjects;
    private double threshold;

    public AttendanceManager(String[] subjects, double threshold) {
        this.students = new HashMap<>();
        this.subjects = subjects;
        this.threshold = threshold;
    }

    public void addStudent(Student s) {
        students.put(s.getId(), s);
        // ensure all subjects exist for student
        for (String sub : subjects) s.ensureSubject(sub);
    }

    public void removeStudent(String studentId) {
        students.remove(studentId);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public Map<String, Student> getStudentsMap() {
        return students;
    }

    public void markAttendance(String subject, LocalDate date, String studentId, boolean present) {
        Student s = students.get(studentId);
        if (s != null) s.markAttendance(subject, date, present);
    }

    // Calculate percentage for a student for a subject in a given month
    public double calculateMonthlyPercentage(Student s, String subject, YearMonth month) {
        Map<LocalDate, Boolean> recs = s.getAttendanceMap().get(subject);
        if (recs == null) return 0.0;
        int total = 0; int present = 0;
        for (Map.Entry<LocalDate, Boolean> e : recs.entrySet()) {
            LocalDate d = e.getKey();
            if (YearMonth.from(d).equals(month)) {
                total++;
                if (Boolean.TRUE.equals(e.getValue())) present++;
            }
        }
        return total == 0 ? 0.0 : (present * 100.0 / total);
    }

    // Calculate overall percentage across all subjects for a student in a given month
    public double calculateOverallMonthlyPercentage(Student s, YearMonth month) {
        double sumPercent = 0.0; int count = 0;
        for (String sub : subjects) {
            double p = calculateMonthlyPercentage(s, sub, month);
            // only include if there was at least one class
            Map<LocalDate, Boolean> recs = s.getAttendanceMap().get(sub);
            if (recs != null) {
                boolean any = recs.keySet().stream().anyMatch(d -> YearMonth.from(d).equals(month));
                if (any) { sumPercent += p; count++; }
            }
        }
        return count == 0 ? 0.0 : (sumPercent / count);
    }

    // Generate alerts for a month: subject-wise or overall below threshold
    public List<String> generateAlerts(YearMonth month) {
        List<String> alerts = new ArrayList<>();
        for (Student s : students.values()) {
            double overall = calculateOverallMonthlyPercentage(s, month);
            if (overall > 0 && overall < threshold) {
                String msg = s.getEmail() + "|Overall|" + String.format("%.2f", overall);
                alerts.add(msg);
            }
            // subject-wise
            for (String sub : subjects) {
                double p = calculateMonthlyPercentage(s, sub, month);
                // only if there were classes
                Map<LocalDate, Boolean> recs = s.getAttendanceMap().get(sub);
                boolean any = recs != null && recs.keySet().stream().anyMatch(d -> YearMonth.from(d).equals(month));
                if (any && p < threshold) {
                    String msg = s.getEmail() + "|" + sub + "|" + String.format("%.2f", p);
                    alerts.add(msg);
                }
            }
        }
        return alerts;
    }
}
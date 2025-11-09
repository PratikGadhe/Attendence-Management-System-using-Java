package utils;

import models.Student;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileHandler {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Load students.csv with header: studentId,rollNo,name,email,class
    public static List<Student> loadStudents(String path) throws IOException {
        List<Student> list = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(path));
        boolean first = true;
        for (String line : lines) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            // parts: studentId,rollNo,name,email,class
            String id = parts[0].trim();
            String name = parts[2].trim();
            String email = parts[3].trim();
            String className = parts.length > 4 ? parts[4].trim() : "S.Y B.Tech Comp (A)";
            Student s = new Student(id, name, email, className);
            list.add(s);
        }
        return list;
    }

    // attendance.csv header: date,subject,studentId,isPresent
    public static void loadAttendance(String path, Map<String, Student> studentMap) throws IOException {
        File f = new File(path);
        if (!f.exists()) return;
        List<String> lines = Files.readAllLines(Paths.get(path));
        boolean first = true;
        for (String line : lines) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] p = line.split(",");
            LocalDate d = LocalDate.parse(p[0].trim(), DF);
            String subject = p[1].trim();
            String sid = p[2].trim();
            boolean present = Boolean.parseBoolean(p[3].trim());
            Student s = studentMap.get(sid);
            if (s != null) s.markAttendance(subject, d, present);
        }
    }

    // write alerts to file
    public static void saveAlerts(String path, List<String> alerts) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String a : alerts) bw.write(a + System.lineSeparator());
        }
    }
}
package main;

import models.Student;
import services.AttendanceManager;
import utils.FileHandler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[] subjects = {"DS", "OOP", "DE"};
        AttendanceManager manager = new AttendanceManager(subjects, 75.0);
        
        // Load students
        try {
            List<Student> students = FileHandler.loadStudents("resources/students.csv");
            for (Student s : students) manager.addStudent(s);
            System.out.println("Loaded " + students.size() + " students.");
        } catch (Exception e) {
            System.out.println("Failed to load students: " + e.getMessage());
        }
        
        // Load attendance
        try {
            FileHandler.loadAttendance("resources/attendance.csv", manager.getStudentsMap());
            System.out.println("Loaded attendance data.");
        } catch (Exception e) {
            System.out.println("Failed to load attendance: " + e.getMessage());
        }
        
        Scanner sc = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n=== Attendance Management System ===");
            System.out.println("1. Mark Attendance");
            System.out.println("2. Generate Alerts");
            System.out.println("3. View Student Report");
            System.out.println("4. Show Today's Attendance");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine().trim());
            
            switch (choice) {
                case 1:
                    System.out.print("Enter subject: ");
                    String subject = sc.nextLine().trim();
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    LocalDate d = LocalDate.parse(sc.nextLine().trim());
                    
                    System.out.println("\n=== Roll Call for " + subject + " on " + d + " ===");
                    System.out.println("Press 'p' for Present, 'a' for Absent");
                    
                    List<Student> allStudents = new ArrayList<>(manager.getAllStudents());
                    allStudents.sort((s1, s2) -> s1.getId().compareTo(s2.getId()));
                    
                    for (Student student : allStudents) {
                        System.out.print("Roll No " + student.getId().substring(1) + " - " + student.getName() + ": ");
                        String response = sc.nextLine().trim().toLowerCase();
                        boolean present = response.equals("p");
                        manager.markAttendance(subject, d, student.getId(), present);
                    }
                    System.out.println("Attendance marked for all students!");
                    
                    // Show attendance summary
                    System.out.println("\n=== Attendance Summary for " + subject + " on " + d + " ===");
                    List<Student> presentStudents = new ArrayList<>();
                    List<Student> absentStudents = new ArrayList<>();
                    
                    for (Student student : allStudents) {
                        Map<LocalDate, Boolean> subjectAttendance = student.getAttendanceMap().get(subject);
                        if (subjectAttendance != null && subjectAttendance.containsKey(d)) {
                            if (subjectAttendance.get(d)) {
                                presentStudents.add(student);
                            } else {
                                absentStudents.add(student);
                            }
                        }
                    }
                    
                    System.out.println("\n✅ PRESENT (" + presentStudents.size() + " students):");
                    for (Student student : presentStudents) {
                        System.out.println("   Roll No " + student.getId().substring(1) + " - " + student.getName());
                    }
                    
                    System.out.println("\n❌ ABSENT (" + absentStudents.size() + " students):");
                    for (Student student : absentStudents) {
                        System.out.println("   Roll No " + student.getId().substring(1) + " - " + student.getName());
                    }
                    
                    System.out.println("\nTotal: " + (presentStudents.size() + absentStudents.size()) + " students");
                    System.out.println("Attendance Rate: " + String.format("%.2f", (presentStudents.size() * 100.0 / (presentStudents.size() + absentStudents.size()))) + "%");
                    break;
                    
                case 2:
                    System.out.print("Enter month (yyyy-MM): ");
                    YearMonth ym = YearMonth.parse(sc.nextLine().trim());
                    List<String> alerts = manager.generateAlerts(ym);
                    System.out.println("--- Simulated Alerts (will be saved to resources/alerts.txt) ---");
                    for (String a : alerts) {
                        String[] p = a.split("\\|");
                        String to = p[0];
                        String subjectLine = "Low Attendance Alert";
                        String body = "Dear Student, your attendance in " + p[1] + " is " + p[2] + "%\nPlease attend classes regularly; otherwise you may not be allowed for exams.";
                        System.out.println("To: " + to);
                        System.out.println("Subject: " + subjectLine);
                        System.out.println("Message: " + body);
                        System.out.println("---------------------------------------");
                    }
                    try {
                        FileHandler.saveAlerts("resources/alerts.txt", alerts);
                        System.out.println("Alerts saved to resources/alerts.txt");
                    } catch (Exception e) {
                        System.out.println("Failed to save alerts: " + e.getMessage());
                    }
                    break;
                    
                case 3:
                    System.out.print("Enter studentId: ");
                    String stid = sc.nextLine().trim();
                    System.out.print("Enter month (yyyy-MM): ");
                    YearMonth mm = YearMonth.parse(sc.nextLine().trim());
                    Student st = manager.getStudent(stid);
                    if (st == null) {
                        System.out.println("Student not found.");
                        break;
                    }
                    System.out.println("Monthly Percentages for " + st.getName() + " (" + mm + ")");
                    for (String sub : subjects) {
                        double p = manager.calculateMonthlyPercentage(st, sub, mm);
                        System.out.println(sub + ": " + String.format("%.2f", p) + "%");
                    }
                    double overall = manager.calculateOverallMonthlyPercentage(st, mm);
                    System.out.println("Overall: " + String.format("%.2f", overall) + "%");
                    break;
                    
                case 4:
                    System.out.print("Enter subject: ");
                    String showSubject = sc.nextLine().trim();
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    LocalDate showDate = LocalDate.parse(sc.nextLine().trim());
                    
                    System.out.println("\n=== Attendance for " + showSubject + " on " + showDate + " ===");
                    List<Student> presentList = new ArrayList<>();
                    List<Student> absentList = new ArrayList<>();
                    List<Student> allStudentsList = new ArrayList<>(manager.getAllStudents());
                    allStudentsList.sort((s1, s2) -> s1.getId().compareTo(s2.getId()));
                    
                    for (Student student : allStudentsList) {
                        Map<LocalDate, Boolean> subjectAttendance = student.getAttendanceMap().get(showSubject);
                        if (subjectAttendance != null && subjectAttendance.containsKey(showDate)) {
                            if (subjectAttendance.get(showDate)) {
                                presentList.add(student);
                            } else {
                                absentList.add(student);
                            }
                        }
                    }
                    
                    if (presentList.isEmpty() && absentList.isEmpty()) {
                        System.out.println("No attendance data found for " + showSubject + " on " + showDate);
                    } else {
                        System.out.println("\n✅ PRESENT (" + presentList.size() + " students):");
                        for (Student student : presentList) {
                            System.out.println("   Roll No " + student.getId().substring(1) + " - " + student.getName());
                        }
                        
                        System.out.println("\n❌ ABSENT (" + absentList.size() + " students):");
                        for (Student student : absentList) {
                            System.out.println("   Roll No " + student.getId().substring(1) + " - " + student.getName());
                        }
                        
                        System.out.println("\nTotal: " + (presentList.size() + absentList.size()) + " students");
                        System.out.println("Attendance Rate: " + String.format("%.2f", (presentList.size() * 100.0 / (presentList.size() + absentList.size()))) + "%");
                    }
                    break;
                    
                case 0:
                    System.out.println("Exiting...");
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
        
        sc.close();
    }
}
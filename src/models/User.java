package models;

public abstract class User {
protected String id;
protected String name;
protected String email;
protected String className; // e.g., "S.Y B.Tech Comp (A)"

public User(String id, String name, String email, String className) {
this.id = id;
this.name = name;
this.email = email;
this.className = className;
}

public String getId() { return id; }
public String getName() { return name; }
public String getEmail() { return email; }
public String getClassName() { return className; }
}
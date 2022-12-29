import java.util.ArrayList;
import java.util.Scanner;

public class CourseManagement {
    public static void main(String[] args) {
        while (true) {
            Session session = Session.getSession();
            System.out.print("Email: ");
            String email = session.inputScanner.nextLine();
            System.out.print("Password: ");
            String password = session.inputScanner.nextLine();
            try {
                User user = login(email, password);
                System.out.println("Welcome!");
                Action action = null;
                if (user instanceof Student) {
                    action = new StudentAction((Student) user);
                } else if (user instanceof Teacher) {
                    action = new TeacherAction((Teacher) user);
                } else if (user instanceof TA) {
                    action = new TAAction((TA) user);
                }
                action.handleActions();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public static User login(String email, String password) throws Exception {
        for (User user : Session.getSession().getUserList()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new Exception("User not found");
    }

    public static class Session {
        private static Session session=new Session();
        private User userList[] = new User[7];
        private Course courseList[] = new Course[6];
        public Scanner inputScanner = new Scanner(System.in);

        private Session() {
            createDatabase();
        }

        private void createDatabase() {
            this.userList[0] = new Student("student_a@northsouth.edu", "password", "A");
            this.userList[1] = new Student("student_b@northsouth.edu", "password", "B");
            this.userList[2] = new Student("student_c@northsouth.edu", "password", "C");
            this.userList[3] = new Teacher("teacher_a@northsouth.edu", "password", "A");
            this.userList[4] = new Teacher("teacher_b@northsouth.edu", "password", "B");
            this.userList[5] = new TA("assistant_a@northsouth.edu", "password", "A");
            this.userList[6] = new TA("assistant_b@northsouth.edu", "password", "B");

            this.courseList[0] = new Course("Course A", "A");
            this.courseList[1] = new Course("Course B", "A");
            this.courseList[2] = new Course("Course C", "B");
            this.courseList[3] = new Course("Course D", "A");
            this.courseList[4] = new Course("Course E", "B");
            this.courseList[5] = new Course("Course F", "C");
        }

        public static Session getSession() {

            return session;
        }

        public Course[] getCourseList() {
            return courseList;
        }

        public User[] getUserList() {
            return userList;
        }
    }

    static class User {
        private String email;
        private String password;
        private String role;


        public User(String email, String password, String role) {
            this.email = email;
            this.password = password;
            this.role = role;
        }

        public String getEmail() {
            return this.email;
        }

        public String getPassword() {
            return this.password;
        }

        public String getRole() {
            return this.role;
        }
    }

    static class Student extends User {
        private ArrayList<Course> enrolledCourses;

        public Student(String email, String password, String alice123) {
            super(email, password, "student");
            this.enrolledCourses = new ArrayList<>();
        }

        public void addCourse(Course course) throws Exception {
            for (Course c : this.enrolledCourses) {
                if (c.getName().equals(course.getName())) {
                    throw new Exception("Student is already enrolled in this course");
                }
            }
            this.enrolledCourses.add(course);
            course.addStudent(this);
        }

        public void removeCourse(Course course) {
            this.enrolledCourses.remove(course);
            course.removeStudent(this);
        }

        public ArrayList<Course> getEnrolledCourses() {
            return this.enrolledCourses;
        }

        public String getName() {
            return this.getEmail();
        }
    }

    static class Teacher extends User {
        private ArrayList<Course> assignedCourses;

        public Teacher(String email, String password, String dave123) {
            super(email, password, "teacher");
            this.assignedCourses = new ArrayList<>();
        }

        public void addCourse(Course course) {
            this.assignedCourses.add(course);
            course.setTeacher(this);
        }

        public void removeCourse(Course course) {
            this.assignedCourses.remove(course);
            course.setTeacher(null);
        }

        public ArrayList<Course> getAssignedCourses() {
            return this.assignedCourses;
        }

        public void addStudent(Course course, Student student) throws Exception {
            if (!this.assignedCourses.contains(course)) {
                throw new Exception("Teacher is not assigned to this course");
            }
            course.addStudent(student);
        }

        public void removeStudent(Course course, Student student) throws Exception {
            if (!this.assignedCourses.contains(course)) {
                throw new Exception("Teacher is not assigned to this course");
            }
            course.removeStudent(student);
        }
    }

    static class TA extends User {
        private ArrayList<Course> assignedCourses;

        public TA(String email, String password, String frank123) {
            super(email, password, "ta");
            this.assignedCourses = new ArrayList<>();
        }

        public void addCourse(Course course) {
            this.assignedCourses.add(course);
            //course.addTA(this);
        }

        public void removeCourse(Course course) {
            this.assignedCourses.remove(course);
            //course.removeTA(this);
        }

        public ArrayList<Course> getAssignedCourses() {
            return this.assignedCourses;
        }
    }

    static class Course {
        private String name;
        private Teacher teacher;
        private ArrayList<Student> students;
        private int capacity;


        public Course(String name, String s) {
            this.name = name;
            this.teacher = null;
            this.students = new ArrayList<>();
            this.capacity = 5;
        }

        public String getName() {
            return this.name;
        }

        public Teacher getTeacher() {
            return this.teacher;
        }

        public void setTeacher(Teacher teacher) {
            this.teacher = teacher;
        }

        public ArrayList<Student> getStudents() {
            return this.students;
        }

        public void addStudent(Student student) throws Exception {
            if (this.students.size() >= this.capacity) {
                throw new Exception("Course capacity has been reached");
            }
            this.students.add(student);
        }

        public void removeStudent(Student student) {
            this.students.remove(student);
        }
    }
    interface Action {
        public void handleActions();
    }

    static class StudentAction implements Action {
        private Student student;


        public StudentAction(Student student) {
            this.student = student;
        }

        @Override
        public void handleActions() {
            Scanner inputScanner = Session.getSession().inputScanner;
            while (true) {
                System.out.println("1. Add course");
                System.out.println("2. Remove course");
                System.out.println("3. View enrolled courses");
                System.out.println("4. Logout");

                int option = inputScanner.nextInt();
                inputScanner.nextLine();

                if (option == 1) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    Course[] courses = Session.getSession().getCourseList();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    try {
                        this.student.addCourse(course);
                        System.out.println("Course added successfully");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else if (option == 2) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    ArrayList<Course> courses = this.student.getEnrolledCourses();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    this.student.removeCourse(course);
                    System.out.println("Course removed successfully");
                } else if (option == 3) {
                    ArrayList<Course> courses = this.student.getEnrolledCourses();

                    System.out.println("Enrolled courses:");
                    for (Course c : courses) {
                        System.out.println(c.getName());
                    }
                } else if (option == 4) {
                    break;
                } else {
                    System.out.println("Invalid option");
                }
            }
        }
    }

    static class TeacherAction implements Action {
        private Teacher teacher;

        public TeacherAction(Teacher teacher) {
            this.teacher = teacher;
        }

        @Override
        public void handleActions() {
            Scanner inputScanner = Session.getSession().inputScanner;
            while (true) {
                System.out.println("1. Add course");
                System.out.println("2. Remove course");
                System.out.println("3. View assigned courses");
                System.out.println("4. Add student to course");
                System.out.println("5. Remove student from course");
                System.out.println("6. Logout");

                int option = inputScanner.nextInt();
                inputScanner.nextLine();

                if (option == 1) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    Course[] courses = Session.getSession().getCourseList();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    this.teacher.addCourse(course);
                    System.out.println("Course added successfully");
                } else if (option == 2) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    ArrayList<Course> courses = this.teacher.getAssignedCourses();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    this.teacher.removeCourse(course);
                    System.out.println("Course removed successfully");
                } else if (option == 3) {
                    ArrayList<Course> courses = this.teacher.getAssignedCourses();
                    System.out.println("Assigned courses:");
                    for (Course c : courses) {
                        System.out.println(c.getName());
                    }
                } else if (option == 4) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    ArrayList<Course> courses = this.teacher.getAssignedCourses();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    System.out.print("Enter student email: ");
                    String studentEmail = inputScanner.nextLine();
                    Student student = null;
                    User[] users = Session.getSession().getUserList();
                    for (User u : users) {
                        if (u instanceof Student && u.getEmail().equals(studentEmail)) {
                            student = (Student) u;
                            break;
                        }
                    }
                    if (student == null) {
                        System.out.println("Student not found");
                        continue;
                    }
                    try {
                        this.teacher.addStudent(course, student);
                        System.out.println("Student added successfully");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else if (option == 5) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    ArrayList<Course> courses = this.teacher.getAssignedCourses();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    System.out.print("Enter student email: ");
                    String studentEmail = inputScanner.nextLine();
                    Student student = null;
                    ArrayList<Student> students = course.getStudents();
                    for (Student s : students) {
                        if (s.getEmail().equals(studentEmail)) {
                            student = s;
                            break;
                        }
                    }
                    if (student == null) {
                        System.out.println("Student not found");
                        continue;
                    }
                    try {
                        this.teacher.removeStudent(course, student);
                        System.out.println("Student removed successfully");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else if (option == 6) {
                    break;
                } else {
                    System.out.println("Invalid option");
                }
            }
        }
    }

    static class TAAction implements Action {
        private TA ta;

        public TAAction(TA ta) {
            this.ta = ta;
        }

        @Override
        public void handleActions() {
            Scanner inputScanner = Session.getSession().inputScanner;
            while (true) {
                System.out.println("1. Add course");
                System.out.println("2. Remove course");
                System.out.println("3. View assigned courses");
                System.out.println("4. View students in course");
                System.out.println("5. Logout");

                int option = inputScanner.nextInt();
                inputScanner.nextLine();

                if (option == 1) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    Course[] courses = Session.getSession().getCourseList();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    this.ta.addCourse(course);
                    System.out.println("Course added successfully");
                } else if (option == 2) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    ArrayList<Course> courses = this.ta.getAssignedCourses();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    this.ta.removeCourse(course);
                    System.out.println("Course removed successfully");
                } else if (option == 3) {
                    ArrayList<Course> courses = this.ta.getAssignedCourses();
                    System.out.println("Assigned courses:");
                    for (Course c : courses) {
                        System.out.println(c.getName());
                    }
                } else if (option == 4) {
                    System.out.print("Enter course name: ");
                    String courseName = inputScanner.nextLine();
                    Course course = null;
                    ArrayList<Course> courses = this.ta.getAssignedCourses();
                    for (Course c : courses) {
                        if (c.getName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        System.out.println("Course not found");
                        continue;
                    }
                    ArrayList<Student> students = course.getStudents();
                    System.out.println("Students in course:");
                    for (Student s : students) {
                        System.out.println(s.getName());
                    }
                } else if (option == 5) {
                    break;
                } else {
                    System.out.println("Invalid option");
                }
            }
        }
    }
}





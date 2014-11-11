package edu.umass.ckc.wo.event.admin;

import ckc.servlet.servbase.ServletParams;


/**
 * The second in a series of events for creating a new Student.
 * Receives the fields that will be used to authenticate a user at login time.
 */
public class UserRegistrationAuthenticationInfoEvent extends UserRegistrationEvent {
    public static final String FNAME = "fname";
    public static final String LNAME = "lname";
    public static final String UNAME = "uname";
    public static final String MOMNAME = "momname";
    public static final String USERID = "userId";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String TEST_USER = "testUser";
    private String fname;
    private String lname;
    private String momName;
    private String password;
    private String userName;
    private String email;
    private String testUser;

    public void init(String fname, String lname, String email, String password, String userName, String testUser) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.testUser = testUser;
    }

    public UserRegistrationAuthenticationInfoEvent(ServletParams p) throws Exception {
        super(p);
        this.init(p.getString(FNAME),p.getString(LNAME),p.getString(EMAIL),p.getString(PASSWORD),p.getString(UNAME),
                p.getString(TEST_USER));
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLname() {
        return lname;
    }

    public void setMomName(String momName) {
        this.momName = momName;
    }

    public String getMomName() {
        return momName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String uname) {
        this.userName = uname;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isTestUser () {
        return this.testUser != null && this.testUser.equals("testUser");
    }
}
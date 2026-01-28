package com.example.legacy;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * UserManager class handles everything related to users
 * This is the main class for user operations
 * Warning: This class is very large and does many things
 */
public class UserManager {
    
    // Database connection - should probably be in a separate class
    private Connection dbConnection;
    private static UserManager instance;
    
    // Email configuration - hardcoded values, not good
    private String SMTP_HOST = "smtp.company.com";
    private String SMTP_PORT = "587";
    private String SMTP_USER = "system@company.com";
    private String SMTP_PASS = "hardcodedpassword123"; // TODO: Move to config
    
    // Password validation regex - maybe should be configurable
    private String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";
    
    // Singleton pattern - not thread safe!
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    // Constructor - does too many things
    public UserManager() {
        try {
            // Database setup - should be injected
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users", 
                "root", 
                "password123" // Another hardcoded password!
            );
            
            // Create tables if they don't exist - should be in migration scripts
            Statement stmt = dbConnection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), password VARCHAR(255), email VARCHAR(100), created_date TIMESTAMP, last_login TIMESTAMP, failed_login_attempts INT DEFAULT 0, is_locked BOOLEAN DEFAULT FALSE, role VARCHAR(20) DEFAULT 'USER', profile_data TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS user_sessions (session_id VARCHAR(255) PRIMARY KEY, user_id INT, created_date TIMESTAMP, expires_date TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_log (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT, action VARCHAR(100), timestamp TIMESTAMP, ip_address VARCHAR(45))");
            
        } catch (Exception e) {
            e.printStackTrace(); // Bad error handling
            System.exit(1); // Even worse!
        }
    }
    
    // This method does EVERYTHING related to user creation
    public String createUser(String username, String password, String email, String role, Map<String, String> profileData) {
        try {
            // Validation - should be in separate methods
            if (username == null || username.length() < 3 || username.length() > 20) {
                return "ERROR: Username must be 3-20 characters";
            }
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                return "ERROR: Username can only contain letters, numbers and underscores";
            }
            if (password == null || !password.matches(PASSWORD_REGEX)) {
                return "ERROR: Password must be at least 8 characters with upper, lower and digit";
            }
            if (email == null || !isValidEmail(email)) {
                return "ERROR: Invalid email format";
            }
            
            // Check if user already exists - SQL in business logic, bad!
            PreparedStatement checkStmt = dbConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ? OR email = ?");
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return "ERROR: Username or email already exists";
            }
            
            // Hash password - algorithm should be configurable
            String hashedPassword = hashPassword(password);
            
            // Insert user - more SQL in business logic
            PreparedStatement insertStmt = dbConnection.prepareStatement(
                "INSERT INTO users (username, password, email, created_date, role, profile_data) VALUES (?, ?, ?, NOW(), ?, ?)"
            );
            insertStmt.setString(1, username);
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, email);
            insertStmt.setString(4, role != null ? role : "USER");
            insertStmt.setString(5, profileDataToJson(profileData)); // Converting to JSON manually - should use library
            
            int result = insertStmt.executeUpdate();
            
            if (result > 0) {
                // Send welcome email - mixing concerns
                sendWelcomeEmail(email, username);
                
                // Log the action - more database code mixed in
                logUserAction(getUserIdByUsername(username), "USER_CREATED", getClientIP());
                
                return "SUCCESS: User created successfully";
            } else {
                return "ERROR: Failed to create user";
            }
            
        } catch (SQLException e) {
            e.printStackTrace(); // Bad error handling again
            return "ERROR: Database error occurred";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Unexpected error occurred";
        }
    }
    
    // Login method - also does too many things
    public Map<String, Object> loginUser(String username, String password, String ipAddress) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check if user exists and get user data - more SQL
            PreparedStatement stmt = dbConnection.prepareStatement(
                "SELECT id, username, password, email, is_locked, failed_login_attempts, last_login FROM users WHERE username = ?"
            );
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                result.put("success", false);
                result.put("message", "Invalid username or password");
                return result;
            }
            
            // Check if account is locked
            boolean isLocked = rs.getBoolean("is_locked");
            int failedAttempts = rs.getInt("failed_login_attempts");
            
            if (isLocked) {
                result.put("success", false);
                result.put("message", "Account is locked due to too many failed login attempts");
                return result;
            }
            
            // Verify password
            String storedPassword = rs.getString("password");
            if (!verifyPassword(password, storedPassword)) {
                // Increment failed attempts - more SQL mixed with business logic
                failedAttempts++;
                PreparedStatement updateStmt = dbConnection.prepareStatement(
                    "UPDATE users SET failed_login_attempts = ?, is_locked = ? WHERE username = ?"
                );
                updateStmt.setInt(1, failedAttempts);
                updateStmt.setBoolean(2, failedAttempts >= 5); // Magic number!
                updateStmt.setString(3, username);
                updateStmt.executeUpdate();
                
                result.put("success", false);
                result.put("message", "Invalid username or password");
                result.put("attempts_remaining", 5 - failedAttempts);
                return result;
            }
            
            // Successful login - reset failed attempts and update last login
            int userId = rs.getInt("id");
            String email = rs.getString("email");
            
            PreparedStatement updateStmt = dbConnection.prepareStatement(
                "UPDATE users SET failed_login_attempts = 0, last_login = NOW() WHERE id = ?"
            );
            updateStmt.setInt(1, userId);
            updateStmt.executeUpdate();
            
            // Create session - should be in separate session manager
            String sessionId = generateSessionId();
            PreparedStatement sessionStmt = dbConnection.prepareStatement(
                "INSERT INTO user_sessions (session_id, user_id, created_date, expires_date) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR))"
            );
            sessionStmt.setString(1, sessionId);
            sessionStmt.setInt(2, userId);
            sessionStmt.executeUpdate();
            
            // Log successful login
            logUserAction(userId, "LOGIN_SUCCESS", ipAddress);
            
            // Send login notification email - more mixing of concerns
            sendLoginNotificationEmail(email, username, ipAddress);
            
            result.put("success", true);
            result.put("message", "Login successful");
            result.put("session_id", sessionId);
            result.put("user_id", userId);
            result.put("username", username);
            result.put("email", email);
            
            return result;
            
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Database error occurred");
            return result;
        }
    }
    
    // Password hashing - using outdated algorithm
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // MD5 is not secure!
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    // Password verification - comparing MD5 hashes
    private boolean verifyPassword(String password, String storedHash) {
        return hashPassword(password).equals(storedHash);
    }
    
    // Email validation - could use library
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    // Profile data to JSON - manual JSON creation, error-prone
    private String profileDataToJson(Map<String, String> profileData) {
        if (profileData == null || profileData.isEmpty()) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : profileData.entrySet()) {
            if (!first) {
                json.append(",");
            }
            // No escaping! This will break with quotes or special chars
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
    
    // Session ID generation - not cryptographically secure
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + new Random().nextInt(10000);
    }
    
    // Get user ID by username - more SQL in business logic
    private int getUserIdByUsername(String username) {
        try {
            PreparedStatement stmt = dbConnection.prepareStatement("SELECT id FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Logging method - mixing database concerns with business logic
    private void logUserAction(int userId, String action, String ipAddress) {
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(
                "INSERT INTO audit_log (user_id, action, timestamp, ip_address) VALUES (?, ?, NOW(), ?)"
            );
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, ipAddress);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Logging failure should not break the operation
        }
    }
    
    // Get client IP - should be injected, not hardcoded
    private String getClientIP() {
        return "127.0.0.1"; // Obviously fake
    }
    
    // Email sending - mixing email concerns with user management
    private void sendWelcomeEmail(String email, String username) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Welcome to Our System!");
            message.setText("Dear " + username + ",\n\nWelcome to our system! Your account has been created successfully.\n\nBest regards,\nThe Team");
            
            Transport.send(message);
            
        } catch (Exception e) {
            e.printStackTrace(); // Email failure should not break user creation
        }
    }
    
    // Login notification email - duplicate email code
    private void sendLoginNotificationEmail(String email, String username, String ipAddress) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Login Notification");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String loginTime = sdf.format(new Date());
            
            message.setText("Dear " + username + ",\n\nYou have successfully logged in to our system.\n\nLogin time: " + 
                           loginTime + "\nIP Address: " + ipAddress + 
                           "\n\nIf this wasn't you, please contact support immediately.\n\nBest regards,\nThe Team");
            
            Transport.send(message);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Get all users - returns too much data, no pagination
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users"); // SELECT * is bad practice
            
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("username", rs.getString("username"));
                user.put("email", rs.getString("email"));
                user.put("password", rs.getString("password")); // NEVER return passwords!
                user.put("created_date", rs.getTimestamp("created_date"));
                user.put("last_login", rs.getTimestamp("last_login"));
                user.put("failed_login_attempts", rs.getInt("failed_login_attempts"));
                user.put("is_locked", rs.getBoolean("is_locked"));
                user.put("role", rs.getString("role"));
                user.put("profile_data", rs.getString("profile_data"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // Update user - no validation, accepts any field changes
    public boolean updateUser(int userId, Map<String, Object> updates) {
        try {
            StringBuilder sql = new StringBuilder("UPDATE users SET ");
            List<Object> values = new ArrayList<>();
            
            // Dynamic SQL building - SQL injection risk!
            boolean first = true;
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                if (!first) {
                    sql.append(", ");
                }
                sql.append(entry.getKey()).append(" = ?"); // No field validation!
                values.add(entry.getValue());
                first = false;
            }
            
            sql.append(" WHERE id = ?");
            values.add(userId);
            
            PreparedStatement stmt = dbConnection.prepareStatement(sql.toString());
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete user - no soft delete, no cascading cleanup
    public boolean deleteUser(int userId) {
        try {
            PreparedStatement stmt = dbConnection.prepareStatement("DELETE FROM users WHERE id = ?");
            stmt.setInt(1, userId);
            int result = stmt.executeUpdate();
            
            // Should also delete sessions and audit logs, but doesn't!
            
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Export users to CSV - no access control, includes passwords!
    public void exportUsersToCSV(String filename) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writer.println("ID,Username,Email,Password,CreatedDate,LastLogin,FailedAttempts,IsLocked,Role");
            
            List<Map<String, Object>> users = getAllUsers();
            for (Map<String, Object> user : users) {
                writer.println(user.get("id") + "," + 
                              user.get("username") + "," + 
                              user.get("email") + "," +
                              user.get("password") + "," + // Exporting passwords!
                              user.get("created_date") + "," +
                              user.get("last_login") + "," +
                              user.get("failed_login_attempts") + "," +
                              user.get("is_locked") + "," +
                              user.get("role"));
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Cleanup old sessions - should be a scheduled job, not manual
    public void cleanupOldSessions() {
        try {
            PreparedStatement stmt = dbConnection.prepareStatement("DELETE FROM user_sessions WHERE expires_date < NOW()");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
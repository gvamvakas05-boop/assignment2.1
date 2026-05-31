package gr.edu.aueb.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import gr.edu.aueb.db.DBconnection;
import mainpackage.Courses;
import mainpackage.Professors;

@WebServlet("/SecretaryServlet")
public class SecretaryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Courses> coursesList = new ArrayList<>();
        List<Professors> professorsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps1 = null; PreparedStatement ps2 = null;
        ResultSet rs1 = null; ResultSet rs2 = null;

        try {
            conn = DBconnection.getConnection();
            
            // 1. Ανάκτηση Μαθημάτων με LEFT JOIN για εμφάνιση υπεύθυνου καθηγητή
            ps1 = conn.prepareStatement("SELECT c.course_code, c.title, u.name AS p_name, u.surname AS p_sur FROM courses c LEFT JOIN users u ON c.professor_id = u.id");
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                Courses course = new Courses(rs1.getString("course_code"), rs1.getString("title"));
                if (rs1.getString("p_name") != null) {
                    course.setProfessor(new Professors(null, rs1.getString("p_name"), rs1.getString("p_sur"), null, null));
                }
                coursesList.add(course);
            }

            // 2. Ανάκτηση Καθηγητών για το Dropdown μενού επιλογής
            ps2 = conn.prepareStatement("SELECT id, username, name, surname, department FROM users WHERE role = 'professor'");
            rs2 = ps2.executeQuery();
            while (rs2.next()) {
                professorsList.add(new Professors(rs2.getString("username"), rs2.getString("name"), rs2.getString("surname"), rs2.getString("department"), String.valueOf(rs2.getInt("id"))));
            }

            request.setAttribute("coursesList", coursesList);
            request.setAttribute("professorsList", professorsList);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs1 != null) rs1.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps1 != null) ps1.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (rs2 != null) rs2.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps2 != null) ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        request.getRequestDispatcher("secretary_dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("assign".equals(action)) {
            String courseCode = request.getParameter("courseCode");
            String profId = request.getParameter("professorId");

            if (courseCode != null && profId != null && !courseCode.trim().isEmpty() && !profId.trim().isEmpty()) {
                Connection conn = null; PreparedStatement ps = null;
                try {
                    conn = DBconnection.getConnection();
                    ps = conn.prepareStatement("UPDATE courses SET professor_id = ? WHERE course_code = ?");
                    ps.setInt(1, Integer.parseInt(profId.trim()));
                    ps.setString(2, courseCode.trim());
                    
                    if (ps.executeUpdate() > 0) {
                        session.setAttribute("successMessage", "Η ανάθεση ολοκληρώθηκε επιτυχώς!");
                    } else {
                        session.setAttribute("errorMessage", "Αποτυχία: Το μάθημα δεν βρέθηκε.");
                    }
                } catch (Exception e) {
                    session.setAttribute("errorMessage", "Σφάλμα κατά την εκτέλεση της ανάθεσης.");
                    e.printStackTrace();
                } finally {
                    try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
            }
        }
        response.sendRedirect("SecretaryServlet");
    }
}
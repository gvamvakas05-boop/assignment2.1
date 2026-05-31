<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="mainpackage.Courses" %>
<%@ page import="mainpackage.Professors" %>
<%@ page import="mainpackage.Users" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard Γραμματείας</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f9f9f9; }
        .header { background: #333; color: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; text-align: left; position: relative; }
        .logout-btn { position: absolute; right: 20px; top: 20px; background: #dc3545; color: white; padding: 8px 15px; text-decoration: none; border-radius: 4px; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; font-weight: bold; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 30px; background: white; }
        table, th, td { border: 1px solid #ddd; }
        th, td { padding: 12px; text-align: left; }
        th { background-color: #0056b3; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .form-container { background: white; padding: 20px; border-radius: 5px; border: 1px solid #ddd; max-width: 500px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group select { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .submit-btn { background: #28a745; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .submit-btn:hover { background: #218838; }
    </style>
</head>
<body>

<%
    Users loggedUser = (Users) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Προσθήκη SuppressWarnings για να σβήσουν τα κίτρινα warnings του Eclipse
    @SuppressWarnings("unchecked")
    List<Courses> coursesList = (List<Courses>) request.getAttribute("coursesList");
    
    @SuppressWarnings("unchecked")
    List<Professors> professorsList = (List<Professors>) request.getAttribute("professorsList");
%>

<div class="header">
    <h2>Καλωσήρθατε, <%= loggedUser.getName() %> <%= loggedUser.getSurname() %></h2>
    <p>Τμήμα: <%= loggedUser.getDepartment() %></p>
    <a href="login.jsp" class="logout-btn">Αποσύνδεση</a>
</div>

<% if (session.getAttribute("successMessage") != null) { %>
    <div class="message success"><%= session.getAttribute("successMessage") %></div>
    <% session.removeAttribute("successMessage"); %>
<% } %>

<% if (session.getAttribute("errorMessage") != null) { %>
    <div class="message error"><%= session.getAttribute("errorMessage") %></div>
    <% session.removeAttribute("errorMessage"); %>
<% } %>

<h3>Λίστα Μαθημάτων και Αναθέσεων</h3>
<table>
    <thead>
        <tr>
            <th>Κωδικός Μαθήματος</th>
            <th>Τίτλος Μαθήματος</th>
            <th>Υπεύθυνος Καθηγητής</th>
        </tr>
    </thead>
    <tbody>
        <% if (coursesList != null && !coursesList.isEmpty()) { 
            for (Courses c : coursesList) { %>
                <tr>
                    <td><%= c.getCourseId() %></td>
                    <td><%= c.getCourseName() %></td>
                    <td>
                        <%= (c.getProfessor() != null) ? c.getProfessor().getName() + " " + c.getProfessor().getSurname() : "<i>Δεν έχει ανατεθεί</i>" %>
                    </td>
                </tr>
            <% } 
        } else { %>
            <tr>
                <td colspan="3" style="text-align:center;">Δεν βρέθηκαν μαθήματα.</td>
            </tr>
        <% } %>
    </tbody>
</table>

<div class="form-container">
    <h3>Νέα Ανάθεση Καθηγητή σε Μάθημα</h3>
    <form action="SecretaryServlet" method="POST">
        <input type="hidden" name="action" value="assign">
        
        <div class="form-group">
            <label for="courseCode">Επιλογή Μαθήματος:</label>
            <select name="courseCode" id="courseCode" required>
                <option value="">-- Επιλέξτε Μάθημα --</option>
                <% if (coursesList != null) {
                    for (Courses c : coursesList) { %>
                        <option value="<%= c.getCourseId() %>"><%= c.getCourseId() %> - <%= c.getCourseName() %></option>
                    <% }
                } %>
            </select>
        </div>

        <div class="form-group">
            <label for="professorId">Επιλογή Καθηγητή:</label>
            <select name="professorId" id="professorId" required>
                <option value="">-- Επιλέξτε Καθηγητή --</option>
                <% if (professorsList != null) {
                    for (Professors p : professorsList) { %>
                        <%-- FIX #1: Χρησιμοποιούμε getProfessorId() (numeric DB id) αντί για getUsername() --%>
                        <%-- ώστε το SecretaryServlet να κάνει σωστά Integer.parseInt(profId) --%>
                        <option value="<%= p.getProfessorId() %>"><%= p.getName() %> <%= p.getSurname() %> (<%= p.getDepartment() %>)</option>
                    <% }
                } %>
            </select>
        </div>

        <button type="submit" class="submit-btn">Υποβολή Ανάθεσης</button>
    </form>
</div>

</body>
</html>

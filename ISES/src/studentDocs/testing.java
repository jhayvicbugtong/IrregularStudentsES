package studentDocs;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class testing {
   
        public static PDDocument fillGradesToPdf(String stud_id) {
        String url = "jdbc:mysql://localhost:3308/university_portal_db";
        String username = "root";
        String password = "";
        String getName = "SELECT CONCAT(first_name,' ', middle_name,' ', last_name) AS full_name FROM students WHERE student_id = ?;";
        String fullName = "";

        Map<String, String> grades = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             CallableStatement stmt = conn.prepareCall("{CALL getStudentGrade(?)}");
             PreparedStatement pstmt = conn.prepareStatement(getName)) {

            stmt.setString(1, stud_id);
            ResultSet rs = stmt.executeQuery();
            pstmt.setString(1, stud_id);
            ResultSet name = pstmt.executeQuery();

            if(name.next()){
                fullName = name.getString("full_name");
            }

            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                String grade = rs.getString("grade");
                grades.put(courseCode, grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        PDDocument pdf;
        try {
            pdf = PDDocument.load(new File("/Users/magnaye.rp/Movies/IM_sem_project/BA-PROSPECTUS.pdf"));
            PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();

            if (form != null) {
                PDField namefield = form.getField("NameA");
                PDField idfield = form.getField("Student NumberA");
                PDField namefieldB = form.getField("NameB");
                PDField idfieldB = form.getField("Student NumberB");
                namefield.setValue(fullName);
                idfield.setValue(stud_id);
                namefieldB.setValue(fullName);
                idfieldB.setValue(stud_id);

                for (Map.Entry<String, String> entry : grades.entrySet()) {
                    String courseCode = entry.getKey();
                    String grade = entry.getValue();

                    PDField field = form.getField(courseCode);
                    if (field != null) {
                        field.setValue(grade);
                    } else {
                        System.out.println("No field found for: " + courseCode);
                    }
                }
                form.flatten();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return pdf;

    }

}

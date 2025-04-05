package studentDocs;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class testing {
    public static void main(String[] args) {
        String pdfPath = "/Users/magnaye.rp/Movies/IM_sem_project/ProposalSlip.pdf";
        String outputPdfPath = "/Users/magnaye.rp/Movies/IM_sem_project/Filled-Proposal-Slip.pdf";

        try {
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDAcroForm form = document.getDocumentCatalog().getAcroForm();

            if (form == null) {
                System.out.println("❌ No AcroForm found in the PDF.");
                document.close();
                return;
            }

            System.out.println("✅ Found AcroForm. Fields available:");
            for (PDField field : form.getFields()) {
                System.out.println(" - " + field.getFullyQualifiedName());
            }

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/university_portal_db", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CONCAT(first_name, ' ', last_name) AS name, student_id, major FROM students WHERE student_no = 4");

            if (rs.next()) {
                setField(form, "Name", rs.getString("name"));
                setField(form, "SR Code", rs.getString("student_id"));
                setField(form, "Major", rs.getString("major"));
                setField(form, "Semester", "2nd Semester");
                setField(form, "Program", "BSIT");
                setField(form, "Section", "BSIT-2205");
            } else {
                System.out.println("⚠️ No matching student record found.");
            }

            // Save the updated PDF
            document.save(outputPdfPath);
            document.close();
            conn.close();
            System.out.println("✅ PDF Form filled successfully: " + outputPdfPath);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to set form fields safely
    private static void setField(PDAcroForm form, String fieldName, String value) throws IOException {
        PDField field = form.getField(fieldName);
        if (field != null) {
            field.setValue(value);
        } else {
            System.out.println("⚠️ Field not found: " + fieldName);
        }
    }
}

package com.hagenthon.uncampoallavolta.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;
import com.hagenthon.uncampoallavolta.service.impl.PdfBoxFormService;

/**
 * Verifica che i PDF di prova possano essere estratti dal backend PdfBoxFormService.
 * Eseguire con: mvn -q test-compile exec:java -Dexec.mainClass="com.hagenthon.uncampoallavolta.tools.VerifySampleForms" -Dexec.classpathScope=test
 */
public class VerifySampleForms {

    public static void main(String[] args) throws IOException {
        PdfBoxFormService service = new PdfBoxFormService();

        System.out.println("=== VERIFICA INTEGRAZIONE CON PdfBoxFormService ===\n");

        String[] pdfFiles = {
            "/Users/luca.capocci/hagenthon/app/samples/dichiarazione-residenza.pdf",
            "/Users/luca.capocci/hagenthon/app/samples/contributo-affitto.pdf"
        };

        for (String pdfPath : pdfFiles) {
            try {
                byte[] pdfBytes = Files.readAllBytes(Paths.get(pdfPath));
                List<QuestionDto> fields = service.extractFields(pdfBytes);

                String fileName = Paths.get(pdfPath).getFileName().toString();
                System.out.println(fileName);
                System.out.println("  Campi estratti: " + fields.size());

                for (QuestionDto field : fields) {
                    System.out.println(
                            "    - fieldName=" + field.fieldName() + ", originalLabel=" + field.originalLabel());
                }
                System.out.println();
            } catch (Exception e) {
                System.err.println("ERRORE in " + pdfPath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Verifica completata.");
    }
}

package org.acme.service;

import io.vertx.core.json.JsonObject;
import org.acme.model.Kebun;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class GenerateDocument {

    public JsonObject generateDocument(Map content) throws IOException {
        InputStream letterInput = null;
        OutputStream letterOutput = null;
        XWPFDocument letterDoc = null;

        try {
            letterInput = Files.newInputStream(new File("./template/template.docx").toPath());
            letterDoc = new XWPFDocument(letterInput);
            replace(letterDoc, content);
            String filename = "./result.docx";
            letterOutput = new FileOutputStream(filename);
            letterDoc.write(letterOutput);

            JsonObject result = new JsonObject();
            result.put("fileName",filename);
            return null;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }  finally {
            if (letterOutput != null) {
                letterOutput.close();
            }
            if (letterInput != null) {
                letterInput.close();
            }
            if (letterDoc != null) {
                letterDoc.close();
            }
        }
    }

    private void replace(XWPFDocument doc, Map payload) {

        for (XWPFParagraph x : doc.getParagraphs()) {
            for (CTBookmark r : x.getCTP().getBookmarkStartList()) {

                XWPFRun run = x.createRun();

                switch (r.getName()) {
                    case "UUID":
                        run.setText(payload.get("id").toString());
                        break;

                    case "KOMODITAS":
                        run.setText(payload.get("komoditas").toString());
                        break;
                    case "TOTAL":
                        run.setText(payload.get("total").toString());
                        break;
                    case "CREATEDAT":
                        run.setText(payload.get("create").toString());
                        break;
                    case "UPDATEAT":
                        run.setText(payload.get("update").toString());
                        break;

                }
                if (run.text() != null) {
                    x.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), r.getDomNode());
                }
            }

        }
    }
}


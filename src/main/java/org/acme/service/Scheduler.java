package org.acme.service;


import com.google.errorprone.annotations.concurrent.LazyInit;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.Location;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.acme.model.Kebun;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.ext.ParamConverter;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Scheduler {
    Calendar c = Calendar.getInstance();
    Kebun kebun = new Kebun();
    int increment = 0;
    String id;

    @Scheduled(cron = "0 0 0 ? * SUN *")
    @Transactional
    void addData() throws IOException {
        Date now = new Date();
        Date after = calendar(increment).getTime();
        if (Kebun.count() == 0) {
            kebun = new Kebun();
            kebun.komoditas = "tomat";


            kebun.total = 0;
            if (kebun.createdAt == null) {
                kebun.createdAt = new Date();
            }
            kebun.persist();

        }
        if (now.after(after)) {
            kebun = new Kebun();
            kebun.komoditas = "tomat";
            kebun.createdAt = new Date();
            kebun.total = 0;
            kebun.persist();
            increment++;
        }

        Kebun find = Kebun.find("id=?1", kebun.id).firstResult();
        find.updateAt = new Date();
        find.total += 500;
        this.id = kebun.id;
        write();

    }

    @Inject
    @Location("hello")
    MailTemplate hello;
    @Inject
    GenerateDocument generateDocument;

    @Scheduled(cron = "0 0 2 28 * ?")
    @Blocking
    public Uni<Void> send() throws IOException {

        return hello.to("nurfaisal1504@gmail.com")
                .subject("report hasil panen tomat")
                .addInlineAttachment("result", new File("result.docx"),"text/plain","quarkus").send();
    }

    void write() throws IOException {
        if (null != this.id) {
            JsonObject result = generateDocument.generateDocument(writeDocument());
        }
    }


    Map<String, String> writeDocument() {

        Kebun find = Kebun.find("id=?1", this.id).firstResult();
        Map<String, String> map = new HashMap();
        map.put("id", find.id);
        map.put("komoditas", find.komoditas);
        map.put("create", find.createdAt.toString());
        map.put("update", find.updateAt.toString());
        map.put("total", find.total.toString());
        return map;

    }

    Calendar calendar(Integer month) {
        c.set(Calendar.DATE, 28);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.AM_PM, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c;
    }
}

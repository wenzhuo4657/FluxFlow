package cn.wenzhuo4657.dailyWeb.tigger.http;


import cn.wenzhuo4657.dailyWeb.Main;
import cn.wenzhuo4657.dailyWeb.domain.system.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@RestController(value = "system")
public class SystemController {




    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemService systemService;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /**
     * 直链下载sqlite文件,
     * 核心使用： VACUUM   INTO  "new_file_name"
     * 参考： https://sqlite.org/lang_vacuum.html
     *
     */
    @RequestMapping(
            value = "DownLoadFile",
            method = RequestMethod.GET
    )
    public ResponseEntity<Resource> DownLoadFile() {
        try {

            Path filePath= Main.getFilePath();
            String ts = LocalDateTime.now().format(TS);
            Path tempBackup  = filePath.resolve("beifen-" + ts + ".db");

            try {
                systemService.export(tempBackup);
            }catch (Exception e){
                log.error("获取数据库连接失败",e);
                return ResponseEntity.status(500).body(null);
            }


            long size = Files.size(tempBackup);
            InputStreamResource body = new InputStreamResource(Files.newInputStream(tempBackup));



            ContentDisposition cd = ContentDisposition.attachment()
                    .filename(tempBackup.getFileName().toString(), StandardCharsets.UTF_8)
                    .build();


            ResponseEntity<Resource> res = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                    .header(HttpHeaders.CACHE_CONTROL, "no-store") // 按需调整缓存策略
                    .contentLength(size)
                    .body(body);

            return res;
        }catch (IOException e){
            log.error("下载文件失败",e);
            return ResponseEntity.status(500).body(null);
        }
    }




    /**
     * 上传sqlite，做系统恢复
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {




        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"文件为空\"}");
        }

        Path filePath = Main.getFilePath();

        Path temp = filePath.resolve(file.getOriginalFilename());


        try (InputStream in = file.getInputStream()) {
            Files.copy(in, temp, REPLACE_EXISTING);
        }
        File tempFile = temp.toFile();

        systemService.reset(tempFile);


        return ResponseEntity.ok().build();
    }


}

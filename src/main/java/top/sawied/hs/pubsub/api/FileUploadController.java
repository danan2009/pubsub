package top.sawied.hs.pubsub.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.sawied.hs.pubsub.api.model.RawMultiPartFile;


import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    public static final String X_FILE_NAME = "x-file-name";
    public static final String FILE = "file";

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<byte[]> uploadWithMultiPart(@RequestParam("file") MultipartFile file){
        if(file!=null && !file.isEmpty()){
            try {
                byte[] bytes=file.getBytes();
                return ResponseEntity.ok().body(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return ResponseEntity.badRequest().body("bad request".getBytes());
    }

    @RequestMapping(value="/raw",method = RequestMethod.POST,consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<byte[]> uploadWithRawData(@RequestHeader Map<String, String> headers , @RequestBody byte[] requestBody){
        if(requestBody!=null && requestBody.length>0){
            try {
                String xFileName = headers.get(X_FILE_NAME);
                String fileName = StringUtils.hasText(xFileName)?xFileName: FILE;
                RawMultiPartFile rawMultiPartFile = new RawMultiPartFile(fileName, requestBody);
                LOGGER.info("Received a multiple file as {}",rawMultiPartFile);
                return ResponseEntity.ok().body(requestBody);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return ResponseEntity.badRequest().body("bad request".getBytes());
    }


}

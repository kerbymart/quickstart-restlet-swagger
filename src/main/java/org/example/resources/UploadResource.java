package org.example.resources;

import com.wordnik.swagger.annotations.Api;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import java.io.InputStream;
import java.util.List;

@Api(value = "/upload")
public class UploadResource extends ServerResource {

    @Override
    protected void doInit() throws ResourceException {
        super.doInit();

        // Get the headers from the request
        Series<Header> headers = (Series<Header>) getRequest().getAttributes().get("org.restlet.http.headers");

        // Print each header
        if (headers != null) {
            for (String name : headers.getNames()) {
                System.out.println(name + ": " + headers.getFirstValue(name));
            }
        }
    }

    @Post
    public void acceptFormUpload(Representation entity) throws Exception {
        // Check if entity is not null first
        if (entity == null) {
            throw new ResourceException(400); // Bad request
        }

        if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
            FileItemFactory factory = new DiskFileItemFactory();
            RestletFileUpload fileUpload = new RestletFileUpload(factory);
            List<FileItem> fileItems = fileUpload.parseRepresentation(entity);

            if (fileItems == null) {
                throw new ResourceException(400); // Bad request
            }

            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    String fieldName = fileItem.getFieldName();
                    String fieldValue = fileItem.getString();
                    // Handle form field here...
                    System.out.println("Field name: " + fieldName + ", Value: " + fieldValue);
                }
                else {
                    String fieldName = fileItem.getFieldName();
                    String fileName = fileItem.getName();
                    long fileSize = fileItem.getSize();

                    System.out.println("Field name: " + fieldName);
                    System.out.println("File name: " + fileName);
                    System.out.println("File size: " + fileSize + " bytes");
                }
            }
        } else {
           MediaType mediaType = entity.getMediaType();
           System.out.println("Media type: " + mediaType);
        }
    }
}

/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.util;

import com.google.cloud.storage.*;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// [START example]
public class CloudStorageHelper {
    
    private static Storage storage = null;
    
    static {
        storage = StorageOptions.getDefaultInstance().getService();
    }
    
    
    /**
     * Uploads a file to Google Cloud Storage to the bucket specified in the BUCKET_NAME
     * environment variable, appending a timestamp to end of the uploaded filename.
     */
    public BlobInfo uploadFile(Part filePart, final String bucketName) throws IOException {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
        DateTime dt = DateTime.now(DateTimeZone.UTC);
        String dtString = dt.toString(dtf);
        System.out.println(filePart.getSubmittedFileName());
        final String fileName = filePart.getSubmittedFileName() + dtString;
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, fileName)
                        // Modify access list to allow all users with link to read file
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
                        .build(),
                filePart.getInputStream());
        // return the public download link
        
        System.out.println("blob " + blobInfo.getName() + " ");
        return blobInfo;
    }
    // [END uploadFile]
    
    // [START getVideoUrl]
    
    /**
     * Extracts the file payload from an HttpServletRequest, checks that the file extension
     * is supported and uploads the file to Google Cloud Storage.
     */
    public BlobInfo getVideoUrl(HttpServletRequest req, final String bucket) throws IOException, ServletException {
        BlobInfo blobInfo = null;
        for (Part filePart : req.getParts()) {
            final String fileName = filePart.getSubmittedFileName();
            // Check extension of file
            long size = filePart.getSize();
            if (fileName != null && !fileName.isEmpty() /*&& fileName.contains(".")*/) {
                final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                
                try {
                    blobInfo = uploadFile(filePart, bucket);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                
            }
        }
        return blobInfo;
    }

    public void deleteAll(String bucketName) {
        Iterable<Blob> blobs = storage.list(bucketName).iterateAll();
        for (Blob blob : blobs) {
            blob.delete(Blob.BlobSourceOption.generationMatch());
        }
    }
}

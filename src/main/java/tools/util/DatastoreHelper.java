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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.DateUtil;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import pojo.Video;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


// [START example]
public class DatastoreHelper {
    
    private static DatastoreService datastore = null;
    
    static {
        
        datastore = DatastoreServiceFactory.getDatastoreService();
    }
    
    public void addVideo(String mail, String size, BlobInfo blobInfo, String title) throws ServletException {
        int point = Integer.parseInt(size) / 10;
        final Query q =
                new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, mail));
        
        PreparedQuery pq = datastore.prepare(q);
        com.google.appengine.api.datastore.Entity entity = pq.asSingleEntity(); // Retrieve up to five posts
        List<EmbeddedEntity> availableVideos1 = (List<EmbeddedEntity>) entity.getProperty("availableVideos");
        if (availableVideos1 == null) {
            availableVideos1 = new LinkedList<>();
        }
        EmbeddedEntity video = new EmbeddedEntity();
        video.setProperty("url", blobInfo.getMediaLink());
        video.setProperty("uploadDate", DateUtil.serializeDate(new Date()));
        video.setProperty("title", title);
        availableVideos1.add(video);
        int score = (int) entity.getProperties().get("score");
        int timeout = 300000;
        if (score > 100 && score <= 200) {
            timeout = 600000;
        } else if (score > 200) {
            timeout = 1800000;
        }
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new BlobDeleter(blobInfo)).countdownMillis(timeout));
        
        
        entity.setProperty("score", ((long) entity.getProperty("score")) + point);
        entity.setProperty("availableVideos", availableVideos1);
        try {
            datastore.put(entity); // store the entity
        } catch (DatastoreFailureException e) {
            throw new ServletException("Datastore error", e);
        }
    }
    
    public Video getVideo(String videoOwner, String videoTitle) {
        final Query q = new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, videoOwner));
        
        PreparedQuery pq = datastore.prepare(q);
        com.google.appengine.api.datastore.Entity entity = pq.asSingleEntity(); // Retrieve up to five posts
        List<EmbeddedEntity> availableVideos1 = (List<EmbeddedEntity>) entity.getProperty("availableVideos");
        if (availableVideos1 == null) {
            return null;
        }
        List<EmbeddedEntity> resList = availableVideos1.stream().filter(e -> e.getProperty("title").equals(videoTitle)).collect(Collectors.toList());
        if (resList.size() != 1) {
            return null;
        }
        EmbeddedEntity res = resList.get(0);
        
        return new Video((String) res.getProperty("url"), (String) res.getProperty("uploadDate"), (String) res.getProperty("title"));
    }
    
    public static class BlobDeleter implements DeferredTask {
        private BlobInfo blobInfo;
        
        public BlobDeleter(BlobInfo blobInfo) {
            this.blobInfo = blobInfo;
        }
        
        @Override
        public void run() {
            System.out.println("salut");
            Storage storage = StorageOptions.getDefaultInstance().getService();
            storage.delete(this.blobInfo.getBlobId());
        }
    }
}

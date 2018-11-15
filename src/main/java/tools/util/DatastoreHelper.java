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
import pojo.Video;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

// [START example]
public class DatastoreHelper {
    
    private static DatastoreService datastore = null;
    
    static {

         datastore= DatastoreServiceFactory.getDatastoreService();
    }

    public void addVideo(String mail, String size, String url) throws ServletException {
        int point = Integer.parseInt(size) / 10;
        final Query q =
                new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, mail));

        PreparedQuery pq = datastore.prepare(q);
        com.google.appengine.api.datastore.Entity entity = pq.asSingleEntity(); // Retrieve up to five posts
        List<Video> availableVideos1 = (List<Video>) entity.getProperty("availableVideos");
        if (availableVideos1 == null)
            availableVideos1 = new LinkedList<>();
        entity.setProperty("score", ((long)entity.getProperty("score")) + point);
        entity.setProperty("availableVideos", availableVideos1.add(new Video(url, DateUtil.serializeDate(new Date()), "yolo")));
        try {
            datastore.put(entity); // store the entity
        } catch (DatastoreFailureException e) {
            throw new ServletException("Datastore error", e);
        }
    }
}

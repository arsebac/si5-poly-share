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
import exceptions.NoobRateExceedException;
import exceptions.UserNotFoundException;
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

    public Entity getUser(String mail) throws UserNotFoundException {
        System.out.println("gettin user with email " + mail);
        Query q = new Query("user").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, mail));
        PreparedQuery pq = datastore.prepare(q);
        Entity entity = pq.asSingleEntity();
        if (entity == null) {
            throw new UserNotFoundException();
        }
        return entity; // Retrieve up to five posts
    }

    public void addVideo(String mail, long size, String url, String title) throws ServletException, UserNotFoundException, NoobRateExceedException {
        addVideo(mail, size, url, title, true);
    }

    public void addVideo(String mail, long size, String url, String title, boolean doVerification) throws ServletException, UserNotFoundException, NoobRateExceedException {
        int point = Math.toIntExact(size / 1000000);
        Entity entity = getUser(mail);
        if (doVerification) {

            // Autrement dit, si un Noob fait une deuxième demande en moins d'une minute, il recevra un email contenant le texte "lol non noob".
            long now = new Date().getTime();
            List<EmbeddedEntity> userVideos = (List<EmbeddedEntity>) entity.getProperty("availableVideos");
            if (userVideos == null) {
                userVideos = new LinkedList<>();
            }
            List<String> downloadedVideos = (List<String>) entity.getProperty("downloadedVideos");
            if (downloadedVideos == null) {
                downloadedVideos = new LinkedList<>();
            }
            long clientScore = (long) entity.getProperty("score");
            int before1Min = countOperationsIntheLastMinute(userVideos, downloadedVideos);

            boolean exceedLimit = (clientScore < 100 && before1Min > 0)
                    || (clientScore < 200 && before1Min > 2)
                    || (before1Min > 4);
        }
        List<EmbeddedEntity> availableVideos = (List<EmbeddedEntity>) entity.getProperty("availableVideos");
        if (availableVideos == null) {
            availableVideos = new LinkedList<>();
        }
        EmbeddedEntity video = new EmbeddedEntity();
        video.setProperty("url", url);
        video.setProperty("uploadDate", DateUtil.serializeDate(new Date()));
        video.setProperty("title", title);
        availableVideos.add(video);


        entity.setProperty("score", ((long) entity.getProperty("score")) + point);
        entity.setProperty("availableVideos", availableVideos);
        try {
            datastore.put(entity); // store the entity
        } catch (DatastoreFailureException e) {
            throw new ServletException("Datastore error", e);
        }
    }

    public Video getVideo(String videoOwner, String videoTitle, String userAskingEmail) throws NoobRateExceedException, UserNotFoundException {
        System.out.println(" getting video " + videoOwner + " " + videoTitle + " " + userAskingEmail);
        Entity owner = getUser(videoOwner);
        Entity client = getUser(userAskingEmail);
        long clientScore = (long) client.getProperty("score");

        List<EmbeddedEntity> availableVideos1 = (List<EmbeddedEntity>) owner.getProperty("availableVideos");
        if (availableVideos1 == null) {
            return null;
        }

        List<EmbeddedEntity> resList = availableVideos1.stream().filter(e -> e.getProperty("title").equals(videoTitle)).collect(Collectors.toList());
        if (resList.size() != 1) {
            return null;
        }
        EmbeddedEntity res = resList.get(0);
        // Autrement dit, si un Noob fait une deuxième demande en moins d'une minute, il recevra un email contenant le texte "lol non noob".
        List<EmbeddedEntity> userVideos = (List<EmbeddedEntity>) client.getProperty("availableVideos");
        if (userVideos == null) {
            userVideos = new LinkedList<>();
        }
        List<String> downloadedVideos = (List<String>) client.getProperty("downloadedVideos");
        if (downloadedVideos == null) {
            downloadedVideos = new LinkedList<>();
        }
        int before1Min = countOperationsIntheLastMinute(userVideos, downloadedVideos);

        boolean exceedLimit = (clientScore < 100 && before1Min > 0)
                || (clientScore < 200 && before1Min > 2)
                || (before1Min > 4);
        if (exceedLimit) {
            throw new NoobRateExceedException((String) client.getProperty("email"));
        }
        client.setProperty("downloadedVideos", downloadedVideos.add(DateUtil.serializeDate(new Date())));
        return new Video((String) res.getProperty("url"), (String) res.getProperty("uploadDate"), (String) res.getProperty("title"));
    }

    public int countOperationsIntheLastMinute(List<EmbeddedEntity> availableVideos, List<String> downloadedVideos) {
        long now = new Date().getTime();
        int uploaded = (int) availableVideos.stream().filter(vid -> {
            long data = now - DateUtil.deserializeDate(String.valueOf(vid.getProperty("uploadDate"))).getTime();
            return data < 60 * 1000; // in milli-seconds.
        }).count();
        int downloaded = (int) downloadedVideos.stream().filter(ts -> {
            long data = now - DateUtil.deserializeDate(ts).getTime();
            return data < 60 * 1000; // in milli-seconds.
        }).count();
        return uploaded + downloaded;
    }
    public void deleteAll() {
        Query query = new Query("user").setKeysOnly();
        Iterable<Entity> iter = datastore.prepare(query).asIterable(FetchOptions.Builder.withLimit(400));
        for (Entity entity : iter) {
            datastore.delete(entity.getKey());
        }
    }

    public void addPointsToUser(String email, int points) {
        Entity oldUser = null;
        try {
            oldUser = getUser(email);

            List<EmbeddedEntity> availableVideos = new LinkedList<>();
            if (oldUser.getProperty("availableVideos") != null) {
                availableVideos = (List<EmbeddedEntity>) oldUser.getProperty("availableVideos");
            }
            List<String> downloadedVideos = new LinkedList<>();
            if (oldUser.getProperty("downloadedVideos") != null) {
                downloadedVideos = (List<String>) oldUser.getProperty("downloadedVideos");
            }
            long oldScore = (long) oldUser.getProperty("score");
            datastore.delete(oldUser.getKey());
            addUser(email, Math.toIntExact(oldScore + points), availableVideos, downloadedVideos);
        } catch (UserNotFoundException ignored) {
        }
    }

    public void addUser(String email, int points) {
        addUser(email, points, new LinkedList<>(), new LinkedList<>());
    }

    public void addUser(String email, int points, List<EmbeddedEntity> availableVideos, List<String> downloadedVideos) {
        Entity user = new Entity("user");
        user.setProperty("score", points);
        user.setProperty("email", email);
        user.setProperty("availableVideos", availableVideos);
        user.setProperty("downloadedVideos", downloadedVideos);
        datastore.put(user);
    }
}

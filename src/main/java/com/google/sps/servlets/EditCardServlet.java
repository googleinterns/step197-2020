package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.sps.tool.ResponseHandler;
import com.google.sps.data.Folder;
import java.util.Map;
import java.util.List;

@WebServlet("/editcard")
public class EditCardServlet extends HttpServlet{
  
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    if (userService.isUserLoggedIn()) {
      String newLabels = request.getParameter("labels");
      String newfromLang = request.getParameter("fromLang");
      String newToLang = request.getParameter("toLang");
      String cardKey = request.getParameter("cardKey");
      String newRawText = request.getParameter("rawText");
      String newTextTranslated = request.getParameter("textTranslated");
      String newBlobKey = getBlobKey(request);
      
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity card = getExistingCardInDatastore(response, datastore, cardKey);

      if (card == null) {
        ResponseHandler.sendErrorMessage(response, "Cannot edit Card at the moment");
      } else {
        updateCard(response, card, datastore, newLabels, newfromLang, newToLang, cardKey, newRawText, newTextTranslated, newBlobKey);
      }
    }
  }

  private void updateCard(
      HttpServletResponse response, 
      Entity card,
      DatastoreService datastore,
      String newLabels,
      String newFromLang, 
      String newToLang, 
      String cardKey,
      String newRawText,
      String newTextTranslated,
      String newBlobKey) throws IOException {

    card.setProperty("labels", newLabels);
    card.setProperty("fromLang", newFromLang);
    card.setProperty("toLang", newToLang);
    card.setProperty("cardKey", cardKey);
    card.setProperty("rawText", newRawText);
    card.setProperty("textTranslated", newTextTranslated);
    card.setProperty("blobKey", newBlobKey);
    datastore.put(card);
  }

  private Entity getExistingCardInDatastore(HttpServletResponse response, DatastoreService datastore, String cardKey) throws IOException {
    Entity card;
    try {
      return datastore.get(KeyFactory.stringToKey(cardKey));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  private String getBlobKey(HttpServletRequest request) {
    // Method to determine whether or not this is a unit test or live server
    // Unit tests will always set blobKey to "null"
    // There should be no paramater testStatus in the live server thus returns null
    String blobKey;

    if (request.getParameter("testStatus") == null) {
      blobKey = getBlobKeyFromBlobstore(request, "image");
    } else {
      blobKey = "null";
    }

    return blobKey;
  } 

  private String getBlobKeyFromBlobstore(HttpServletRequest request, String formInputElementName) {
    
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a BlobKey. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so the blobInfo has 0 byte (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
    return blobKey.getKeyString();
  }
}
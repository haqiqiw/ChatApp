package com.neogeekscamp.workshop2.helper;

import android.content.Context;

import com.neogeekscamp.workshop2.model.MessageModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by M. Asrof Bayhaqqi on 11/26/2016.
 */

public class RealmHelper {

    private Context context;
    private Realm realm;

    public RealmHelper(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    public void addMessage(MessageModel model) {
        MessageModel message = new MessageModel();
        message.setId(model.getId());
        message.setUsername(model.getUsername());
        message.setMessage(model.getMessage());
        message.setImage(model.getImage());
        message.setTime(model.getTime());
        message.setType(model.getType());

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(message);
        realm.commitTransaction();
    }

    public void clearMessage() {
        RealmResults<MessageModel> results = realm.where(MessageModel.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public ArrayList<MessageModel> getListMessage(){
        ArrayList<MessageModel> data = new ArrayList<>();
        RealmResults<MessageModel> results = realm.where(MessageModel.class).findAll();
        if(results.size() > 0){
            for(int i = 0; i <results.size(); i++){
                data.add(new MessageModel(results.get(i)));
            }
        }
        return data;
    }
}

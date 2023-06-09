package etu1777.framework;

import java.util.HashMap;
import java.util.LinkedList;

public class ModelView{
    private String view;
    private HashMap<String, Object> data;
    private HashMap<String, Object> session;
    private LinkedList<String> deleteSession;
    private boolean isJson;
    public ModelView(){
        data=new HashMap<String, Object>();
        session=new HashMap<String, Object>();
        deleteSession=new LinkedList<>();
        setJson(false);
    }
    public HashMap<String, Object> getSession() {
        return session;
    }
    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    public void addSessionAttribute(String key, Object value){
        session.put(key, value);
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void addSessionToDelete(String delete){
        deleteSession.add(delete);
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
    public void addItem(String key, Object value){
        data.put(key, value);
    }
    public boolean isJson() {
        return isJson;
    }
    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }
    public LinkedList<String> getDeleteSession() {
        return deleteSession;
    }
    public void setDeleteSession(LinkedList<String> deleteSession) {
        this.deleteSession = deleteSession;
    }
    
}
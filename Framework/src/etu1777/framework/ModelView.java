package etu1777.framework;

import java.util.HashMap;

public class ModelView{
    private String view;
    private HashMap<String, Object> data;
    private HashMap<String, Object> session;
    public ModelView(){
        data=new HashMap<String, Object>();
        session=new HashMap<String, Object>();
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
}
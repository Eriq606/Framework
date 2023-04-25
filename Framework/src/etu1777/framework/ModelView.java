package etu1777.framework;

import java.util.HashMap;

public class ModelView{
    private String view;
    private HashMap<String, Object> data;
    public ModelView(){
        data=new HashMap<String, Object>();
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
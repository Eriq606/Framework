package asset;

import java.util.LinkedList;

import etu1777.framework.ModelView;
import etu1777.framework.annotations.urlpattern;

public class Emp {
    private int id;
    private String nom;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setId(String id){
        setId(Integer.parseInt(id));
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Emp(){}
    public Emp(int i, String n){
        id=i;
        nom=n;
    }
    @urlpattern(url = "huhu")
    public ModelView bidon(){
        ModelView model=new ModelView();
        model.setView("hello.jsp");
        LinkedList<Emp> l=new LinkedList<>();
        l.add(new Emp(1, "Jean"));
        l.add(new Emp(2, "Marie"));
        l.add(new Emp(3, "Jules"));
        model.addItem("lst", l);
        return model;
    }
}

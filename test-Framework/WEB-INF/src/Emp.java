import etu1777.framework.ModelView;
import etu1777.framework.annotations.urlpattern;

public class Emp {
    @urlpattern(url = "huhu")
    public ModelView bidon(){
        ModelView model=new ModelView();
        model.setView("hello.jsp");
        return model;
    }
}

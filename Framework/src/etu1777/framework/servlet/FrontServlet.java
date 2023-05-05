package etu1777.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu1777.framework.Mapping;
import etu1777.framework.ModelView;
import etu1777.framework.Utils;

public class FrontServlet extends HttpServlet{
    HashMap<String, Mapping> mappingUrls;
    public void init(){
        Utils utils=new Utils();
        try {
            String classpath=getInitParameter("classpath");
            mappingUrls=utils.getAllURLMapping(classpath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void processRequest(HttpServletRequest req, HttpServletResponse res) throws Exception{
        String url=req.getRequestURI();
        Utils utils=new Utils();
        url=utils.getCoreURL(url);
        PrintWriter writer=res.getWriter();
        for(Map.Entry<String, Mapping> entry:mappingUrls.entrySet()){
            writer.println(entry.getKey()+": "+entry.getValue());
        }
        if(mappingUrls.containsKey(url)){
            ClassLoader loader=Thread.currentThread().getContextClassLoader();
            Class<? extends Object > classe=loader.loadClass(mappingUrls.get(url).getClassName());
            Object objet=classe.getConstructor().newInstance();
            Field[] fields=classe.getDeclaredFields();
            for(Field f:fields){
                String param=req.getParameter(f.getName());
                if(param!=null){
                    Class<? extends Object> typeClass=utils.getClassFromName(f.getType().getName());
                    Method setter=classe.getMethod("set"+utils.majStart(f.getName()), typeClass);
                    if(typeClass.getSimpleName().equals("Date")){
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                        Date d=format.parse(param);
                        setter.invoke(objet, d);
                    }
                    else if(f.getType().getSimpleName().equals("String")==false){
                        String parse=utils.getParseMethod(typeClass);
                        Method parser=typeClass.getMethod(parse, String.class);
                        setter.invoke(objet, parser.invoke(typeClass, param));
                    }else{
                        setter.invoke(objet, param);
                    }
                }
            }

            Method method_view=utils.getMethodeByAnnotation("urlpattern", url, classe);
            Parameter[] params=method_view.getParameters();
            ModelView view=null;
            if(params.length==1){
                Annotation[] annotes=params[0].getAnnotations();
                for(Annotation a:annotes){
                    if(a.annotationType().getSimpleName().equals("annote_param")){
                        String req_param=req.getParameter(a.annotationType().getMethod("value").invoke(a).toString());
                        if(req_param!=null){
                            Class class_param=utils.getClassFromName(params[0].getType().getName());
                            if(class_param.getSimpleName().equals("Date")){
                                SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                                Date d=format.parse(req_param);
                                view=(ModelView)(method_view.invoke(objet, d));
                            }
                            else if(class_param.getSimpleName().equals("String")==false){
                                String parse=utils.getParseMethod(class_param);
                                Method parser=class_param.getMethod(parse, String.class);
                                view=(ModelView)(method_view.invoke(objet, parser.invoke(class_param, req_param)));
                            }else{
                                view=(ModelView)(method_view.invoke(objet, req_param));
                            }
                        }
                        break;
                    }
                }
            }else if(params.length==0){
                view=(ModelView)(method_view.invoke(objet));
            }
            for(Map.Entry<String, Object> entry:view.getData().entrySet()){
                req.setAttribute(entry.getKey(), entry.getValue());
            }
            RequestDispatcher dispat=req.getRequestDispatcher(view.getView());
            dispat.forward(req, res);
        }else{
            writer.println("URL introuvable");
        }
    }
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        try {
            processRequest(req, res);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        try {
            processRequest(req, res);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
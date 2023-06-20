package etu1777.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import etu1777.framework.FileUpload;
import etu1777.framework.Mapping;
import etu1777.framework.ModelView;
import etu1777.framework.Utils;
import etu1777.framework.exceptions.AuthentificationErrorException;

@MultipartConfig
@SuppressWarnings("rawtypes")
public class FrontServlet extends HttpServlet{
    HashMap<String, Mapping> mappingUrls;
    HashMap<String, Object> singletonClass;
    String isConnected, role;
    public void init(){
        Utils utils=new Utils();
        try {
            String classpath=getInitParameter("classpath");
            String singletonAnnote="singleton";
            mappingUrls=utils.getAllURLMapping(classpath);
            singletonClass=utils.getAllSingletonClass(classpath, singletonAnnote);
            isConnected=getInitParameter("isConnected");
            role=getInitParameter("role");
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
        writer.println("Mapping urls: ");
        for(Map.Entry<String, Mapping> entry:mappingUrls.entrySet()){
            writer.println(entry.getKey()+": "+entry.getValue());
        }
        writer.println("Classes singletons: ");
        for(Map.Entry<String, Object> entry:singletonClass.entrySet()){
            writer.println(entry.getKey());
        }
        if(mappingUrls.containsKey(url)){
            ClassLoader loader=Thread.currentThread().getContextClassLoader();
            String className=mappingUrls.get(url).getClassName();
            Class<? extends Object > classe=loader.loadClass(className);
            Object objet=utils.instanceObjectSingleton(singletonClass, className, classe);
            Field[] fields=classe.getDeclaredFields();
            for(Field f:fields){
                Class typeClass=utils.getClassFromName(f.getType().getName());
                String param=req.getParameter(f.getName());
                Method setter=classe.getMethod("set"+utils.majStart(f.getName()), typeClass);
                utils.setFieldValue(f, typeClass, setter, objet, param);
                if(req.getContentType()!=null&&req.getContentType().startsWith("multipart/")){
                    Part filePart=req.getPart(f.getName());
                    if(filePart!=null&&typeClass.equals(FileUpload.class)){
                        InputStream fileData=filePart.getInputStream();
                        String fileName=filePart.getSubmittedFileName();
                        try{
                            FileUpload file=utils.setFileUploadFieldValue(objet, fileData, fileName);
                            setter.invoke(objet, file);
                        }finally{
                            fileData.close();
                        }
                    }
                }
            }

            Method method_view=utils.getMethodeByAnnotation("urlpattern", url, classe);
            try{
                Object status=req.getSession().getAttribute(role);
                Object connect=req.getSession().getAttribute(isConnected);
                String status_String="";
                boolean connecte=false;
                if(status!=null&&connect!=null){
                    status_String=status.toString();
                    connecte=(boolean)connect;
                }
                boolean authentificate=utils.checkMethod(method_view, status_String, connecte);
                if(authentificate==false){
                    throw new AuthentificationErrorException();
                }
                Parameter[] params=method_view.getParameters();
                ModelView view=null;
                if(params.length>0){
                    Object[] listParams=new Object[params.length];
                    for(int i=0; i<params.length; i++){
                        Annotation[] annotes=params[i].getAnnotations();
                        listParams[i]=null;
                        for(Annotation a:annotes){
                            if(a.annotationType().getSimpleName().equals("annote_param")){
                                String req_param=req.getParameter(a.annotationType().getMethod("value").invoke(a).toString());
                                if(req_param!=null){
                                    utils.addMethodParameterValue(params[i], listParams, i, req_param);
                                }
                                if(req.getContentType()!=null&&req.getContentType().startsWith("multipart/")){
                                    Part filePart=req.getPart(a.annotationType().getMethod("value").invoke(a).toString());
                                    if(filePart!=null){
                                        InputStream fileData=filePart.getInputStream();
                                        String fileName=filePart.getSubmittedFileName();
                                        try{
                                            FileUpload uploaded=utils.setFileUploadFieldValue(objet, fileData, fileName);
                                            listParams[i]=uploaded;
                                        }finally{
                                            fileData.close();
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    view=(ModelView)(method_view.invoke(objet, listParams));
                }else if(params.length==0){
                    view=(ModelView)(method_view.invoke(objet));
                }
                for(Map.Entry<String, Object> entry:view.getData().entrySet()){
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                for(Map.Entry<String, Object> entry:view.getSession().entrySet()){
                    req.getSession().setAttribute(entry.getKey(), entry.getValue());
                }
                RequestDispatcher dispat=req.getRequestDispatcher(view.getView());
                dispat.forward(req, res);
            }catch(AuthentificationErrorException e){
                throw e;
            }
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
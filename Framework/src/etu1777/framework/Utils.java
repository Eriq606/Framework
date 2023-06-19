package etu1777.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import etu1777.framework.annotations.auth;
import etu1777.framework.annotations.scope;

public class Utils {
    public String getCoreURL(String url){
        String[] newUrl=url.split("/");
        String rep=new String();
        for(int i=2; i<newUrl.length-1; i++){
            rep+=newUrl[i]+"/";
        }
        rep+=newUrl[newUrl.length-1];
        return rep;
    }
    public String majStart(String n){
        String nouveau=n.replaceFirst(String.valueOf(n.charAt(0)), String.valueOf(n.charAt(0)).toUpperCase());
        return nouveau;
    }
    @SuppressWarnings("rawtypes")
    public Class getClassFromName(String type) throws ClassNotFoundException{
        if(type.equals("int")){
            return Integer.class;
        }else if(type.equals("char")){
            return Character.class;
        }else if(type.equals("double")){
            return Double.class;
        }else if(type.equals("boolean")){
            return Boolean.class;
        }else if(type.equals("byte")){
            return Byte.class;
        }else if(type.equals("long")){
            return Long.class;
        }else if(type.equals("short")){
            return Short.class;
        }else if(type.equals("float")){
            return Float.class;
        }
        return Class.forName(type);
    }
    @SuppressWarnings("rawtypes")
    public String getParseMethod(Class classe){
        String rep="parse";
        if(classe.getSimpleName().equals("Integer")){
            rep+="Int";
        }else{
            rep+=classe.getSimpleName();
        }
        return rep;
    }
    @SuppressWarnings("rawtypes")
    public Method getMethodeByAnnotation(String annote, String valueAnnote, Class classe) throws Exception{
        HashMap<Method, Annotation> methodes=getAllAnnotedMethods(annote, classe);
        for(Map.Entry<Method,Annotation> entry:methodes.entrySet()){
            if(entry.getValue().annotationType().getMethod("url").invoke(entry.getValue()).equals(valueAnnote)){
                return entry.getKey();
            }
        }
        return null;
    }
    @SuppressWarnings("rawtypes")
    public LinkedList<Class> getAllPackagesClasses(String path, String packageName) throws ClassNotFoundException{
        File first=new File(path);
        LinkedList<Class> liste=new LinkedList<>();
        File[] listFiles=first.listFiles();
        ClassLoader loader=Thread.currentThread().getContextClassLoader();
        for(File f:listFiles){
            if(f.isDirectory()){
                LinkedList<Class> otherList=getAllPackagesClasses(f.getAbsolutePath(), packageName+f.getName()+".");
                for(Class c:otherList){
                    liste.add(c);
                }
            }else if(f.getName().endsWith(".class")){
                liste.add(loader.loadClass(packageName+f.getName().substring(0, f.getName().length()-6)));
            }
        }
        return liste;
    }
    @SuppressWarnings("rawtypes")
    public HashMap<Method, Annotation> getAllAnnotedMethods(String annote, Class classe){
        HashMap<Method, Annotation> liste=new HashMap<>();
        Method[] methods=classe.getDeclaredMethods();
        for(Method m:methods){
            Annotation[] annotes=m.getAnnotations();
            for(Annotation an:annotes){
                if(an.annotationType().getSimpleName().equals(annote)){
                    liste.put(m, an);
                    break;
                }
            }
        }
        return liste;
    }
    @SuppressWarnings("rawtypes")
    public HashMap<String, Mapping> getAllURLMapping(String path) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        LinkedList<Class> listeClass=getAllPackagesClasses(path, "");
        HashMap<String, Mapping> listeUrl=new HashMap<>();
        for(Class c:listeClass){
            HashMap<Method, Annotation> listeMeth=getAllAnnotedMethods("urlpattern", c);
            Object[] methodes=listeMeth.keySet().toArray();
            for(Object m:methodes){
                Mapping map=new Mapping();
                map.setClassName(c.getName());
                Method meth=(Method)m;
                map.setMethod(meth.getName());
                Annotation annote=listeMeth.get(meth);
                String value=annote.getClass().getMethod("url").invoke(annote).toString();
                listeUrl.put(value, map);
            }
        }
        return listeUrl;
    }
    public HashMap<String, Object> getAllSingletonClass(String path, String singletonAnnote) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        LinkedList<Class> listeClass=getAllPackagesClasses(path, "");
        HashMap<String, Object> singletons=new HashMap<>();
        for(Class c:listeClass){
            Annotation annotes=c.getAnnotation(scope.class);
            if(annotes!=null){
                String scope=annotes.annotationType().getMethod("value").invoke(annotes).toString();
                if(scope.equals(singletonAnnote)){
                    singletons.put(c.getName(), null);
                }
            }
        }
        return singletons;
    }
    public Object getDefaultValue(Field field){
        if(field.getType().getSimpleName().equals("int")||field.getType().getSimpleName().equals("float")||
            field.getType().getSimpleName().equals("double")||field.getType().getSimpleName().equals("long")||
            field.getType().getSimpleName().equals("short")){
            return 0;
        }else if(field.getType().getSimpleName().equals("boolean")){
            return false;
        }else if(field.getType().getSimpleName().equals("char")){
            return ' ';
        }
        return null;
    }
    public Constructor getEmptyConstructor(Class classe){
        Constructor[] constructors=classe.getConstructors();
        for(Constructor c:constructors){
            if(c.getParameterCount()==0){
                return c;
            }
        }
        return constructors[0];
    }
    public void resetAttributes(Object obj, Class classe) throws IllegalArgumentException, IllegalAccessException{
        Field[] champs=classe.getDeclaredFields();
        for(Field f:champs){
            f.setAccessible(true);
            f.set(obj, getDefaultValue(f));
        }
    }
    public Object instanceObjectSingleton(HashMap<String, Object> singletons, String classeName, Class classe) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException{
        Object obj=null;
        boolean isSingletonClass=singletons.containsKey(classeName);
        if(isSingletonClass){
            obj=singletons.get(classeName);
            if(obj==null){
                singletons.put(classeName, getEmptyConstructor(classe).newInstance());
                obj=singletons.get(classeName);
            }
            resetAttributes(obj,classe);
        }else{
            obj=getEmptyConstructor(classe).newInstance();
        }
        return obj;
    }
    public boolean checkMethod(Method methode, HttpSession session, String role) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        Annotation annote=methode.getAnnotation(auth.class);
        if(annote==null||annote.annotationType().getMethod("admin").invoke(annote).equals(session.getAttribute(role))){
            return true;
        }
        return false;
    }
    public void setFieldValue(Field f, Class typeClass, Method setter, Object objet, String param) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        if(param!=null){    
            if(f.getType().getSimpleName().equals("String")==false){
                String parse=getParseMethod(typeClass);
                Method parser=typeClass.getMethod(parse, String.class);
                setter.invoke(objet, parser.invoke(typeClass, param));
            }else{
                setter.invoke(objet, param);
            }
        }
    }
    public FileUpload setFileUploadFieldValue(Object objet, InputStream fileData, String fileName) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
        try{
            byte[] byteArray=new byte[8192];
            int byteRead;
            while((byteRead=fileData.read(byteArray))!=-1){
                byteOut.write(byteArray, 0, byteRead);
            }
            byte[] file=byteOut.toByteArray();
            FileUpload uploaded=new FileUpload();
            uploaded.setName(fileName);
            uploaded.setFile(file);
            return uploaded;
        }finally{
            byteOut.close();
        }
    }
    public void addMethodParameterValue(Parameter params, Object[] listParams, int i, String req_param) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Class class_param=getClassFromName(params.getType().getName());
        if(class_param.getSimpleName().equals("String")==false){
            String parse=getParseMethod(class_param);
            Method parser=class_param.getMethod(parse, String.class);
            listParams[i]=parser.invoke(class_param, req_param);
        }else{
            listParams[i]=req_param;
        }
    }
}
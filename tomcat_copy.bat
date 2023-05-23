jar -cvf test-Framework/WEB-INF/lib/FrameURL.jar Framework/bin/etu1777
xcopy test-Framework TEMP\ /E
jar -cvf E:/Eriq_RohWeltall/apache-tomcat-8/webapps/test-Framework.war TEMP/*
rmdir /s /q TEMP
cmd
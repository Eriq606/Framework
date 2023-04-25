cd Framework/bin
jar -cvf ../../test-Framework/WEB-INF/lib/FrameURL.jar etu1777
cd ../../test-Framework
jar -cvf /var/lib/tomcat9/webapps/test-Framework.war *
cd ..
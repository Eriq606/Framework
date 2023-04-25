<%@page import="asset.Emp, java.util.LinkedList" %>
<html>
<head>
    <title>Employee</title>
</head>
<body>
    <h1>Hello</h1>
    <h1>Liste des employes</h1>
    <ul>
    <% LinkedList<Emp> lst=(LinkedList<Emp>)request.getAttribute("lst");
    for(Emp e:lst){ %>
        <li><% out.print(e.getId()+": "+e.getNom()); %></li>
    <% } %>
    </ul>
</body>
</html>
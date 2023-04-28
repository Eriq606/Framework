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
    <form action="emp-save" method="post">
        <p>Nom: <input type="text" name="nom"><p>
        <p>ID: <input type="number" name="id"></p>
        <p><input type="submit" value="Valider"></p>
    </form>
</body>
</html>
<%@page import="asset.Emp, java.util.LinkedList" %>
<html>
<head>
    <title>Employee</title>
    <link rel="stylesheet" type="text/css" href="assets/css/style.css">
</head>
<body>
    <h1>Hello</h1>
    <h1>Liste des employes</h1>
    <ul>
    <% LinkedList<Emp> lst=(LinkedList<Emp>)request.getAttribute("lst");
    for(Emp e:lst){ %>
        <li><a href="find-by-id.dodo?id=<%= e.getId() %>&nom=<%= e.getNom() %>"><% out.print(e.getId()+": "+e.getNom()); %></a></li>
    <% } %>
    </ul>
    <form action="emp-save.dodo" method="post">
        <p>Nom: <input type="text" name="nom"><p>
        <p>ID: <input type="number" name="id"></p>
        <p><input type="submit" value="Valider"></p>
    </form>
    <p><a href="">Clients</a></p>
</body>
</html>
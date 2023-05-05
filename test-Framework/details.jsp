<%@page import="asset.Emp" %>
<html>
<head>
    <title>Employee</title>
</head>
<body>
    <h1>Hello <% Emp e=(Emp)request.getAttribute("emp");
    out.print(e.getNom()); %></h1>
    <p>ID: <%= e.getId() %></p>
    <p><a href="huhu">Retour</a></p>
</body>
</html>
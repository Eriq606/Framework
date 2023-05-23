<%@page import="asset.Client" %>
<html>
<head>
    <title>Client</title>
</head>
<body>
    <h1>Hello <% Client e=(Client)request.getAttribute("client");
    out.print(e.getNom()); %></h1>
    <p>Document: <%= e.getFile().getName() %></p>
    <p><a href="huhu.dodo">Retour</a></p>
</body>
</html>
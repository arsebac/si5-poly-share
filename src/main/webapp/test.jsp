<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="test.HelloAppEngine" %>
<html>
<head>
    <title>PolyShare</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
</head>
<body>
<h1>Salut</h1>

<p>This is <%= HelloAppEngine.getInfo() %>.</p>
<table>
    <tr>
        <td colspan="2" style="font-weight:bold;">Available Servlets:</td>
    </tr>
    <tr>
        <td><a href='${pageContext.request.contextPath}/hello'>Hello App Engine</a></td>
    </tr>
    <tr>
        <td><%= System.currentTimeMillis() %>
        </td>
    </tr>
    <tr>
    </tr>
</table>
<form action="/sendmail" method="post">
    <div class="form-group">
        <label for="mail">Email address</label>
        <input type="email" class="form-control" id="mail" aria-describedby="emailHelp" placeholder="Enter email">
    </div>
    <div class="form-group">
        <label for="exampleInputPassword1">Password</label>
        <input type="password" class="form-control" id="exampleInputPassword1" placeholder="Password">
    </div>
    <button type="submit" class="btn btn-primary">Submit</button>
</form>
</body>
</html>

<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>PolyShare</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <script>
        function signIn() {
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {
                    alert(this.responseText);
                }
            };
            xhttp.open("POST", "/users", true);
            xhttp.setRequestHeader("Content-type", "application/json");
            xhttp.send("{\"score\":\""+document.getElementById("score").value+"\",\"email\":\"" + document.getElementById("email").value + "\"}");
        }

    </script>
</head>
<body>
<div class="container">
    <h1>Home page</h1>
    <form method="POST" action="/api/upload" enctype="multipart/form-data">
        <div class="form-group row">
            <label for="inputEmail" class="col-sm-2 col-form-label">Email</label>
            <div class="col-sm-10">
                <input type="email" class="form-control" id="inputEmail" placeholder="Email" name="email">
            </div>
        </div>
        <div class="form-group row">
            <label for="videoFile" class="col-sm-2 col-form-label">Video</label>
            <div class="col-sm-10">
                <input type="file" class="form-control-file" id="videoFile" name="file">
            </div>
        </div>
        <div class="form-group row">
            <label for="inputTitle" class="col-sm-2 col-form-label">Title (please no space)</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="inputTitle" placeholder="Title" name="title">
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <h1>Inscription</h1>
    <form enctype='application/json' action="/users" method="post">
        <div class="form-group row">
            <label for="email">Email :</label><input id="email" value="email" type="text">
        </div>
        <div class="form-group row">
            <label for="score">Score :</label><input id="score" value="score" type="text"/>
        </div>
        <div class="form-group row">
            <input type="submit"/>
        </div>
    </form>
</body>
</html>

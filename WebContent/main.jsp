<!DOCTYPE HTML>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.io.*,java.util.*" %>
<%@page import="home.WordScrambler"%>

<html>
<head>
<title>Word Scrambler</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style>
    table {
        table-layout: fixed;
        border-collapse: collapse;
        border: 1px solid black;
        width: 40%;
        font-size: 1.2em
    }
    table td {
        border: 1px solid black;
    }
    
    .taggedWord {
        color: red;
        font-weight: bold;
    }
</style>
</head>


<body>
<% WordScrambler ws = new home.WordScrambler(); %>

<h2>What you have requested:</h2>
<table>
<%

    String letters = request.getParameter("inputLetters");
    // String length = request.getParameter("inputLength");
    
    out.print("<tr><td>Scrambled letters: </td>");
    out.print("<td>" + letters + "</td></tr>");
    
    // out.print("<tr><td>Desirable length: </td>");
    //out.print("<td>" + length + "</td></tr>");
%>
</table>

<%
     out.print(ws.unscrambleAll(letters)); 
%>

<br><br><br><br><br><br>
</body>
</html>
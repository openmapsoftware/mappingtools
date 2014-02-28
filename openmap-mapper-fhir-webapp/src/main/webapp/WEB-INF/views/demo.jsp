<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body>
<div id="start">
	<h1>FHIR Server Farm</h1>
	<p>The time is now <%= new java.util.Date() %></p>
	<p>Base URL is ${baseUrl} .</p>
	<p>This page demonstrates two FHIR servers - <b>silver</b> and <b>bronze</b> -
	configured by mapping two existing application databases onto FHIR resource class models. </p>
	
	<h3>Unsupported FHIR searches, and base URL convention</h3>
	
	<table>
		<tr>
			<td>Server Gold not known</td>
			<td><a href="${baseUrl}/gold/Patient?gender=male">/gold/Patient?gender=male</a></td>
		</tr>
		<tr>
			<td>Resource Document not supported</td>
			<td><a href="${baseUrl}/silver/Document?1234">/silver/Document?1234</a></td>
		</tr>
		<tr>
			<td>Valid query on server Silver</td>
			<td><a href="${baseUrl}/silver/Patient?gender=M">/silver/Patient?gender=M</a></td>
		</tr>
		<tr>
			<td>Heads of links are not displayed from now on</td>
			<td><a href="${baseUrl}/silver/Patient?gender=M">/silver/Patient?gender=M</a></td>
		</tr>
	</table>
	
	<h3>supported FHIR searches on the <b>silver</b> server</h3>
	
	<table>
		<tr>
			<td>Search for all patients of given gender</td>
			<td><a href="${baseUrl}/silver/Patient?gender=M">/silver/Patient?gender=M</a></td>
		</tr>
		<tr>
			<td>Search for a patient with a given FHIR id</td>
			<td><a href="${baseUrl}/silver/Patient?_id=72428">/silver/Patient?_id=72428</a></td>
		</tr>
		<tr>
			<td>Search for a patient with a given NHS number</td>
			<td><a href="${baseUrl}/silver/Patient?identifier=NHS|9091762749">/silver/Patient?identifier=NHS|9091762749</a></td>
		</tr>
		<tr>
			<td>Search for patients with an exact family name</td>
			<td><a href="${baseUrl}/silver/Patient?family:exact=GREWAL">/silver/Patient?family:exact=GREWAL</a></td>
		</tr>
		<tr>
			<td>Search for patients with a given birthdate</td>
			<td><a href="${baseUrl}/silver/Patient?birthDate=2013-08-11">/silver/Patient?birthDate=2013-08-11</a></td>
		</tr>
		<tr>
			<td>Search on two criteria at once</td>
			<td><a href="${baseUrl}/silver/Patient?birthDate=2013-08-11&family:exact=SANTOS">/silver/Patient?birthDate=2013-08-11&family:exact=SANTOS</a></td>
		</tr>
	</table>
	
	<h3>supported FHIR searches on the <b>bronze</b> server</h3>
	
	<table>
		<tr>
			<td>Search for all patients of given gender</td>
			<td><a href="${baseUrl}/bronze/Patient?gender=F">/bronze/Patient?gender=F</a></td>
		</tr>
		<tr>
			<td>Search for a patient with a given FHIR id</td>
			<td><a href="${baseUrl}/bronze/Patient?_id=101">/bronze/Patient?_id=101</a></td>
		</tr>
		<tr>
			<td>Search for a patient with a given NHS number</td>
			<td><a href="${baseUrl}/bronze/Patient?identifier=NHS|9091762749">/bronze/Patient?identifier=NHS|9091762749</a></td>
		</tr>
		<tr>
			<td>Search for patients with an exact family name</td>
			<td><a href="${baseUrl}/bronze/Patient?family:exact=Jones">/bronze/Patient?family:exact=Jones</a></td>
		</tr>
		<tr>
			<td>Search for patients with a given birthdate</td>
			<td><a href="${baseUrl}/bronze/Patient?birthDate=1987-03-03">/bronze/Patient?birthDate=1987-03-03</a></td>
		</tr>
		<tr>
			<td>Search on two criteria at once</td>
			<td><a href="${baseUrl}/bronze/Patient?birthDate=1987-03-03&family:exact=Stride">/bronze/Patient?birthDate=1987-03-03&family:exact=Stride</a></td>
		</tr>
	</table>
	
</div>
</body>

</html>

